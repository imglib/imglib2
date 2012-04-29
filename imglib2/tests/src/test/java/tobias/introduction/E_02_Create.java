package tobias.introduction;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Create an ArrayImg< T > in a generic method.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_02_Create
{
	public static < T extends NativeType< T > >
			Img< T > create( final T type )
	{
		final ImgFactory< T > factory = new ArrayImgFactory< T >();
		final long[] dimensions = new long[] { 400, 300 };
		final Img< T > img = factory.create( dimensions, type );
		return img;
	}

	public static void main( final String[] args )
	{
		final Img< UnsignedByteType > img = create( new UnsignedByteType() );
//		final Img< FloatType > img = create( new FloatType() );
//		final Img< IntType > img = create( new IntType() );

		ImageJFunctions.show( img );
	}
}
