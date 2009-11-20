package mpi.imglib.algorithm.fft;

import mpi.imglib.algorithm.Benchmark;
import mpi.imglib.algorithm.MultiThreadedOutputAlgorithm;
import mpi.imglib.image.Image;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

public class FourierTransform implements MultiThreadedOutputAlgorithm<ComplexFloatType>, Benchmark
{
	final Image<FloatType> img;
	Image<ComplexFloatType> fft;
	
	String errorMessage = "";
	int numThreads;
	long processingTime;
	
	public FourierTransform( final Image<FloatType> image )
	{
		this.img = image;
		this.processingTime = -1;
		
		setNumThreads();
	}
	
	@Override
	public boolean process() 
	{
		// TODO Auto-generated method stub
		return false;
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
