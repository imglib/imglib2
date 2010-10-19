package mpicbg.imglib.algorithm.math;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.multithreading.Chunk;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.type.ComparableType;

public class ComputeMinMax<T extends ComparableType<T>> implements Algorithm, MultiThreaded, Benchmark
{
	final Image<T> image;
	final T min, max;
	
	String errorMessage = "";
	int numThreads;
	long processingTime;
	
	public ComputeMinMax( final Image<T> image )
	{
		setNumThreads();
		
		this.image = image;
	
		this.min = image.createType();
		this.max = image.createType();
	}
	
	public T getMin() { return min; }
	public T getMax() { return max; }
	
	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();

		final long imageSize = image.getNumPixels();

		final AtomicInteger ai = new AtomicInteger(0);					
        final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );

        final Vector<Chunk> threadChunks = SimpleMultiThreading.divideIntoChunks( imageSize, numThreads );
        final Vector<T> minValues = new Vector<T>();
        final Vector<T> maxValues = new Vector<T>();
	
        for (int ithread = 0; ithread < threads.length; ++ithread)
        {
        	minValues.add( image.createType() );
        	maxValues.add( image.createType() );
        	
            threads[ithread] = new Thread(new Runnable()
            {
                public void run()
                {
                	// Thread ID
                	final int myNumber = ai.getAndIncrement();
        
                	// get chunk of pixels to process
                	final Chunk myChunk = threadChunks.get( myNumber );
                	
                	// compute min and max
					compute( myChunk.getStartPosition(), myChunk.getLoopSize(), minValues.get( myNumber ), maxValues.get( myNumber ) );

                }
            });
        }
        
        SimpleMultiThreading.startAndJoin( threads );
        
        // compute overall min and max
        min.set( minValues.get( 0 ) );
        max.set( maxValues.get( 0 ) );
        
        for ( int i = 0; i < threads.length; ++i )
        {
        	T value = minValues.get( i );
			if ( MathLib.min( min, value ) == value )
				min.set( value );
			
			value = maxValues.get( i );
			if ( MathLib.max( max, value ) == value )
				max.set( value );        	
        }
        
		processingTime = System.currentTimeMillis() - startTime;
        
		return true;
	}	

	protected void compute( final long startPos, final long loopSize, final T min, final T max )
	{
		final Cursor<T> cursor = image.createCursor();
		
		// init min and max
		cursor.fwd();
		
		min.set( cursor.getType() );
		max.set( cursor.getType() );
		
		cursor.reset();

		// move to the starting position of the current thread
		cursor.fwd( startPos );		

        // do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
        {
			cursor.fwd();
			
			final T value = cursor.getType();
			
			if ( MathLib.min( min, value ) == value )
				min.set( value );
			
			if ( MathLib.max( max, value ) == value )
				max.set( value );
		}
		
		cursor.close();
	}
	
	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
		{
			errorMessage = "ScaleSpace: [Image<A> img] is null.";
			return false;
		}
		else
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
	public String getErrorMessage() { return errorMessage; }
}
