/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.view;

import java.util.Arrays;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.transform.integer.ReshapeTransform;

/**
 * Wrap a {@code source} RandomAccess which is related to this by a
 * {@link ReshapeTransform} {@code transformToSource}.
 * <p>
 * The source RandomAccess is kept at the transformed position at all times, and
 * is moved incrementally where possible: moving along a dimension that belongs
 * to a group with a single target dimension (a split, or a copy) moves the
 * source by a multiple of the stride along that target dimension. In groups
 * with several target dimensions (a merge), unit steps and short moves are
 * carried from digit to digit without division, and only larger moves
 * recompute the position of the group.
 * <p>
 * Moves are computed in {@code long} arithmetic. The product of a distance and
 * the stride of the moved dimension must not overflow.
 *
 * <h2>Encoding of targetAxis and sourceGroup</h2>
 *
 * We use {@link #targetAxisOrGroup} to compactly encode
 * <ul>
 * <li>whether a source axis is a singleton axis, belongs to a group with a single target dimension, or a digit group,</li>
 * <li>the target dimension if it's a single target dimension group, and</li>
 * <li>the group index if it's a digit group.</li>
 * </ul>
 * {@code targetAxisOrGroup[d]} for source axis {@code d} is
 * <pre>
 * {@code >=  0}  target axis for single target dimension group (){@code groupTargetAxes[sourceGroup[d]][0]}
 * {@code == -1}  for singleton axis
 * {@code <= -2}  group index for a digit group, encoded as {@code -2 - sourceGroup[d]}
 * </pre>
 * Additionally, {@link #firstAxis firstAxis[g]} stores the first (fastest)
 * target axis of every group {@code g}.
 * <p>
 * This saves several dependent array lookups.
 *
 * <h2>Strides</h2>
 *
 * The first (fastest) source axis of every group has stride 1. All other axes
 * of the group have larger strides, because all axes in the group have size of
 * at least 2. Source axes of size 1 are not in any group, and have stride 0.
 * Therefore {@code sourceStride[ d ] == 1} if and only if {@code d} is the
 * first axis of a group.
 * <p>
 * This is what {@code updateAll()} relies on: It goes backwards through the source
 * axes, summing up {@code f}. When it gets to an axis with stride 1, then
 * {@code f} is complete for the current group.
 * <p>
 * In general, stride 1 (meaning that {@code d} is the first dimension of its
 * group) is handled separately, because it is the common case when iterating,
 * and {@code fwd()} of the source can be cheaper than {@code move()}.
 *
 * @param <T>
 */
public final class ReshapeRandomAccess< T > extends AbstractLocalizable implements RandomAccess< T >
{
	/**
	 * source RandomAccess. note that this is the <em>target</em> of the
	 * transformToSource.
	 */
	private final RandomAccess< T > s;

	/**
	 * For each dimension of this, its stride in the flat index of its group. 1
	 * for the first dimension of each group, and 0 for dimensions of size 1
	 * (which are not in any group).
	 */
	private final long[] stride;

	/**
	 * For each source axis, the target axis or the digit group that it maps to:
	 * <ul>
	 * <li>{@code >= 0}: the target axis, for source axes in a group with a single
	 * target axis,</li>
	 * <li>{@code -1}: for source axes of size 1,</li>
	 * <li>{@code <= -2}: the {@link #encodeGroup encoded} index of the group in
	 * {@link #groupTargetAxes}, for source axes in a digit group.</li>
	 * </ul>
	 */
	private final int[] targetAxisOrGroup;

	/**
	 * Encode the index of a digit group for {@link #targetAxisOrGroup}.
	 */
	private static int encodeGroup( final int g )
	{
		return -2 - g;
	}

	/**
	 * Decode the index of a digit group from {@link #targetAxisOrGroup}.
	 */
	private static int decodeGroup( final int encoded )
	{
		return -2 - encoded;
	}

	/**
	 * For each group, its target axes (without axes of size 1), fastest first.
	 * <p>
	 * This contains all groups, so that it can be indexed with the groups of the
	 * {@link ReshapeTransform}. After construction, only the entries of digit
	 * groups (which have at least 2 target axes) are used. For groups with a
	 * single target axis, that axis is directly in {@link #targetAxisOrGroup}.
	 */
	private final int[][] groupTargetAxes;

	/**
	 * For each source axis, the first (fastest) target axis of its group {@code
	 * g}: {@code groupTargetAxes[ g ][ 0 ]}. Undefined for source axes of size 1.
	 * Only used for source axes in digit groups.
	 * <p>
	 * This is redundant, but saves two array lookups in {@link #fwd} and
	 * {@link #bck}.
	 */
	private final int[] firstAxis;

