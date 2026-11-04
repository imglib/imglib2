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

package net.imglib2.blocks;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.transform.integer.ReshapeTransform;
import net.imglib2.type.PrimitiveType;
import net.imglib2.util.Intervals;

/**
 * Copies blocks from a View chain that contains a {@link ReshapeTransform}:
 * <pre>
 * view --top--&gt; A --reshape--&gt; B --bottom--&gt; root
 * </pre>
 * where {@code top} and {@code bottom} are {@code MixedTransform}s.
 * <p>
 * The requested block is mapped to a box in A (by {@code top}). The box in A
 * is mapped to its bounding box in B (by the {@code reshape}), which is copied
 * from the root (by the {@link RangeCopier}, followed by an axis
 * permutation/inversion if {@code bottom} requires it). The elements of the
 * box in A are then gathered from the bounding box in B, and finally permuted
 * and inverted into the view block (if {@code top} requires it).
 * <p>
 * If the box in A maps exactly to its bounding box in B (for example, a block
 * that is a single run of complete rows), no gathering is needed.
 * <p>
 * The bounding box in B can be much larger than the box in A, for example if a
 * block on a merged axis straddles a row boundary. If the bounding box is
 * larger than {@code wasteFactor} times the box in A (and larger than {@code
 * minSplitSize}), the box in A is split into pieces with tighter bounding
 * boxes, each of which is copied separately.
 */
class ReshapeCopier
{
	static final int DEFAULT_WASTE_FACTOR = 4;

	static final long DEFAULT_MIN_SPLIT_SIZE = 4096;

	static final long DEFAULT_MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

	private final ReshapeTransform reshape;

	private final MixedTransform top;

	private final MixedTransform bottom;

	private final MemCopy memCopy;

	private final int wasteFactor;

	private final long minSplitSize;

	private final long maxBufferSize;

	/**
	 * number of dimensions of A (source of the reshape)
	 */
	private final int nA;

	/**
	 * number of dimensions of B (target of the reshape)
	 */
	private final int nB;

	/**
	 * number of dimensions of the root
	 */
	private final int nRoot;

	/**
	 * For each group of the reshape, its non-singleton dimensions in A.
	 */
	private final int[][] groupA;

	/**
	 * For each group of the reshape, its non-singleton dimensions in B.
	 */
	private final int[][] groupB;

	private final long[] sourceDimensions;

	private final long[] targetDimensions;

	// -- per instance --

	private final RangeCopier copier;

	private final PermuteInvert topPermute;

	private final PermuteInvert bottomPermute;

	private final TempArray< ? > tempRoot;

	private final TempArray< ? > tempB;

	private final TempArray< ? > tempA;

	/**
	 * @param reshape
	 * 		the reshape from A to B
	 * @param top
	 * 		the transform from the view to A, or {@code null} if it is the identity
	 * @param topPermuteInvert
	 * 		the permute/invert part of {@code top}, or {@code null} if it is the identity
	 * @param bottom
	 * 		the transform from B to the root, or {@code null} if it is the identity
	 * @param bottomPermuteInvert
	 * 		the permute/invert part of {@code bottom}, or {@code null} if it is the identity
	 * @param copier
	 * 		copies boxes from the root
	 * @param primitiveType
	 * 		the primitive type of the root
	 */
	ReshapeCopier(
			final ReshapeTransform reshape,
			final MixedTransform top,
			final MixedTransform topPermuteInvert,
			final MixedTransform bottom,
			final MixedTransform bottomPermuteInvert,
			final RangeCopier copier,
			final PrimitiveType primitiveType,
			final int wasteFactor,
			final long minSplitSize,
			final long maxBufferSize )
	{
		this.reshape = reshape;
		this.top = top;
		this.bottom = bottom;
		this.memCopy = MemCopy.forPrimitiveType( primitiveType );
		this.wasteFactor = wasteFactor;
		this.minSplitSize = minSplitSize;
		this.maxBufferSize = maxBufferSize;
		nA = reshape.numSourceDimensions();
		nB = reshape.numTargetDimensions();
		nRoot = bottom == null ? nB : bottom.numTargetDimensions();
		final int numGroups = reshape.numGroups();
		groupA = new int[ numGroups ][];
		groupB = new int[ numGroups ][];
		for ( int g = 0; g < numGroups; ++g )
		{
			groupA[ g ] = reshape.getGroupSourceAxes( g );
			groupB[ g ] = reshape.getGroupTargetAxes( g );
		}
		sourceDimensions = reshape.getSourceDimensions();
		targetDimensions = reshape.getTargetDimensions();

		this.copier = copier;
		topPermute = topPermuteInvert == null ? null : new PermuteInvert( memCopy, topPermuteInvert );
		bottomPermute = bottomPermuteInvert == null ? null : new PermuteInvert( memCopy, bottomPermuteInvert );
		tempRoot = TempArray.forPrimitiveType( primitiveType );
		tempB = TempArray.forPrimitiveType( primitiveType );
		tempA = TempArray.forPrimitiveType( primitiveType );
	}

