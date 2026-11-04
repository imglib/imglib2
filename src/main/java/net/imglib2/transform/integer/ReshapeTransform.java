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

package net.imglib2.transform.integer;

import java.util.Arrays;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.transform.InvertibleTransform;

/**
 * A reshape: maps positions in an n-dimensional source (the view) to positions
 * in an m-dimensional target (the underlying image) with the same number of
 * elements, such that both are traversed in the same flat order (dimension 0
 * fastest). This is the transform used by
 * {@link net.imglib2.view.Views#reshape}.
 * <p>
 * Both source and target are zero-min. The two shapes are cut into independent
 * <em>groups</em> wherever their running products (ignoring size-1 axes)
 * coincide. Within a group, the source position is flattened to an index
 * {@code f} and {@code f} is unflattened into the target axes of the group. A
 * group with a single target axis (a <em>split</em>, or a copy) is linear and
 * needs no division.
 * <p>
 * Size-1 axes are not part of any group: a size-1 source axis does not
 * contribute to the target position, and a size-1 target axis is always 0.
 * <p>
 * Positions outside the source interval are mapped using floor division, so
 * that the map is consistent with incremental moves: the outermost target axis
 * of each group is unbounded, all other target axes of the group stay inside
 * their dimensions.
 * <p>
 * Unlike {@link Mixed} transforms, a reshape depends on the dimensions of the
 * source and target, and it can only be concatenated with a reshape whose
 * target dimensions match its source dimensions (see
 * {@link #concatenate(ReshapeTransform)}).
 */
public class ReshapeTransform implements InvertibleTransform, BoundingBoxTransform
{
	private final long[] sourceDimensions;

	private final long[] targetDimensions;

	/**
	 * For each source dimension, the group it belongs to, or -1 if it has size 1.
	 */
	private final int[] sourceGroup;

	/**
	 * For each source dimension, its stride in the flat index of its group (0
	 * for size-1 dimensions).
	 */
	private final long[] sourceStride;

	/**
	 * For each target dimension, the group it belongs to, or -1 if it has size 1.
	 */
	private final int[] targetGroup;

	/**
	 * For each target dimension, its stride in the flat index of its group (0
	 * for size-1 dimensions).
	 */
	private final long[] targetStride;

	/**
	 * For each group, its non-singleton source dimensions, in increasing order.
	 */
	private final int[][] groupSourceAxes;

	/**
	 * For each group, its non-singleton target dimensions, in increasing order.
	 */
	private final int[][] groupTargetAxes;

	/**
	 * The target dimensions of size 1.
	 */
	private final int[] zeroAxes;

	private ReshapeTransform inverse;

	/**
	 * Create the reshape that maps positions in a zero-min source of the given
	 * {@code sourceDimensions} to positions in a zero-min target of the given
	 * {@code targetDimensions}.
	 *
	 * @throws IllegalArgumentException
	 *             if any dimension is not positive, or if source and target do
	 *             not have the same number of elements.
	 */
	public ReshapeTransform( final long[] sourceDimensions, final long[] targetDimensions )
	{
		this( sourceDimensions, targetDimensions, null );
	}

