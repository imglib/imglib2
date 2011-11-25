package net.imglib2.algorithm;

import java.util.ArrayList;

import net.imglib2.algorithm.fft.Bandpass;
import net.imglib2.algorithm.fft.FourierConvolution;
import net.imglib2.algorithm.fft.FourierTransform;
import net.imglib2.algorithm.fft.InverseFourierTransform;
import net.imglib2.algorithm.fft.PhaseCorrelation;
import net.imglib2.algorithm.fft.PhaseCorrelationPeak;
import net.imglib2.algorithm.fft.FourierTransform.PreProcessing;
import net.imglib2.algorithm.fft.FourierTransform.Rearrangement;
import net.imglib2.algorithm.floydsteinberg.FloydSteinbergDithering;
import net.imglib2.algorithm.gauss.DownSample;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

public class AlgorithmPerformance
{
	public AlgorithmPerformance( final ImgFactory<FloatType> containerFactory, final int numDimensions )
	{
		boolean show = false;
		
		final Img<FloatType> image = FourierConvolution.createGaussianKernel( containerFactory, 30, numDimensions );
		ImageJFunctions.show( image );
		
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
		new AlgorithmPerformance( new ArrayImgFactory<FloatType>(), 2 );
	}
	
	public static <T extends RealType<T>> double testFFTConvolution( final Img<T> img, boolean show )
	{
		final Img<FloatType> kernel = FourierConvolution.createGaussianKernel( new ArrayImgFactory(), 30 + System.currentTimeMillis()%10/10.0, img.getNumDimensions() );		
		final FourierConvolution<T, FloatType> fftConvol = new FourierConvolution<T, FloatType>( img, kernel );
		
		if ( fftConvol.checkInput() && fftConvol.process() )
		{
			Img<T> convolved = fftConvol.getResult();
			
			if ( show )
			{
				ImageJFunctions.show( convolved );
			}
			
			return fftConvol.getProcessingTime();						
		}
		else
		{
			System.err.println( fftConvol.getErrorMessage() );
			return -1;
		}
	}

	public static <T extends RealType<T>> double testDownSampling( final Img<T> img, boolean show )
	{
		final DownSample<T> downSample = new DownSample<T>( img, 0.5f );
		
		if ( !downSample.checkInput() || !downSample.process() )
		{
			System.out.println( "DownSampling failed: " + downSample.getErrorMessage() );
			return -1;
		}
		
		final Img<T> downSampledImage = downSample.getResult();
		
		if ( show )
		{
			ImageJFunctions.show( downSampledImage );
		}
		
		return -1;
	}

	public <S extends RealType<S>, T extends RealType<T>> double testPhaseCorrelation( final Img<S> image1, final Img<T> image2, boolean show )	
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
	
	public static <T extends RealType<T>> double testDithering( final Img<T> image, boolean show )
	{
		final FloydSteinbergDithering<T> dither = new FloydSteinbergDithering<T>( image );
		
		if ( dither.checkInput() && dither.process() )
		{
			final Img<BitType> dithered = dither.getResult();
			
			if ( show )
				ImageJFunctions.show( dithered );
			
			return dither.getProcessingTime();
		}
		else
		{
			System.out.println("An error occured during dithering: " + dither.getErrorMessage() );
			return -1;
		}
			
	}

	public static <T extends RealType<T>> double testBandpass( final Img<T> img, boolean show )
	{
		// init fft
		final FourierTransform<T, ComplexFloatType> fft = new FourierTransform<T, ComplexFloatType>( img, new ComplexFloatType() );
		
		double processingTime = 0;
		
		// check parameters and compute fft
		if ( fft.checkInput() && fft.process() )
		{
			// get result image
			final Img<ComplexFloatType> fftImage = fft.getResult();
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
				ImageJFunctions.show( fftImage );
			}
			
			// init inverse fft
			final InverseFourierTransform<T, ComplexFloatType> invFFT = new InverseFourierTransform<T, ComplexFloatType>( fftImage, fft );
						
			// comute inverse fft and display result
			if ( invFFT.checkInput() && invFFT.process())
			{
				final Img<T> inv = invFFT.getResult();
								
				if ( show )
				{
					ImageJFunctions.show( inv, "Inverse FFT" );
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
	
	public double testFFT( final Img<FloatType> img, boolean show )
	{
		final FourierTransform<FloatType, ComplexFloatType> fft = new FourierTransform<FloatType, ComplexFloatType>( img, new ComplexFloatType() );
		fft.setNumThreads( 1 );
		fft.setPreProcessing( PreProcessing.NONE );
		fft.setRearrangement( Rearrangement.UNCHANGED );
		
		double processingTime = 0;
		
		final Img<ComplexFloatType> fftImage;
		
		if ( fft.checkInput() && fft.process() )
		{
			fftImage = fft.getResult();
			
			processingTime += fft.getProcessingTime();
		
			if ( show )
			{
				ImageJFunctions.show( fftImage );			
	
				//fftImage.setDisplay( new ComplexTypePhaseSpectrumDisplay<ComplexFloatType>( fftImage ) );
				ImageJFunctions.show( fftImage );
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
			final Img<FloatType> inverseFFT = invfft.getResult();
			
			if ( show )
			{
				ImageJFunctions.show( inverseFFT );
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
	
	public static <T extends RealType<T>> double testCanvas( final Img<T> img, final float factor, final float fadingRange, final float exponent, boolean show )
	{
		final int[] newSize = new int[ img.numDimensions() ];
		
		for ( int d = 0; d < img.numDimensions(); ++d )
			newSize[ d ] = Util.round( img.dimension( d ) * factor );
		
		final CanvasImg<T> canvas = new CanvasImg<T>( img, newSize, new OutOfBoundsPeriodicFactory<T, Img<T>>() );
		
		if ( canvas.checkInput() && canvas.process() )
		{
			Img<T> out = canvas.getResult();
			
			if ( show )
			{
				ImageJFunctions.show( out );
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
