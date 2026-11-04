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
	 * For each dimension of this, the group it belongs to, or -1.
	 */
	private final int[] group;

	/**
	 * For each dimension of this, its stride in the flat index of its group.
	 */
	private final long[] stride;

	/**
	 * The non-singleton target dimensions of all groups, in order. The target
	 * dimensions of group {@code g} are {@code axis[ start[ g ] ]} to {@code
	 * axis[ start[ g + 1 ] - 1 ]}. We call their positions "digits".
	 */
	private final int[] axis;

	/**
	 * The size of each target dimension in {@link #axis}.
	 */
	private final long[] dim;

	/**
	 * For each group, the index of its first digit in {@link #axis}. Has one
	 * extra element at the end.
	 */
	private final int[] start;

	/**
	 * For each group, the current flat index.
	 */
	private final long[] flat;

	/**
	 * The current position in each target dimension in {@link #axis}.
	 */
	private final long[] digit;

	/**
	 * Current target position. Only used for bulk position updates. Target
	 * dimensions of size 1 stay 0.
	 */
	private final long[] tmpPosition;

	ReshapeRandomAccess( final RandomAccess< T > source, final ReshapeTransform transformToSource )
	{
		super( transformToSource.numSourceDimensions() );

		assert source.numDimensions() == transformToSource.numTargetDimensions();

		s = source;
		group = new int[ n ];
		stride = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			group[ d ] = transformToSource.getSourceGroup( d );
			stride[ d ] = transformToSource.getSourceStride( d );
		}

		final int numGroups = transformToSource.numGroups();
		start = new int[ numGroups + 1 ];
		for ( int g = 0; g < numGroups; ++g )
			start[ g + 1 ] = start[ g ] + transformToSource.getGroupTargetAxes( g ).length;
		axis = new int[ start[ numGroups ] ];
		dim = new long[ start[ numGroups ] ];
		for ( int g = 0; g < numGroups; ++g )
		{
			final int[] axes = transformToSource.getGroupTargetAxes( g );
			for ( int i = 0; i < axes.length; ++i )
			{
				axis[ start[ g ] + i ] = axes[ i ];
				dim[ start[ g ] + i ] = transformToSource.getTargetDimension( axes[ i ] );
			}
		}
		flat = new long[ numGroups ];
		digit = new long[ axis.length ];

		tmpPosition = new long[ transformToSource.numTargetDimensions() ];
		s.setPosition( tmpPosition );
	}

	private ReshapeRandomAccess( final ReshapeRandomAccess< T > randomAccess )
	{
		super( randomAccess.positionAsLongArray() );

		s = randomAccess.s.copy();
		group = randomAccess.group;
		stride = randomAccess.stride;
		axis = randomAccess.axis;
		dim = randomAccess.dim;
		start = randomAccess.start;
		flat = randomAccess.flat.clone();
		digit = randomAccess.digit.clone();
		tmpPosition = randomAccess.tmpPosition.clone();
	}

	/**
	 * floor( a / b ) for {@code b > 0}.
	 */
	private static long floorDiv( final long a, final long b )
	{
		return a >= 0 ? a / b : Math.floorDiv( a, b );
	}

	/**
	 * Add {@code df} to the flat index of group {@code g}, and move the source
	 * accordingly.
	 */
	private void moveGroup( final int g, final long df )
	{
		flat[ g ] += df;
		final int first = start[ g ];
		final int last = start[ g + 1 ] - 1;
		if ( first == last )
		{
			digit[ first ] += df;
			s.move( df, axis[ first ] );
			return;
		}

		if ( df == 1 )
		{
			increment( first, last );
			return;
		}
		if ( df == -1 )
		{
			decrement( first, last );
			return;
		}

		final long d0 = dim[ first ];
		if ( df > -d0 && df < d0 )
		{
			// at most one carry
			final long p = digit[ first ] + df;
			if ( p >= d0 )
			{
				s.move( p - d0 - digit[ first ], axis[ first ] );
				digit[ first ] = p - d0;
				increment( first + 1, last );
			}
			else if ( p < 0 )
			{
				s.move( p + d0 - digit[ first ], axis[ first ] );
				digit[ first ] = p + d0;
				decrement( first + 1, last );
			}
			else
			{
				s.move( df, axis[ first ] );
				digit[ first ] = p;
			}
			return;
		}

		// recompute the position in the group
		long f = flat[ g ];
		for ( int i = first; i < last; ++i )
		{
			final long q = floorDiv( f, dim[ i ] );
			final long r = f - q * dim[ i ];
			if ( r != digit[ i ] )
			{
				s.move( r - digit[ i ], axis[ i ] );
				digit[ i ] = r;
			}
			f = q;
		}
		if ( f != digit[ last ] )
		{
			s.move( f - digit[ last ], axis[ last ] );
			digit[ last ] = f;
		}
	}

	/**
	 * Add 1 to digit {@code i}, with carry up to digit {@code last}.
	 */
	private void increment( int i, final int last )
	{
		while ( i < last && digit[ i ] == dim[ i ] - 1 )
		{
			s.move( -digit[ i ], axis[ i ] );
			digit[ i ] = 0;
			++i;
		}
		s.fwd( axis[ i ] );
		++digit[ i ];
	}

	/**
	 * Subtract 1 from digit {@code i}, with borrow up to digit {@code last}.
	 */
	private void decrement( int i, final int last )
	{
		while ( i < last && digit[ i ] == 0 )
		{
			final long max = dim[ i ] - 1;
			s.move( max, axis[ i ] );
			digit[ i ] = max;
			++i;
		}
		s.bck( axis[ i ] );
		--digit[ i ];
	}

	/**
	 * Recompute the source position from {@link #position} and set it.
	 */
	private void updateAll()
	{
		Arrays.fill( flat, 0 );
		for ( int d = 0; d < n; ++d )
		{
			final int g = group[ d ];
			if ( g >= 0 )
				flat[ g ] += stride[ d ] * position[ d ];
		}
		for ( int g = 0; g < flat.length; ++g )
		{
			final int last = start[ g + 1 ] - 1;
			long f = flat[ g ];
			for ( int i = start[ g ]; i < last; ++i )
			{
				final long q = floorDiv( f, dim[ i ] );
				final long r = f - q * dim[ i ];
				digit[ i ] = r;
				tmpPosition[ axis[ i ] ] = r;
				f = q;
			}
			digit[ last ] = f;
			tmpPosition[ axis[ last ] ] = f;
		}
		s.setPosition( tmpPosition );
	}

	@Override
	public void fwd( final int d )
	{
		assert d < n;
		++position[ d ];
		final int g = group[ d ];
		if ( g >= 0 )
			moveGroup( g, stride[ d ] );
	}

	@Override
	public void bck( final int d )
	{
		assert d < n;
		--position[ d ];
		final int g = group[ d ];
		if ( g >= 0 )
			moveGroup( g, -stride[ d ] );
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
		final int g = group[ d ];
		if ( g >= 0 && distance != 0 )
			moveGroup( g, distance * stride[ d ] );
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
		final int g = group[ d ];
		if ( g >= 0 && distance != 0 )
			moveGroup( g, distance * stride[ d ] );
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
