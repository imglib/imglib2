package net.imglib2.ui.experimental;


import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;


public class OpenAndDisplayInterpolated
{
	public static <T extends NumericType< T > > void copyInterpolatedGeneric( RandomAccessible< T > from, IterableInterval< T > to, double[] offset, double scale, InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final int n = to.numDimensions();
		final double[] fromPosition = new double[ n ];
		Cursor< T > cursor = to.localizingCursor();
		RealRandomAccess< T > interpolator =  interpolatorFactory.create( from );
		while ( cursor.hasNext() )
		{
			final T t = cursor.next();
			for ( int d = 0; d < n; ++d )
			{
				fromPosition[ d ] = scale * cursor.getDoublePosition( d ) + offset[ d ];
			}
			interpolator.setPosition( fromPosition );
			t.set( interpolator.get() );
		}
	} 

	final static public void main( final String[] args )
	{
		//new ImageJ();
		
		ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
		Img< FloatType > img = null;
		try
		{
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/imglibworkshop/DrosophilaWing.tif", imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		Img< FloatType > interpolatedImg = imgFactory.create( new long[] {200, 200}, new FloatType () );
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )interpolatedImg.dimension( 0 ), ( int )interpolatedImg.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( interpolatedImg, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );

//		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
//		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
//		imp.show();
		
		double[] offset;
		double scale;
		InterpolatorFactory< FloatType, RandomAccessible< FloatType > > interpolatorFactory;

		offset = new double[] {50, 10};
		scale = 1.0;
		interpolatorFactory = new NLinearInterpolatorFactory< FloatType >();
		for ( int i=0; i<2000; ++i ) {
			copyInterpolatedGeneric( img, interpolatedImg, offset, scale, interpolatorFactory );
			projector.map();
//			final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
//			imp.setProcessor( cpa );
			offset[0] += 0.2;
			offset[0] += 0.04;
			scale *= 0.999;
		}

		offset = new double[] {50, 10};
		scale = 1.0;
		interpolatorFactory = new NearestNeighborInterpolatorFactory< FloatType >();
		for ( int i=0; i<2000; ++i ) {
			copyInterpolatedGeneric( img, interpolatedImg, offset, scale, interpolatorFactory );
			projector.map();
//			final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
//			imp.setProcessor( cpa );
			offset[0] += 0.2;
			offset[0] += 0.04;
			scale *= 0.999;
		}
	}
}
