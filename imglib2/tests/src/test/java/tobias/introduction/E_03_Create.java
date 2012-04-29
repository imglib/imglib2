package tobias.introduction;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Create a CellImg< T > by using a different factory.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_03_Create
{
	public static < T extends NativeType< T > >
			Img< T > create( final T type )
	{
		// final ImgFactory< T > factory = new ArrayImgFactory< T >();
		final ImgFactory< T > factory = new CellImgFactory< T >( 30 );
		final long[] dimensions = new long[] { 400, 300 };
		final Img< T > img = factory.create( dimensions, type );
		return img;
	}

	public static void main( final String[] args )
	{
		final Img< FloatType > img = create( new FloatType() );

		ImageJFunctions.show( img );
	}
}
