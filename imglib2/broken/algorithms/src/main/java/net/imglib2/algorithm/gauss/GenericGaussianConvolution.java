/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.gauss;

import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.container.Img;
import net.imglib2.container.ImgFactory;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class GenericGaussianConvolution< T extends NumericType<T>, F extends RandomAccessibleInterval<T, F>> implements MultiThreaded, OutputAlgorithm<Img<T>>, Benchmark
{
	final F input;
	final ImgFactory<T> outputFactory;
	final Img<T> convolved;
	final OutOfBoundsFactory< T, F > outOfBoundsFactory1;
	final OutOfBoundsFactory< T, Img<T> > outOfBoundsFactory2;
	final int numDimensions;
	final double[] sigma;
    final double[][] kernel;

	long processingTime;
	int numThreads;
	String errorMessage = "";

	public GenericGaussianConvolution( 
			final F input, 
			final ImgFactory<T> outputFactory,
			final OutOfBoundsFactory<T,F> outOfBoundsFactory1, 
			final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory2, 
			final double[] sigma )
	{
		this.input = input;
		this.outputFactory = outputFactory;
		this.convolved = outputFactory.create( input, input.firstElement().createVariable() );
		this.sigma = sigma;
		this.processingTime = -1;
		setNumThreads();
		
		this.outOfBoundsFactory1 = outOfBoundsFactory1;
		this.outOfBoundsFactory2 = outOfBoundsFactory2;
		this.numDimensions = input.numDimensions();

		this.kernel = new double[ numDimensions ][];
		
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = Util.createGaussianKernel1DDouble( sigma[ d ], true );
	}

	public GenericGaussianConvolution( 
			final F input, 
			final ImgFactory<T> outputFactory,
			final OutOfBoundsFactory<T,F> outOfBoundsFactory1, 
			final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory2, 
			final double sigma )
	{
		this ( input, outputFactory, outOfBoundsFactory1, outOfBoundsFactory2, createArray(input, sigma));
	}
	
	protected static double[] createArray( final Interval interval, final double sigma )
	{
		final double[] sigmas = new double[ interval.numDimensions() ];
		
		for ( int d = 0; d < interval.numDimensions(); ++d )
			sigmas[ d ] = sigma;
		
		return sigmas;
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	
	
	/**
	 * The sigma the container was convolved with
	 * @return - double sigma
	 */
	public double[] getSigmas() { return sigma; }
	
	public int getKernelSize( final int dim ) { return kernel[ dim ].length; }
	
	@Override
	public Img<T> getResult() { return convolved;	}

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( input == null )
		{
			errorMessage = "GaussianConvolution: [Image<T> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactory1 == null )
		{
			errorMessage = "GaussianConvolution: [OutOfBoundsStrategyFactory<T>] is null.";
			return false;
		}
		else if ( outOfBoundsFactory2 == null )
		{
			errorMessage = "GaussianConvolution: [OutOfBoundsStrategyFactory<T>] is null.";
			return false;
		}
		else
			return true;
	}

	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public boolean process() 
	{		
		final long startTime = System.currentTimeMillis();
		/*
		if ( container.numDimensions() == 3 && Array.class.isInstance( container ) && FloatType.class.isInstance( container.createVariable() ))
		{
    		//System.out.println( "GaussianConvolution: Input is instance of Image<Float> using an Array3D, fast forward algorithm");
    		computeGaussFloatArray3D();
    		
    		processingTime = System.currentTimeMillis() - startTime;
    		
    		return true;
		}
    	*/
        final Img<T> temp = outputFactory.create( input, input.firstElement().createVariable() );        
    	final long containerSize = input.size();

        //
        // Folding loop
        //
        for ( int dim = 0; dim < numDimensions; dim++ )
        {
         	final int currentDim = dim;
        	
			final AtomicInteger ai = new AtomicInteger(0);					
	        final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
	        
	        final long threadChunkSize = containerSize / threads.length;
	        final long threadChunkMod = containerSize % threads.length;
	
	        for (int ithread = 0; ithread < threads.length; ++ithread)
	            threads[ithread] = new Thread(new Runnable()
	            {
	                public void run()
	                {
	                	// Thread ID
	                	final int myNumber = ai.getAndIncrement();

	                	//System.out.println("Thread " + myNumber + " folds in dimension " + currentDim);

	                	final RandomAccess<T> inputIterator;
	                	final Cursor<T> outputIterator;
	                	
	                	if ( numDimensions % 2 == 0 ) // even number of dimensions ( 2d, 4d, 6d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the temporary container
	                		{
			                	inputIterator = input.randomAccess( outOfBoundsFactory1 );
			                    outputIterator = temp.localizingCursor();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output container, because that might be the last convolution  
	                		{
			                	inputIterator = temp.randomAccess( outOfBoundsFactory2 );
			                    outputIterator = convolved.localizingCursor();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp container, it is not the last convolution for sure
	                		{
			                	inputIterator = convolved.randomAccess( outOfBoundsFactory2 );
			                    outputIterator = temp.localizingCursor();
	                		}	                		
	                	}
	                	else // ( numDimensions % 2 != 0 ) // even number of dimensions ( 1d, 3d, 5d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the output container, in the 1d case we are done then already
	                		{
			                	inputIterator = input.randomAccess( outOfBoundsFactory1 );
			                    outputIterator = convolved.localizingCursor();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output container, because that might be the last convolution  
	                		{
			                	inputIterator = convolved.randomAccess( outOfBoundsFactory2 );
			                    outputIterator = temp.localizingCursor();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp container, it is not the last convolution for sure
	                		{
			                	inputIterator = temp.randomAccess( outOfBoundsFactory2 );
			                    outputIterator = convolved.localizingCursor();
	                		}	 
	                	}
	                	
	                	// move to the starting position of the current thread
	                	final long startPosition = myNumber * threadChunkSize;

	                    // the last thread may has to run longer if the number of pixels cannot be divided by the number of threads
	                    final long loopSize;		                    
	                    if ( myNumber == numThreads - 1 )
	                    	loopSize = threadChunkSize + threadChunkMod;
	                    else
	                    	loopSize = threadChunkSize;
	                	
	                    // convolve the container in the current dimension using the given cursors
	                    float[] kernelF = new float[ kernel[ currentDim ].length ];
	                    
	                    for ( int i = 0; i < kernelF.length; ++i )
	                    	kernelF[ i ] = (float)kernel[ currentDim ][ i ];
	                    
	                    convolve( inputIterator, outputIterator, currentDim, kernelF, startPosition, loopSize );
	                }
	            });
	        SimpleMultiThreading.startAndJoin(threads);
        }
        
        processingTime = System.currentTimeMillis() - startTime;
        
        return true;
	}
	
	protected void convolve( final RandomAccess<T> inputIterator, final Cursor<T> outputIterator, 
															   final int dim, final float[] kernel,
															   final long startPos, final long loopSize )
	{
    	// move to the starting position of the current thread
    	outputIterator.jumpFwd( startPos );

        final int filterSize = kernel.length;
        final int filterSizeMinus1 = filterSize - 1;
        final int filterSizeHalf = filterSize / 2;
        final int filterSizeHalfMinus1 = filterSizeHalf - 1;
        final int numDimensions = inputIterator.numDimensions();
        
    	final int iteratorPosition = filterSizeHalf;
    	
    	final int[] to = new int[ numDimensions ];
    	
    	final T sum = inputIterator.get().createVariable();
    	final T tmp = inputIterator.get().createVariable();
        
    	
        // do as many pixels as wanted by this thread
        for ( long j = 0; j < loopSize; ++j )
        {
        	outputIterator.fwd();			                			                	
        	
        	// set the sum to zero
        	sum.setZero();
        	
        	//
        	// we move filtersize/2 of the convolved pixel in the input container
        	//
        	
        	// get the current positon in the output container
    		outputIterator.localize( to );
    		
    		// position in the input container is filtersize/2 to the left
    		to[ dim ] -= iteratorPosition;
    		
    		// set the input cursor to this very position
    		inputIterator.setPosition( to );
    		
    		//System.out.println( "out: " + outputIterator );
    		//System.out.println( "iteratorPosition: " + iteratorPosition );
    		//System.out.println( "in: " + inputIterator );
    		//System.exit ( 0 );
    		
    		// iterate over the kernel length across the input container
        	for ( int f = -filterSizeHalf; f <= filterSizeHalfMinus1; ++f )
    		{
        		// get value from the input container
        		tmp.set( inputIterator.get() );

         		// multiply the kernel
        		tmp.mul( kernel[ f + filterSizeHalf ] );
        		
        		// add up the sum
        		sum.add( tmp );
        		
        		// move the cursor forward for the next iteration
    			inputIterator.fwd( dim );
    		}

        	//
        	// for the last pixel we do not move forward
        	//
        	    		
    		// get value from the input container
    		tmp.set( inputIterator.get() );
    		    		
    		// multiply the kernel
    		tmp.mul( kernel[ filterSizeMinus1 ] );
    		
    		// add up the sum
    		sum.add( tmp );

            outputIterator.get().set( sum );
        }
	}	
}
