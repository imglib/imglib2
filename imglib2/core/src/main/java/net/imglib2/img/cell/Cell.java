package net.imglib2.img.cell;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public interface Cell< A extends ArrayDataAccess< A > >
{
	public A getData();

	/**
	 * 
	 * @param d dimension
	 * @return minimum
	 */
	public long min( final int d );

	/**
	 * Write the minimum of each dimension into long[].
	 * 
	 * @param min
	 */
	public void min( long[] min );

	/**
	 * Write the number of pixels in each dimension into long[].
	 * 
	 * @param dimensions
	 */
	public void dimensions( int[] dimensions );

	/**
	 * Get the number of pixels in a given dimension <em>d</em>.
	 * 
	 * @param d
	 */
	public int dimension( int d );

	/**
	 * @return number of pixels in this cell.
	 */
	public long size();

	public long indexToGlobalPosition( int index, int d );

	public void indexToGlobalPosition( int index, final long[] position );

	/**
	 * compute the index in the underlying flat array of this cell
	 * which corresponds to a local position (i.e., relative to the
	 * origin of this cell).
	 * 
	 * @param position   a local position
	 * @return corresponding index
	 */
	public int localPositionToIndex( final long[] position );
	
	// TODO: is this really faster, or should we just copy, e.g., void getStepsArray( final int steps[] )?
	public int[] getStepsArray();
	public long[] getMinArray();
	public long[] getMaxArray();	
}
