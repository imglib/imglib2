package mpicbg.imglib.algorithm;

import java.util.ArrayList;

import mpicbg.imglib.algorithm.fft.Bandpass;
import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.algorithm.fft.InverseFourierTransform;
import mpicbg.imglib.algorithm.fft.PhaseCorrelation;
import mpicbg.imglib.algorithm.fft.PhaseCorrelationPeak;
import mpicbg.imglib.algorithm.fft.FourierTransform.PreProcessing;
import mpicbg.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpicbg.imglib.algorithm.floydsteinberg.FloydSteinbergDithering;
import mpicbg.imglib.algorithm.gauss.DownSample;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ComplexTypePhaseSpectrumDisplay;
import mpicbg.imglib.image.display.ComplexTypePowerSpectrumDisplay;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyPeriodicFactory;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.complex.ComplexFloatType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

public class AlgorithmPerformance
{
	public AlgorithmPerformance( final ContainerFactory containerFactory, final int numDimensions )
	{
		boolean show = false;
		
		final Image<FloatType> image = FourierConvolution.createGaussianKernel( containerFactory, 30, numDimensions );
		image.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();
		
		System.out.println( "Created Image: " + image );
		
		final int numAlgorithms = 7;
		
		for ( int i = 0; i < 20; ++i )
		{
			double overAllProcessingTime = 0;
			
			overAllProcessingTime += testDithering( image, show );
			overAllProcessingTime += testBandpass( image, show );		
			overAllProcessingTime += testPhaseCorrelation( image, image, show );			
			overAllProcessingTime += testCanvas( image, 3f, 0.25f, 10f, show );
			overAllProcessingTime += testFFT( image, show );
			overAllProcessingTime += testFFTConvolution( image, show );
			overAllProcessingTime += testDownSampling( image, show );
			
			System.out.println( "Processing Time: " + overAllProcessingTime/numAlgorithms );
		}
	}
	
	public static void main( String[] args )
	{
		new AlgorithmPerformance( new ArrayContainerFactory(), 2 );
	}
	
	public static <T extends RealType<T>> double testFFTConvolution( final Image<T> img, boolean show )
	{
		final Image<FloatType> kernel = FourierConvolution.createGaussianKernel( new ArrayContainerFactory(), 30 + System.currentTimeMillis()%10/10.0, img.getNumDimensions() );		
		final FourierConvolution<T, FloatType> fftConvol = new FourierConvolution<T, FloatType>( img, kernel );
		
		if ( fftConvol.checkInput() && fftConvol.process() )
		{
			Image<T> convolved = fftConvol.getResult();
			kernel.close();
			
			if ( show )
			{
				convolved.getDisplay().setMinMax();
				ImageJFunctions.copyToImagePlus( convolved ).show();
			}
			
			return fftConvol.getProcessingTime();						
		}
		else
		{
			System.err.println( fftConvol.getErrorMessage() );
			return -1;
		}
	}

	public static <T extends RealType<T>> double testDownSampling( final Image<T> img, boolean show )
	{
		final DownSample<T> downSample = new DownSample<T>( img, 0.5f );
		
		if ( !downSample.checkInput() || !downSample.process() )
		{
			System.out.println( "DownSampling failed: " + downSample.getErrorMessage() );
			return -1;
		}
		
		final Image<T> downSampledImage = downSample.getResult();
		
		if ( show )
		{
			downSampledImage.getDisplay().setMinMax();
			ImageJFunctions.displayAsVirtualStack( downSampledImage ).show();
		}
		
		return -1;
	}

	public <S extends RealType<S>, T extends RealType<T>> double testPhaseCorrelation( final Image<S> image1, final Image<T> image2, boolean show )	
	{
		PhaseCorrelation<S, T> pc = new PhaseCorrelation<S, T>( image1, image2 );
		pc.setInvestigateNumPeaks( 10 );
		
		if ( !pc.checkInput() || !pc.process() )
		{
			System.out.println( "Phase Correlation failed: " + pc.getErrorMessage() );
			return -1;
		}
		
		final ArrayList<PhaseCorrelationPeak> peaks = pc.getAllShifts();
		
		if ( show )
			for ( PhaseCorrelationPeak peak : peaks )
			System.out.println( Util.printCoordinates( peak.getPosition() ) + " " + peak.getCrossCorrelationPeak() );
		
		return pc.getProcessingTime();
	}
	
	public static <T extends RealType<T>> double testDithering( final Image<T> image, boolean show )
	{
		final FloydSteinbergDithering<T> dither = new FloydSteinbergDithering<T>( image );
		
		if ( dither.checkInput() && dither.process() )
		{
			final Image<BitType> dithered = dither.getResult();
			
			if ( show )
				ImageJFunctions.copyToImagePlus( dithered ).show();
			
			return dither.getProcessingTime();
		}
		else
		{
			System.out.println("An error occured during dithering: " + dither.getErrorMessage() );
			return -1;
		}
			
	}

