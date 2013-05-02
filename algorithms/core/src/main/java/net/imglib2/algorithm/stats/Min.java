package net.imglib2.algorithm.stats;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;

/**
 * Find the minimum value and its position in an {@link IterableInterval}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Min
{
	/**
	 * Find the minimum value and its position in an {@link IterableInterval}.
	 *
	 * @param iterable
	 *            input interval.
	 * @return a cursor positioned on the global minimum. If several minima with
	 *         the same value exist, the cursor is on the first one.
	 */
	public static < T extends Comparable< T > > Cursor< T > findMin( final IterableInterval< T > iterable )
	{
		final Cursor< T > cursor = iterable.cursor();
		cursor.fwd();
		Cursor< T > min = cursor.copyCursor();
		while ( cursor.hasNext() )
			if ( cursor.next().compareTo( min.get() ) < 0 )
				min = cursor.copyCursor();
		return min;
	}

}
