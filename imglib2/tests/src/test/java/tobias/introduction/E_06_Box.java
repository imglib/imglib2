package tobias.introduction;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Draw a filled square using RandomAccess.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_06_Box
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = create( new FloatType() );

		final RandomAccess< FloatType > a = img.randomAccess();
		for ( long y = 20; y < 120; ++y )
			for ( long x = 20; x < 120; ++x )
			{
				a.setPosition( new long[] { x, y } );
//				a.setPosition( x, 0 );
//				a.setPosition( y, 1 );
				final FloatType t = a.get();
				t.set( 1.0f );
			}

		ImageJFunctions.show( img );
	}

	public static < T extends NativeType< T > >
			Img< T > create( final T type )
	{
		final ImgFactory< T > factory = new CellImgFactory< T >( 100 );
		final long[] dimensions = new long[] { 400, 300 };
		final Img< T > img = factory.create( dimensions, type );
		return img;
	}
}
