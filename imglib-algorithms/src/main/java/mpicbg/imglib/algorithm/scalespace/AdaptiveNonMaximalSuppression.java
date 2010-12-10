package mpicbg.imglib.algorithm.scalespace;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;

import mpicbg.imglib.algorithm.Algorithm;

public class AdaptiveNonMaximalSuppression implements Algorithm, Benchmark, MultiThreaded
{
	long processingTime;
	int numThreads;
	String errorMessage = "";

	public AdaptiveNonMaximalSuppression()
	{
		processingTime = -1;
		setNumThreads();
	}
	
	@Override
	public boolean process()
	{
		return true;
	}
	
	@Override
	public boolean checkInput()
	{
		return true;
	}
	
	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	
}
