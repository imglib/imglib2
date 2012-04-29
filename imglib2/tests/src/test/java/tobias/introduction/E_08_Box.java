package tobias.introduction;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Draw a filled square using RandomAccess (generic).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_08_Box
{
	public static < T extends Type< T > >
			void box( final RandomAccessibleInterval< T > img, final long offset, final long size, final T value )
	{
		final long startx = img.min( 0 ) + offset;
		final long starty = img.min( 1 ) + offset;

		final RandomAccess< T > a = img.randomAccess();
		a.setPosition( starty, 1 );
		for ( long y = 0; y < size; ++y )
		{
			a.setPosition( startx, 0 );
			for ( long x = 0; x < size; ++x )
			{
				a.get().set( value );
				a.fwd( 0 );
			}
			a.fwd( 1 );
		}
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = create( new FloatType() );
		box( img, 20, 100, new FloatType( 1.0f ) );

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
