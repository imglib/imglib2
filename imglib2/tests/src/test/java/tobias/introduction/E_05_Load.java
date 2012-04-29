package tobias.introduction;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Open image file (generic).
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_05_Load
{
	public static < T extends RealType< T > & NativeType< T > >
			Img< T > open( final String filename, final T type ) throws ImgIOException
	{
		final ImgOpener opener = new ImgOpener();
		final ImgFactory< T > factory = new ArrayImgFactory< T >();
		final Img< T > img = opener.openImg( filename, factory, type );
		return img;
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = open( "src/test/java/resources/leaf-400x300.tif", new FloatType() );
//		final Img< UnsignedByteType > img = open( "src/test/java/resources/leaf-400x300.tif", new UnsignedByteType() );

		ImageJFunctions.show( img );
	}
}