	// creates an independent copy of {@code c}
	private ReshapeCopier( final ReshapeCopier c )
	{
		reshape = c.reshape;
		top = c.top;
		bottom = c.bottom;
		memCopy = c.memCopy;
		wasteFactor = c.wasteFactor;
		minSplitSize = c.minSplitSize;
		maxBufferSize = c.maxBufferSize;
		nA = c.nA;
		nB = c.nB;
		nRoot = c.nRoot;
		groupA = c.groupA;
		groupB = c.groupB;
		sourceDimensions = c.sourceDimensions;
		targetDimensions = c.targetDimensions;

		copier = c.copier.newInstance();
		topPermute = c.topPermute == null ? null : c.topPermute.newInstance();
		bottomPermute = c.bottomPermute == null ? null : c.bottomPermute.newInstance();
		tempRoot = c.tempRoot.newInstance();
		tempB = c.tempB.newInstance();
		tempA = c.tempA.newInstance();
	}

	ReshapeCopier newInstance()
	{
		return new ReshapeCopier( this );
	}

	/**
	 * Copy the view block at {@code viewPos} of size {@code viewSize} into
	 * {@code dest}, a primitive array of the root type.
	 */
	void copy( final long[] viewPos, final int[] viewSize, final Object dest )
	{
		final int length = ( int ) Intervals.numElements( viewSize );
		final long[] aMin = new long[ nA ];
		final int[] aSize = new int[ nA ];
		if ( top == null )
		{
			System.arraycopy( viewPos, 0, aMin, 0, nA );
			System.arraycopy( viewSize, 0, aSize, 0, nA );
		}
		else
			PrimitiveBlocksUtils.transformInterval( top, viewPos, viewSize, aMin, aSize );

		final Object aDest = topPermute == null ? dest : tempA.get( length );
		process( aMin, aSize, aMin, aSize, aDest, true );
		if ( topPermute != null )
			topPermute.permuteAndInvert( aDest, dest, viewSize );
	}

