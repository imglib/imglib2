package tobias.introduction;

import ij.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Draw a filled sphere using RandomAccess (generic, n-dimensional).
 * Derived from {@link HyperSphereCursor}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_10_Sphere
{
	public static < T extends Type< T > >
			void sphere( final RandomAccessibleInterval< T > img, final long offset, final long radius, final T value )
	{
		final int n = img.numDimensions();

		final long[] r = new long[ n ]; // the current radius in each dimension we are at
		final long[] s = new long[ n ]; // the remaining number of steps in each dimension we still have to go
		r[ n - 1 ] = radius;
		s[ n - 1 ] = 1 + 2 * radius;

		final long[] center = new long[ n ];
		for ( int d = 0; d < n; ++d )
			center[ d ] = img.min( 0 ) + offset;

		final RandomAccess< T > a = img.randomAccess();
		a.setPosition( center );
		a.move( -radius, n - 1 );
		while( true )
		{
			a.get().set( value );
			int d;
			for ( d = 0; d < n; ++d )
			{
				if ( --s[ d ] >= 0 )
				{
					a.fwd( d );
					break;
				}
				else
				{
					if ( d == n - 1 )
						return;
					s[ d ] = r[ d ] = 0;
					a.setPosition( center[ d ], d );
				}
			}

			if ( d > 0 )
			{
				final int e = d - 1;
				final long rd = r[ d ];
				final long pd = rd - s[ d ];

				final long rad = (long)( Math.sqrt( rd * rd - pd * pd ) );
				s[ e ] = 2 * rad;
				r[ e ] = rad;

				a.setPosition( center[ e ] - rad, e );
			}
		}
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = create( new FloatType() );
		sphere( img, 100, 80, new FloatType( 1.0f ) );

		new ImageJ();
		ImageJFunctions.show( img );
	}

	public static < T extends NativeType< T > >
			Img< T > create( final T type )
	{
		final ImgFactory< T > factory = new CellImgFactory< T >( 100 );
		final long[] dimensions = new long[] { 400, 300, 300 };
		final Img< T > img = factory.create( dimensions, type );
		return img;
	}
}
