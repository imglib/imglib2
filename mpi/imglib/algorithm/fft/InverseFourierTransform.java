package mpi.imglib.algorithm.fft;

import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.algorithm.MultiThreadedOutputAlgorithm;
import mpi.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpi.imglib.image.Image;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

public class InverseFourierTransform implements MultiThreadedOutputAlgorithm<FloatType>, Benchmark
{
	final Image<ComplexFloatType> fftImage;	
	final int numDimensions;
	Image<FloatType> image;
	
	Rearrangement rearrangement;

	String errorMessage = "";
	int numThreads;
	long processingTime;
	boolean scale, inPlace, cropBack;
	int[] originalSize, originalOffset; 

	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final Rearrangement rearrangement, 
									final boolean inPlace, final boolean scale, final boolean cropBack, 
									final int[] originalSize, final int[] originalOffset )
	{
		this.fftImage = fftImage;
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
	
	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final FourierTransform forwardTransform )
	{
		this ( fftImage, forwardTransform.getRearrangement(), false, true, true, forwardTransform.getOriginalSize(), forwardTransform.getOriginalOffset() );
	}
	
	public InverseFourierTransform( final Image<ComplexFloatType> fftImage )
	{
		this( fftImage, Rearrangement.RearrangeQuadrants, false, true, false, null, null );
	}
	
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }
	public void setInPlaceTransform( final boolean inPlace ) { this.inPlace = inPlace; }
	public void setDoScaling( final boolean scale ) { this.scale = scale; }
	public void setCropBackToOriginalSize( final boolean cropBack ) { this.cropBack = cropBack; }
	public void setOriginalSize( final int[] originalSize ) { this.originalSize = originalSize; }
	public void setOriginalOffset( final int[] originalOffset ) { this.originalOffset = originalOffset; }

	public Rearrangement getRearrangement() { return rearrangement; }
	public boolean getInPlaceTransform() { return inPlace; }
	public boolean setDoScaling() { return scale; }
	public boolean setCropBackToOriginalSize() { return cropBack; }
	public int[] getOriginalSize() { return originalSize.clone(); }
	public int[] getOriginalOffset() { return originalOffset.clone(); }

	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();
		
		if ( rearrangement == Rearrangement.RearrangeQuadrants )
			FFTFunctions.rearrangeFFTQuadrants( fftImage, getNumThreads() );

		// perform inverse FFT 
		image = FFTFunctions.computeInverseFFT( fftImage, getNumThreads(), inPlace, scale, cropBack, originalSize, originalOffset );

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
	public Image<FloatType> getResult() { return image; }

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