	/**
	 * Copy the piece {@code [pMin, pMin + pSize)} of the box {@code [aMin,
	 * aMin + aSize)} in A into the corresponding elements of {@code aDest}, a
	 * dense array of the box in A.
	 */
	private void process( final long[] pMin, final int[] pSize, final long[] aMin, final int[] aSize, final Object aDest, final boolean whole )
	{
		final long[] pMax = new long[ nA ];
		for ( int d = 0; d < nA; ++d )
			pMax[ d ] = pMin[ d ] + pSize[ d ] - 1;
		final long[] bMin = new long[ nB ];
		final long[] bMax = new long[ nB ];
		reshape.boundingBox( pMin, pMax, bMin, bMax );

		final long bCount = numElements( bMin, bMax );
		final long pCount = Intervals.numElements( pSize );
		if ( bCount > maxBufferSize || ( bCount > minSplitSize && bCount > wasteFactor * pCount ) )
		{
			if ( split( pMin, pSize, bMin, bMax, aMin, aSize, aDest ) )
				return;
			if ( bCount > maxBufferSize && halve( pMin, pSize, aMin, aSize, aDest ) )
				return;
		}

		final int[] bSize = new int[ nB ];
		for ( int e = 0; e < nB; ++e )
			bSize[ e ] = ( int ) ( bMax[ e ] - bMin[ e ] + 1 );

		// If the piece maps exactly to its bounding box, then the dense
		// arrays of the box in A and of the box in B are the same.
		final boolean direct = whole && bCount == pCount && inDomain( pMin, pMax );
		final Object bDest = direct ? aDest : tempB.get( ( int ) bCount );
		fetch( bMin, bSize, bDest, ( int ) bCount );
		if ( !direct )
			gather( bDest, bMin, bSize, pMin, pSize, aDest, aMin, aSize );
	}

	private boolean inDomain( final long[] min, final long[] max )
	{
		for ( int d = 0; d < nA; ++d )
			if ( min[ d ] < 0 || max[ d ] >= sourceDimensions[ d ] )
				return false;
		return true;
	}

	private static long numElements( final long[] min, final long[] max )
	{
		long n = 1;
		for ( int d = 0; d < min.length; ++d )
			n *= max[ d ] - min[ d ] + 1;
		return n;
	}

	/**
	 * Copy the box {@code [bMin, bMin + bSize)} in B into {@code dest}, a dense
	 * array of the box.
	 */
	private void fetch( final long[] bMin, final int[] bSize, final Object dest, final int count )
	{
		final long[] rootMin;
		final int[] rootSize;
		if ( bottom == null )
		{
			rootMin = bMin;
			rootSize = bSize;
		}
		else
		{
			rootMin = new long[ nRoot ];
			rootSize = new int[ nRoot ];
			PrimitiveBlocksUtils.transformInterval( bottom, bMin, bSize, rootMin, rootSize );
		}

		if ( bottomPermute == null )
			copier.copy( rootMin, dest, rootSize );
		else
		{
			final Object rootDest = tempRoot.get( count );
			copier.copy( rootMin, rootDest, rootSize );
			bottomPermute.permuteAndInvert( rootDest, dest, bSize );
		}
	}