	/**
	 * The size of each source view axis. For the axes of a digit group, these
	 * are the bases of the digits.
	 */
	private final long[] targetDimensions;

	/**
	 * Position of the source RandomAccess.
	 * <p>
	 * This is always valid for axes in digit groups, where it holds the current
	 * digits. Source axes of size 1 are always 0. For all other axes, it is only
	 * set (temporarily) in {@link #updateAll()}. The position along these axes is
	 * maintained by the source RandomAccess itself.
	 */
	private final long[] targetPosition;

	ReshapeRandomAccess( final RandomAccess< T > source, final ReshapeTransform transformToSource )
	{
		super( transformToSource.numSourceDimensions() );

		assert source.numDimensions() == transformToSource.numTargetDimensions();

		s = source;
		targetDimensions = transformToSource.getTargetDimensions();
		groupTargetAxes = new int[ transformToSource.numGroups() ][];
		Arrays.setAll( groupTargetAxes, transformToSource::getGroupTargetAxes );
		stride = transformToSource.getSourceStride();

		targetAxisOrGroup = new int[ n ];
		firstAxis = new int[ n ];
		for ( int d = 0; d < n; ++d )
		{
			final int g = transformToSource.getSourceGroup( d );
			if ( g < 0 )
			{
				// d is a singleton axis: skipped
				targetAxisOrGroup[ d ] = -1;
			}
			else
			{
				final int[] group = groupTargetAxes[ g ];
				firstAxis[ d ] = group[ 0 ];
				if ( group.length == 1 )
				{
					// d belongs to a group with a single target axis:
					// point to target axis directly
					targetAxisOrGroup[ d ] = group[ 0 ];
				}
				else
				{
					// d belongs to a group with multiple target axis:
					// encode the group index
					targetAxisOrGroup[ d ] = encodeGroup( g );
				}
			}
		}

		targetPosition = new long[ targetDimensions.length ];
		s.setPosition( targetPosition );
	}

	private ReshapeRandomAccess( final ReshapeRandomAccess< T > randomAccess )
	{
		super( randomAccess.positionAsLongArray() );

		s = randomAccess.s.copy();
		stride = randomAccess.stride;
		targetAxisOrGroup = randomAccess.targetAxisOrGroup;
		groupTargetAxes = randomAccess.groupTargetAxes;
		firstAxis = randomAccess.firstAxis;
		targetDimensions = randomAccess.targetDimensions;
		targetPosition = randomAccess.targetPosition.clone();
	}

	/**
	 * floor( a / b ) for {@code b > 0}.
	 */
	private static long floorDiv( final long a, final long b )
	{
		return a >= 0 ? a / b : Math.floorDiv( a, b );
	}

	/**
	 * Add {@code df} to the flat index of the digit group with the given source
	 * {@code axes}, and move the source accordingly.
	 * <p>
	 * This is an addition in a mixed-radix number system, starting from the
	 * first (fastest) digit: If the sum is outside the range of the digit, then
	 * the remainder is the new digit, and the (floored) quotient is carried, that
	 * is, added to the next digit. It ends at the first digit without a carry.
	 * The last digit is unbounded.
	 */
	private void moveGroup( final int[] axes, long df )
	{
		final int last = axes.length - 1;
		for ( int i = 0; df != 0 && i < last; ++i )
		{
			final int a = axes[ i ];
			final long p = targetPosition[ a ] + df;
			if ( p >= 0 && p < targetDimensions[ a ] )
			{
				s.move( df, a );
				targetPosition[ a ] = p;
				return;
			}
			final long q = floorDiv( p, targetDimensions[ a ] );
			final long r = p - q * targetDimensions[ a ];
			if ( r != targetPosition[ a ] )
				s.move( r - targetPosition[ a ], a );
			targetPosition[ a ] = r;
			df = q;
		}
		if ( df != 0 )
		{
			s.move( df, axes[ last ] );
			targetPosition[ axes[ last ] ] += df;
		}
	}

	/**
	 * Add 1 to the first digit, with carry.
	 */
	private void increment( final int[] axes )
	{
		final int last = axes.length - 1;
		int i = 0;
		while ( i < last && targetPosition[ axes[ i ] ] == targetDimensions[ axes[ i ] ] - 1 )
		{
			s.move( -targetPosition[ axes[ i ] ], axes[ i ] );
			targetPosition[ axes[ i ] ] = 0;
			++i;
		}
		s.fwd( axes[ i ] );
		++targetPosition[ axes[ i ] ];
	}

	/**
	 * Subtract 1 from the first digit, with borrow.
	 */
	private void decrement( final int[] axes )
	{
		final int last = axes.length - 1;
		int i = 0;
		while ( i < last && targetPosition[ axes[ i ] ] == 0 )
		{
			final long max = targetDimensions[ axes[ i ] ] - 1;
			s.move( max, axes[ i ] );
			targetPosition[ axes[ i ] ] = max;
			++i;
		}
		s.bck( axes[ i ] );
		--targetPosition[ axes[ i ] ];
	}

