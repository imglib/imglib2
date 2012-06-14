package net.imglib2.algorithm.stats;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.NumericType;

public class Normalize
{
	/**
	 * Normalize values of an {@link IterableInterval} to the range [min, max].
	 *
	 * @param iterable
	 *            the interval to be normalized.
	 * @param min
	 *            target minimum value of the normalized interval.
	 * @param max
	 *            target maximum value of the normalized interval.
	 */
	public static < T extends NumericType< T > & Comparable< T > > void normalize( final IterableInterval< T > iterable, final T min, final T max )
	{
		final Cursor< T > cursor = iterable.cursor();
		final T currentMax = cursor.next().copy();
		final T currentMin = currentMax.copy();
		for ( final T t : iterable )
			if ( t.compareTo( currentMax ) > 0 )
				currentMax.set( t );
			else if ( t.compareTo( currentMin ) < 0 )
				currentMin.set( t );

		final T scale = max.copy();
		scale.sub( min );
		final T currentScale = currentMax; // no need to currentMax.copy(). We
											// don't use currentMax after this.
		currentScale.sub( currentMin );

		for ( final T t : iterable )
		{
			t.sub( currentMin );
			t.mul( scale );
			t.div( currentScale );
			t.add( min );
		}
	}
}