	/**
	 * Split the piece {@code [pMin, pMin + pSize)} into smaller pieces, and
	 * {@link #process} them. The piece is split in the dimensions of the
	 * group of the reshape whose bounding box is the most wasteful:
	 * <ul>
	 * <li>If the piece is a contiguous flat run within that group, it is cut
	 * at the boundaries of the highest target dimension that differs between
	 * start and end of the run. This yields up to three pieces, the middle of
	 * which maps exactly to its bounding box.</li>
	 * <li>Otherwise, the outermost dimension of the group is split in half.</li>
	 * </ul>
	 *
	 * @return {@code false} if the piece cannot be split.
	 */
	private boolean split( final long[] pMin, final int[] pSize, final long[] bMin, final long[] bMax, final long[] aMin, final int[] aSize, final Object aDest )
	{
		int best = -1;
		double bestWaste = 1;
		for ( int g = 0; g < groupA.length; ++g )
		{
			long pc = 1;
			for ( final int d : groupA[ g ] )
				pc *= pSize[ d ];
			long bc = 1;
			for ( final int e : groupB[ g ] )
				bc *= bMax[ e ] - bMin[ e ] + 1;
			final double waste = ( double ) bc / pc;
			if ( waste > bestWaste )
			{
				bestWaste = waste;
				best = g;
			}
		}
		if ( best < 0 )
			return false;

		final int[] axes = groupA[ best ];
		int outer = -1;
		int numExtended = 0;
		for ( int k = 0; k < axes.length; ++k )
		{
			if ( pSize[ axes[ k ] ] > 1 )
			{
				outer = k;
				++numExtended;
			}
		}
		if ( outer < 0 )
			return false;
		final int d = axes[ outer ];

		if ( numExtended == 1 && outer == 0 )
		{
			// The piece is a contiguous run of flat indices within the group
			// (dimension d has stride 1).
			long fmin = 0;
			for ( final int a : axes )
				fmin += reshape.getSourceStride( a ) * pMin[ a ];
			final long fmax = fmin + pSize[ d ] - 1;
			final long stride = highestDifferingStride( best, fmin, fmax );
			if ( stride > 1 )
			{
				final long m1 = -Math.floorDiv( -fmin, stride ) * stride;
				final long m2 = Math.floorDiv( fmax + 1, stride ) * stride;
				final List< Long > cuts = new ArrayList<>();
				cuts.add( fmin );
				if ( m1 > fmin && m1 <= fmax )
					cuts.add( m1 );
				if ( m2 > m1 && m2 > fmin && m2 <= fmax )
					cuts.add( m2 );
				cuts.add( fmax + 1 );
				if ( cuts.size() > 2 )
				{
					for ( int i = 0; i < cuts.size() - 1; ++i )
					{
						final long[] min = pMin.clone();
						final int[] size = pSize.clone();
						min[ d ] = pMin[ d ] + ( cuts.get( i ) - fmin );
						size[ d ] = ( int ) ( cuts.get( i + 1 ) - cuts.get( i ) );
						process( min, size, aMin, aSize, aDest, false );
					}
					return true;
				}
			}
		}

		halve( d, pMin, pSize, aMin, aSize, aDest );
		return true;
	}

	/**
	 * Split the piece {@code [pMin, pMin + pSize)} in half along its
	 * outermost dimension of size &gt; 1, and {@link #process} the halves.
	 *
	 * @return {@code false} if the piece is a single element.
	 */
	private boolean halve( final long[] pMin, final int[] pSize, final long[] aMin, final int[] aSize, final Object aDest )
	{
		for ( int d = nA - 1; d >= 0; --d )
		{
			if ( pSize[ d ] > 1 )
			{
				halve( d, pMin, pSize, aMin, aSize, aDest );
				return true;
			}
		}
		return false;
	}

	/**
	 * Split the piece {@code [pMin, pMin + pSize)} in half along dimension
	 * {@code d}, and {@link #process} the halves.
	 */
	private void halve( final int d, final long[] pMin, final int[] pSize, final long[] aMin, final int[] aSize, final Object aDest )
	{
		final int half = pSize[ d ] / 2;
		final long[] min = pMin.clone();
		final int[] size = pSize.clone();
		size[ d ] = half;
		process( min, size, aMin, aSize, aDest, false );
		min[ d ] += half;
		size[ d ] = pSize[ d ] - half;
		process( min, size, aMin, aSize, aDest, false );
	}

	/**
	 * Unflatten {@code fmin} and {@code fmax} into the target dimensions of
	 * group {@code g}, and return the stride of the highest target dimension
	 * in which they differ (or 1, if they differ at most in the innermost
	 * target dimension).
	 */
	private long highestDifferingStride( final int g, long fmin, long fmax )
	{
		final int[] tAxes = groupB[ g ];
		final int last = tAxes.length - 1;
		int highest = 0;
		for ( int i = 0; i < last; ++i )
		{
			final long dim = targetDimensions[ tAxes[ i ] ];
			final long qmin = Math.floorDiv( fmin, dim );
			final long qmax = Math.floorDiv( fmax, dim );
			if ( fmin - qmin * dim != fmax - qmax * dim )
				highest = i;
			fmin = qmin;
			fmax = qmax;
		}
		if ( fmin != fmax )
			highest = last;
		return reshape.getTargetStride( tAxes[ highest ] );
	}

