package mpicbg.imglib.view;

import mpicbg.imglib.RandomAccessibleInterval;

/**
 * A bounded view of a RandomAccessible.
 * 
 * @author Tobias Pietzsch
 */
public interface RandomAccessibleIntervalView< T > extends RandomAccessibleView< T >, RandomAccessibleInterval< T >
{
}
