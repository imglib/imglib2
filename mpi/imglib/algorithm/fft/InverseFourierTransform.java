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

	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final Rearrangement rearrangement )
	{
		this.fftImage = fftImage;
		this.numDimensions = fftImage.getNumDimensions();
		
		this.rearrangement = rearrangement;
		
		setNumThreads();
	}
	
	public InverseFourierTransform( final Image<ComplexFloatType> fftImage, final FourierTransform forwardTransform )
	{
		this ( fftImage, forwardTransform.getRearrangement() );
	}
	
	public InverseFourierTransform( final Image<ComplexFloatType> fftImage )
	{
		this( fftImage, Rearrangement.RearrangeQuadrants );
	}
	
	public void setRearrangement( final Rearrangement rearrangement ) { this.rearrangement = rearrangement; }

	public Rearrangement getRearrangement() { return rearrangement; }

	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();
		
		if ( rearrangement == Rearrangement.RearrangeQuadrants )
			FFTFunctions.rearrangeFFTQuadrants( fftImage, getNumThreads() );

        
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
