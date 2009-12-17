package mpicbg.imglib.algorithm.fft;

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.outside.OutsideStrategyMirrorExpWindowingFactory;
import mpicbg.imglib.outside.OutsideStrategyMirrorFactory;
import mpicbg.imglib.outside.OutsideStrategyValueFactory;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.ComplexFloatType;

public class FourierTransform<T extends NumericType<T>> implements MultiThreaded, OutputAlgorithm<ComplexFloatType>, Benchmark
{
	public static enum PreProcessing { None, ExtendMirror, ExtendMirrorFading, UseGivenOutsideStrategy }
	public static enum Rearrangement { RearrangeQuadrants, Unchanged }
	public static enum FFTOptimization { OptimizeSpeed, OptimizeMemory }
	
	final Image<T> img;
	final int numDimensions;
	Image<ComplexFloatType> fftImage;
	
	PreProcessing preProcessing;
	Rearrangement rearrangement;
	FFTOptimization fftOptimization;	
	float relativeImageExtensionRatio;
	int[] imageExtension;
	float relativeFadeOutDistance;
	int minExtension;
	OutsideStrategyFactory<T> strategy;
	int[] originalSize, originalOffset, extendedSize, extendedZeroPaddedSize;
	
	// if you want the image to be extended more use that
	int[] inputSize = null, inputSizeOffset = null;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public FourierTransform( final Image<T> image, final PreProcessing preProcessing, final Rearrangement rearrangement,
							 final FFTOptimization fftOptimization, final float relativeImageExtension, final float relativeFadeOutDistance,
							 final int minExtension )
	{
		this.img = image;
		this.numDimensions = img.getNumDimensions();
		this.extendedSize = new int[ numDimensions ];
		this.extendedZeroPaddedSize = new int[ numDimensions ];
		this.imageExtension = new int[ numDimensions ];
			
		setPreProcessing( preProcessing );
		setRearrangement( rearrangement );
		setFFTOptimization( fftOptimization );
		setRelativeFadeOutDistance( relativeFadeOutDistance );
		setRelativeImageExtension( relativeImageExtension );
		setMinExtension( minExtension );
		
		setCustomOutsideStrategy( null );

		this.originalSize = image.getDimensions();
		this.originalOffset = new int[ numDimensions ];
		
		this.processingTime = -1;		
		
		setNumThreads();
	}
	
	public FourierTransform( final Image<T> image ) 
	{ 
		this ( image, PreProcessing.ExtendMirrorFading, Rearrangement.RearrangeQuadrants, 
		       FFTOptimization.OptimizeSpeed, 0.25f, 0.25f, 12 ); 
	}

	public FourierTransform( final Image<T> image, final Rearrangement rearrangement ) 
	{ 
		this ( image );
		setRearrangement( rearrangement );
	}

	public FourierTransform( final Image<T> image, final FFTOptimization fftOptimization ) 
	{ 
		this ( image );
		setFFTOptimization( fftOptimization );
	}

	public FourierTransform( final Image<T> image, final PreProcessing preProcessing ) 
	{ 
		this ( image );
		setPreProcessing( preProcessing );
	}

	public FourierTransform( final Image<T> image, final OutsideStrategyFactory<T> strategy ) 
	{ 
		this ( image );
		setPreProcessing( PreProcessing.UseGivenOutsideStrategy );
		setCustomOutsideStrategy( strategy );
	}
	
	public void setPreProcessing( final PreProcessing preProcessing ) { this.preProcessing = preProcessing; }
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setFFTOptimization( final FFTOptimization fftOptimization ) { this.fftOptimization = fftOptimization; }
	public void setRelativeFadeOutDistance( final float relativeFadeOutDistance ) { this.relativeFadeOutDistance = relativeFadeOutDistance; }
	public void setCustomOutsideStrategy( final OutsideStrategyFactory<T> strategy ) { this.strategy = strategy; } 
	public void setMinExtension( final int minExtension ) { this.minExtension = minExtension; }	
	public void setImageExtension( final int[] imageExtension ) { this.imageExtension = imageExtension.clone(); }
	public boolean setExtendedOriginalImageSize( final int[] inputSize )
	{
		for ( int d = 0; d < numDimensions; ++d )
			if ( inputSize[ d ] < originalSize[ d ])
			{
				errorMessage = "Cannot set extended original image size smaller than image size";
				return false;
			}

		this.inputSize = inputSize.clone();
		this.inputSizeOffset = new int[ numDimensions ]; 
		
		setRelativeImageExtension( relativeImageExtensionRatio );
		
		return true;
	}
	
