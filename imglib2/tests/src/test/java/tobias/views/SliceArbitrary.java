package tobias.views;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.realtransform.RealViews;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class SliceArbitrary
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = new ImgOpener().openImg( "src/test/java/resources/flybrain-32bit.tif", new ArrayImgFactory< FloatType >(), new FloatType() );
		ImageJFunctions.show( img );

//		final RandomAccessible< FloatType > extended =
//				Views.extendValue( img, new FloatType( 50 ) );
//
//		final RealRandomAccessible< FloatType > interpolated =
//				Views.interpolate( extended, new NLinearInterpolatorFactory< FloatType >() );
//
//		final AffineTransform3D transform = new AffineTransform3D();
//		transform.rotate( 0, 0.5 );
//		final RandomAccessible< FloatType > rotated = RealViews.constantAffine( interpolated, transform );
//		ImageJFunctions.show( Views.interval( rotated, FinalInterval.createMinSize( 0, -25, -1, 256, 256, 175 ) ) );

//		final RandomAccessible< FloatType > rotatedSliced =
//				Views.hyperSlice( rotated, 2, 100 );
//		final RandomAccessibleInterval< FloatType > rotatedSlicedCropped =
//				Views.interval( rotatedSliced, FinalInterval.createMinSize( 0, 0, 256, 256 ) );
//		ImageJFunctions.show( rotatedSlicedCropped );
	}
}
