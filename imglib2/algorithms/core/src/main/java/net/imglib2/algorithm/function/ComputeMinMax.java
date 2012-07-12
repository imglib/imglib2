package net.imglib2.algorithm.function;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class ComputeMinMax
{
	/**
	 * Computes minimal and maximal value in a given interval
	 * 
	 * @param interval
	 * @param min
	 * @param max
	 */
	final public static < T extends Comparable< T > & Type< T > > void computeMinMax( final RandomAccessibleInterval< T > interval, final T min, final T max )
	{ 
		final IterableInterval< T > iterable = Views.iterable( interval );
		
		// initialize min and max with value of the first element
		final T firstElement = iterable.firstElement();
		min.set( firstElement );
		max.set( firstElement );
		
		for ( final T value : iterable )
		{
			if ( value.compareTo( min ) < 0 )
				min.set( value );
			else if ( value.compareTo( max ) > 0 )
				max.set( value );
		}
		
	}
}
