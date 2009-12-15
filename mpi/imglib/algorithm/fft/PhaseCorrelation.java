package mpi.imglib.algorithm.fft;

import java.util.concurrent.atomic.AtomicInteger;

import mpi.imglib.algorithm.Algorithm;
import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.algorithm.MultiThreaded;
import mpi.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.display.imagej.ImageJFunctions;
import mpi.imglib.multithreading.SimpleMultiThreading;
import mpi.imglib.type.NumericType;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

public class PhaseCorrelation<T extends NumericType<T>, S extends NumericType<S>> implements MultiThreaded, Algorithm, Benchmark
{
	final int numDimensions;
	boolean computeFFTinParalell = true;
	Image<T> image1;
	Image<S> image2;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public PhaseCorrelation( final Image<T> image1, final Image<S> image2 )
	{
		this.image1 = image1;
		this.image2 = image2;

		this.numDimensions = image1.getNumDimensions();
	
		setNumThreads();
		processingTime = -1;
	}
	
	public void setComputeFFTinParalell( final boolean computeFFTinParalell ) { this.computeFFTinParalell = computeFFTinParalell; }
	
	public boolean getComputeFFTinParalell() { return computeFFTinParalell; }
	
	@Override
	public boolean process()
	{
		// get the maximal dimensions of both images
		final int[] maxDim = getMaxDim( image1, image2 );
		
		// compute fourier transforms
		final FourierTransform<T> fft1 = new FourierTransform<T>( image1 );
		final FourierTransform<S> fft2 = new FourierTransform<S>( image2 );
		fft1.setRelativeImageExtension( 0.1f );
		fft2.setRelativeImageExtension( 0.1f );
		fft1.setRelativeFadeOutDistance( 0.1f );
		fft2.setRelativeFadeOutDistance( 0.1f );		
		fft1.setRearrangement( Rearrangement.Unchanged );
		fft2.setRearrangement( Rearrangement.Unchanged );
		fft1.setExtendedOriginalImageSize( maxDim );
		fft2.setExtendedOriginalImageSize( maxDim );
				
		if ( !fft1.checkInput() )
		{
			errorMessage = "Fourier Transform of first image failed: " + fft1.getErrorMessage(); 
			return false;
		}
			
		if ( !fft2.checkInput() )
		{
			errorMessage = "Fourier Transform of second image failed: " + fft2.getErrorMessage(); 
			return false;
		}
		
		// compute the fft's
		if ( !computeFFT( fft1, fft2 ) )
		{
			errorMessage = "Fourier Transform of failed: fft1=" + fft1.getErrorMessage() + " fft2=" + fft2.getErrorMessage();
			return false;
		}
		
		final Image<ComplexFloatType> fftImage1 = fft1.getResult();
		final Image<ComplexFloatType> fftImage2 = fft2.getResult();
		
		// normalize and compute complex conjugate of fftImage2
		normalizeAndConjugate( fftImage1, fftImage2 );
		
		// multiply fftImage1 and fftImage2
		multiplyInPlace( fftImage1, fftImage2 );
		
		// invert fftImage1
		final InverseFourierTransform<FloatType> invFFT = new InverseFourierTransform<FloatType>( fftImage1, fft1, new FloatType() );
		invFFT.setInPlaceTransform( true );
		
		if ( !invFFT.checkInput() || !invFFT.process() )
		{
			errorMessage = "Inverse Fourier Transform of failed: " + invFFT.getErrorMessage();
			return false;			
		}
		
		fftImage1.close();
		fftImage2.close();
		
		final Image<FloatType> invPCM = invFFT.getResult();

		invPCM.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( invPCM ).show();
		
		return true;
	}
	
	protected static int[] getMaxDim( final Image<?> image1, final Image<?> image2 )
	{
		final int[] maxDim = new int[ image1.getNumDimensions() ];
		
		for ( int d = 0; d < image1.getNumDimensions(); ++d )
			maxDim[ d ] = Math.max( image1.getDimension( d ), image2.getDimension( d ) );
		
		return maxDim;
	}
	
	protected void multiplyInPlace( final Image<ComplexFloatType> fftImage1, final Image<ComplexFloatType> fftImage2 )
	{
		final Cursor<ComplexFloatType> cursor1 = fftImage1.createCursor();
		final Cursor<ComplexFloatType> cursor2 = fftImage2.createCursor();
		
		System.out.println( MathLib.printCoordinates( fftImage1.getDimensions() ));
		System.out.println( MathLib.printCoordinates( fftImage2.getDimensions() ));
		
		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();
			
			cursor1.getType().mul( cursor2.getType() );
		}
				
		cursor1.close();
		cursor2.close();
	}
	
	protected void normalizeAndConjugate( final Image<ComplexFloatType> fftImage1, final Image<ComplexFloatType> fftImage2 )
	{
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( Math.min( 2, numThreads ) );
		final int numThreads = threads.length;
			
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement(); 
					
					if ( numThreads == 1 )
					{
						normalizeComplexImage( fftImage1 );
						normalizeAndConjugateComplexImage( fftImage2 );
					}
					else
					{
						if ( myNumber == 0 )
							normalizeComplexImage( fftImage1 );
						else
							normalizeAndConjugateComplexImage( fftImage2 );
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );		
	}
	
	private static final void normalizeComplexImage( final Image<ComplexFloatType> fftImage )
	{
		final Cursor<ComplexFloatType> cursor = fftImage.createCursor();
		final float threshold = 1E-5f;
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.getType().normalizeLength( threshold );
		}
				
		cursor.close();		
	}
	
	private static final void normalizeAndConjugateComplexImage( final Image<ComplexFloatType> fftImage )
	{
		final Cursor<ComplexFloatType> cursor = fftImage.createCursor();
		final float threshold = 1E-5f;
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			
			cursor.getType().normalizeLength( threshold );
			cursor.getType().complexConjugate();
		}
				
		cursor.close();		
	}
		
	protected boolean computeFFT( final FourierTransform fft1, final FourierTransform fft2 )
	{
		// use two threads in paralell if wanted
		final int minThreads = computeFFTinParalell ? 2 : 1;
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( Math.min( minThreads, numThreads ) );
		final int numThreads = threads.length;
		
		final boolean[] sucess = new boolean[ 2 ];
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement(); 
					
					if ( numThreads == 1 )
					{
						fft1.setNumThreads( getNumThreads() );
						fft2.setNumThreads( getNumThreads() );
						sucess[ 0 ] = fft1.process();
						sucess[ 1 ] = fft2.process();
					}
					else
					{
						if ( myNumber == 0 )
						{
							System.out.println( "fft1" );
							fft1.setNumThreads( getNumThreads() / 2 );
							sucess[ 0 ] = fft1.process();							
						}
						else
						{
							System.out.println( "fft2" );
							fft2.setNumThreads( getNumThreads() / 2 );
							sucess[ 1 ] = fft2.process();														
						}
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );
		
		return sucess[ 0 ] && sucess[ 1 ]; 
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		
		if ( image1 == null || image2 == null)
		{
			errorMessage = "One of the input images is null";
			return false;
		}
		
		if ( image1.getNumDimensions() != image2.getNumDimensions() )
		{
			errorMessage = "Dimensionality of images is not the same";
			return false;
		}
		
		return true;
	}

	@Override
	public String getErrorMessage()  { return errorMessage; }
	
}
