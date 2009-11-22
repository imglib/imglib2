package mpi.imglib.algorithm.fft;

import java.util.concurrent.atomic.AtomicInteger;

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;
import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.algorithm.CanvasImage;
import mpi.imglib.algorithm.MultiThreadedOutputAlgorithm;
import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.container.array.FakeArray;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpi.imglib.cursor.array.ArrayLocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.ImageFactory;
import mpi.imglib.multithreading.SimpleMultiThreading;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.outside.OutsideStrategyMirrorExpWindowingFactory;
import mpi.imglib.outside.OutsideStrategyMirrorFactory;
import mpi.imglib.outside.OutsideStrategyValueFactory;
import mpi.imglib.type.label.FakeType;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

public class FourierTransform implements MultiThreadedOutputAlgorithm<ComplexFloatType>, Benchmark
{
	public static enum PreProcessing { None, ExtendMirror, ExtendMirrorFading, UseGivenOutsideStrategy }
	public static enum Rearrangement { RearrangeQuadrants, Unchanged }
	public static enum FFTOptimization { OptimizeSpeed, OptimizeMemory }
	
	final Image<FloatType> img;
	final int numDimensions;
	Image<ComplexFloatType> fft;
	
	PreProcessing preProcessing;
	Rearrangement rearrangement;
	FFTOptimization fftOptimization;	
	float relativeImageExtension;
	float relativeFadeOutDistance;
	OutsideStrategyFactory<FloatType> strategy;
	
	String errorMessage = "";
	int numThreads;
	long processingTime;
	
	public FourierTransform( final Image<FloatType> image, final PreProcessing preProcessing, final Rearrangement rearrangement,
							 final FFTOptimization fftOptimization, final float relativeImageExtension, final float relativeFadeOutDistance )
	{
		this.img = image;
		this.numDimensions = img.getNumDimensions();
		
		this.preProcessing = preProcessing;
		this.rearrangement = rearrangement;
		this.fftOptimization = fftOptimization;
		this.relativeImageExtension = relativeImageExtension;
		this.relativeFadeOutDistance = relativeFadeOutDistance;
		this.strategy = null;
		
		this.processingTime = -1;
		
		setNumThreads();
	}
	
	public void setPreProcessing( final PreProcessing preProcessing ) { this.preProcessing = preProcessing; }
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setFFTOptimization( final FFTOptimization fftOptimization ) { this.fftOptimization = fftOptimization; }
	public void setRelativeImageExtension( final float extensionRatio ) { this.relativeImageExtension = extensionRatio; } 
	public void setRelativeFadeOutDistance( final float relativeFadeOutDistance ) { this.relativeFadeOutDistance = relativeFadeOutDistance; }
	public void setCustomOutsideStrategy( final OutsideStrategyFactory<FloatType> strategy ) { this.strategy = strategy; } 
	
	public PreProcessing getPreProcessing() { return preProcessing; }
	public Rearrangement getRearrangement() { return rearrangement; }
	public FFTOptimization getFFOptimization() { return fftOptimization; }
	public float getRelativeImageExtension() { return relativeImageExtension; } 
	public float getRelativeFadeOutDistance() { return relativeFadeOutDistance; }
	public OutsideStrategyFactory<FloatType> getCustomOutsideStrategy() { return strategy; } 

	public FourierTransform( final Image<FloatType> image ) 
	{ 
		this ( image, PreProcessing.ExtendMirrorFading, Rearrangement.RearrangeQuadrants, 
		       FFTOptimization.OptimizeSpeed, 0.25f, 0.25f ); 
	}

