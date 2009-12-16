/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpi.imglib;

import ij.ImagePlus;
import ij.io.Opener;

import java.awt.Rectangle;
import java.util.ArrayList;

import javax.media.j3d.Transform3D;
import javax.vecmath.Vector3d;

import mpi.imglib.algorithm.CanvasImage;
import mpi.imglib.algorithm.fft.FFTFunctions;
import mpi.imglib.algorithm.fft.FourierConvolution;
import mpi.imglib.algorithm.fft.FourierTransform;
import mpi.imglib.algorithm.fft.InverseFourierTransform;
import mpi.imglib.algorithm.fft.PhaseCorrelation;
import mpi.imglib.algorithm.fft.FourierTransform.PreProcessing;
import mpi.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpi.imglib.algorithm.gauss.DownSample;
import mpi.imglib.algorithm.gauss.GaussianConvolution;
import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.algorithm.transformation.AffineTransform;
import mpi.imglib.algorithm.transformation.ImageTransform;
import mpi.imglib.container.ContainerFactory;
import mpi.imglib.container.array.Array3D;
import mpi.imglib.container.array.ArrayContainerFactory;
import mpi.imglib.container.array.BitArray;
import mpi.imglib.container.cube.CubeContainerFactory;
import mpi.imglib.container.imageplus.ImagePlusContainerFactory;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.cursor.special.LocalNeighborhoodCursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.ImageFactory;
import mpi.imglib.image.ImagePlusAdapter;
import mpi.imglib.image.display.ComplexFloatTypePhaseSpectrumDisplay;
import mpi.imglib.image.display.imagej.ImageJFunctions;
import mpi.imglib.image.display.imagej.InverseTransformDescription;
import mpi.imglib.interpolation.Interpolator;
import mpi.imglib.interpolation.InterpolatorFactory;
//import mpi.imglib.interpolation.InverseTransformableInterpolator;
import mpi.imglib.interpolation.LinearInterpolatorFactory;
import mpi.imglib.interpolation.NearestNeighborInterpolatorFactory;
import mpi.imglib.io.LOCI;
import mpi.imglib.multithreading.SimpleMultiThreading;
import mpi.imglib.outside.OutsideStrategyPeriodicFactory;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.outside.OutsideStrategyMirrorExpWindowingFactory;
import mpi.imglib.outside.OutsideStrategyMirrorFactory;
import mpi.imglib.outside.OutsideStrategyValueFactory;
import mpi.imglib.type.ComparableType;
import mpi.imglib.type.NumericType;
import mpi.imglib.type.Type;
import mpi.imglib.type.logic.BooleanType;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;
import mpi.imglib.type.numeric.ShortType;
import mpicbg.models.AffineModel2D;
import mpicbg.models.AffineModel3D;

public class Test
{
	protected void initImageJWindow()
	{
		System.getProperties().setProperty("plugins.dir", "D:/Program Files/ImageJ");
		final String params[] = {"-ijpath " + "D:/Program Files/ImageJ"};
		ij.ImageJ.main(params);		
	}
	
	public <T extends NumericType<T>> Test()
	{
		System.out.println( "Starting..." );
		
		// open imageJ window
		initImageJWindow();				
				
		//Image<?> image = LOCI.openLOCI("D:/Temp/", "73.tif", new ArrayContainerFactory());
		//Image<FloatType> image = LOCI.openLOCIFloatType("D:/Temp/Truman/MoreTiles/73.tif", new ArrayContainerFactory());
		//Image<FloatType> image = LOCI.openLOCIFloatType("D:/Documents and Settings/Stephan/Desktop/ls-1 f5-01-1 3500x-1.tif", new ArrayContainerFactory());
		//Image<FloatType> image = LOCI.openLOCIFloatType("F:/Stephan/OldMonster/Stephan/Stitching/Truman/73.tif", new ArrayContainerFactory());				
			
		Image<FloatType> image = LOCI.openLOCIFloatType("D:/Documents and Settings/Stephan/My Documents/My Pictures/rockface_odd-1.tif", new ArrayContainerFactory());

		image.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();

		/*
		
		Image<FloatType> image1 = LOCI.openLOCIFloatType("D:/Documents and Settings/Stephan/My Documents/My Pictures/rockface_odd-1.tif", new ArrayContainerFactory());
		Image<FloatType> image2 = LOCI.openLOCIFloatType("D:/Documents and Settings/Stephan/My Documents/My Pictures/rockface_odd-1020.tif", new ArrayContainerFactory());
		
		
		image1.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image1 ).show();

		image2.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image2 ).show();
		*/
		
		testPhaseCorrelation( image );
		//testPhaseCorrelation( image1, image2 );
		
		//ImageFactory<FloatType> f = new ImageFactory<FloatType>( new FloatType(), new ArrayContainerFactory() );
		//Image<FloatType> image = f.createImage( new int[]{ 24, 24 } );		
		//fillUp( image );
		
		//testCanvas( image, 3f, 0.25f, 10f );
		//testFFT( image );
		//testFFTConvolution( image );				
		//testFFTConvolutionLoop();		
		//testFFTConvolutionAlg( image );
		//testDownSampling( image );

		
		if ( true )
			return;

		OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyValueFactory<FloatType>( new FloatType(0) );
		//OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyMirrorFactory<FloatType>();
		
		InterpolatorFactory<FloatType> interpolatorFactory = new LinearInterpolatorFactory<FloatType>( outsideStrategyFactory );
		//InterpolatorFactory<FloatType> interpolatorFactory = new NearestNeighborInterpolatorFactory<FloatType>( outsideStrategyFactory );

		//testVirtualDisplay( image, interpolatorFactory );
		//genericProcessing( image );		
		
		if ( true )
			return;
		
		ImagePlus imp = new Opener().openImage("D:/Temp/Truman/TilesForStitching/L031/TopLeft.tif");
		
		Image< T > img = ImagePlusAdapter.wrap( imp );
		Image<FloatType> img2 = ImagePlusAdapter.wrapFloat( imp );
		
		
		ImageJFunctions.displayAsVirtualStack( img, ImageJFunctions.COLOR_RGB, new int[]{ 0, 1, 2} ).show();
		
		genericProcessing( img );
	}
	