	/**
	 * Copy the elements of the piece {@code [pMin, pMin + pSize)} in A from
	 * {@code src}, a dense array of the box {@code [bMin, bMin + bSize)} in B,
	 * to the corresponding elements of {@code dest}, a dense array of the box
	 * {@code [aMin, aMin + aSize)} in A.
	 * <p>
	 * The groups of the reshape are independent: the offset in {@code src} is
	 * a sum of per-group offsets, as is the offset in {@code dest}. The
	 * innermost group (with extent &gt; 1 in the piece) is decomposed into runs
	 * that are contiguous in {@code src}. The offsets of the outer groups are
	 * tabulated, and all combinations are copied run by run.
	 */
	private void gather( final Object src, final long[] bMin, final int[] bSize, final long[] pMin, final int[] pSize, final Object dest, final long[] aMin, final int[] aSize )
	{
		final int[] bStride = new int[ nB ];
		for ( int e = 0, s = 1; e < nB; ++e )
		{
			bStride[ e ] = s;
			s *= bSize[ e ];
		}
		final int[] aStride = new int[ nA ];
		for ( int d = 0, s = 1; d < nA; ++d )
		{
			aStride[ d ] = s;
			s *= aSize[ d ];
		}
		int outBase = 0;
		for ( int d = 0; d < nA; ++d )
			outBase += ( int ) ( pMin[ d ] - aMin[ d ] ) * aStride[ d ];

		// The units to enumerate, ordered by their first dimension in A: the
		// groups of the reshape that have extent > 1 in the piece (encoded as
		// g), and dimensions of size 1 in the reshape that have extent > 1
		// (encoded as -1-d). The latter don't affect the position in B, and
		// only occur outside the reshape's domain. Groups with extent 1 map to
		// a single position in B, where their offset is 0.
		final int[] units = new int[ groupA.length + nA ];
		int numUnits = 0;
		for ( int d = 0; d < nA; ++d )
		{
			final int g = reshape.getSourceGroup( d );
			if ( g < 0 )
			{
				if ( pSize[ d ] > 1 )
					units[ numUnits++ ] = -1 - d;
			}
			else if ( groupA[ g ][ 0 ] == d && count( g, pSize ) > 1 )
				units[ numUnits++ ] = g;
		}

		if ( numUnits == 0 )
		{
			memCopy.copyForward( src, 0, dest, outBase, 1 );
			return;
		}

		// runs of the innermost unit
		final Runs runs;
		final int runStride;
		final int inner = units[ 0 ];
		if ( inner >= 0 && bStride[ groupB[ inner ][ 0 ] ] == 1 )
		{
			runs = runs( inner, pMin, pSize, bMin, bStride, aStride );
			runStride = aStride[ groupA[ inner ][ 0 ] ];
		}
		else
		{
			final int[][] table = tabulate( inner, pMin, pSize, bMin, bStride, aStride );
			runs = new Runs( table[ 0 ].length );
			for ( int j = 0; j < table[ 0 ].length; ++j )
				runs.add( table[ 0 ][ j ], table[ 1 ][ j ], 1 );
			runStride = 1;
		}

		// offsets of the outer units
		final int numOuter = numUnits - 1;
		final int[][] outerIn = new int[ numOuter ][];
		final int[][] outerOut = new int[ numOuter ][];
		for ( int i = 0; i < numOuter; ++i )
		{
			final int[][] table = tabulate( units[ i + 1 ], pMin, pSize, bMin, bStride, aStride );
			outerIn[ i ] = table[ 0 ];
			outerOut[ i ] = table[ 1 ];
		}

		// iterate all combinations of the outer units
		final int[] index = new int[ numOuter ];
		while ( true )
		{
			int in = 0;
			int out = outBase;
			for ( int i = 0; i < numOuter; ++i )
			{
				in += outerIn[ i ][ index[ i ] ];
				out += outerOut[ i ][ index[ i ] ];
			}
			for ( int r = 0; r < runs.size; ++r )
			{
				if ( runStride == 1 )
					memCopy.copyForward( src, in + runs.in[ r ], dest, out + runs.out[ r ], runs.length[ r ] );
				else
					memCopy.copyStrided( src, in + runs.in[ r ], dest, out + runs.out[ r ], runStride, runs.length[ r ] );
			}

			int i = 0;
			for ( ; i < numOuter; ++i )
			{
				if ( ++index[ i ] < outerIn[ i ].length )
					break;
				index[ i ] = 0;
			}
			if ( i == numOuter )
				break;
		}
	}

