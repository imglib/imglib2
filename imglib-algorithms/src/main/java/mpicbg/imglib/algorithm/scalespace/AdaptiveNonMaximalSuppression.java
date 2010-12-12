package mpicbg.imglib.algorithm.scalespace;

import java.util.List;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.kdtree.KDTree;
import mpicbg.imglib.algorithm.kdtree.NNearestNeighborSearch;
import mpicbg.imglib.type.numeric.NumericType;

public class AdaptiveNonMaximalSuppression<T extends NumericType<T>> implements Algorithm, Benchmark, MultiThreaded
{
	final List<DifferenceOfGaussianPeak<T>> detections;
	double radius;
	
	long processingTime;
	int numThreads;
	String errorMessage = "";

	public AdaptiveNonMaximalSuppression( final List<DifferenceOfGaussianPeak<T>> detections, final double radius )
	{
		this.detections = detections;
		this.radius = radius;
		
		processingTime = -1;
		setNumThreads();
	}
	
	@Override
	public boolean process()
	{
		final KDTree<DifferenceOfGaussianPeak<T>> tree = new KDTree<DifferenceOfGaussianPeak<T>>( detections );
		final NNearestNeighborSearch<DifferenceOfGaussianPeak<T>> nNearestNeighborSearch = new NNearestNeighborSearch<DifferenceOfGaussianPeak<T>>( tree );

		for ( final DifferenceOfGaussianPeak<T> det : detections )
		{
			
		}
		
		return true;
	}
	
	@Override
	public boolean checkInput()
	{
		if ( detections == null )
		{
			errorMessage = "List<DifferenceOfGaussianPeak<T>> detections is null.";
			return false;
		}
		else if ( detections.size() == 0 )
		{
			errorMessage = "List<DifferenceOfGaussianPeak<T>> detections is empty.";
			return false;			
		}
		else if ( Double.isNaN( radius ) )
		{
			errorMessage = "Radius is NaN.";
			return false;						
		}
		else
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
