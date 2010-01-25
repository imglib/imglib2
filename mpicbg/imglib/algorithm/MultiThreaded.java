package mpicbg.imglib.algorithm;

public interface MultiThreaded 
{
	/**
	 * Sets the number of threads to the amount of processors available
	 */
	public void setNumThreads();
	
	/**
	 * Sets the number of threads 
	 * @param numThreads - number of threads to use
	 */
	public void setNumThreads( final int numThreads );
	
	/**
	 * The number of threads used by the algorithm
	 * @return - the number of threads
	 */
	public int getNumThreads();
}