	/**
	 * @return the number of elements of the piece of size {@code pSize} in
	 *         the dimensions of group {@code g}.
	 */
	private int count( final int g, final int[] pSize )
	{
		int count = 1;
		for ( final int d : groupA[ g ] )
			count *= pSize[ d ];
		return count;
	}

	/**
	 * Runs of elements that are contiguous in the source.
	 */
	private static class Runs
	{
		final int[] in;

		final int[] out;

		final int[] length;

		int size;

		Runs( final int capacity )
		{
			in = new int[ capacity ];
			out = new int[ capacity ];
			length = new int[ capacity ];
		}

		void add( final int in, final int out, final int length )
		{
			this.in[ size ] = in;
			this.out[ size ] = out;
			this.length[ size ] = length;
			++size;
		}
	}

	/**
	 * Decompose the piece {@code [pMin, pMin + pSize)} in the dimensions of
	 * group {@code g} into runs that are contiguous in the dense array of the
	 * box in B. A run lies along the first dimension of the group in A (whose
	 * stride in the flat index of the group is 1), and ends where the first
	 * dimension of the group in B wraps around.
	 *
	 * @return the runs, with offsets into the dense array of the box in B
	 *         ({@code in}), and into the dense array of the box in A
	 *         ({@code out}, relative to {@code pMin}). Consecutive elements of
	 *         a run are {@code aStride[ groupA[ g ][ 0 ] ]} apart in A.
	 */
	private Runs runs( final int g, final long[] pMin, final int[] pSize, final long[] bMin, final int[] bStride, final int[] aStride )
	{
		final int[] axes = groupA[ g ];
		final int[] tAxes = groupB[ g ];
		final int lineLength = pSize[ axes[ 0 ] ];
		final int numLines = count( g, pSize ) / lineLength;
		final long dim0 = targetDimensions[ tAxes[ 0 ] ];
		final int maxRunsPerLine = tAxes.length == 1 ? 1 : ( int ) Math.min( lineLength, lineLength / dim0 + 2 );
		final Runs runs = new Runs( numLines * maxRunsPerLine );

		final long[] digit = new long[ tAxes.length ];
		final int[] local = new int[ axes.length ];
		final int outStride0 = aStride[ axes[ 0 ] ];
		for ( int line = 0; line < numLines; ++line )
		{
			final int lineOut = startLine( g, pMin, local, aStride, digit );
			int pos = 0;
			while ( pos < lineLength )
			{
				final int length = tAxes.length == 1
						? lineLength - pos
						: ( int ) Math.min( lineLength - pos, dim0 - digit[ 0 ] );
				runs.add( offset( g, digit, bMin, bStride ), lineOut + pos * outStride0, length );
				pos += length;
				digit[ 0 ] += length;
				if ( tAxes.length > 1 && digit[ 0 ] == dim0 )
				{
					digit[ 0 ] = 0;
					carry( g, digit, 1 );
				}
			}
			nextLine( axes, pSize, local );
		}
		return runs;
	}