	public void testPhaseCorrelation( final Image<FloatType> image )
	{
		final int[] size = new int[]{ 2*image.getDimension( 0 )/3 + 1, 2*image.getDimension( 1 )/3 };
		
		for ( int x = 0; x < 10; ++x )
			for ( int y = 0; y < 10; ++y )
			{
				final int[] offset = new int[]{ x, y };
				final CanvasImage<FloatType> cropping = new CanvasImage<FloatType>( image, size, offset, new OutsideStrategyValueFactory<FloatType>()  );
				
				cropping.process();
				
				final Image<FloatType> crop = cropping.getResult();

				//crop.getDisplay().setMinMax();
				//crop.setName("crop");
				//ImageJFunctions.copyToImagePlus( crop ).show();

				final PhaseCorrelation<FloatType, FloatType> pcm = new PhaseCorrelation<FloatType, FloatType>( image, crop );
				pcm.process();
				
				final int[] shift = pcm.getShift().getPosition();
				
				if ( shift[ 0 ] == offset[ 0 ] && shift[ 1 ] == offset[ 1 ])
					System.out.println( "Works - " + MathLib.printCoordinates( offset ));
				else
					System.out.println( "FAIL - " + MathLib.printCoordinates( offset ) + " != " + MathLib.printCoordinates( shift ) );
				
				//SimpleMultiThreading.threadHaltUnClean();
			}
	}
	
	public void testPhaseCorrelation( final Image<FloatType> image1, final Image<FloatType> image2 )	
	{
		PhaseCorrelation<FloatType, FloatType> pc = new PhaseCorrelation<FloatType, FloatType>( image1, image2 );
		
		if ( !pc.checkInput() || !pc.process() )
		{
			System.out.println( "Phase Correlation failed: " + pc.getErrorMessage() );
			return;
		}
	}
	
	public <T extends NumericType<T>> void testDownSampling( final Image<T> img )
	{
		final DownSample<T> downSample = new DownSample<T>( img, 0.5f );
		
		if ( !downSample.checkInput() || !downSample.process() )
		{
			System.out.println( "DownSampling failed: " + downSample.getErrorMessage() );
			return;
		}
		
		final Image<T> downSampledImage = downSample.getResult();
		
		downSampledImage.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( downSampledImage ).show();		
	}
	
	public void testFFTConvolutionAlg( Image<FloatType> img )
	{
		img.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( img ).show();
		
		Image<FloatType> kernel = FourierConvolution.getGaussianKernel( img.getImageFactory(), 20, img.getNumDimensions() );
		
		kernel.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( kernel ).show();		
		
		FourierConvolution<FloatType, FloatType> fftConv = new FourierConvolution<FloatType, FloatType>( img, kernel );
		fftConv.setNumThreads();
		
		if ( !fftConv.checkInput() || !fftConv.process() )
		{
			System.out.println( "error: " + fftConv.getErrorMessage() );
			return;
		}
		
		System.out.println( "FFT Convolution: " + fftConv.getProcessingTime()  + " ms"  );
		
		Image<FloatType> conv = fftConv.getResult();
		
		conv.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( conv ).show();
		
		final GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( img, new OutsideStrategyMirrorFactory<FloatType>(), 20 );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Gaussian Convolution failed: " + gauss.getErrorMessage() );
			return;
		}

