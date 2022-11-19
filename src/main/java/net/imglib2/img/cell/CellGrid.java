/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img.cell;

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
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.RandomAccessibleIntervalCursor;

/**
 * Defines {@link AbstractCellImg} geometry and translates between image, cell,
 * and grid coordinates.
 *
 * @author Tobias Pietzsch
 */
public class CellGrid
{
	private final int n;

	private final long[] dimensions;

	private final int[] cellDimensions;

	private final long[] numCells;

	private final int[] borderSize;

	private final int hashcode;

	/**
	 * @param dimensions
	 * 		the dimensions of the image (in pixels, not in cells).
	 * @param cellDimensions
	 * 		the dimensions of a standard cell (in pixels). Cells on the max border
	 * 		of the image may be cut off and have different dimensions.
	 */
	public CellGrid(
			final long[] dimensions,
			final int[] cellDimensions )
	{
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.cellDimensions = cellDimensions.clone();

		numCells = new long[ n ];
		borderSize = new int[ n ];
		for ( int d = 0; d < n; ++d )
		{
			numCells[ d ] = ( dimensions[ d ] - 1 ) / cellDimensions[ d ] + 1;
			borderSize[ d ] = ( int ) ( dimensions[ d ] - ( numCells[ d ] - 1 ) * cellDimensions[ d ] );
		}
		hashcode = 31 * Arrays.hashCode( dimensions ) + Arrays.hashCode( cellDimensions );

		cellIntervals = new CellIntervals();
	}