	@Override
	public boolean process() 
	{
		final CanvasImage<FloatType> canvas;
				
		switch ( preProcessing )
		{
			case UseGivenOutsideStrategy:
			{
				if ( strategy == null )
				{
					errorMessage = "Custom OutsideStrategyFactory is null, cannot use custom strategy";
					return false;
				}
				
				canvas = new CanvasImage<FloatType>( img, 
						 getZeroPaddingSize( getExtendedImageSize( img, relativeImageExtension ), fftOptimization), 
						 strategy );
				
				break;
			}
			case ExtendMirror:
			{				
				canvas = new CanvasImage<FloatType>( img, 
						 getZeroPaddingSize( getExtendedImageSize( img, relativeImageExtension ), fftOptimization), 
						 new OutsideStrategyMirrorFactory<FloatType>() );
				break;
				
			}
			
			case ExtendMirrorFading:
			{
				canvas = new CanvasImage<FloatType>( img, 
						 getZeroPaddingSize( getExtendedImageSize( img, relativeImageExtension ), fftOptimization), 
						 new OutsideStrategyMirrorExpWindowingFactory<FloatType>( relativeFadeOutDistance ) );
				break;
			}
			
			default: // or NONE
			{
				canvas = new CanvasImage<FloatType>( img, 
						 getZeroPaddingSize( img.getDimensions(), fftOptimization), 
						 new OutsideStrategyValueFactory<FloatType>( new FloatType( 0 ) ) );
				break;
			}
		}
		
		// zero pad and maybe extend the image using the given strategy
		if ( !canvas.checkInput() && canvas.process() )			
		{
			errorMessage = canvas.getErrorMessage();
			return false;
		}
		
		// get the result
		final Image<FloatType> tmp = canvas.getResult();
		
		// perform FFT on the temporary image
		fft = computeFFT( tmp, getNumThreads(), false );
		
		if ( fft == null )
			return false;
		
		// close temporary image
		tmp.close();
		
		return true;
	}
	
	
	protected Image<ComplexFloatType> computeFFT( final Image<FloatType> img, final int numThreads, final boolean scale )
	{
		final int complexSize[] = new int[ numDimensions ];
		
		// the size of the first dimension is changed
		complexSize[ 0 ] = ( img.getDimension( 0 )  / 2 + 1);
		
		for ( int d = 0; d < numDimensions; ++d )
			complexSize[ d ] = img.getDimension( d ) / 2;
		
		final ImageFactory<ComplexFloatType> imgFactory = new ImageFactory<ComplexFloatType>( new ComplexFloatType(), img.getStorageFactory() );
		final Image<ComplexFloatType> fftImage = imgFactory.createImage( complexSize );
		
		// not enough memory
		if ( fft == null )
			return null;
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					
					final int complexSize = fft.getDimension( 0 );
					final int size = fft.getDimension( 0 ) * 2;
		
					final float[] tempIn = new float[ size ];
					float[] tempOut;
					final FftReal fft = new FftReal( size );
					
					final LocalizableByDimCursor<FloatType> cursor = img.createLocalizableByDimCursor(); 
					final LocalizableByDimCursor<ComplexFloatType> cursorOut = fftImage.createLocalizableByDimCursor(); 
					
					if ( img.getNumDimensions() > 1 )
					{
						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = img.getDimension( d );
						
						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );

						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{							
								cursorDim.getPosition( fakeSize );

								tmp[ 0 ] = 0;
								for ( int d = 1; d < numDimensions; ++d )
									tmp[ d ] = fakeSize[ d - 1 ];							
								
								cursor.setPosition( tmp );
								
								for ( int x = 0; x < size; ++x )
								{
									tempIn[ x ] = cursor.getType().get();
									cursor.fwd( 0 );
								}
								
								fft.realToComplex( -1, tempIn, tempOut );
								
								if (scale)
									fft.scale( size, tempOut );
	
								cursorOut.setPosition( tmp );
								
								for ( int x = 0; x < complexSize; x++ )
								{
									cursorOut.getType().set( tempOut[x] * 2, tempOut[x] * 2 + 1 );
									cursorOut.fwd( 0 );
								}
							}
						}
						