	/**
	 * Recompute the source position from {@link #position} and set it.
	 * <p>
	 * This is equivalent to {@link ReshapeTransform#apply}, but goes backwards
	 * through the dimensions, summing up the flat index {@code f} of the current
	 * group. The first dimension of each group has stride 1. When we get there,
	 * {@code f} is complete.
	 */
	private void updateAll()
	{
		long f = 0;
		for ( int d = n - 1; d >= 0; --d )
		{
			final long st = stride[ d ];
			f += st * position[ d ];
			if ( st == 1 )
			{
				final int t = targetAxisOrGroup[ d ];
				if ( t >= 0 )
					targetPosition[ t ] = f;
				else
				{
					final int[] axes = groupTargetAxes[ decodeGroup( t ) ];
					final int last = axes.length - 1;
					for ( int i = 0; i < last; ++i )
					{
						final int a = axes[ i ];
						final long q = floorDiv( f, targetDimensions[ a ] );
						targetPosition[ a ] = f - q * targetDimensions[ a ];
						f = q;
					}
					targetPosition[ axes[ last ] ] = f;
				}
				f = 0;
			}
		}
		s.setPosition( targetPosition );
	}

	@Override
	public void fwd( final int d )
	{
		assert d < n;
		++position[ d ];
		final long st = stride[ d ];
		if ( st == 1 )
		{
			final int t = targetAxisOrGroup[ d ];
			if ( t >= 0 )
				s.fwd( t );
			else
			{
				// in most cases, only the first digit changes
				final int a = firstAxis[ d ];
				if ( targetPosition[ a ] < targetDimensions[ a ] - 1 )
				{
					++targetPosition[ a ];
					s.fwd( a );
				}
				else
					increment( groupTargetAxes[ decodeGroup( t ) ] );
			}
		}
		else if ( st != 0 )
		{
			final int t = targetAxisOrGroup[ d ];
			if ( t >= 0 )
				s.move( st, t );
			else
				moveGroup( groupTargetAxes[ decodeGroup( t ) ], st );
		}
	}

	@Override
	public void bck( final int d )
	{
		assert d < n;
		--position[ d ];
		final long st = stride[ d ];
		if ( st == 1 )
		{
			final int t = targetAxisOrGroup[ d ];
			if ( t >= 0 )
				s.bck( t );
			else
			{
				// in most cases, only the first digit changes
				final int a = firstAxis[ d ];
				if ( targetPosition[ a ] > 0 )
				{
					--targetPosition[ a ];
					s.bck( a );
				}
				else
					decrement( groupTargetAxes[ decodeGroup( t ) ] );
			}
		}
		else if ( st != 0 )
		{
			final int t = targetAxisOrGroup[ d ];
			if ( t >= 0 )
				s.move( -st, t );
			else
				moveGroup( groupTargetAxes[ decodeGroup( t ) ], -st );
		}
	}

	@Override
	public void move( final int distance, final int d )
	{
		move( ( long ) distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		assert d < n;
		position[ d ] += distance;
		final int t = targetAxisOrGroup[ d ];
		if ( t >= 0 )
			s.move( distance * stride[ d ], t );
		else if ( t != -1 )
			moveGroup( groupTargetAxes[ decodeGroup( t ) ], distance * stride[ d ] );
	}

	@Override
	public void move( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] += localizable.getLongPosition( d );
		updateAll();
	}

	@Override
	public void move( final int[] distance )
	{
		assert distance.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		updateAll();
	}

	@Override
	public void move( final long[] distance )
	{
		assert distance.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ];
		updateAll();
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		assert localizable.numDimensions() >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = localizable.getLongPosition( d );
		updateAll();
	}

	@Override
	public void setPosition( final int[] pos )
	{
		assert pos.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
		updateAll();
	}

	@Override
	public void setPosition( final long[] pos )
	{
		assert pos.length >= n;
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
		updateAll();
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		setPosition( ( long ) pos, d );
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		assert d < n;
		final long distance = pos - position[ d ];
		position[ d ] = pos;
		final int t = targetAxisOrGroup[ d ];
		if ( t >= 0 )
			s.move( distance * stride[ d ], t );
		else if ( t != -1 )
			moveGroup( groupTargetAxes[ decodeGroup( t ) ], distance * stride[ d ] );
	}

	@Override
	public T get()
	{
		return s.get();
	}

	@Override
	public T getType()
	{
		return s.getType();
	}

	@Override
	public ReshapeRandomAccess< T > copy()
	{
		return new ReshapeRandomAccess<>( this );
	}
}
