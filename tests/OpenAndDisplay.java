package tests;

import javax.media.j3d.Transform3D;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.algorithm.gauss.GaussianConvolution;
import mpicbg.imglib.algorithm.gauss.GaussianConvolutionRealType;
import mpicbg.imglib.algorithm.transformation.ImageTransform;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.display.imagej.ImgLib2Display;
import mpicbg.imglib.interpolation.nearestneighbor.NearestNeighborInterpolatorFactory;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.type.numeric.real.FloatTypeValue;
import mpicbg.models.AffineModel3D;
import ij.ImageJ;

public class OpenAndDisplay
{
	public void testPerformance( final Img<FloatType> img )
	{
		final Cursor<FloatType> cursor = img.cursor();
		
		FloatType f = FloatType.create();
		FloatType f2 = null;
		
		for ( int i = 0; i < 100; ++i )
		{
			cursor.fwd();
			
			cursor.get().add( f );
			f.set( cursor.get() );

			cursor.fwd();
			f2 = cursor.get();
		}
				
		long start = System.nanoTime();
		
		for ( int i = 0; i < 10000000; ++i )
		{
			f.add( f2 ); 
			f2.sub( f );
			f.add( f );
			f.add( f );
		}
		
		long duration = System.nanoTime() - start;
		
		System.out.println( duration );
	}
	
	
	public static void main( String[] args )
	{
		new ImageJ();
		
		Img<FloatType> img = LOCI.openLOCIFloatType( "D:/Temp/Truman/MoreTiles/73.tif",  new ArrayContainerFactory<FloatType>() );
		
		new OpenAndDisplay().testPerformance( img );
		
		System.exit( 0 );
		
		ImgLib2Display.copyToImagePlus( img, new int[] {2, 0, 1} ).show();
		
		// compute a gaussian convolution with sigma = 3
		GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( img, new OutOfBoundsConstantValueFactory<FloatType, Img<FloatType>>(), 2 );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( gauss.getErrorMessage() );
			return;
		}
		
		ImgLib2Display.copyToImagePlus( gauss.getResult() ).show();

		// Affine Model rotates 45 around X, 45 around Z and scales by 0.5		
		AffineModel3D model = new AffineModel3D();
		model.set( 0.35355338f, -0.35355338f, 0.0f, 0.0f, 0.25f, 0.25f, -0.35355338f, 0.0f, 0.25f, 0.25f, 0.35355338f, 0.0f );

		OutOfBoundsFactory<FloatType, Img<FloatType>> oob = new OutOfBoundsConstantValueFactory<FloatType, Img<FloatType>>( new FloatTypeValue( 255 ) );
		NearestNeighborInterpolatorFactory< FloatType > interpolatorFactory = new NearestNeighborInterpolatorFactory< FloatType >( oob );
		ImageTransform< FloatType > transform = new ImageTransform<FloatType>( img, model, interpolatorFactory );
		
		if ( !transform.checkInput() || !transform.process() )
		{
			System.out.println( transform.getErrorMessage() );
			return;
		}
		
		ImgLib2Display.copyToImagePlus( transform.getResult() ).show();

	}
	
	public static AffineModel3D getAffineModel3D( Transform3D transform )
	{
		final float[] m = new float[16];
		transform.get( m );

		AffineModel3D model = new AffineModel3D();
		model.set( m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11] );

		return model;
	}	
}
