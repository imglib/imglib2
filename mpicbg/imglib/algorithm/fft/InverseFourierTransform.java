package mpicbg.imglib.algorithm.fft;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.ComplexFloatType;

public class InverseFourierTransform<T extends NumericType<T>> implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{
	final Image<ComplexFloatType> fftImage;	
	final int numDimensions;
	Image<T> image;
	T type;
	
	Rearrangement rearrangement;

	String errorMessage = "";
	int numThreads;
	long processingTime;
	boolean scale, inPlace, cropBack;
	int[] originalSize, originalOffset; 

	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final T type, final Rearrangement rearrangement, 
									final boolean inPlace, final boolean scale, final boolean cropBack, 
									final int[] originalSize, final int[] originalOffset )
	{
		this.fftImage = fftImage;
		this.type = type;
		this.numDimensions = fftImage.getNumDimensions();
		
		this.rearrangement = rearrangement;
		this.scale = scale;
		this.inPlace = inPlace;
		this.cropBack = cropBack;
		
		if ( originalSize != null )
			this.originalSize = originalSize.clone();
		
		if ( originalOffset != null )
			this.originalOffset = originalOffset.clone();
		
		setNumThreads();
	}
	
	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final FourierTransform<T> forwardTransform )
	{
		this ( fftImage, forwardTransform.getImageType(), forwardTransform.getRearrangement(), false, true, true, forwardTransform.getFFTInputSize(), forwardTransform.getFFTInputOffset() );
	}

	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final FourierTransform<?> forwardTransform, final T type )
	{
		this ( fftImage, type, forwardTransform.getRearrangement(), false, true, true, forwardTransform.getFFTInputSize(), forwardTransform.getFFTInputOffset() );
	}

	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final T type )
	{
		this( fftImage, type, Rearrangement.RearrangeQuadrants, false, true, false, null, null );
	}
	
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setInPlaceTransform( final boolean inPlace ) { this.inPlace = inPlace; }
	public void setDoScaling( final boolean scale ) { this.scale = scale; }
	public void setCropBackToOriginalSize( final boolean cropBack ) { this.cropBack = cropBack; }
	public void setOriginalSize( final int[] originalSize ) { this.originalSize = originalSize; }
	public void setOriginalOffset( final int[] originalOffset ) { this.originalOffset = originalOffset; }

	public Rearrangement getRearrangement() { return rearrangement; }
	public boolean getInPlaceTransform() { return inPlace; }
	public boolean getDoScaling() { return scale; }
	public boolean getCropBackToOriginalSize() { return cropBack; }
	public int[] getOriginalSize() { return originalSize.clone(); }
	public int[] getOriginalOffset() { return originalOffset.clone(); }

	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();

		// in Place computation will destroy the image
		final Image<ComplexFloatType> complex;		
		
		if ( inPlace )
			complex = fftImage;
		else
			complex = fftImage.clone();
			
		if ( rearrangement == Rearrangement.RearrangeQuadrants )
			FFTFunctions.rearrangeFFTQuadrants( complex, getNumThreads() );

		// perform inverse FFT 					
		image = FFTFunctions.computeInverseFFT( complex, type, getNumThreads(), scale, cropBack, originalSize, originalOffset );
		
		if ( !inPlace )
			complex.close();

		processingTime = System.currentTimeMillis() - startTime;

        return true;
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
	public Image<T> getResult() { return image; }

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( fftImage == null )
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