		System.out.println( "Gauss Convolution: " + gauss.getProcessingTime() + " ms"  );
		
		Image<FloatType> res = gauss.getResult();
		res.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( res ).show();
		
	}
	
	public void testFFTConvolutionLoop()
	{
		for ( int i = 3; i < 100; ++i )
		{
			ImageFactory<FloatType> f = new ImageFactory<FloatType>( new FloatType(), new ArrayContainerFactory() );
			Image<FloatType> image = f.createImage( new int[]{ i, i } );		
			fillUp( image );
			
			Image<FloatType> conv = testFFTConvolution( image );
			
			final Cursor<FloatType> c1 = image.createCursor();
			final Cursor<FloatType> c2 = conv.createCursor();
			
			double error = 0;
			
			while ( c1.hasNext() )
			{
				c1.fwd();
				c2.fwd();
				
				error += Math.pow( c1.getType().get() - c2.getType().get(), 2 );
			}
			
			error /= image.getNumPixels();			
			System.out.println( i + ": " + error );
			
			image.close();
			conv.close();
		}
	}
	
	public Image<FloatType> testFFTConvolution( final Image<FloatType> img )
	{
		final FourierTransform fft1 = new FourierTransform( img );
		fft1.setPreProcessing( PreProcessing.None );
		fft1.setRearrangement( Rearrangement.Unchanged );

		if ( !fft1.checkInput() || !fft1.process() )
		{
			System.out.println( "FFT of image failed: " + fft1.getErrorMessage() );
			return null;			
		}
		
		final Image<ComplexFloatType> imgFFT = fft1.getResult();

		final int kernelDim[] = imgFFT.getDimensions();
		kernelDim[ 0 ]= ( imgFFT.getDimension( 0 ) - 1 ) * 2;
		
		final Image<FloatType> kernelTemplate = img.createNewImage( kernelDim );			
		final int[] center = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
			center[ d ] = 0;				
		
		final LocalizableByDimCursor<FloatType> c = kernelTemplate.createLocalizableByDimCursor();
		c.setPosition( center );
		c.getType().setOne();
		c.close();
		/*
		final GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( kernelTemplate, new OutsideStrategyValueFactory<FloatType>(), 2.0f );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Gaussian Convolution failed: " + gauss.getErrorMessage() );
			return;
		}
		
		kernelTemplate.close();
		*/
		
		final Image<FloatType> kernel = kernelTemplate;//gauss.getResult();
		kernel.setName( "Convolution Kernel" );
		
		//kernel.getDisplay().setMinMax();
		//ImageJFunctions.displayAsVirtualStack( kernel ).show();
		
		final FourierTransform fft2 = new FourierTransform( kernel );
		
		fft2.setPreProcessing( fft1.getPreProcessing() );		
		fft2.setRearrangement( fft1.getRearrangement() );
		
		if ( !fft2.checkInput() || !fft2.process() )
		{
			System.out.println( "FFT of kernel failed: " + fft2.getErrorMessage() );
			return null;			
		}
		
		kernel.close();

		final Image<ComplexFloatType> kernelFFT = fft2.getResult();

		imgFFT.setName( "fft of image" );
		kernelFFT.setName( "fft of kernel" );
						
		final Cursor<ComplexFloatType> cursorImgFFT = imgFFT.createCursor();
		final Cursor<ComplexFloatType> cursorKernelFFT = kernelFFT.createCursor();
		
		while ( cursorImgFFT.hasNext() )
		{
			cursorImgFFT.fwd();
			cursorKernelFFT.fwd();
			
			cursorImgFFT.getType().mul( cursorKernelFFT.getType() );
		}
		
		cursorImgFFT.close();
		cursorKernelFFT.close();
		
		final InverseFourierTransform invFFT = new InverseFourierTransform( imgFFT, fft1 );
		//invFFT.setCropBackToOriginalSize( false );

		if ( !invFFT.checkInput() || !invFFT.process() )
		{
			System.out.println( "InverseFFT of image failed: " + invFFT.getErrorMessage() );
			return null;			
		}
		
		imgFFT.close();
		kernelFFT.close();
		
		final Image<FloatType> fftConvol = invFFT.getResult();	
		//FFTFunctions.rearrangeAllQuadrants( fftConvol, 2 );
		
		//fftConvol.getDisplay().setMinMax( );
		//ImageJFunctions.displayAsVirtualStack( fftConvol ).show();
		/*		
		final CanvasImage<FloatType> canvas = new CanvasImage<FloatType>( fftConvol, fft1.getOriginalSize(), fft1.getOriginalOffset(), null );
		
		if ( !canvas.checkInput() || !canvas.process() )
		{
			System.out.println( "Cropping of convolved image failed: " + canvas.getErrorMessage() );
			return;						
		}
		
		fftConvol.close();
		
		final Image<FloatType> result = canvas.getResult();
		
		result.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( result ).show();
		*/
		
		return fftConvol;
	}
	
	public void testFFT( final Image<FloatType> img )
	{
		final FourierTransform<FloatType> fft = new FourierTransform<FloatType>( img );
		fft.setNumThreads( 2 );
		fft.setPreProcessing( PreProcessing.None );
		fft.setRearrangement( Rearrangement.Unchanged );
		
		final Image<ComplexFloatType> fftImage;
		
		if ( fft.checkInput() && fft.process() )
		{
			System.out.println( fft.getProcessingTime() );
			fftImage = fft.getResult();
		
			
			fftImage.getDisplay().setMinMax();
			ImageJFunctions.displayAsVirtualStack( fftImage ).show();			

			fftImage.setDisplay( new ComplexFloatTypePhaseSpectrumDisplay( fftImage ) );
			fftImage.getDisplay().setMinMax();
			ImageJFunctions.displayAsVirtualStack( fftImage ).show();
			
		}
		else
		{
			System.out.println( fft.getErrorMessage() );
			fftImage = null;
		}
				
		final InverseFourierTransform<FloatType> invfft = new InverseFourierTransform<FloatType>( fftImage, fft );
		//invfft.setCropBackToOriginalSize( false );
		
		if ( invfft.checkInput() && invfft.process() )
		{
			System.out.println( invfft.getProcessingTime() );
			
			final Image<FloatType> inverseFFT = invfft.getResult();
			
			inverseFFT.getDisplay().setMinMax();
			ImageJFunctions.copyToImagePlus( inverseFFT ).show();
		}
	}
	
	public <T extends NumericType<T>> void testCanvas( final Image<T> img, final float factor, final float fadingRange, final float exponent )
	{
		final int[] newSize = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
			newSize[ d ] = MathLib.round( img.getDimension( d ) * factor );
		
		//final CanvasImage<T> canvas = new CanvasImage<T>( img, newSize, new OutsideStrategyMirrorExpWindowingFactory<T>( fadingRange ) );
		//final CanvasImage<T> canvas = new CanvasImage<T>( img, newSize, new OutsideStrategyMirrorFactory<T>() );
		final CanvasImage<T> canvas = new CanvasImage<T>( img, newSize, new OutsideStrategyPeriodicFactory<T>() );
		
		if ( canvas.checkInput() && canvas.process() )
		{
			Image<T> out = canvas.getResult();
			out.getDisplay().setMinMax();
			
			System.out.println( canvas.getProcessingTime() );
			
			ImageJFunctions.displayAsVirtualStack( out ).show();
		}
		else
		{
			System.out.println( canvas.getErrorMessage() );
		}
	}
	
	public void testBinarization()
	{
		Image<FloatType> image = LOCI.openLOCIFloatType("F:/Stephan/OldMonster/Stephan/Stitching/Truman/73.tif", new ArrayContainerFactory());
		
		image.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( image ).show();
		
		ImageFactory<BooleanType> f = new ImageFactory<BooleanType>( new BooleanType(), new ArrayContainerFactory() );
		Image<BooleanType> image2 = f.createImage( image.getDimensions() );
		
		LocalizableCursor<FloatType> c1 = image.createLocalizableCursor();
		LocalizableByDimCursor<BooleanType> c2 = image2.createLocalizableByDimCursor();
		
		final double min = image.getDisplay().getMin();
		final double max = image.getDisplay().getMax();
		
		while( c1.hasNext() )
		{
			c1.fwd(); c2.setPosition( c1.getPosition() );

			if ( c1.getType().get() > (max - min) / 5 )
				c2.getType().set( true );
			else
				c2.getType().set( false );
		}
		
		
		ImageJFunctions.displayAsVirtualStack( image2 ).show();
		
		OutsideStrategyFactory<BooleanType> of = new OutsideStrategyValueFactory<BooleanType>( image2.createType() );		
		InterpolatorFactory<BooleanType> f2 = new NearestNeighborInterpolatorFactory<BooleanType>( of );
		
		imageTransform( image2, of, f2 );		
	}
	
	public void testLocalCursor()
	{
		final ImageFactory<FloatType> imageFactory = new ImageFactory<FloatType>( new FloatType(), new ArrayContainerFactory() );		
		final Image<FloatType> img = imageFactory.createImage( new int[] { 5, 5, 5 } );		

		img.getDisplay().setMinMax();		
		ImageJFunctions.displayAsVirtualStack( img ).show();
		
		final LocalizableByDimCursor<FloatType> c = img.createLocalizableByDimCursor();
		
		int i = 0;
		
		while ( c.hasNext() )
		{
			c.fwd();
			c.getType().set( i++ );
		}
		
		c.setPosition( new int[]{ 1, 1, 1} );		

		final LocalNeighborhoodCursor<FloatType> nc = c.createLocalNeighborhoodCursor();
		
		
		while ( nc.hasNext() )
		{
			nc.fwd();
			System.out.println( c );
		}	
	}
	
	
	public void testNDImageJFunctions()
	{
		ImageFactory<FloatType> imageFactory = new ImageFactory<FloatType>( new FloatType(), new CubeContainerFactory( 2 ) );		
		Image<FloatType> img = imageFactory.createImage( new int[] { 5 } );		
		Cursor<FloatType> c = img.createCursor();
		
		int i = 0;
		
		while ( c.hasNext() )
		{
			c.fwd();
			c.getType().set( i++ );
		}
		
		img.getDisplay().setMinMax();
		
		ImageJFunctions.displayAsVirtualStack( img ).show();
		ImageJFunctions.copyToImagePlus( img ).show();
		
		ImageJFunctions.saveAsTiffs( img, "D:/Documents and Settings/Stephan/Desktop/tmp", ImageJFunctions.GRAY32 );
		
		c.close();
		
	}

	public <T extends NumericType<T>> void genericProcessing( final Image<T> image )
	{
		OutsideStrategyFactory<T> outsideStrategyFactory = new OutsideStrategyValueFactory<T>( image.createType() );
		//OutsideStrategyFactory<T> outsideStrategyFactory = new OutsideStrategyMirrorFactory<T>();
		
		InterpolatorFactory<T> interpolatorFactory = new NearestNeighborInterpolatorFactory<T>( outsideStrategyFactory );
		//InterpolatorFactory<T> interpolatorFactory = new LinearInterpolatorFactory<T>( outsideStrategyFactory );
		
		gaussianConvolution( image, outsideStrategyFactory, 1.1 );
		//imageTransform( image, outsideStrategyFactory, interpolatorFactory );
		//testVirtualDisplay( image, interpolatorFactory );				
	}

	public <T extends NumericType<T>> void gaussianConvolution( final Image<T> image, final OutsideStrategyFactory<T> outsideStrategyFactory, final double sigma )
	{
		
		GaussianConvolution<T> convolve = new GaussianConvolution<T>( image, outsideStrategyFactory, sigma );
		convolve.setNumThreads( 1 );
		
		if ( convolve.checkInput() )
		{
			if ( convolve.process() )
			{
				final Image<T> gauss = convolve.getResult();				
				System.out.println( "Computation took: " + convolve.getProcessingTime()/1000 + "." + convolve.getProcessingTime()%1000  + " seconds." );

				gauss.getDisplay().setMinMax();
				ImageJFunctions.displayAsVirtualStack( gauss ).show();
			}
			else
			{
				System.out.println( convolve.getErrorMessage() );
			}
		}
		else
		{
			System.out.println( convolve.getErrorMessage() );
		}
		
		System.out.println("done.");
	}
	
	public <T extends Type<T>> void imageTransform( final Image<T> image, OutsideStrategyFactory<T> outsideStrategyFactory, InterpolatorFactory<T> interpolatorFactory )
	{
		Transform3D trans = new Transform3D();
		trans.rotZ( Math.toRadians(45) );
		
		Transform3D trans2 = new Transform3D();
		trans.setScale( new Vector3d( 1, 1, 1.544));
		
		trans.mul(trans2);
		
		AffineTransform<T> affine = new AffineTransform<T>( image, trans, interpolatorFactory );
		
		if ( !affine.checkInput() )
		{
			System.out.println( "Error: " + affine.getErrorMessage() );
		}
		else			
		{			
			for ( int i = 1; i < 10; ++i )
			{
				long start = System.currentTimeMillis();
				
				if ( affine.process() )
				{
					Image<T> t = affine.getResult();
					
					long end = System.currentTimeMillis();
					
					System.out.println( (end-start)/1000.0 );
					
					t.getDisplay().setMinMax();
					ImageJFunctions.displayAsVirtualStack( t ).show();
				}
				else
				{
					System.out.println( "Error: " + affine.getErrorMessage() );
				}
			}
		}
		System.out.println( "Done." );				
	}
	
	
	public <T extends NumericType<T>> void genericProcessing( final ImageFactory<T> imageFactory )
	{				
		//OutsideStrategyFactory<T> outsideStrategyFactory = new OutsideStrategyValueFactory<T>( imageFactory.createType() );
		OutsideStrategyFactory<T> outsideStrategyFactory = new OutsideStrategyMirrorFactory<T>();
		
		InterpolatorFactory<T> interpolatorFactory = new NearestNeighborInterpolatorFactory<T>( outsideStrategyFactory );
		//InterpolatorFactory<T> interpolatorFactory = new LinearInterpolatorFactory<T>( outsideStrategyFactory );
		
		illustrateOutsideStrategy( imageFactory, outsideStrategyFactory, 2 );
		testInterpolation( imageFactory, interpolatorFactory );
	}
		
	public <T extends NumericType<T>>void illustrateOutsideStrategy( final ImageFactory<T> imageFactory, final OutsideStrategyFactory<T> outsideFactory, final int numDimensions )
	{
		// set up the image size
		final int[] imageSize = new int[ numDimensions ];		
		for ( int i = 0; i < numDimensions; i++ )
			imageSize[ i ] = 5;
		
		// create the image
		Image<T> img = imageFactory.createImage( imageSize, numDimensions + "D Float Test image" );
		
		// fill up the image with increasing entries
		fillUp( img );
		
		// display as image plus by copying
		img.getDisplay().setMinMax();
		ImagePlus imp = ImageJFunctions.copyToImagePlus( img );		
		imp.show();

		// zoom in
		for ( int i = 0; i < 10; i++ )
			imp.getWindow().getCanvas().zoomIn(0, 0);		
		
		// create extended image with three times bigger size
		final int[] imageSizeExt = new int[ numDimensions ];
		for ( int i = 0; i < numDimensions; i++ )
			imageSizeExt[ i ] = imageSize[ i ] * 3;
		
		// create bigger image
		Image<T> imgExt = imageFactory.createImage( imageSizeExt, numDimensions + "D Extended Float Test image" );

		// create a cursor for the bigger image that gives back its position
		LocalizableCursor<T> cursor1 = imgExt.createLocalizableCursor();
		
		// create a cursor for the smaller image that can move anywhere, even outside
		LocalizableByDimCursor<T> cursor2 = img.createLocalizableByDimCursor( outsideFactory );

		// get the variables associated with the cursors
		T output = cursor1.getType();
		T input = cursor2.getType(); 
		
		// iterate over bigger image
		while ( cursor1.hasNext() )
		{
			// move forward
			cursor1.fwd();
			
			// move to position
			for ( int d = 0; d < numDimensions; d++ )
				cursor2.setPosition( cursor1.getPosition( d ) - img.getDimension( d ), d );

			// set value in output image
			output.set( input );
		}
		
		// close cursors
		cursor1.close();
		cursor2.close();

		// display output image
		imgExt.getDisplay().setMinMax();
		ImagePlus impExt = ImageJFunctions.copyToImagePlus( imgExt );
		impExt.show();

		// zoom in 
		for ( int i = 0; i < 10; i++ )
			impExt.getWindow().getCanvas().zoomIn(0, 0);
		
		// set roi to the original image
		impExt.setRoi( new Rectangle(5,5,5,5) );
	}
	
	public <T extends Type<T>>void testVirtualDisplay( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory )
	{
		Transform3D trans1 = new Transform3D();
		trans1.rotX( Math.toRadians(0) );
		
		Transform3D tmp = new Transform3D();
		tmp.rotZ( Math.toRadians(45) );
		
		trans1.mul(tmp);

		Transform3D trans2 = new Transform3D();
		trans2.rotY( Math.toRadians(-45) );
		
		InverseTransformDescription<T> i1 = new InverseTransformDescription<T>( MathLib.getAffineModel3D(trans1), interpolatorFactory, img ); 
		
		ArrayList<InverseTransformDescription<T>> list = new ArrayList<InverseTransformDescription<T>>();
		list.add( i1 );
		//list.add( i2 );
		
		ImageJFunctions.displayAsVirtualStack( list, ImageJFunctions.COLOR_RGB, new int[]{0,1,2}, new int[3] ).show();
		
		System.out.println( "Done." );
		
	}
	
	public <T extends NumericType<T>> void testInterpolation( final ImageFactory<T> imageFactory, InterpolatorFactory<T> interpolatorFactory )
	{
		final Image<T> img = imageFactory.createImage(new int[]{5, 5}, "2D Float Test image" );
		final float scale = 4f;
		final float rotX = (float)Math.toRadians( 10 );
		final float rotY = (float)Math.toRadians( 20 );
		final float rotZ = (float)Math.toRadians( 30 );

		// fill up the image with increasing entries
		fillUp( img );
		
		// display as image plus by copying
		img.getDisplay().setMinMax();
		ImagePlus imp = ImageJFunctions.copyToImagePlus( img );		
		imp.show();

		// zoom in
		for ( int i = 0; i < 10; i++ )
			imp.getWindow().getCanvas().zoomIn(0, 0);		
						
		AffineModel2D model = new AffineModel2D();
		model.set( scale * (float)Math.cos( rotZ ), (float)Math.sin( rotZ ), 
		           -(float)Math.sin( rotZ ),        scale * (float)Math.cos( rotZ ),  0, 0);
		
		ImageTransform<T> imgTransform = new ImageTransform<T>(img, model, interpolatorFactory );
		
		Transform3D transform = new Transform3D();
		transform.setScale( scale );
		//ImageTransform<T> imgTransform = new ImageTransform<T>(img, MathLib.getAffineModel3D( transform ), interpolatorFactory );
		//AffineTransform<FloatType> imgTransform = new AffineTransform<FloatType>(img, transform, interpolatorFactory );
		
		if ( imgTransform.checkInput() )
		{
			imgTransform.process();
			Image<T> transformed = imgTransform.getResult();
			transformed.getDisplay().setMinMax();
			ImagePlus imp2 = ImageJFunctions.copyToImagePlus( transformed );

			imp2.show();
			
			for ( int i = 0; i < 10; i++ )
				imp2.getWindow().getCanvas().zoomIn(0, 0);		
		}
		
	}
	
	public void simpleTest()
	{
		ImageFactory<FloatType> factoryFloat = new ImageFactory<FloatType>( new FloatType(), new ArrayContainerFactory() );
		//FloatTypeImageFactory factoryFloat = new FloatTypeImageFactory( new CubeContainerFactory( new int[]{10,10,10} ) );
		Image<FloatType> img = factoryFloat.createImage(new int[]{100}, "3D Float Test image" );
		
		// fillUpPattern( img );
		fillUpWithValue( img, new FloatType( 1 ) );
		
		
		img.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( img, ImageJFunctions.GRAY32 ).show();
		//img.getImageJFunctions().copyToImagePlus( ImageJFunctions.COLOR_RGB, new int[]{1,0,2} ).show();
		
		//OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyMirrorFactory<FloatType>( );
		OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyValueFactory<FloatType>( new FloatType(0) );
		//InterpolatorFactory<FloatType> interpolatorFactory = new NearestNeighborInterpolatorFactory<FloatType>( outsideStrategyFactory );
		InterpolatorFactory<FloatType> interpolatorFactory = new LinearInterpolatorFactory<FloatType>( outsideStrategyFactory );

		Transform3D trans = new Transform3D();
		trans.rotX( Math.toRadians(30) );
		
		AffineTransform<FloatType> affine = new AffineTransform<FloatType>( img, trans, interpolatorFactory );
		
		if ( !affine.checkInput() )
		{
			System.out.println( "Error: " + affine.getErrorMessage() );
		}
		else			
		{
			if ( affine.process() )
			{
				Image<FloatType> t = affine.getResult();
				t.getDisplay().setMinMax();
				ImageJFunctions.displayAsVirtualStack( t ).show();
			}
			else
			{
				System.out.println( "Error: " + affine.getErrorMessage() );
			}
		}
		
		//LocalizableByDimCursor<FloatType> cursor = img.createLocalizableByDimCursor( factory );
		
		System.out.println( "Done." );
	}
	
	public void testAffine( Image<FloatType> img )
	{
		//OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyMirrorFactory<FloatType>( );
		OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyValueFactory<FloatType>( new FloatType(0) );
		//InterpolatorFactory<FloatType> interpolatorFactory = new NearestNeighborInterpolatorFactory<FloatType>( outsideStrategyFactory );
		InterpolatorFactory<FloatType> interpolatorFactory = new LinearInterpolatorFactory<FloatType>( outsideStrategyFactory );

		Transform3D trans = new Transform3D();
		trans.rotZ( Math.toRadians(45) );
		
		AffineTransform<FloatType> affine = new AffineTransform<FloatType>( img, trans, interpolatorFactory );
		
		if ( !affine.checkInput() )
		{
			System.out.println( "Error: " + affine.getErrorMessage() );
		}
		else			
		{
			if ( affine.process() )
			{
				Image<FloatType> t = affine.getResult();
				t.getDisplay().setMinMax();
				ImageJFunctions.displayAsVirtualStack( t ).show();
			}
			else
			{
				System.out.println( "Error: " + affine.getErrorMessage() );
			}
		}
		
		//LocalizableByDimCursor<FloatType> cursor = img.createLocalizableByDimCursor( factory );
		
		System.out.println( "Done." );		
	}
	
	public void testImageTransform( Image<FloatType> img )
	{
		OutsideStrategyFactory<FloatType> outsideStrategyFactory = new OutsideStrategyValueFactory<FloatType>( new FloatType(0) );
		InterpolatorFactory<FloatType> interpolatorFactory = new LinearInterpolatorFactory<FloatType>( outsideStrategyFactory );
		
		Transform3D trans = new Transform3D();
		trans.rotY( Math.toRadians(15) );
		
		float[] m = new float[16];
		trans.get( m );
				
		AffineModel3D model = new AffineModel3D();		
		model.set( m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11] );

		//AffineModel2D model = new AffineModel2D();		
		//model.set( m[0], m[4], m[1], m[5], m[3], m[7] );
		
		System.out.println( trans );
		System.out.println( model );
		
		ImageTransform<FloatType> imageTransform = new ImageTransform<FloatType>( img, model, interpolatorFactory );
		
		if ( !imageTransform.checkInput() )
		{
			System.out.println( "Error: " + imageTransform.getErrorMessage() );
		}
		else			
		{
			if ( imageTransform.process() )
			{
				Image<FloatType> t = imageTransform.getResult();
				t.getDisplay().setMinMax();
				ImageJFunctions.displayAsVirtualStack( t ).show();
			}
			else
			{
				System.out.println( "Error: " + imageTransform.getErrorMessage() );
			}
		}
		
		//LocalizableByDimCursor<FloatType> cursor = img.createLocalizableByDimCursor( factory );
		
		System.out.println( "Done." );
		
	}
	
	
	public void simpleTestCube()
	{
		ImageFactory<FloatType> factoryFloat = new ImageFactory<FloatType>( new FloatType(), new CubeContainerFactory( new int[]{4,2,1} ) );
		Image<FloatType> imageFloat = factoryFloat.createImage(new int[]{6, 5, 4}, "3D Float Test image" );
					
		LocalizableByDimCursor<FloatType> cursor = imageFloat.createLocalizableByDimCursor();
		
		int i = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.getType().set( i++ );
			
			//System.out.println( cursor.getPosition(0) + " " + cursor.getPosition(1) + " " + cursor.getPosition(2) );
		}
		
		cursor.reset();

		int[] position = new int[ imageFloat.getNumDimensions() ];
		int[] dim = imageFloat.getDimensions();

		cursor.setPosition( position );
		
		for ( int z = 0; z < dim[2]; z++ )
		{
			for ( int y = 0; y < dim[1]; y++ )
			{
				String line = "";
				for ( int x = 0; x < dim[0]; x++ )
				{
					line += " " + cursor.getType().get();
					
					if ( x < dim[0] - 1)
						cursor.fwd( 0 );
				}

				System.out.println( line );
				
				for ( int x = 0; x < dim[0] - 1; x++ )
					cursor.bck( 0 );
				
				if ( y < dim[1] - 1)
					cursor.fwd( 1 );						
			}			

			for ( int y = 0; y < dim[1] - 1; y++ )
				cursor.bck( 1 );
			
			if ( z < dim[2] - 1)
				cursor.fwd( 2 );						
			System.out.println();
		}
		
		cursor.close();
		
	}
	
	public <T extends NumericType<T>> void fillUp( Image<T> image )
	{
		// create cursor
		final Cursor<T> c = image.createCursor();
		
		// create variable of same type and set to one
		final T type = image.createType();
		type.setOne();
				
		// iterate over image
		while ( c.hasNext() )
		{
			// move iterator forward
			c.fwd();
			
			// set cursor to the value of type
			c.getType().set( type );
			
			// increase type
			type.inc();
		}
		
		// close the cursor
		c.close();
	}	

	public <T extends NumericType<T>> void fillUpPattern( Image<T> image )
	{
		Cursor<T> cursor = image.createCursor();
		
		T value = image.createType();
		value.setZero();
		
		int count = 0;
		int direction = 1;
		
		for ( final T v : cursor )
		{
			v.set( value );
			
			if ( direction > 0 )
				value.inc();
			else
				value.dec();
			
			count++;
			
			if ( count == 255 )
			{
				direction *= -1;
				count = 0;
			}
		}

		cursor.close();
	}
	
	public <T extends Type<T>> void fillUpWithValue( Image<T> image, T value )
	{
		Cursor<T> cursor = image.createLocalizableCursor();
		
		for ( final T v : cursor )
			v.set( value );	

		cursor.close();
	}

	public <T extends NumericType<T>> Image<T> divideImage( Image<T> input, T divisor )
	{
		final Image<T> output = input.createNewImage();
		
		final Cursor<T> cursor1 = input.createCursor();
		final Cursor<T> cursor2 = output.createCursor();
		
		final T type1 = cursor1.getType();
		final T type2 = cursor2.getType();
		
		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();

			type2.set( type1 );
			type2.div( divisor );
		}
		
		return output;
	}
	
	public static void main( String[] args )
	{
		new Test();
	}
}