	private ReshapeTransform( final long[] sourceDimensions, final long[] targetDimensions, final ReshapeTransform inverse )
	{
		checkDimensions( sourceDimensions, targetDimensions );
		this.sourceDimensions = sourceDimensions.clone();
		this.targetDimensions = targetDimensions.clone();
		this.inverse = inverse;

		final int n = sourceDimensions.length;
		final int m = targetDimensions.length;
		sourceGroup = new int[ n ];
		sourceStride = new long[ n ];
		targetGroup = new int[ m ];
		targetStride = new long[ m ];
		Arrays.fill( sourceGroup, -1 );
		Arrays.fill( targetGroup, -1 );

		final int[] sourceAxes = nonSingletonAxes( sourceDimensions );
		final int[] targetAxes = nonSingletonAxes( targetDimensions );
		zeroAxes = singletonAxes( targetDimensions );

		// Grow a group from each side, extending whichever has the smaller
		// product, until the products meet. Every size is at least 2, so the
		// products strictly increase, and since the totals are equal both
		// sides run out together.
		final int[] sourceEnd = new int[ sourceAxes.length ];
		final int[] targetEnd = new int[ sourceAxes.length ];
		int v = 0, t = 0, g = 0;
		while ( v < sourceAxes.length )
		{
			long pv = 1, pt = 1;
			sourceStride[ sourceAxes[ v ] ] = pv;
			sourceGroup[ sourceAxes[ v ] ] = g;
			pv *= sourceDimensions[ sourceAxes[ v++ ] ];
			targetStride[ targetAxes[ t ] ] = pt;
			targetGroup[ targetAxes[ t ] ] = g;
			pt *= targetDimensions[ targetAxes[ t++ ] ];
			while ( pv != pt )
			{
				if ( pv < pt )
				{
					sourceStride[ sourceAxes[ v ] ] = pv;
					sourceGroup[ sourceAxes[ v ] ] = g;
					pv *= sourceDimensions[ sourceAxes[ v++ ] ];
				}
				else
				{
					targetStride[ targetAxes[ t ] ] = pt;
					targetGroup[ targetAxes[ t ] ] = g;
					pt *= targetDimensions[ targetAxes[ t++ ] ];
				}
			}
			sourceEnd[ g ] = v;
			targetEnd[ g ] = t;
			++g;
		}

		groupSourceAxes = new int[ g ][];
		groupTargetAxes = new int[ g ][];
		for ( int i = 0, vb = 0, tb = 0; i < g; ++i )
		{
			groupSourceAxes[ i ] = Arrays.copyOfRange( sourceAxes, vb, sourceEnd[ i ] );
			groupTargetAxes[ i ] = Arrays.copyOfRange( targetAxes, tb, targetEnd[ i ] );
			vb = sourceEnd[ i ];
			tb = targetEnd[ i ];
		}
	}

	private static void checkDimensions( final long[] sourceDimensions, final long[] targetDimensions )
	{
		checkPositive( sourceDimensions, "source" );
		checkPositive( targetDimensions, "target" );
		long ns = 1, nt = 1;
		for ( final long d : sourceDimensions )
			ns *= d;
		for ( final long d : targetDimensions )
			nt *= d;
		if ( ns != nt )
			throw new IllegalArgumentException( "cannot reshape "
					+ Arrays.toString( sourceDimensions ) + " (" + ns + " elements) to "
					+ Arrays.toString( targetDimensions ) + " (" + nt + " elements)" );
	}

	private static void checkPositive( final long[] dimensions, final String which )
	{
		for ( int d = 0; d < dimensions.length; ++d )
			if ( dimensions[ d ] <= 0 )
				throw new IllegalArgumentException( "reshape dimensions must be positive, but "
						+ which + " dimension " + d + " is " + dimensions[ d ] );
	}

	private static int[] nonSingletonAxes( final long[] dimensions )
	{
		int count = 0;
		for ( final long d : dimensions )
			if ( d != 1 )
				++count;
		final int[] axes = new int[ count ];
		for ( int d = 0, i = 0; d < dimensions.length; ++d )
			if ( dimensions[ d ] != 1 )
				axes[ i++ ] = d;
		return axes;
	}

	private static int[] singletonAxes( final long[] dimensions )
	{
		int count = 0;
		for ( final long d : dimensions )
			if ( d == 1 )
				++count;
		final int[] axes = new int[ count ];
		for ( int d = 0, i = 0; d < dimensions.length; ++d )
			if ( dimensions[ d ] == 1 )
				axes[ i++ ] = d;
		return axes;
	}

	@Override
	public int numSourceDimensions()
	{
		return sourceDimensions.length;
	}

	@Override
	public int numTargetDimensions()
	{
		return targetDimensions.length;
	}

	/**
	 * @return a copy of the dimensions of the (zero-min) source.
	 */
	public long[] getSourceDimensions()
	{
		return sourceDimensions.clone();
	}

	/**
	 * @return a copy of the dimensions of the (zero-min) target.
	 */
	public long[] getTargetDimensions()
	{
		return targetDimensions.clone();
	}

	public long getSourceDimension( final int d )
	{
		return sourceDimensions[ d ];
	}

	public long getTargetDimension( final int d )
	{
		return targetDimensions[ d ];
	}

	/**
	 * @return the number of independent groups the reshape consists of.
	 */
	public int numGroups()
	{
		return groupSourceAxes.length;
	}

