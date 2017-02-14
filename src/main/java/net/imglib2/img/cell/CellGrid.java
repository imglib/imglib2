package net.imglib2.img.cell;

import net.imglib2.Dimensions;

public interface CellGrid extends Dimensions
{
	/**
	 * Write the number of pixels in each dimension into long[]. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 *
	 * @param dimensions
	 */
	@Override
	public void dimensions( long[] dimensions );

	/**
	 * Get the number of pixels in a given dimension <em>d</em>. Note, that this
	 * is the number of pixels in all cells combined, not the number of cells!
	 *
	 * @param d
	 */
	@Override
	public long dimension( int d );

	/**
	 * Write the number of pixels in a standard cell in each dimension into
	 * long[]. Cells on the max border of the image may be cut off and have
	 * different dimensions.
	 *
	 * @param dimensions
	 */
	public void cellDimensions( int[] dimensions );

	/**
	 * Get the number of pixels in a standard cell in a given dimension
	 * <em>d</em>. Cells on the max border of the image may be cut off and have
	 * different dimensions.
	 *
	 * @param d
	 */
	public int cellDimension( int d );

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
	public void getCellDimensions( long index, final long[] cellMin, final int[] cellDims );

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
	public void getCellDimensions( final long[] cellGridPosition, final long[] cellMin, final int[] cellDims );
}
