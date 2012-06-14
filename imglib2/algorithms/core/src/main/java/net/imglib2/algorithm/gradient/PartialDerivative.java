package net.imglib2.algorithm.gradient;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class PartialDerivative
{
	// nice version...
	/**
	 * Compute the partial derivative of source in a particular dimension.
	 *
	 * @param source
	 *            source image, has to provide valid data in the interval of the
	 *            gradient image plus a one pixel border in dimension.
	 * @param gradient
	 *            output image
	 * @param dimension
	 *            along which dimension the partial derivatives are computed
	 */
	public static < T extends NumericType< T > > void gradientCentralDifference2( final RandomAccessible< T > source, final RandomAccessibleInterval< T > gradient, final int dimension )
	{
		final Cursor< T > front = Views.flatIterable( Views.interval( source, Intervals.translate( gradient, 1, dimension ) ) ).cursor();
		final Cursor< T > back = Views.flatIterable( Views.interval( source, Intervals.translate( gradient, -1, dimension ) ) ).cursor();

		for( final T t : Views.flatIterable( gradient ) )
		{
			t.set( front.next() );
			t.sub( back.next() );
			t.mul( 0.5 );
		}
	}

	// fast version
	/**
	 * Compute the partial derivative of source in a particular dimension.
	 *
	 * @param source
	 *            source image, has to provide valid data in the interval of the
	 *            gradient image plus a one pixel border in dimension.
	 * @param gradient
	 *            output image
	 * @param dimension
	 *            along which dimension the partial derivatives are computed
	 */
	public static < T extends NumericType< T > > void gradientCentralDifference( final RandomAccessible< T > source, final RandomAccessibleInterval< T > gradient, final int dimension )
	{
		final int n = gradient.numDimensions();

		final long[] min = new long[ n ];
		gradient.min( min );
		final long[] max = new long[ n ];
		gradient.max( max );
		final long[] shiftback = new long[ n ];
		for ( int d = 0; d < n; ++d )
			shiftback[ d ] = min[ d ] - max[ d ];

		final RandomAccess< T > result = gradient.randomAccess();
		final RandomAccess< T > back = source.randomAccess( Intervals.translate( gradient, 1, dimension ) );
		final RandomAccess< T > front = source.randomAccess( Intervals.translate( gradient, -1, dimension ) );

		result.setPosition( min );
		back.setPosition( min );
		back.bck( dimension );
		front.setPosition( min );
		front.fwd( dimension );

		final long max0 = max[ 0 ];
		while ( true )
		{
			// process pixel
			final T t = result.get();
			t.set( front.get() );
			t.sub( back.get() );
			t.mul( 0.5 );

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most iterations
			if ( result.getLongPosition( 0 ) == max0 )
			{
				if ( n == 1 )
					return;
				result.move( shiftback[ 0 ], 0 );
				back.move( shiftback[ 0 ], 0 );
				front.move( shiftback[ 0 ], 0 );
				// now check the remaining dimensions
				for ( int d = 1; d < n; ++d )
					if ( result.getLongPosition( d ) == max[ d ] )
					{
						result.move( shiftback[ d ], d );
						back.move( shiftback[ d ], d );
						front.move( shiftback[ d ], d );
						if ( d == n - 1 )
							return;
					}
					else
					{
						result.fwd( d );
						back.fwd( d );
						front.fwd( d );
						break;
					}
			}
			else
			{
				result.fwd( 0 );
				back.fwd( 0 );
				front.fwd( 0 );
			}
		}
	}
}
