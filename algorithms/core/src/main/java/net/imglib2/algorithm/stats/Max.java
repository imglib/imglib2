package net.imglib2.algorithm.stats;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;

/**
 * Find the maximum value and its position in an {@link IterableInterval}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Max
{
	/**
	 * Find the maximum value and its position in an {@link IterableInterval}.
	 *
	 * @param iterable
	 *            input interval.
	 * @return a cursor positioned on the global maximum. If several maxima with
	 *         the same value exist, the cursor is on the first one.
	 */
	public static < T extends Comparable< T > > Cursor< T > findMax( final IterableInterval< T > iterable )
	{
		final Cursor< T > cursor = iterable.cursor();
		cursor.fwd();
		Cursor< T > max = cursor.copyCursor();
		while ( cursor.hasNext() )
			if ( cursor.next().compareTo( max.get() ) > 0 )
				max = cursor.copyCursor();
		return max;
	}
}
