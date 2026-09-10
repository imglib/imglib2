/*-
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

import static net.imglib2.blocks.Ranges.Direction.CONSTANT;

import java.util.Arrays;
import java.util.List;

import net.imglib2.RandomAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileAccess;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;

/**
 * TODO: revise javadoc
 * <p>
 * Does the actual copying work from an {@code AbstractCellImg} into a primitive
 * array.
 *
 * @param <T> a primitive array type, e.g., {@code byte[]}.
 */
class VolatileCellImgRangeCopier< S, P, T > implements VolatileRangeCopier< T >
{
	private final int n;
	private final CellGrid cellGrid;
	private final RandomAccess< ? extends Cell< ? > > cellAccess;
	private final long[] srcDims;
	private final Ranges findRanges;
	private final MemCopy< S, T > memCopy;
	private final MemCopy< P, T > fillCopy;
	private final P oob;

	private final List< Ranges.Range >[] rangesPerDimension;
	private final Ranges.Range[] ranges;

	private final int[] dsteps;
	private final int[] doffsets;
	private final int[] cdims;
	private final int[] csteps;
	private final int[] lengths;

	/**
	 * Flattened offsets in range space, the range-space analogue of {@code
	 * doffsets}. {@code roffsets[d]} pins the ranges of dimensions {@code d}
	 * and above; the ranges of dimensions below {@code d} span the {@code
	 * rsteps[d]} entries starting there.
	 */
	private final int[] roffsets;

	/**
	 * Grid coordinates of the cells a block touches.
	 * For {@link #allValid(long[], int[], boolean)}.
	 */
	private final int[] gridMin;
	private final int[] gridMax;

	/** {@code RangeValidity.values()} of the copy in progress. */
	private byte[] rvalid;

	/** {@code RangeValidity.rangeSteps()} of the copy in progress. */
	private int[] rsteps;

	public VolatileCellImgRangeCopier(
			final AbstractCellImg< ?, ?, ?, ? > cellImg,
			final Ranges findRanges,
			final MemCopy< S, T > memCopy,
			final MemCopy< P, T > fillCopy,
			final P oob )
	{
		n = cellImg.numDimensions();
		cellGrid = cellImg.getCellGrid();
		cellAccess = cellImg.getCells().randomAccess();
		srcDims = cellImg.dimensionsAsLongArray();

		this.findRanges = findRanges;
		this.memCopy = memCopy;
		this.fillCopy = fillCopy;
		this.oob = oob;

		rangesPerDimension = new List[ n ];
		ranges = new Ranges.Range[ n ];

		dsteps = new int[ n ];
		doffsets = new int[ n + 1 ];
		cdims = new int[ n ];
		csteps = new int[ n ];
		lengths = new int[ n ];
		roffsets = new int[ n + 1 ];
		gridMin = new int[ n ];
		gridMax = new int[ n ];
	}

	// creates an independent copy of {@code other}
	private VolatileCellImgRangeCopier( VolatileCellImgRangeCopier< S, P, T > copier )
	{
		n = copier.n;
		cellGrid = copier.cellGrid;
		cellAccess = copier.cellAccess.copy();
		srcDims = copier.srcDims.clone();
		findRanges = copier.findRanges;
		memCopy = copier.memCopy;
		fillCopy = copier.fillCopy;
		oob = copier.oob;

		rangesPerDimension = new List[ n ];
		ranges = new Ranges.Range[ n ];
		dsteps = new int[ n ];
		doffsets = new int[ n + 1 ];
		cdims = new int[ n ];
		csteps = new int[ n ];
		lengths = new int[ n ];
		roffsets = new int[ n + 1 ];
		gridMin = new int[ n ];
		gridMax = new int[ n ];
	}

	@Override
	public VolatileCellImgRangeCopier< S, P, T > newInstance()
	{
		return new VolatileCellImgRangeCopier<>( this );
	}