	/**
	 * @return the group source dimension {@code d} belongs to, or -1 if it has
	 *         size 1 (and does not affect the target position).
	 */
	public int getSourceGroup( final int d )
	{
		return sourceGroup[ d ];
	}

	/**
	 * @return the stride of source dimension {@code d} in the flat index of its
	 *         group, or 0 if it has size 1.
	 */
	public long getSourceStride( final int d )
	{
		return sourceStride[ d ];
	}

	/**
	 * @return the group target dimension {@code d} belongs to, or -1 if it has
	 *         size 1 (and is always 0).
	 */
	public int getTargetGroup( final int d )
	{
		return targetGroup[ d ];
	}

	/**
	 * @return the stride of target dimension {@code d} in the flat index of its
	 *         group, or 0 if it has size 1.
	 */
	public long getTargetStride( final int d )
	{
		return targetStride[ d ];
	}

	/**
	 * @return a copy of the non-singleton source dimensions of group
	 *         {@code g}, in increasing order.
	 */
	public int[] getGroupSourceAxes( final int g )
	{
		return groupSourceAxes[ g ].clone();
	}

	/**
	 * @return a copy of the non-singleton target dimensions of group
	 *         {@code g}, in increasing order.
	 */
	public int[] getGroupTargetAxes( final int g )
	{
		return groupTargetAxes[ g ].clone();
	}

	/**
	 * @return whether source and target dimensions are identical.
	 */
	public boolean isIdentity()
	{
		return Arrays.equals( sourceDimensions, targetDimensions );
	}

	/**
	 * @return whether the reshape only inserts or removes size-1 dimensions,
	 *         that is, every group maps one source dimension to one target
	 *         dimension. Such a reshape can be expressed as a
	 *         {@link #toMixed() MixedTransform}.
	 */
	public boolean isSingletonOnly()
	{
		for ( int g = 0; g < groupSourceAxes.length; ++g )
			if ( groupSourceAxes[ g ].length != 1 || groupTargetAxes[ g ].length != 1 )
				return false;
		return true;
	}

	/**
	 * Express a {@link #isSingletonOnly() singleton-only} reshape as a
	 * {@link MixedTransform}: the k-th non-singleton source dimension maps to
	 * the k-th non-singleton target dimension, target dimensions of size 1 are
	 * set to 0, and source dimensions of size 1 are discarded. The two
	 * transforms agree everywhere, also outside the source interval.
	 *
	 * @throws IllegalStateException
	 *             if the reshape is not singleton-only.
	 */
	public MixedTransform toMixed()
	{
		if ( !isSingletonOnly() )
			throw new IllegalStateException( "reshape " + this + " is not singleton-only" );
		final int m = targetDimensions.length;
		final int[] component = new int[ m ];
		final boolean[] zero = new boolean[ m ];
		for ( final int z : zeroAxes )
			zero[ z ] = true;
		for ( int g = 0; g < groupSourceAxes.length; ++g )
			component[ groupTargetAxes[ g ][ 0 ] ] = groupSourceAxes[ g ][ 0 ];
		final MixedTransform t = new MixedTransform( sourceDimensions.length, m );
		t.setComponentMapping( component );
		t.setComponentZero( zero );
		return t;
	}

	/**
	 * Concatenate this reshape with {@code t}, such that {@code t} is applied
	 * first. This is only possible if {@code t}'s target dimensions are
	 * {@code this}'s source dimensions. The result is the reshape from
	 * {@code t}'s source dimensions to {@code this}'s target dimensions.
	 *
	 * @throws IllegalArgumentException
	 *             if the dimensions don't match.
	 */
	public ReshapeTransform concatenate( final ReshapeTransform t )
	{
		if ( !isConcatenable( t ) )
			throw new IllegalArgumentException( "cannot concatenate " + this + " with " + t
					+ ": source dimensions " + Arrays.toString( sourceDimensions )
					+ " do not match target dimensions " + Arrays.toString( t.targetDimensions ) );
		return new ReshapeTransform( t.sourceDimensions, targetDimensions );
	}

	/**
	 * @return whether {@link #concatenate(ReshapeTransform) concatenate(t)} is
	 *         possible, that is whether {@code t}'s target dimensions are
	 *         {@code this}'s source dimensions.
	 */
	public boolean isConcatenable( final ReshapeTransform t )
	{
		return Arrays.equals( sourceDimensions, t.targetDimensions );
	}

