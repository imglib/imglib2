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
package net.imglib2.util;

import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.view.RandomAccessibleIntervalCursor;

/**
 * Defines a regular grid on an interval and translates between interval and grid
 * coordinates.
 * <p>
 * In contrast to {@link CellGrid}, all coordinates are in {@code long}, and
 * gridlines can be shifted such that cells at the min border are truncated.
 *
 * @author Tobias Pietzsch
 */
public class Grid
{
	private final int n;

	private final long[] dimensions;

	private final long[] cellDimensions;

	private final long[] minBorderSize;

	private final long[] numCells;

	private final long[] maxBorderSize;

	private final int hashcode;

	/**
	 * @param dimensions
	 * 		the dimensions of the image (in pixels, not in cells).
	 * @param cellDimensions
	 * 		the dimensions of a standard cell (in pixels). Cells on the max border
	 * 		of the image may be cut off and have different dimensions.
	 */
	public Grid(
			final long[] dimensions,
			final long[] cellDimensions )
	{
		this( dimensions, cellDimensions, new long[ dimensions.length ] );
	}

	/**
	 * @param dimensions
	 * 		the dimensions of the image (in pixels, not in cells).
	 * @param cellDimensions
	 * 		the dimensions of a standard cell (in pixels). Cells on the borders
	 * 		of the image may be cut off and have different dimensions.
	 * @param offset
	 * 		offset of the grid on the min border. Cell on the min border of the
	 * 		image will be cut off according to offset. For example, if {@code
	 * 		cellDimensions={10,10}} and {@code offset={5,0}} then the first cell
	 * 		will have dimensions {@code {5,10}} (assuming that {@code dimensions}
	 * 		is larger than {@code {10,10}}).
	 */
	public Grid(
			final long[] dimensions,
			final long[] cellDimensions,
			final long[] offset )
	{
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.cellDimensions = cellDimensions.clone();

		numCells = new long[ n ];
		minBorderSize = new long[ n ];
		maxBorderSize = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			final long cd = cellDimensions[ d ];
			final long po = ( offset[ d ] % cd + cd ) % cd; // project offset into [0, cd). Also works for negative offsets.
			minBorderSize[ d ] = Math.min( po == 0 ? cellDimensions[ d ] : po, dimensions[ d ] );
			final long d0 = dimensions[ d ] - minBorderSize[ d ];
			numCells[ d ] = d0 == 0 ? 1 : ( d0 - 1 ) / cd + 2;
			maxBorderSize[ d ] = ( int ) ( d0 - ( numCells[ d ] - 2 ) * cd );
		}

		int hash = Arrays.hashCode( dimensions );
		hash = hash * 31 + Arrays.hashCode( cellDimensions );
		hash = hash * 31 + Arrays.hashCode( minBorderSize );
		hashcode = hash;