	/**
	 * TODO: revise javadoc
	 * <p>
	 * Copy the block starting at {@code srcPos} with the given {@code size}
	 * into the (appropriately sized) {@code dest} array.
	 * <p>
	 * This finds the src range lists for all dimensions and then calls
	 * {@link #copy(Object, byte[], int)} to iterate all range combinations.
	 *
	 * @param srcPos
	 * 		min coordinates of block to copy from src Img.
	 * @param dest
	 * 		destination array. Type is {@code byte[]}, {@code float[]},
	 * 		etc, corresponding to the src Img's native type.
	 * @param size
	 * 		dimensions of block to copy from src Img.
	 */
	@Override
	public RangeValidity copy( final long[] srcPos, final T dest, final int[] size )
	{
		// find ranges
		for ( int d = 0; d < n; ++d )
			rangesPerDimension[ d ] = findRanges.findRanges( srcPos[ d ], size[ d ], srcDims[ d ], cellGrid.cellDimension( d ) );

		// validity in range space, filled in as we copy
		final RangeValidity validity = new RangeValidity( rangesPerDimension );
		rvalid = validity.values();
		rsteps = validity.rangeSteps();

		// copy data
		setupDestSize( size );
		copy( dest, n - 1 );

		return validity;
	}

	/**
	 * Iterates the {@code rangesPerDimension} list for the given dimension
	 * {@code d} and recursively calls itself for iterating dimension {@code
	 * d-1}.
	 *
	 * @param dest
	 * 		destination array. Type is {@code byte[]}, {@code float[]},
	 * 		etc, corresponding to the src Img's native type.
	 * @param d
	 * 		current dimension. This method calls itself recursively with
	 * 		        {@code d-1} until {@code d==0} is reached.
	 */
	private void copy( final T dest, final int d )
	{
		final List< Ranges.Range > rangesd = rangesPerDimension[ d ];
		final int numRanges = rangesd.size();
		for ( int i = 0; i < numRanges; ++i )
		{
			final Ranges.Range range = rangesd.get( i );
			updateRange( range, d, i );
			if ( range.dir == CONSTANT )
				fillRanges( dest, d );
			else if ( d > 0 )
				copy( dest, d - 1 );
			else
				copyRanges( dest );
		}
	}

	private void setupDestSize( final int[] size )
	{
		dsteps[ 0 ] = 1;
		for ( int d = 0; d < n - 1; ++d )
			dsteps[ d + 1 ] = dsteps[ d ] * size[ d ];
	}

	/**
	 * @param r
	 * 		current range
	 * @param d
	 * 		current dimension
	 * @param i
	 * 		index of the current range within {@code rangesPerDimension[d]}
	 */
	private void updateRange( final Ranges.Range r, final int d, final int i )
	{
		ranges[ d ] = r;
		cellAccess.setPosition( r.gridx, d );
		lengths[ d ] = r.w;
		doffsets[ d ] = doffsets[ d + 1 ] + dsteps[ d ] * r.x; // doffsets[ n ] == 0
		roffsets[ d ] = roffsets[ d + 1 ] + rsteps[ d ] * i;   // roffsets[ n ] == 0
		cdims[ d ] = cellGrid.getCellDimension( d, r.gridx );
	}

	/**
	 * Once we get here, {@link #setupDestSize} and {@link #updateRange} for
	 * all dimensions have been called, so the {@code dsteps}, {@code
	 * doffsets}, {@code roffsets}, {@code cdims}, and {@code lengths} fields
	 * have been appropriately set up for the current Range combination. Also
	 * {@code cellAccess} is positioned on the corresponding cell.
	 */
	private void copyRanges( final T dest )
	{
		final Cell< ? > cell = cellAccess.get();
		final ArrayDataAccess< ? > data = ( ArrayDataAccess< ? > ) cell.getData();
		final boolean isValid = ( ( VolatileAccess ) data ).isValid();

		// Every pixel of this range combination comes from this one Cell, so
		// its validity is one entry in range space. roffsets[ 0 ] pins all
		// dimensions, so that entry is exactly roffsets[ 0 ].
		rvalid[ roffsets[ 0 ] ] = ( byte ) ( isValid ? 1 : 0 );

		if ( !isValid )
		{
			// The data of an invalid Cell is meaningless, so write zeros
			// instead of copying it: invalid pixels read as zero. This also
			// avoids touching the Cell's storage, which for an invalid Cell is
			// typically one array shared by every invalid Cell of the image.
			clearRanges( dest );
			return;
		}

		csteps[ 0 ] = 1;
		for ( int d = 0; d < n - 1; ++d )
			csteps[ d + 1 ] = csteps[ d ] * cdims[ d ];

		int sOffset = 0;
		for ( int d = 0; d < n; ++d )
		{
			final Ranges.Range r = ranges[ d ];
			sOffset += csteps[ d ] * r.cellx;
			switch( r.dir )
			{
			case BACKWARD:
				csteps[ d ] = -csteps[ d ];
				break;
			case STAY:
				csteps[ d ] = 0;
				break;
			}
		}

		final int dOffset = doffsets[ 0 ];

		final S src = ( S ) data.getCurrentStorageArray();
		if ( n > 1 )
			copyRangesRecursively( src, sOffset, dest, dOffset, n - 1 );
		else
		{
			final int l0 = lengths[ 0 ];
			final int cstep0 = csteps[ 0 ];
			memCopy.copyLines( cstep0, l0, 1, src, sOffset, 0, dest, dOffset, 0 );
		}
	}