	/**
	 * Tabulate the offsets of all elements of the piece {@code [pMin, pMin +
	 * pSize)} in the dimensions of unit {@code u} (see {@link #gather}).
	 *
	 * @return {@code {in, out}}, the offsets into the dense array of the box in
	 *         B, and into the dense array of the box in A (relative to {@code
	 *         pMin}).
	 */
	private int[][] tabulate( final int u, final long[] pMin, final int[] pSize, final long[] bMin, final int[] bStride, final int[] aStride )
	{
		if ( u < 0 )
		{
			final int d = -1 - u;
			final int[] in = new int[ pSize[ d ] ];
			final int[] out = new int[ pSize[ d ] ];
			for ( int j = 0; j < pSize[ d ]; ++j )
				out[ j ] = j * aStride[ d ];
			return new int[][] { in, out };
		}

		final int g = u;
		final int[] axes = groupA[ g ];
		final int[] tAxes = groupB[ g ];
		final int lineLength = pSize[ axes[ 0 ] ];
		final int count = count( g, pSize );
		final int[] in = new int[ count ];
		final int[] out = new int[ count ];
		final long[] digit = new long[ tAxes.length ];
		final int[] local = new int[ axes.length ];
		final int outStride0 = aStride[ axes[ 0 ] ];
		for ( int j = 0; j < count; )
		{
			final int lineOut = startLine( g, pMin, local, aStride, digit );
			for ( int pos = 0; pos < lineLength; ++pos, ++j )
			{
				in[ j ] = offset( g, digit, bMin, bStride );
				out[ j ] = lineOut + pos * outStride0;
				carry( g, digit, 0 );
			}
			nextLine( axes, pSize, local );
		}
		return new int[][] { in, out };
	}

	/**
	 * Set {@code digit} to the position in B of the start of the line at
	 * {@code local} (with {@code local[0] == 0}) in the dimensions of group
	 * {@code g} in A.
	 *
	 * @return the offset of the start of the line in the dense array of the
	 *         box in A (relative to {@code pMin}).
	 */
	private int startLine( final int g, final long[] pMin, final int[] local, final int[] aStride, final long[] digit )
	{
		final int[] axes = groupA[ g ];
		final int[] tAxes = groupB[ g ];
		long f = 0;
		int out = 0;
		for ( int k = 0; k < axes.length; ++k )
		{
			f += reshape.getSourceStride( axes[ k ] ) * ( pMin[ axes[ k ] ] + local[ k ] );
			out += local[ k ] * aStride[ axes[ k ] ];
		}
		final int last = tAxes.length - 1;
		for ( int i = 0; i < last; ++i )
		{
			final long dim = targetDimensions[ tAxes[ i ] ];
			final long q = Math.floorDiv( f, dim );
			digit[ i ] = f - q * dim;
			f = q;
		}
		digit[ last ] = f;
		return out;
	}

	/**
	 * Advance {@code local}, the position in the dimensions {@code axes[1..]}
	 * of a group, to the next line.
	 */
	private static void nextLine( final int[] axes, final int[] pSize, final int[] local )
	{
		for ( int k = 1; k < axes.length; ++k )
		{
			if ( ++local[ k ] < pSize[ axes[ k ] ] )
				break;
			local[ k ] = 0;
		}
	}

	/**
	 * Add 1 to digit {@code i} of the position {@code digit} in B of group
	 * {@code g}, with carry.
	 */
	private void carry( final int g, final long[] digit, int i )
	{
		final int[] tAxes = groupB[ g ];
		final int last = tAxes.length - 1;
		while ( i < last && digit[ i ] == targetDimensions[ tAxes[ i ] ] - 1 )
		{
			digit[ i ] = 0;
			++i;
		}
		++digit[ i ];
	}

	/**
	 * @return the offset of the position {@code digit} in B of group {@code g}
	 *         in the dense array of the box in B.
	 */
	private int offset( final int g, final long[] digit, final long[] bMin, final int[] bStride )
	{
		final int[] tAxes = groupB[ g ];
		int in = 0;
		for ( int i = 0; i < tAxes.length; ++i )
			in += ( int ) ( digit[ i ] - bMin[ tAxes[ i ] ] ) * bStride[ tAxes[ i ] ];
		return in;
	}
}
