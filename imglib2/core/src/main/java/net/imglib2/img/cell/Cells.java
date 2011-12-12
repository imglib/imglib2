package net.imglib2.img.cell;

import net.imglib2.Cursor;
import net.imglib2.EuclideanSpace;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public interface Cells< A extends ArrayDataAccess< A > > extends EuclideanSpace
{
	/**
	 * Write the number of pixels in each dimension into long[].
	 * 
	 * @param dimensions
	 */
	public void dimensions( long[] dimensions );
	
	/**
	 * Get the number of pixels in a given dimension <em>d</em>.
	 * 
	 * @param d
	 */
	public long dimension( int d );

	/**
	 * Write the number of pixels in a standard cell in each dimension into long[].
	 * Cells on the max border of the image may be cut off and have different dimensions.
	 * 
	 * @param dimensions
	 */
	public void cellDimensions( int[] dimensions );
	
	/**
	 * Get the number of pixels in a standard cell in a given dimension <em>d</em>.
	 * Cells on the max border of the image may be cut off and have different dimensions.
	 * 
	 * @param d
	 */
	public int cellDimension( int d );
	
	public int getEntitiesPerPixel();

	public RandomAccess< Cell< A > > randomAccess();

	public Cursor< Cell< A > > cursor();

	public Cursor< Cell< A >> localizingCursor();
}
