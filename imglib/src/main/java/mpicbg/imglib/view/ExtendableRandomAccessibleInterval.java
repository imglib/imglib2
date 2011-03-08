package mpicbg.imglib.view;

import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;

public interface ExtendableRandomAccessibleInterval< T > extends Interval, RandomAccessible< T >, View< T >
{
	/**
	 * Test whether the given position falls out of bounds.
	 * 
	 * @param position
	 * @return
	 */
	public boolean isOutOfBounds( final long[] position );
	
	/**
	 * Get {@link RandomAccess} that can access out of bounds.
	 * 
	 * @return
	 */
	public RandomAccess< T > extendedRandomAccess();
}