	private void copyRangesRecursively( final S src, final int srcPos, final T dest, final int destPos, final int d )
	{
		final int length = lengths[ d ];
		final int cstep = csteps[ d ];
		final int dstep = dsteps[ d ];
		if ( d > 1 )
			for ( int i = 0; i < length; ++i )
				copyRangesRecursively( src, srcPos + i * cstep, dest, destPos + i * dstep, d - 1 );
		else
		{
			final int l0 = lengths[ 0 ];
			final int cstep0 = csteps[ 0 ];
			memCopy.copyLines( cstep0, l0, length, src, srcPos, cstep, dest, destPos, dstep );
		}
	}

	/**
	 * Fill the destination region of the current Range combination with zeros.
	 * This is what {@link #copyRanges} does instead of copying, when the Cell
	 * is invalid.
	 * <p>
	 * Unlike in {@link #fillRanges}, all {@code n} dimensions are pinned here,
	 * so only dimension {@code 0} is contiguous in {@code dest}: the region is
	 * the same box that {@link #copyRanges} would write, just without a source.
	 * <p>
	 * {@link MemCopy#clear} does not look at the source, so it does not matter
	 * whether it is called on {@code memCopy} or on {@code fillCopy}.
	 */
	private void clearRanges( final T dest )
	{
		final int dOffset = doffsets[ 0 ];
		if ( n > 1 )
			clearRangesRecursively( dest, dOffset, n - 1 );
		else
			memCopy.clear( dest, dOffset, lengths[ 0 ] );
	}

	private void clearRangesRecursively( final T dest, final int destPos, final int d )
	{
		final int length = lengths[ d ];
		final int dstep = dsteps[ d ];
		if ( d > 1 )
			for ( int i = 0; i < length; ++i )
				clearRangesRecursively( dest, destPos + i * dstep, d - 1 );
		else
		{
			final int l0 = lengths[ 0 ];
			for ( int i = 0; i < length; ++i )
				memCopy.clear( dest, destPos + i * dstep, l0 );
		}
	}

	/**
	 * Once we get here, {@link #setupDestSize} and {@link #updateRange} for
	 * all dimensions have been called, so the {@code dsteps}, {@code
	 * doffsets}, {@code roffsets}, {@code cdims}, and {@code lengths} fields
	 * have been appropriately set up for the current Range combination. Also
	 * {@code cellAccess} is positioned on the corresponding cell.
	 */
	void fillRanges( final T dest, final int dConst )
	{
		// Out-of-bounds pixels are valid. This range combination pins
		// dimensions dConst and above and leaves the dimensions below it free,
		// which in range space is the contiguous run of rsteps[ dConst ]
		// entries starting at roffsets[ dConst ].
		Arrays.fill( rvalid, roffsets[ dConst ], roffsets[ dConst ] + rsteps[ dConst ], ( byte ) 1 );

		final int dOffset = doffsets[ dConst ];
		lengths[ dConst ] *= dsteps[ dConst ];

		if ( n - 1 > dConst )
			fillRangesRecursively( dest, dOffset, n - 1, dConst );
		else
			fillCopy.copyValue( oob, 0, dest, dOffset, lengths[ dConst ] );
	}