						cursorOut.close();
						cursor.close();
						cursorDim.close();						
					}
					else
					{
						// multithreading makes no sense here
						if ( myNumber == 0)
						{
							cursor.setPosition( 0, 0 );
							
							for ( int x = 0; x < size; ++x )
							{
								tempIn[ x ] = cursor.getType().get();
								cursor.fwd( 0 );
							}
							
							fft.realToComplex( -1, tempIn, tempOut );
							
							if (scale)
								fft.scale( size, tempOut );
	
							cursorOut.setPosition( 0, 0 );
							
							for ( int x = 0; x < complexSize; x++ )
							{
								cursorOut.getType().set( tempOut[x] * 2, tempOut[x] * 2 + 1 );
								cursorOut.fwd( 0 );
							}
						}
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin(threads);
		
		/*
		final int height = values.height;
		final int width = values.width;
		final int depth = values.depth;
		final int complexWidth = (width / 2 + 1) * 2;

		final FloatArray3D result = new FloatArray3D(complexWidth, height, depth);

		//do fft's in x direction
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = newThreads();
		final int numThreads = threads.length;

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					int myNumber = ai.getAndIncrement();

					float[] tempIn = new float[width];
					float[] tempOut;
					FftReal fft = new FftReal(width);

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
							for (int y = 0; y < height; y++)
							{
								tempOut = new float[complexWidth];

								for (int x = 0; x < width; x++)
									tempIn[x] = values.get(x, y, z);

								fft.realToComplex( -1, tempIn, tempOut);

								if (scale)
									fft.scale(width, tempOut);

								for (int x = 0; x < complexWidth; x++)
									result.set(tempOut[x], x, y, z);
							}
				}
			});
		startAndJoin(threads);

		//do fft's in y direction
		ai.set(0);
		threads = newThreads();

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					float[] tempIn = new float[height * 2];
					float[] tempOut;
					FftComplex fftc = new FftComplex(height);

					int myNumber = ai.getAndIncrement();

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
							for (int x = 0; x < complexWidth / 2; x++)
							{
								tempOut = new float[height * 2];

								for (int y = 0; y < height; y++)
								{
									tempIn[y * 2] = result.get(x * 2, y, z);
									tempIn[y * 2 + 1] = result.get(x * 2 + 1, y, z);
								}

								fftc.complexToComplex( -1, tempIn, tempOut);

								for (int y = 0; y < height; y++)
								{
									result.set(tempOut[y * 2], x * 2, y, z);
									result.set(tempOut[y * 2 + 1], x * 2 + 1, y, z);
								}
							}
				}
			});

		startAndJoin(threads);
		 */
		return null;
	}
	
	protected int[] getExtendedImageSize( final Image<?> img, final float extensionRatio )
	{
		final int[] extendedSize = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
		{
			extendedSize[ d ] = MathLib.round( img.getDimension( d ) * extensionRatio );

			// add an even number so that both sides extend equally
			if ( extendedSize[ d ] % 2 != 0) 
				extendedSize[ d ]++;			
		}		
		
		return extendedSize;
	}
	
	protected int[] getZeroPaddingSize( final int[] imageSize, final FFTOptimization fftOptimization )
	{
		final int[] fftSize = new int[ imageSize.length ];
		
		// the first dimension is real to complex
		fftSize[ 0 ] = FftReal.nfftFast( imageSize[ 0 ] );
		
		// all the other dimensions complex to complex
		for ( int d = 1; d < fftSize.length; ++d )
		{
			if ( fftOptimization == FFTOptimization.OptimizeSpeed )
				fftSize[ d ] = FftComplex.nfftSmall( imageSize[ d ] );
			else
				fftSize[ d ] = FftComplex.nfftFast( imageSize[ d ] );
		}
		
		return fftSize;
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
	public Image<ComplexFloatType> getResult() { return fft; }

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( img == null )
		{
			errorMessage = "Input image is null";
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getErrorMessage()  { return errorMessage; }
	
}
