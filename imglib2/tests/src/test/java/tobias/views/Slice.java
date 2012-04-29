package tobias.views;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * Slicing a 3D volume.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class Slice
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = new ImgOpener().openImg( "src/test/java/resources/flybrain-32bit.tif", new ArrayImgFactory< FloatType >(), new FloatType() );
		ImageJFunctions.show( img );

//		final RandomAccessibleInterval< FloatType > sliced =
//				Views.hyperSlice( img, 2, 20 );
//		ImageJFunctions.show( sliced );

//		final RandomAccessibleInterval< FloatType > rotated =
//				Views.zeroMin( Views.rotate( img, 2, 1 ) );
//		ImageJFunctions.show( rotated );

//		final RandomAccessibleInterval< FloatType > rotatedSliced =
//				Views.hyperSlice( rotated, 1, 20 );
//		ImageJFunctions.show( rotatedSliced );
	}
}