	private void fillRangesRecursively( final T dest, final int destPos, final int d, final int dConst )
	{
		final int length = lengths[ d ];
		final int dstep = dsteps[ d ];
		if ( d > dConst + 1 )
			for ( int i = 0; i < length; ++i )
				fillRangesRecursively( dest, destPos + i * dstep, d - 1, dConst );
		else
			for ( int i = 0; i < length; ++i )
				fillCopy.copyValue( oob, 0, dest, destPos + i * dstep, lengths[ dConst ] );
	}

	/**
	 * Whether every pixel of the block at {@code srcPos} with the given {@code
	 * size} is valid, without copying any data.
	 * <p>
	 * Pixels that would be filled with Out-of-bounds value count as valid.
	 *
	 * @param failFast
	 * 		if {@code true}, return as soon as an invalid cell is found.
	 * 		If {@code false}, every cell the block touches is visited, which for
	 * 		a cached source has the side effect of requesting all of them.
	 */
	@Override
	public boolean allValid( final long[] srcPos, final int[] size, final boolean failFast )
	{
		// Find the ranges of one dimension at a time, reducing each range list
		// to the grid coordinates it reads from right away. As soon as one
		// dimension reads no cell at all, the block is entirely out-of-bounds,
		// and we are done without building the ranges of the remaining
		// dimensions. Position cellAccess on the way, so that iterating the
		// grid box below only ever has to move it.
		for ( int d = 0; d < n; ++d )
		{
			final List< Ranges.Range > ranges = findRanges.findRanges( srcPos[ d ], size[ d ], srcDims[ d ], cellGrid.cellDimension( d ) );
			if ( !collectCellPositions( d, ranges ) )
				// whole block is filled with oob value
				return true;
			cellAccess.setPosition( gridMin[ d ], d );
		}

		boolean allValid = true;
		while ( true )
		{
			final ArrayDataAccess< ? > data = ( ArrayDataAccess< ? > ) cellAccess.get().getData();
			if ( !( ( VolatileAccess ) data ).isValid() )
			{
				if ( failFast )
					return false;
				allValid = false;
			}

			// move cellAccess to the next cell of the grid box
			int d = 0;
			for ( ; d < n; ++d )
			{
				cellAccess.fwd( d );
				if ( cellAccess.getIntPosition( d ) <= gridMax[ d ] )
					break;
				cellAccess.setPosition( gridMin[ d ], d );
			}
			if ( d == n )
				return allValid;
		}
	}

	/**
	 * Find the extent, in grid coordinates, of the cells that the given {@code
	 * ranges} of dimension {@code d} read from, and store it in {@code
	 * gridMin[d]}, {@code gridMax[d]}.
	 * <p>
	 * The extent is exact: every contained cell is really need for copying.
	 *
	 * @return {@code true} if at least one cell is read. {@code false} if all
	 * 		{@code ranges} are CONSTANT, that is, the block is entirely
	 * 		out-of-bounds in dimension {@code d}. The stored extent is then
	 * 		empty, {@code gridMin[d] > gridMax[d]}.
	 */
	private boolean collectCellPositions( final int d, final List< Ranges.Range > ranges )
	{
		int mind = Integer.MAX_VALUE;
		int maxd = Integer.MIN_VALUE;
		// NB: The extremes have to be found by scanning all ranges of a
		// dimension. For CONSTANT and BORDER extension, grid coordinates
		// are monotonically non-decreasing, so the first and last
		// non-CONSTANT range would bracket them. For MIRROR extension they
		// do not: the sweep reverses at the image border, which puts the
		// extreme in the middle.
		for ( int i = 0; i < ranges.size(); ++i )
		{
			final Ranges.Range range = ranges.get( i );
			if ( range.dir != CONSTANT )
			{
				final int x = range.gridx;
				if ( x < mind )
					mind = x;
				if ( x > maxd )
					maxd = x;
			}
		}
		gridMin[ d ] = mind;
		gridMax[ d ] = maxd;
		return mind <= maxd;
	}
}
