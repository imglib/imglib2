package mpi.imglib.algorithm.fft;

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;
import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.algorithm.CanvasImage;
import mpi.imglib.algorithm.MultiThreadedOutputAlgorithm;
import mpi.imglib.algorithm.math.MathLib;
import mpi.imglib.image.Image;
import mpi.imglib.image.display.imagej.ImageJFunctions;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.outside.OutsideStrategyMirrorExpWindowingFactory;
import mpi.imglib.outside.OutsideStrategyMirrorFactory;
import mpi.imglib.outside.OutsideStrategyValueFactory;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

public class FourierTransform implements MultiThreadedOutputAlgorithm<ComplexFloatType>, Benchmark
{
	public static enum PreProcessing { None, ExtendMirror, ExtendMirrorFading, UseGivenOutsideStrategy }
	public static enum Rearrangement { RearrangeQuadrants, Unchanged }
	public static enum FFTOptimization { OptimizeSpeed, OptimizeMemory }
	
	final Image<FloatType> img;
	final int numDimensions;
	Image<ComplexFloatType> fftImage;
	
	PreProcessing preProcessing;
	Rearrangement rearrangement;
	FFTOptimization fftOptimization;	
	float relativeImageExtension;
	float relativeFadeOutDistance;
	int minExtension;
	OutsideStrategyFactory<FloatType> strategy;
	int[] originalSize, originalOffset; 

	String errorMessage = "";
	int numThreads;
	long processingTime;
	
	public FourierTransform( final Image<FloatType> image, final PreProcessing preProcessing, final Rearrangement rearrangement,
							 final FFTOptimization fftOptimization, final float relativeImageExtension, final float relativeFadeOutDistance,
							 final int minExtension )
	{
		this.img = image;
		this.numDimensions = img.getNumDimensions();
		
		this.preProcessing = preProcessing;
		this.rearrangement = rearrangement;
		this.fftOptimization = fftOptimization;
		this.relativeImageExtension = relativeImageExtension;
		this.relativeFadeOutDistance = relativeFadeOutDistance;
		this.minExtension = minExtension;
		this.strategy = null;
		this.originalSize = image.getDimensions();
		this.originalOffset = new int[ numDimensions ];
		
		this.processingTime = -1;		
		
		setNumThreads();
	}
	
	public void setPreProcessing( final PreProcessing preProcessing ) { this.preProcessing = preProcessing; }
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setFFTOptimization( final FFTOptimization fftOptimization ) { this.fftOptimization = fftOptimization; }
	public void setRelativeImageExtension( final float extensionRatio ) { this.relativeImageExtension = extensionRatio; } 
	public void setRelativeFadeOutDistance( final float relativeFadeOutDistance ) { this.relativeFadeOutDistance = relativeFadeOutDistance; }
	public void setCustomOutsideStrategy( final OutsideStrategyFactory<FloatType> strategy ) { this.strategy = strategy; } 
	public void setMinExtension( final int minExtension ) { this.minExtension = minExtension; }
	
	public PreProcessing getPreProcessing() { return preProcessing; }
	public Rearrangement getRearrangement() { return rearrangement; }
	public FFTOptimization getFFOptimization() { return fftOptimization; }
	public float getRelativeImageExtension() { return relativeImageExtension; } 
	public float getRelativeFadeOutDistance() { return relativeFadeOutDistance; }
	public OutsideStrategyFactory<FloatType> getCustomOutsideStrategy() { return strategy; }
	public int getMinExtension() { return minExtension; }
	public int[] getOriginalSize() { return originalSize.clone(); }
	public int[] getOriginalOffset() { return originalOffset.clone(); }

	public FourierTransform( final Image<FloatType> image ) 
	{ 
		this ( image, PreProcessing.ExtendMirrorFading, Rearrangement.RearrangeQuadrants, 
		       FFTOptimization.OptimizeSpeed, 0.25f, 0.25f, 12 ); 
	}

	public FourierTransform( final Image<FloatType> image, final Rearrangement rearrangement ) 
	{ 
		this ( image );
		setRearrangement( rearrangement );
	}

	public FourierTransform( final Image<FloatType> image, final FFTOptimization fftOptimization ) 
	{ 
		this ( image );
		setFFTOptimization( fftOptimization );
	}

	public FourierTransform( final Image<FloatType> image, final PreProcessing preProcessing ) 
	{ 
		this ( image );
		setPreProcessing( preProcessing );
	}

	public FourierTransform( final Image<FloatType> image, final OutsideStrategyFactory<FloatType> strategy ) 
	{ 
		this ( image );
		setPreProcessing( PreProcessing.UseGivenOutsideStrategy );
		setCustomOutsideStrategy( strategy );
	}
	
	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();

		// extend the original image
		final Image<FloatType> tmp = extendImage( img, preProcessing );
		
		tmp.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( tmp ).show();
		
		if ( tmp == null )
			return false;
		
		// perform FFT on the temporary image
		fftImage = FFTFunctions.computeFFT( tmp, getNumThreads(), false );
		
		// close temporary image
		tmp.close();
		
		if ( fftImage == null )
			return false;
		
		if ( rearrangement == Rearrangement.RearrangeQuadrants )
			FFTFunctions.rearrangeFFTQuadrants( fftImage, getNumThreads() );
			
        processingTime = System.currentTimeMillis() - startTime;

        return true;
	}	
	
	protected Image<FloatType> extendImage( final Image<FloatType> img, final PreProcessing preProcessing )
	{
		final CanvasImage<FloatType> canvas;
		
		switch ( preProcessing )
		{
			case UseGivenOutsideStrategy:
			{
				if ( strategy == null )
				{
					errorMessage = "Custom OutsideStrategyFactory is null, cannot use custom strategy";
					return null;
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
		if ( !(canvas.checkInput() && canvas.process()) )			
		{
			errorMessage = canvas.getErrorMessage();
			return null;
		}
		
		this.originalOffset = canvas.getOffset();
		
		return canvas.getResult();		
	}
			
	protected int[] getExtendedImageSize( final Image<?> img, final float extensionRatio )
	{
		final int[] extendedSize = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
		{
			// how much do we want to extend
			extendedSize[ d ] = MathLib.round( img.getDimension( d ) * ( 1 + extensionRatio ) ) - img.getDimension( d );
			
			if ( extendedSize[ d ] < minExtension )
				extendedSize[ d ] = minExtension;

			// add an even number so that both sides extend equally
			if ( extendedSize[ d ] % 2 != 0) 
				++extendedSize[ d ];
			
			// the new size includes the current image size
			extendedSize[ d ] += img.getDimension( d );
		}		
		
		return extendedSize;
	}
	
	protected int[] getZeroPaddingSize( final int[] imageSize, final FFTOptimization fftOptimization )
	{
		final int[] fftSize = new int[ imageSize.length ];
		
		// the first dimension is real to complex
		if ( fftOptimization == FFTOptimization.OptimizeSpeed )
			fftSize[ 0 ] = FftReal.nfftFast( imageSize[ 0 ] );
		else
			fftSize[ 0 ] = FftReal.nfftSmall( imageSize[ 0 ] );
		
		// all the other dimensions complex to complex
		for ( int d = 1; d < fftSize.length; ++d )
		{
			if ( fftOptimization == FFTOptimization.OptimizeSpeed )
				fftSize[ d ] = FftComplex.nfftFast( imageSize[ d ] );
			else
				fftSize[ d ] = FftComplex.nfftSmall( imageSize[ d ] );
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
	public Image<ComplexFloatType> getResult() { return fftImage; }

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