	@Override
	public void apply( final long[] source, final long[] target )
	{
		assert source.length >= sourceDimensions.length;
		assert target.length >= targetDimensions.length;

		for ( final int z : zeroAxes )
			target[ z ] = 0;
		for ( int g = 0; g < groupSourceAxes.length; ++g )
		{
			final int[] vAxes = groupSourceAxes[ g ];
			long f = 0;
			for ( final int d : vAxes )
				f += sourceStride[ d ] * source[ d ];
			final int[] tAxes = groupTargetAxes[ g ];
			final int last = tAxes.length - 1;
			for ( int i = 0; i < last; ++i )
			{
				final int d = tAxes[ i ];
				final long q = Math.floorDiv( f, targetDimensions[ d ] );
				target[ d ] = f - q * targetDimensions[ d ];
				f = q;
			}
			target[ tAxes[ last ] ] = f;
		}
	}

	@Override
	public void apply( final int[] source, final int[] target )
	{
		assert source.length >= sourceDimensions.length;
		assert target.length >= targetDimensions.length;

		for ( final int z : zeroAxes )
			target[ z ] = 0;
		for ( int g = 0; g < groupSourceAxes.length; ++g )
		{
			final int[] vAxes = groupSourceAxes[ g ];
			long f = 0;
			for ( final int d : vAxes )
				f += sourceStride[ d ] * source[ d ];
			final int[] tAxes = groupTargetAxes[ g ];
			final int last = tAxes.length - 1;
			for ( int i = 0; i < last; ++i )
			{
				final int d = tAxes[ i ];
				final long q = Math.floorDiv( f, targetDimensions[ d ] );
				target[ d ] = ( int ) ( f - q * targetDimensions[ d ] );
				f = q;
			}
			target[ tAxes[ last ] ] = ( int ) f;
		}
	}

	@Override
	public void apply( final Localizable source, final Positionable target )
	{
		assert source.numDimensions() >= sourceDimensions.length;
		assert target.numDimensions() >= targetDimensions.length;

		for ( final int z : zeroAxes )
			target.setPosition( 0, z );
		for ( int g = 0; g < groupSourceAxes.length; ++g )
		{
			final int[] vAxes = groupSourceAxes[ g ];
			long f = 0;
			for ( final int d : vAxes )
				f += sourceStride[ d ] * source.getLongPosition( d );
			final int[] tAxes = groupTargetAxes[ g ];
			final int last = tAxes.length - 1;
			for ( int i = 0; i < last; ++i )
			{
				final int d = tAxes[ i ];
				final long q = Math.floorDiv( f, targetDimensions[ d ] );
				target.setPosition( f - q * targetDimensions[ d ], d );
				f = q;
			}
			target.setPosition( f, tAxes[ last ] );
		}
	}

	/**
	 * Apply the inverse reshape. Note that this is only the exact inverse for
	 * positions inside the target interval.
	 */
	@Override
	public void applyInverse( final long[] source, final long[] target )
	{
		inverse().apply( target, source );
	}

	/**
	 * Apply the inverse reshape. Note that this is only the exact inverse for
	 * positions inside the target interval.
	 */
	@Override
	public void applyInverse( final int[] source, final int[] target )
	{
		inverse().apply( target, source );
	}

	/**
	 * Apply the inverse reshape. Note that this is only the exact inverse for
	 * positions inside the target interval.
	 */
	@Override
	public void applyInverse( final Positionable source, final Localizable target )
	{
		inverse().apply( target, source );
	}

	/**
	 * The reshape from target dimensions back to source dimensions. It is the
	 * exact inverse for positions inside the source and target intervals.
	 */
	@Override
	public ReshapeTransform inverse()
	{
		if ( inverse == null )
			inverse = new ReshapeTransform( targetDimensions, sourceDimensions, this );
		return inverse;
	}