		cellIntervals = new CellIntervals();
	}

	public Grid( final Grid grid )
	{
		n = grid.n;
		dimensions = grid.dimensions.clone();
		cellDimensions = grid.cellDimensions.clone();
		numCells = grid.numCells.clone();
		minBorderSize = grid.minBorderSize.clone();
		maxBorderSize = grid.maxBorderSize.clone();
		hashcode = grid.hashcode;
		cellIntervals = new CellIntervals();
	}

	public int numDimensions()
	{
		return n;
	}

	/**
	 * Get the number of cells in each dimension as a new long[].
	 */
	public long[] getGridDimensions()
	{
		return numCells.clone();
	}

	/**
	 * Write the number of cells in each dimension into the provided {@code
	 * dimensions} array.
	 */
	public void gridDimensions( final long[] dimensions )
	{
		for ( int i = 0; i < n; ++i )
			dimensions[ i ] = numCells[ i ];
	}

	/**
	 * Get the number of cells in dimension {@code d}.
	 */
	public long gridDimension( final int d )
	{
		return numCells[ d ];
	}

	/**
	 * Get the number of pixels in each dimension as a new long[]. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 */
	public long[] getImgDimensions()
	{
		return dimensions.clone();
	}

	/**
	 * Write the number of pixels in each dimension into the provided {@code
	 * dimensions} array. Note, that this is the number of pixels in all cells
	 * combined, not the number of cells!
	 */
	public void imgDimensions( final long[] dimensions )
	{
		for ( int i = 0; i < n; ++i )
			dimensions[ i ] = this.dimensions[ i ];
	}

	/**
	 * Get the number of pixels in dimension {@code d}. Note, that this is the number
	 * of pixels in all cells combined, not the number of cells!
	 */
	public long imgDimension( final int d )
	{
		return dimensions[ d ];
	}

	/**
	 * Get the number of pixels in a standard cell in each dimension as a new long[].
	 * Cells on the borders of the image may be cut off and have different dimensions.
	 */
	public long[] getCellDimensions()
	{
		return cellDimensions.clone();
	}

	/**
	 * Write the number of pixels in a standard cell in each dimension into the
	 * provided {@code dimensions} array. Cells on the max borders of the image may be
	 * cut off and have different dimensions.
	 */
	public void cellDimensions( final long[] dimensions )
	{
		for ( int i = 0; i < n; ++i )
			dimensions[ i ] = cellDimensions[ i ];
	}

	/**
	 * Get the number of pixels in a standard cell in dimension {@code d}. Cells on the
	 * borders of the image may be cut off and have different dimensions.
	 */
	public long cellDimension( final int d )
	{
		return cellDimensions[ d ];
	}

	/**
	 * From the index of a cell in the grid, compute the image position of the
	 * first pixel of the cell (the offset of the cell in image coordinates) and
	 * the dimensions of the cell. The dimensions will be the standard
	 * {@link #cellDimensions} unless the cell is at the border of the image in
	 * which case it might be truncated.
	 * <p>
	 * Note, that this method assumes that the cell grid has flat iteration
	 * order. It this is not the case, use
	 * {@link #getCellDimensions(long[], long[], long[])}.
	 * </p>
	 *
	 * @param index
	 * 		flattened grid coordinates of the cell.
	 * @param cellMin
	 * 		offset of the cell in image coordinates are written here.
	 * @param cellDims
	 * 		dimensions of the cell are written here.
	 */
	public void getCellDimensions( long index, final long[] cellMin, final long[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long j = index / numCells[ d ];
			final long gridPos = index - j * numCells[ d ];
			index = j;
			if ( gridPos == 0 )
			{
				cellDims[ d ] = minBorderSize[ d ];
				cellMin[ d ] = 0;
			}
			else if ( gridPos == numCells[ d ] - 1 )
			{
				cellDims[ d ] = maxBorderSize[ d ];
				cellMin[ d ] = minBorderSize[ d ] + ( gridPos - 1 ) * cellDimensions[ d ];
			}
			else
			{
				cellDims[ d ] = cellDimensions[ d ];
				cellMin[ d ] = minBorderSize[ d ] + ( gridPos - 1 ) * cellDimensions[ d ];
			}
		}
	}

	/**
	 * From the position of a cell in the grid, compute the image position of
	 * the first pixel of the cell (the offset of the cell in image coordinates)
	 * and the dimensions of the cell. The dimensions will be the standard
	 * {@link #cellDimensions} unless the cell is at the border of the image in
	 * which case it might be truncated.
	 *
	 * @param cellGridPosition
	 * 		grid coordinates of the cell.
	 * @param cellMin
	 * 		offset of the cell in image coordinates are written here.
	 * @param cellDims
	 * 		dimensions of the cell are written here.
	 */
	public void getCellDimensions( final long[] cellGridPosition, final long[] cellMin, final long[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long gridPos = cellGridPosition[ d ];
			if ( gridPos == 0 )
			{
				cellDims[ d ] = minBorderSize[ d ];
				cellMin[ d ] = 0;
			}
			else if ( gridPos == numCells[ d ] - 1 )
			{
				cellDims[ d ] = maxBorderSize[ d ];
				cellMin[ d ] = minBorderSize[ d ] + ( gridPos - 1 ) * cellDimensions[ d ];
			}
			else
			{
				cellDims[ d ] = cellDimensions[ d ];
				cellMin[ d ] = minBorderSize[ d ] + ( gridPos - 1 ) * cellDimensions[ d ];
			}
		}
	}

	public void getCellInterval( final long[] cellGridPosition, final long[] cellMin, final long[] cellMax )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long gridPos = cellGridPosition[ d ];
			if ( gridPos == 0 )
			{
				cellMin[ d ] = 0;
				cellMax[ d ] = minBorderSize[ d ] - 1;
			}
			else if ( gridPos == numCells[ d ] - 1 )
			{
				cellMin[ d ] = minBorderSize[ d ] + ( gridPos - 1 ) * cellDimensions[ d ];
				cellMax[ d ] = cellMin[ d ] + maxBorderSize[ d ] - 1;
			}
			else
			{
				cellMin[ d ] = minBorderSize[ d ] + ( gridPos - 1 ) * cellDimensions[ d ];
				cellMax[ d ] = cellMin[ d ] + cellDimensions[ d ] - 1;
			}
		}
	}

	/**
	 * From the position of a cell in the grid, compute the size of the cell in
	 * dimension {@code d}. The size will be the standard
	 * {@link #cellDimensions} unless the cell is at the border of the image in
	 * which case it might be truncated.
	 *
	 * @param d
	 * 		dimension index
	 * @param cellGridPosition
	 * 		grid coordinates of the cell in dimension {@code d}.
	 *
	 * @return size of the cell in dimension {@code d}.
	 */
	public long getCellDimension( final int d, final long cellGridPosition )
	{
		if ( cellGridPosition == 0 )
			return minBorderSize[ d ];
		else if ( cellGridPosition == numCells[ d ] - 1 )
			return maxBorderSize[ d ];
		else
			return cellDimensions[ d ];
	}

	/**
	 * From the position of a cell in the grid, compute the image position in
	 * dimension {@code d} of the first pixel of the cell (the offset of the
	 * cell in image coordinates).
	 *
	 * @param d
	 * 		dimension index
	 * @param cellGridPosition
	 * 		grid coordinates of the cell in dimension {@code d}.
	 *
	 * @return offset of the cell in dimension {@code d} (in image coordinates).
	 */
	public long getCellMin( final int d, final long cellGridPosition )
	{
		if ( cellGridPosition == 0 )
			return 0;
		else
			return minBorderSize[ d ] + ( cellGridPosition - 1 ) * cellDimensions[ d ];
	}

	/**
	 * From the flattened index of a cell in the grid, compute the position of a
	 * cell in the grid.
	 *
	 * @param index
	 * 		flattened grid coordinates of the cell.
	 * @param cellGridPosition
	 * 		grid coordinates of the cell are written here.
	 */
	public void getCellGridPositionFlat( final long index, final long[] cellGridPosition )
	{
		IntervalIndexer.indexToPosition( index, numCells, cellGridPosition );
	}

	/**
	 * Get the grid position of the cell containing the element at {@code position}.
	 *
	 * @param position
	 * 		position of an element in the image.
	 * @param cellPos
	 * 		is set to the grid position of the cell containing the element.
	 */
	public void getCellPosition( final long[] position, final long[] cellPos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( position[ d ] < minBorderSize[ d ] )
				cellPos[ d ] = 0;
			else
				cellPos[ d ] = ( position[ d ] - minBorderSize[ d ] ) / cellDimensions[ d ];
		}
	}

	/**
	 * Get the grid position of the cell containing the element at {@code position}.
	 *
	 * @param position
	 * 		position of an element in the image.
	 * @param cellPos
	 * 		is set to the grid position of the cell containing the element.
	 */
	public void getCellPosition( final long[] position, final Positionable cellPos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( position[ d ] < minBorderSize[ d ] )
				cellPos.setPosition( 0, d );
			else
				cellPos.setPosition( ( position[ d ] - minBorderSize[ d ] ) / cellDimensions[ d ], d );
		}
	}

	@Override
	public int hashCode()
	{
		return hashcode;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( obj instanceof Grid )
		{
			final Grid other = ( Grid ) obj;
			return Arrays.equals( dimensions, other.dimensions )
					&& Arrays.equals( cellDimensions, other.cellDimensions )
					&& Arrays.equals( minBorderSize, other.minBorderSize );
		}
		return false;
	}

	@Override
	public String toString()
	{
		final long[] offset = new long[ n ];
		Arrays.setAll( offset, d -> cellDimensions[ d ] == minBorderSize[ d ] ? 0 : minBorderSize[ d ] );

		return getClass().getSimpleName()
				+ "( dims = " + Util.printCoordinates( dimensions )
				+ ", cellDims = " + Util.printCoordinates( cellDimensions )
				+ ", offset = " + Util.printCoordinates( offset ) + " )";
	}

	private class CellIntervalsRA extends Point implements RandomAccess< Interval >
	{
		private final long[] min = new long[ Grid.this.n ];

		private final long[] max = new long[ Grid.this.n ];

		private final Interval interval = FinalInterval.wrap( min, max );

		@Override
		public Interval get()
		{
			getCellInterval( position, min, max );
			return interval;
		}

		CellIntervalsRA()
		{
			super( Grid.this.n );
		}

		CellIntervalsRA( final CellIntervalsRA ra )
		{
			super( ra );
		}

		@Override
		public RandomAccess< Interval > copy()
		{
			return new CellIntervalsRA( this );
		}
	}

	public class CellIntervals implements RandomAccessibleInterval< Interval >, IterableInterval< Interval >
	{
		private final long size = Intervals.numElements( numCells );

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public long min( final int d )
		{
			return 0;
		}

		@Override
		public long max( final int d )
		{
			return numCells[ d ] - 1;
		}

		@Override
		public RandomAccess< Interval > randomAccess()
		{
			return new CellIntervalsRA();
		}

		@Override
		public RandomAccess< Interval > randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		@Override
		public Cursor< Interval > cursor()
		{
			return new RandomAccessibleIntervalCursor<>( this );
		}

		@Override
		public Cursor< Interval > localizingCursor()
		{
			return cursor();
		}

		@Override
		public long size()
		{
			return size;
		}

		@Override
		public FlatIterationOrder iterationOrder()
		{
			return new FlatIterationOrder( this );
		}

		@Override
		public Interval getType()
		{
			return firstElement();
		}
	}

	private final CellIntervals cellIntervals;

	public CellIntervals cellIntervals()
	{
		return cellIntervals;
	}
}
