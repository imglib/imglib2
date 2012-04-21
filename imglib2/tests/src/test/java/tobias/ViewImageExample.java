package tobias;

import net.imglib2.converter.Converter;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class ViewImageExample
{

	public static void main( final String[] args ) throws ImgIOException
	{
		final String filename = "src/test/java/resources/preikestolen.tif";
		final Img< UnsignedByteType > img = new ImgOpener().openImg( filename, new ArrayImgFactory< UnsignedByteType >(), new UnsignedByteType() );
		final Converter< UnsignedByteType, ARGBType > converter = new RealARGBConverter< UnsignedByteType >( 0, 255 );
		new RandomAccessible2DViewer< UnsignedByteType >( img, converter );
	}
}