	public void setRelativeImageExtension( final float extensionRatio ) 
	{ 
		this.relativeImageExtensionRatio = extensionRatio;
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
		{
			// how much do we want to extend
			if ( inputSize == null )
				imageExtension[ d ] = MathLib.round( img.getDimension( d ) * ( 1 + extensionRatio ) ) - img.getDimension( d );
			else
				imageExtension[ d ] = MathLib.round( inputSize[ d ] * ( 1 + extensionRatio ) ) - img.getDimension( d );
			
			if ( imageExtension[ d ] < minExtension )
				imageExtension[ d ] = minExtension;

			// add an even number so that both sides extend equally
			//if ( imageExtensionSum[ d ] % 2 != 0)
			//	++imageExtension[ d ];
						
			// the new size includes the current image size
			extendedSize[ d ] = imageExtension[ d ] + img.getDimension( d );
		}			
	} 

	public T getImageType() { return img.createType(); }
	public int[] getExtendedSize() { return extendedSize.clone(); }	
	public PreProcessing getPreProcessing() { return preProcessing; }
	public Rearrangement getRearrangement() { return rearrangement; }
	public FFTOptimization getFFOptimization() { return fftOptimization; }
	public float getRelativeImageExtension() { return relativeImageExtensionRatio; } 
	public int[] getImageExtension() { return imageExtension.clone(); }
	public float getRelativeFadeOutDistance() { return relativeFadeOutDistance; }
	public OutsideStrategyFactory<T> getCustomOutsideStrategy() { return strategy; }
	public int getMinExtension() { return minExtension; }
	public int[] getOriginalSize() { return originalSize.clone(); }
	public int[] getOriginalOffset() { return originalOffset.clone(); }
	public int[] getFFTInputOffset( )
	{
		if ( inputSize == null )
			return originalOffset;
		else
			return inputSizeOffset;
	}
	public int[] getFFTInputSize( )
	{
		if ( inputSize == null )
			return originalSize.clone();
		else
			return inputSize.clone();
	}
	
	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();

		//
		// perform FFT on the temporary image
		//			
		final OutsideStrategyFactory<T> outsideFactory;		
		switch ( preProcessing )
		{
			case UseGivenOutsideStrategy:
			{
				if ( strategy == null )
				{
					errorMessage = "Custom OutsideStrategyFactory is null, cannot use custom strategy";
					return false;
				}				
				extendedZeroPaddedSize = getZeroPaddingSize( getExtendedImageSize( img, imageExtension ), fftOptimization );
				outsideFactory = strategy;				
				break;
			}
			case ExtendMirror:
			{	
				extendedZeroPaddedSize = getZeroPaddingSize( getExtendedImageSize( img, imageExtension ), fftOptimization );
				outsideFactory = new OutsideStrategyMirrorFactory<T>();
				break;
				
			}			
			case ExtendMirrorFading:
			{
				extendedZeroPaddedSize = getZeroPaddingSize( getExtendedImageSize( img, imageExtension ), fftOptimization );
				outsideFactory = new OutsideStrategyMirrorExpWindowingFactory<T>( relativeFadeOutDistance );				
				break;
			}			
			default: // or NONE
			{
				if ( inputSize == null )
					extendedZeroPaddedSize = getZeroPaddingSize( img.getDimensions(), fftOptimization );
				else
					extendedZeroPaddedSize = getZeroPaddingSize( inputSize, fftOptimization );
				
				outsideFactory = new OutsideStrategyValueFactory<T>( img.createType() );
				break;
			}		
		}
		
		originalOffset = new int[ numDimensions ];		
		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( inputSize != null )
				inputSizeOffset[ d ] = ( extendedZeroPaddedSize[ d ] - inputSize[ d ] ) / 2;
			
			originalOffset[ d ] = ( extendedZeroPaddedSize[ d ] - img.getDimension( d ) ) / 2;			
		}
		
		
		fftImage = FFTFunctions.computeFFT( img, outsideFactory, originalOffset, extendedZeroPaddedSize, getNumThreads(), false );
		
		if ( fftImage == null )
		{
			errorMessage = "Could not compute the FFT transformation, most likely out of memory";
			return false;
		}

		// rearrange quadrants if wanted
		if ( rearrangement == Rearrangement.RearrangeQuadrants )
			FFTFunctions.rearrangeFFTQuadrants( fftImage, getNumThreads() );
			
        processingTime = System.currentTimeMillis() - startTime;

        return true;
	}	
				
	protected int[] getExtendedImageSize( final Image<?> img, final int[] imageExtension )
	{
		final int[] extendedSize = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
		{
			// the new size includes the current image size
			extendedSize[ d ] = imageExtension[ d ] + img.getDimension( d );
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
