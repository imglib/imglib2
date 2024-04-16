/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.List;
import net.imglib2.RandomAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;

import static net.imglib2.blocks.Ranges.Direction.CONSTANT;

/**
 * Does the actual copying work from an {@code AbstractCellImg} into a primitive
 * array.
 *
 * @param <T> a primitive array type, e.g., {@code byte[]}.
 */
class CellImgRangeCopier< T > implements RangeCopier< T >
{
	private final int n;
	private final CellGrid cellGrid;
	private final RandomAccess< ? extends Cell< ? > > cellAccess;
	private final long[] srcDims;
	private final Ranges findRanges;
	private final MemCopy< T > memCopy;
	private final T oob;

	private final List< Ranges.Range >[] rangesPerDimension;
	private final Ranges.Range[] ranges;

	private final int[] dsteps;
	private final int[] doffsets;
	private final int[] cdims;
	private final int[] csteps;
	private final int[] lengths;

	public CellImgRangeCopier(
			final AbstractCellImg< ?, ?, ?, ? > cellImg,
			final Ranges findRanges,
			final MemCopy< T > memCopy,
			final T oob )
	{
		n = cellImg.numDimensions();
		cellGrid = cellImg.getCellGrid();
		cellAccess = cellImg.getCells().randomAccess();
		srcDims = cellImg.dimensionsAsLongArray();

		this.findRanges = findRanges;
		this.memCopy = memCopy;
		this.oob = oob;

		rangesPerDimension = new List[ n ];
		ranges = new Ranges.Range[ n ];

		dsteps = new int[ n ];
		doffsets = new int[ n + 1 ];
		cdims = new int[ n ];
		csteps = new int[ n ];
		lengths = new int[ n ];
	}

	// creates an independent copy of {@code other}
	private CellImgRangeCopier( CellImgRangeCopier< T > copier )
	{
		n = copier.n;
		cellGrid = copier.cellGrid;
		cellAccess = copier.cellAccess.copy();
		srcDims = copier.srcDims.clone();
		findRanges = copier.findRanges;
		memCopy = copier.memCopy;
		oob = copier.oob;

		rangesPerDimension = new List[ n ];
		ranges = new Ranges.Range[ n ];
		dsteps = new int[ n ];
		doffsets = new int[ n + 1 ];
		cdims = new int[ n ];
		csteps = new int[ n ];
		lengths = new int[ n ];
	}

	@Override
	public CellImgRangeCopier< T > newInstance()
	{
		return new CellImgRangeCopier<>( this );
	}

	/**
	 * Copy the block starting at {@code srcPos} with the given {@code size}
	 * into the (appropriately sized) {@code dest} array.
	 * <p>
	 * This finds the src range lists for all dimensions and then calls
	 * {@link #copy(Object, int)} to iterate all range combinations.
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
	public void copy( final long[] srcPos, final T dest, final int[] size )
	{
		// find ranges
		for ( int d = 0; d < n; ++d )
			rangesPerDimension[ d ] = findRanges.findRanges( srcPos[ d ], size[ d ], srcDims[ d ], cellGrid.cellDimension( d ) );

		// copy data
		setupDestSize( size );
		copy( dest, n - 1 );
	}

	/**
	 * Iterates the {@code rangesPerDimension} list for the given dimension {@code d}
	 * and recursively calls itself for iterating dimension {@code d-1}.
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
		for ( Ranges.Range range : rangesPerDimension[ d ] )
		{
			ranges[ d ] = range;
			updateRange( d );
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

	private void updateRange( final int d )
	{
		final Ranges.Range r = ranges[ d ];
		cellAccess.setPosition( r.gridx, d );
		lengths[ d ] = r.w;
		doffsets[ d ] = doffsets[ d + 1 ] + dsteps[ d ] * r.x; // doffsets[ n ] == 0
		cdims[ d ] = cellGrid.getCellDimension( d, r.gridx );
	}

	/**
     * Once we get here, {@link #setupDestSize} and {@link #updateRange} for
     * all dimensions have been called, so the {@code dsteps}, {@code
     * doffsets}, {@code cdims}, and {@code lengths} fields have been
     * appropriately set up for the current Range combination. Also {@code
     * cellAccess} is positioned on the corresponding cell.
     */
	private void copyRanges( final T dest )
	{
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

		final T src = ( T ) ( ( ( ArrayDataAccess< ? > ) cellAccess.get().getData() ).getCurrentStorageArray() );
		if ( n > 1 )
			copyRangesRecursively( src, sOffset, dest, dOffset, n - 1 );
		else
		{
			final int l0 = lengths[ 0 ];
			final int cstep0 = csteps[ 0 ];
			memCopy.copyLines( cstep0, l0, 1, src, sOffset, 0, dest, dOffset, 0 );
		}
	}

	private void copyRangesRecursively( final T src, final int srcPos, final T dest, final int destPos, final int d )
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
     * Once we get here, {@link #setupDestSize} and {@link #updateRange} for
     * all dimensions have been called, so the {@code dsteps}, {@code
     * doffsets}, {@code cdims}, and {@code lengths} fields have been
     * appropriately set up for the current Range combination. Also {@code
     * cellAccess} is positioned on the corresponding cell.
     */
	void fillRanges( final T dest, final int dConst )
	{
		final int dOffset = doffsets[ dConst ];
		lengths[ dConst ] *= dsteps[ dConst ];

		if ( n - 1 > dConst )
			fillRangesRecursively( dest, dOffset, n - 1, dConst );
		else
			memCopy.copyValue( oob, 0, dest, dOffset, lengths[ dConst ] );
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
				memCopy.copyValue( oob, 0, dest, destPos + i * dstep, lengths[ dConst ] );
	}
}