	/**
	 * Compute a bounding box of the target positions of all source positions
	 * in the box {@code [min, max]}. The box may extend outside the source
	 * interval.
	 * <p>
	 * Every group is handled independently: the flat indices of the source
	 * box lie in the range between the flat indices of its corners. The
	 * bounding box of that range is exact if the source box is a single flat
	 * run (for instance the full interval, or a box that is full in all but
	 * the outermost non-singleton dimension of every group), and conservative
	 * otherwise.
	 *
	 * @param min
	 *            minimum of the source box
	 * @param max
	 *            maximum of the source box ({@code max[d] >= min[d]})
	 * @param tmin
	 *            is set to the minimum of the target bounding box
	 * @param tmax
	 *            is set to the maximum of the target bounding box
	 */
	public void boundingBox( final long[] min, final long[] max, final long[] tmin, final long[] tmax )
	{
		for ( final int z : zeroAxes )
		{
			tmin[ z ] = 0;
			tmax[ z ] = 0;
		}
		for ( int g = 0; g < groupSourceAxes.length; ++g )
		{
			long fmin = 0, fmax = 0;
			for ( final int d : groupSourceAxes[ g ] )
			{
				fmin += sourceStride[ d ] * min[ d ];
				fmax += sourceStride[ d ] * max[ d ];
			}
			final int[] tAxes = groupTargetAxes[ g ];
			final int last = tAxes.length - 1;
			// unflatten both ends into tmin and tmax
			for ( int i = 0; i < last; ++i )
			{
				final int d = tAxes[ i ];
				final long qmin = Math.floorDiv( fmin, targetDimensions[ d ] );
				final long qmax = Math.floorDiv( fmax, targetDimensions[ d ] );
				tmin[ d ] = fmin - qmin * targetDimensions[ d ];
				tmax[ d ] = fmax - qmax * targetDimensions[ d ];
				fmin = qmin;
				fmax = qmax;
			}
			tmin[ tAxes[ last ] ] = fmin;
			tmax[ tAxes[ last ] ] = fmax;
			// Digits above the highest differing digit are constant. The
			// highest differing digit spans [tmin, tmax]. The run wraps through
			// all digits below it, which therefore span their full extent.
			int h = last;
			while ( h >= 0 && tmin[ tAxes[ h ] ] == tmax[ tAxes[ h ] ] )
				--h;
			for ( int i = 0; i < h; ++i )
			{
				final int d = tAxes[ i ];
				tmin[ d ] = 0;
				tmax[ d ] = targetDimensions[ d ] - 1;
			}
		}
	}

	@Override
	public BoundingBox transform( final BoundingBox boundingBox )
	{
		assert boundingBox.numDimensions() == numSourceDimensions();

		final int n = sourceDimensions.length;
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			// corners may be flipped by axis inversions
			min[ d ] = Math.min( boundingBox.corner1[ d ], boundingBox.corner2[ d ] );
			max[ d ] = Math.max( boundingBox.corner1[ d ], boundingBox.corner2[ d ] );
		}
		final BoundingBox b = new BoundingBox( targetDimensions.length );
		boundingBox( min, max, b.corner1, b.corner2 );
		return b;
	}

	@Override
	public boolean equals( final Object o )
	{
		if ( this == o )
			return true;
		if ( !( o instanceof ReshapeTransform ) )
			return false;
		final ReshapeTransform that = ( ReshapeTransform ) o;
		return Arrays.equals( sourceDimensions, that.sourceDimensions )
				&& Arrays.equals( targetDimensions, that.targetDimensions );
	}

	@Override
	public int hashCode()
	{
		return 31 * Arrays.hashCode( sourceDimensions ) + Arrays.hashCode( targetDimensions );
	}

	/**
	 * For example {@code ReshapeTransform{[300, 300, 8, 5]->[300, 300, 40]:
	 * [300]<->[300] | [300]<->[300] | [8, 5]<->[40]}}.
	 */
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder( "ReshapeTransform{" )
				.append( Arrays.toString( sourceDimensions ) )
				.append( "->" )
				.append( Arrays.toString( targetDimensions ) )
				.append( ":" );
		for ( int g = 0; g < groupSourceAxes.length; ++g )
		{
			sb.append( g == 0 ? " " : " | " );
			sb.append( Arrays.toString( dims( groupSourceAxes[ g ], sourceDimensions ) ) )
					.append( "<->" )
					.append( Arrays.toString( dims( groupTargetAxes[ g ], targetDimensions ) ) );
		}
		return sb.append( "}" ).toString();
	}

	private static long[] dims( final int[] axes, final long[] dimensions )
	{
		final long[] s = new long[ axes.length ];
		for ( int i = 0; i < axes.length; ++i )
			s[ i ] = dimensions[ axes[ i ] ];
		return s;
	}
}