	public CellGrid( final CellGrid grid )
	{
		n = grid.n;
		dimensions = grid.dimensions.clone();
		cellDimensions = grid.cellDimensions.clone();
		numCells = grid.numCells.clone();
		borderSize = grid.borderSize.clone();
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
	 * Get the number of pixels in a standard cell in each dimension as a new int[].
	 * Cells on the borders of the image may be cut off and have different dimensions.
	 */
	public int[] getCellDimensions()
	{
		return cellDimensions.clone();
	}

	/**
	 * Write the number of pixels in a standard cell in each dimension into the
	 * provided {@code dimensions} array. Cells on the max border of the image may be
	 * cut off and have different dimensions.
	 */
	public void cellDimensions( final int[] dimensions )
	{
		for ( int i = 0; i < n; ++i )
			dimensions[ i ] = cellDimensions[ i ];
	}

	/**
	 * Get the number of pixels in a standard cell in dimension {@code d}. Cells on the
	 * max border of the image may be cut off and have different dimensions.
	 */
	public int cellDimension( final int d )
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
	 * {@link #getCellDimensions(long[], long[], int[])}.
	 * </p>
	 *
	 * @param index
	 *            flattened grid coordinates of the cell.
	 * @param cellMin
	 *            offset of the cell in image coordinates are written here.
	 * @param cellDims
	 *            dimensions of the cell are written here.
	 */
	public void getCellDimensions( long index, final long[] cellMin, final int[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long j = index / numCells[ d ];
			final long gridPos = index - j * numCells[ d ];
			index = j;
			cellDims[ d ] = ( ( gridPos == numCells[ d ] - 1 ) ? borderSize[ d ] : cellDimensions[ d ] );
			cellMin[ d ] = gridPos * cellDimensions[ d ];
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
	 *            grid coordinates of the cell.
	 * @param cellMin
	 *            offset of the cell in image coordinates are written here.
	 * @param cellDims
	 *            dimensions of the cell are written here.
	 */
	public void getCellDimensions( final long[] cellGridPosition, final long[] cellMin, final int[] cellDims )
	{
		for ( int d = 0; d < n; ++d )
		{
			cellDims[ d ] = ( ( cellGridPosition[ d ] + 1 == numCells[ d ] ) ? borderSize[ d ] : cellDimensions[ d ] );
			cellMin[ d ] = cellGridPosition[ d ] * cellDimensions[ d ];
		}
	}

	public void getCellInterval( final long[] cellGridPosition, final long[] cellMin, final long[] cellMax )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long gridPos = cellGridPosition[ d ];
			final int cellDim = ( gridPos + 1 == numCells[ d ] ) ? borderSize[ d ] : cellDimensions[ d ];
			cellMin[ d ] = gridPos * cellDimensions[ d ];
			cellMax[ d ] = cellMin[ d ] + cellDim - 1;
		}
	}

	/**
	 * From the position of a cell in the grid, compute the size of the cell in
	 * dimension {@code d}. The size will be the standard
	 * {@link #cellDimensions} unless the cell is at the border of the image in
	 * which case it might be truncated.
	 *
	 * @param d
	 *            dimension index
	 * @param cellGridPosition
	 *            grid coordinates of the cell in dimension {@code d}.
	 * @return size of the cell in dimension {@code d}.
	 */
	public int getCellDimension( final int d, final long cellGridPosition )
	{
		return ( ( cellGridPosition + 1 == numCells[ d ] ) ? borderSize[ d ] : cellDimensions[ d ] );
	}

	/**
	 * From the position of a cell in the grid, compute the image position in
	 * dimension {@code d} of the first pixel of the cell (the offset of the
	 * cell in image coordinates).
	 *
	 * @param d
	 *            dimension index
	 * @param cellGridPosition
	 *            grid coordinates of the cell in dimension {@code d}.
	 * @return offset of the cell in dimension {@code d} (in image coordinates).
	 */
	public long getCellMin( final int d, final long cellGridPosition )
	{
		return cellGridPosition * cellDimensions[ d ];
	}

	/**
	 * From the flattened index of a cell in the grid, compute the position of a
	 * cell in the grid.
	 *
	 * @param index
	 *            flattened grid coordinates of the cell.
	 * @param cellGridPosition
	 *            grid coordinates of the cell are written here.
	 */
	public void getCellGridPositionFlat( final long index, final long[] cellGridPosition )
	{
		IntervalIndexer.indexToPosition( index, numCells, cellGridPosition );
	}

	/**
	 * Get the grid position of the cell containing the element at {@code position}.
	 *
	 * @param position
	 *            position of an element in the image.
	 * @param cellPos
	 *            is set to the grid position of the cell containing the element.
	 */
	public void getCellPosition( final long[] position, final long[] cellPos )
	{
		for ( int d = 0; d < n; ++d )
			cellPos[ d ] = position[ d ] / cellDimensions[ d ];
	}

	/**
	 * Get the grid position of the cell containing the element at {@code position}.
	 *
	 * @param position
	 *            position of an element in the image.
	 * @param cellPos
	 *            is set to the grid position of the cell containing the element.
	 */
	public void getCellPosition( final long[] position, final Positionable cellPos )
	{
		for ( int d = 0; d < n; ++d )
			cellPos.setPosition( position[ d ] / cellDimensions[ d ], d );
	}

	@Override
	public int hashCode()
	{
		return hashcode;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( obj instanceof CellGrid )
		{
			final CellGrid other = ( CellGrid ) obj;
			return Arrays.equals( dimensions, other.dimensions )
					&& Arrays.equals( cellDimensions, other.cellDimensions );
		}
		return false;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName()
				+ "( dims = " + Util.printCoordinates( dimensions )
				+ ", cellDims = " + Util.printCoordinates( cellDimensions ) + " )";
	}

	private class CellIntervalsRA extends Point implements RandomAccess< Interval >
	{
		private final long[] min = new long[ CellGrid.this.n ];

		private final long[] max = new long[ CellGrid.this.n ];

		private final Interval interval = FinalInterval.wrap( min, max );

		@Override
		public Interval get()
		{
			getCellInterval( position, min, max );
			return interval;
		}

		CellIntervalsRA()
		{
			super( CellGrid.this.n );
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
	}

	private final CellIntervals cellIntervals;

	public CellIntervals cellIntervals()
	{
		return cellIntervals;
	}
}
