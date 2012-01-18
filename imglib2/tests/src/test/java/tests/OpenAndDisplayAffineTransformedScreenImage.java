package tests;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RandomAccessibleOnRealRandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.realtransform.Affine;
import net.imglib2.realtransform.AffineTransform;
import net.imglib2.realtransform.RealTransformRealRandomAccessible;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;
import Jama.Matrix;

public class OpenAndDisplayAffineTransformedScreenImage
{	
	final static public void main( final String[] args )
		throws ImgIOException
	{
		new ImageJ();
		
		final ImgOpener io = new ImgOpener();
		final RandomAccessibleInterval< UnsignedShortType > img = io.openImg( "/home/saalfeld/phd/light-microscopy/presentation/saalfeld-05-05-4-DPX-05_L1_Sum.lsm", new ArrayImgFactory< UnsignedShortType >(), new UnsignedShortType());
		
		final double[][] matrix = new double[][]{
				{ 0.5, 0, 0, img.dimension( 0 ) * 0.25 },
				{ 0, 0.5, 0, img.dimension( 1 ) * 0.25 },
				{ 0, 0, 0.5, 0 }
		};
		final AffineTransform affine = new AffineTransform( new Matrix( matrix ) );
		//final AffineTransform3D affine = new AffineTransform3D();
		//affine.set( matrix[ 0 ][ 0 ], matrix[ 0 ][ 1 ], matrix[ 0 ][ 2 ], matrix[ 0 ][ 3 ], matrix[ 1 ][ 0 ], matrix[ 1 ][ 1 ], matrix[ 1 ][ 2 ], matrix[ 1 ][ 3 ], matrix[ 2 ][ 0 ], matrix[ 2 ][ 1 ], matrix[ 2 ][ 2 ], matrix[ 2 ][ 3 ] );
		
		final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType> > interpolatorFactory = new NearestNeighborInterpolatorFactory< UnsignedShortType >();
		//final InterpolatorFactory< UnsignedShortType, RandomAccessible< UnsignedShortType> > interpolatorFactory = new NLinearInterpolatorFactory< UnsignedShortType >();
		
		final RandomAccessible< UnsignedShortType > extendedImg = Views.extendValue( img, new UnsignedShortType() );
		final RandomAccessible< UnsignedShortType > channel = Views.hyperSlice( extendedImg, 2, 0 );
		final Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > > interpolant = new Interpolant< UnsignedShortType, RandomAccessible< UnsignedShortType > >( channel, interpolatorFactory );
		final RealTransformRealRandomAccessible< UnsignedShortType, Affine > mapping = new RealTransformRealRandomAccessible< UnsignedShortType, Affine >( interpolant, affine );
		final RandomAccessible< UnsignedShortType > transformedPixels = new RandomAccessibleOnRealRandomAccessible< UnsignedShortType >( mapping );
		
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )img.dimension( 0 ), ( int )img.dimension( 1 ) );
		final XYProjector< UnsignedShortType, ARGBType > projector = new XYProjector< UnsignedShortType, ARGBType >( transformedPixels, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
//		final XYProjector< UnsignedShortType, ARGBType > projector = new XYProjector< UnsignedShortType, ARGBType >( channel, screenImage, new RealARGBConverter< UnsignedShortType >( 0, 4095 ) );
		
		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		
		for ( int k = 0; k < 3; ++k ) 
			for ( int i = 0; i < img.dimension( 3 ) * 2; ++i )
			{
				projector.setPosition( i, 2 );
				projector.map();
				final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
				imp.setProcessor( cpa );
				imp.updateAndDraw();
			}
		
		projector.map();
		
		projector.setPosition( img.dimension( 3 ) / 2, 2 );
		projector.map();
		final ColorProcessor cpa = new ColorProcessor( screenImage.image() );
		imp.setProcessor( cpa );
		imp.updateAndDraw();
	}
}