	public static <T extends RealType<T>> double testBandpass( final Image<T> img, boolean show )
	{
		// init fft
		final FourierTransform<T, ComplexFloatType> fft = new FourierTransform<T, ComplexFloatType>( img, new ComplexFloatType() );
		
		double processingTime = 0;
		
		// check parameters and compute fft
		if ( fft.checkInput() && fft.process() )
		{
			// get result image
			final Image<ComplexFloatType> fftImage = fft.getResult();
			processingTime += fft.getProcessingTime();
	
			// compute bandpass in place
			final Bandpass<ComplexFloatType> bandpass = new Bandpass<ComplexFloatType>( fftImage, 40, 50 );
			bandpass.setInPlace( true );
			bandpass.process();
			processingTime += bandpass.getProcessingTime();

			// show power spectrum
			fftImage.setDisplay( new ComplexTypePowerSpectrumDisplay<ComplexFloatType>( fftImage ) );
			
			if ( show )
			{
				fftImage.getDisplay().setMinMax();
				ImageJFunctions.copyToImagePlus( fftImage ).show();
			}
			
			// init inverse fft
			final InverseFourierTransform<T, ComplexFloatType> invFFT = new InverseFourierTransform<T, ComplexFloatType>( fftImage, fft );
						
			// comute inverse fft and display result
			if ( invFFT.checkInput() && invFFT.process())
			{
				final Image<T> inv = invFFT.getResult();
								
				if ( show )
				{
					inv.getDisplay().setMinMax();
					inv.setName( "Inverse FFT" );
					ImageJFunctions.copyToImagePlus( inv ).show();
				}
			}
			processingTime += invFFT.getProcessingTime();
		}
		else
		{
			System.out.println( fft.getErrorMessage() );			
		}
		
		return processingTime;
	}
	
	public double testFFT( final Image<FloatType> img, boolean show )
	{
		final FourierTransform<FloatType, ComplexFloatType> fft = new FourierTransform<FloatType, ComplexFloatType>( img, new ComplexFloatType() );
		fft.setNumThreads( 1 );
		fft.setPreProcessing( PreProcessing.NONE );
		fft.setRearrangement( Rearrangement.UNCHANGED );
		
		double processingTime = 0;
		
		final Image<ComplexFloatType> fftImage;
		
		if ( fft.checkInput() && fft.process() )
		{
			fftImage = fft.getResult();
			
			processingTime += fft.getProcessingTime();
		
			if ( show )
			{
				fftImage.getDisplay().setMinMax();
				ImageJFunctions.displayAsVirtualStack( fftImage ).show();			
	
				fftImage.setDisplay( new ComplexTypePhaseSpectrumDisplay<ComplexFloatType>( fftImage ) );
				fftImage.getDisplay().setMinMax();
				ImageJFunctions.displayAsVirtualStack( fftImage ).show();
			}			
		}
		else
		{
			System.out.println( fft.getErrorMessage() );
			fftImage = null;
			return -1;			
		}
				
		final InverseFourierTransform<FloatType, ComplexFloatType> invfft = new InverseFourierTransform<FloatType, ComplexFloatType>( fftImage, fft );
		//invfft.setCropBackToOriginalSize( false );
		
		if ( invfft.checkInput() && invfft.process() )
		{
			final Image<FloatType> inverseFFT = invfft.getResult();
			
			if ( show )
			{
				inverseFFT.getDisplay().setMinMax();
				ImageJFunctions.copyToImagePlus( inverseFFT ).show();
			}
			processingTime += invfft.getProcessingTime();
			return processingTime;
		}
		else
		{
			System.out.println( fft.getErrorMessage() );
			return -1;						
		}
	}
	
	public static <T extends RealType<T>> double testCanvas( final Image<T> img, final float factor, final float fadingRange, final float exponent, boolean show )
	{
		final int[] newSize = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
			newSize[ d ] = Util.round( img.getDimension( d ) * factor );
		
		final CanvasImage<T> canvas = new CanvasImage<T>( img, newSize, new OutOfBoundsStrategyPeriodicFactory<T>() );
		
		if ( canvas.checkInput() && canvas.process() )
		{
			Image<T> out = canvas.getResult();
			
			if ( show )
			{
				out.getDisplay().setMinMax();			
				ImageJFunctions.displayAsVirtualStack( out ).show();
			}
			
			return canvas.getProcessingTime();
		}
		else
		{
			System.out.println( canvas.getErrorMessage() );
			return -1;
		}
	}
	
}
