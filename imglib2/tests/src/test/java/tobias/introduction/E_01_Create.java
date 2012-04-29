package tobias.introduction;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Create an ArrayImg< UnsignedByteType >
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_01_Create
{
	public static void main( final String[] args )
	{
		final ImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		final long[] dimensions = new long[] { 400, 300 };
//		final long[] dimensions = new long[] { 400, 300, 100 };
//		final long[] dimensions = new long[] { 20, 20, 20, 20 };
		final UnsignedByteType type = new UnsignedByteType();
		final Img< UnsignedByteType > img = factory.create( dimensions, type );

		ImageJFunctions.show( img );
	}
}
