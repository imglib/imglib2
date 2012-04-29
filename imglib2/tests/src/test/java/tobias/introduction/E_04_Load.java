package tobias.introduction;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Open image file.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_04_Load
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final ImgOpener opener = new ImgOpener();
		final ImgFactory< UnsignedByteType > factory = new ArrayImgFactory< UnsignedByteType >();
		final UnsignedByteType type = new UnsignedByteType();
		final Img< UnsignedByteType > img = opener.openImg( "src/test/java/resources/leaf-400x300.tif", factory, type );

		ImageJFunctions.show( img );
	}
}
