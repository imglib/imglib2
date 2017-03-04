package net.imglib2.img.cell;

import java.util.Arrays;

import net.imglib2.Positionable;
import net.imglib2.util.IntervalIndexer;

public class CellGrid
{
	private final int n;

	private final long[] dimensions;

	private final int[] cellDimensions;

	private final long[] numCells;

	private final int[] borderSize;

	private final int hashcode;

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
	}

	public CellGrid( final CellGrid grid )
	{
		n = grid.n;
		dimensions = grid.dimensions.clone();
		cellDimensions = grid.cellDimensions.clone();
		numCells = grid.numCells.clone();
		borderSize = grid.borderSize.clone();
		hashcode = grid.hashcode;
	}

	public int numDimensions()
	{
		return n;
	}

	public long[] getGridDimensions()
	{
		return numCells.clone();
	}

	public void gridDimensions( final long[] s )
	{
		for ( int i = 0; i < n; ++i )
			s[ i ] = numCells[ i ];
	}

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
	 * Write the number of pixels in each dimension into long[]. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 *
	 * @param dimensions
	 */
	public void imgDimensions( final long[] dimensions )
	{
		for ( int i = 0; i < n; ++i )
			dimensions[ i ] = this.dimensions[ i ];
	}

	/**
	 * Get the number of pixels in a given dimension <em>d</em>. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 *
	 * @param d
	 */
	public long imgDimension( final int d )
	{
		return dimensions[ d ];
	}

	/**
	 * Write the number of pixels in a standard cell in each dimension into
	 * long[]. Cells on the max border of the image may be cut off and have
	 * different dimensions.
	 *
	 * @param dimensions
	 */
	public void cellDimensions( final int[] dimensions )
	{
		for ( int i = 0; i < n; ++i )
			dimensions[ i ] = cellDimensions[ i ];
	}

	/**
	 * Get the number of pixels in a standard cell in a given dimension
	 * <em>d</em>. Cells on the max border of the image may be cut off and have
	 * different dimensions.
	 *
	 * @param d
	 */
	public int cellDimension( final int d )
	{
		return cellDimensions[ d ];
	}

	/**
	 * From the index of a cell in the {@link #cells()} grid, compute the image
	 * position of the first pixel of the cell (the offset of the cell in image
	 * coordinates) and the dimensions of the cell. The dimensions will be the
	 * standard {@link #cellDimensions} unless the cell is at the border of the
	 * image in which case it might be truncated.
	 *
	 * <em> Note, that this method assumes that the cell grid has flat iteration
	 * order. It this is not the case, use </em>
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
	 * From the position of a cell in the {@link #cells()} grid, compute the
	 * image position of the first pixel of the cell (the offset of the cell in
	 * image coordinates) and the dimensions of the cell. The dimensions will be
	 * the standard {@link #cellDimensions} unless the cell is at the border of
	 * the image in which case it might be truncated.
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
	 * Get the grid position of the cell containing the element at {@link position}.
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
	 * Get the grid position of the cell containing the element at {@link position}.
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
}
