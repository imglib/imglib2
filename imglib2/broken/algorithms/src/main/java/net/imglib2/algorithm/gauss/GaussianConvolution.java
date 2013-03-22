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

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.container.Img;
import net.imglib2.container.ImgCursor;
import net.imglib2.container.ImgRandomAccess;
import net.imglib2.container.NativeImg;
import net.imglib2.container.basictypecontainer.FloatAccess;
import net.imglib2.container.basictypecontainer.array.FloatArray;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class GaussianConvolution< T extends NumericType<T>> implements MultiThreaded, OutputAlgorithm<Img<T>>, Benchmark
{	
	final Img<T> container, convolved;
	final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory;
	final int numDimensions;
	final double[] sigma;
    final double[][] kernel;

	long processingTime;
	int numThreads;
	String errorMessage = "";

	public GaussianConvolution( final Img<T> container, final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory, final double[] sigma )
	{
		this.container = container;
		this.convolved = container.factory().create( container, container.firstElement().createVariable() );
		this.sigma = sigma;
		this.processingTime = -1;
		setNumThreads();
		
		this.outOfBoundsFactory = outOfBoundsFactory;
		this.numDimensions = container.numDimensions();

		this.kernel = new double[ numDimensions ][];
		
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = Util.createGaussianKernel1DDouble( sigma[ d ], true );
	}

	public GaussianConvolution( final Img<T> container, final OutOfBoundsFactory<T,Img<T>> outOfBoundsFactory, final double sigma )
	{
		this ( container, outOfBoundsFactory, createArray(container, sigma));
	}
	
	protected static double[] createArray( final Img<?> container, final double sigma )
	{
		final double[] sigmas = new double[ container.numDimensions() ];
		
		for ( int d = 0; d < container.numDimensions(); ++d )
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
		else if ( container == null )
		{
			errorMessage = "GaussianConvolution: [Image<T> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactory == null )
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
        final Img<T> temp = container.factory().create( container, container.firstElement().createVariable() );        
    	final long containerSize = container.size();

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

	                	final ImgRandomAccess<T> inputIterator;
	                	final ImgCursor<T> outputIterator;
	                	
	                	if ( numDimensions % 2 == 0 ) // even number of dimensions ( 2d, 4d, 6d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the temporary container
	                		{
			                	inputIterator = container.randomAccess( outOfBoundsFactory );
			                    outputIterator = temp.localizingCursor();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output container, because that might be the last convolution  
	                		{
			                	inputIterator = temp.randomAccess( outOfBoundsFactory );
			                    outputIterator = convolved.localizingCursor();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp container, it is not the last convolution for sure
	                		{
			                	inputIterator = convolved.randomAccess( outOfBoundsFactory );
			                    outputIterator = temp.localizingCursor();
	                		}	                		
	                	}
	                	else // ( numDimensions % 2 != 0 ) // even number of dimensions ( 1d, 3d, 5d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the output container, in the 1d case we are done then already
	                		{
			                	inputIterator = container.randomAccess( outOfBoundsFactory );
			                    outputIterator = convolved.localizingCursor();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output container, because that might be the last convolution  
	                		{
			                	inputIterator = convolved.randomAccess( outOfBoundsFactory );
			                    outputIterator = temp.localizingCursor();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp container, it is not the last convolution for sure
	                		{
			                	inputIterator = temp.randomAccess( outOfBoundsFactory );
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
	
	protected void convolve( final ImgRandomAccess<T> inputIterator, final ImgCursor<T> outputIterator, 
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
	
	final private static int getPos( final int x, final int y, final int z, final int width, final int height )
	{
		return x + y*width + z*width*height;
	}
	
	/**
	 * This class does the gaussian filtering of an container. On the edges of
	 * the container it does mirror the pixels. It also uses the seperability of
	 * the gaussian convolution.
	 *
	 * @param input FloatProcessor which should be folded (will not be touched)
	 * @param sigma Standard Derivation of the gaussian function
	 * @return FloatProcessor The folded container
	 *
	 */

	@SuppressWarnings("unchecked")
	public void computeGaussFloatArray3D()
	{
		/* inconvertible types due to javac bug 6548436: final OutOfBoundsStrategyFactory<FloatType> outOfBoundsFactoryFloat = (OutOfBoundsStrategyFactory<FloatType>)outOfBoundsFactory;  */
		@SuppressWarnings("rawtypes")
		final OutOfBoundsFactory<FloatType,Img<FloatType>> outOfBoundsFactoryFloat = (OutOfBoundsFactory)outOfBoundsFactory;
		
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> containerFloat = (Image<FloatType>) container; */
		final Img<FloatType> containerFloat = (Img<FloatType>)container;
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> convolvedFloat = (Image<FloatType>) convolved; */
		final Img<FloatType> convolvedFloat = (Img<FloatType>)convolved;
		
		final FloatArray inputArray = (FloatArray) ( (NativeImg<FloatType, FloatAccess>) containerFloat ).update( null );
		final FloatArray outputArray = (FloatArray) ( (NativeImg<FloatType, FloatAccess>) convolvedFloat ).update( null );
		
		// Array supports only int anyways...
  		final int width = (int)containerFloat.dimension( 0 );
		final int height = (int)containerFloat.dimension( 1 );
		final int depth = (int)containerFloat.dimension( 2 );

		final AtomicInteger ai = new AtomicInteger(0);
		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		final int numThreads = threads.length;

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					double avg;

					final float[] in = inputArray.getCurrentStorageArray();
					final float[] out = outputArray.getCurrentStorageArray();
					final double[] kernel1 = kernel[ 0 ].clone();
					final int filterSize = kernel[ 0 ].length;
					final int filterSizeHalf = filterSize / 2;
					
					final ImgRandomAccess<FloatType> it = containerFloat.randomAccess( outOfBoundsFactoryFloat );

					// fold in x
					int kernelPos, count;

					// precompute direct positions inside container data when multiplying with kernel
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f;

					// precompute wheater we have to use mirroring or not (mirror when kernel goes out of container bounds)
					final boolean directlyComputable[] = new boolean[width];
					for (int x = 0; x < width; x++)
						directlyComputable[x] = (x - filterSizeHalf >= 0 && x + filterSizeHalf < width);

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
						{
							count = getPos( 0, 0, z, width, height );
							for (int y = 0; y < height; y++)
								for (int x = 0; x < width; x++)
								{
									avg = 0;

									if (directlyComputable[x]) 
										for (kernelPos = 0; kernelPos < filterSize; kernelPos++)
											avg += in[count + posLUT[kernelPos]] * kernel1[kernelPos];
									else
									{
										kernelPos = 0;

										it.setPosition( x - filterSizeHalf - 1, 0 );
										it.setPosition( y, 1 );
										it.setPosition( z, 2 );
										
										for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
										{
											it.fwd( 0 );
											avg += it.get().get() * kernel1[kernelPos++];
										}
									}
									out[count++] = (float) avg;
								}
						}
				}
			});
		SimpleMultiThreading.startAndJoin(threads);

		ai.set(0);
		// fold in y
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					double avg;
					int kernelPos, count;

					final float[] out =  outputArray.getCurrentStorageArray();
					final ImgRandomAccess<FloatType> it = convolvedFloat.randomAccess( outOfBoundsFactoryFloat );
					final double[] kernel1 = kernel[ 1 ].clone();
					final int filterSize = kernel[ 1 ].length;
					final int filterSizeHalf = filterSize / 2;

					final int inc = getPos( 0, 1, 0, width, height );
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f * inc;

					final boolean[] directlyComputable = new boolean[height];
					for (int y = 0; y < height; y++)
						directlyComputable[y] = (y - filterSizeHalf >= 0 && y + filterSizeHalf < height);

					final float[] tempOut = new float[height];

					for (int z = 0; z < depth; z++)
						if (z % numThreads == myNumber)
							for (int x = 0; x < width; x++)
							{
								count = getPos( x, 0, z, width, height );

								for (int y = 0; y < height; y++)
								{
									avg = 0;

									if (directlyComputable[y]) for (kernelPos = 0; kernelPos < filterSize; kernelPos++)
										avg += out[count + posLUT[kernelPos]] * kernel1[kernelPos];
									else
									{
										kernelPos = 0;

										it.setPosition( x, 0 );
										it.setPosition( y - filterSizeHalf - 1, 1 ); 
										it.setPosition( z, 2 );
										               
										for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
										{
											it.fwd( 1 );
											avg += it.get().get() * kernel1[kernelPos++];
										}
									}

									tempOut[y] = (float) avg;

									count += inc;
								}

								count = getPos( x, 0, z, width, height );

								for (int y = 0; y < height; y++)
								{
									out[count] = tempOut[y];
									count += inc;
								}
							}
				}
			});
		SimpleMultiThreading.startAndJoin(threads);

		ai.set(0);

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					double avg;
					int kernelPos, count;
					final double[] kernel1 = kernel[ 2 ].clone();
					final int filterSize = kernel[ 2 ].length;
					final int filterSizeHalf = filterSize / 2;

					final float[] out = outputArray.getCurrentStorageArray();
					final ImgRandomAccess<FloatType> it = convolvedFloat.randomAccess( outOfBoundsFactoryFloat );

					final int inc = getPos( 0, 0, 1, width, height );
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f * inc;

					final boolean[] directlyComputable = new boolean[depth];
					for (int z = 0; z < depth; z++)
						directlyComputable[z] = (z - filterSizeHalf >= 0 && z + filterSizeHalf < depth);

					final float[] tempOut = new float[depth];

					// fold in z
					for (int x = 0; x < width; x++)
						if (x % numThreads == myNumber)
							for (int y = 0; y < height; y++)
							{
								count = getPos( x, y, 0, width, height );
								
								for (int z = 0; z < depth; z++)
								{
									avg = 0;

									if (directlyComputable[z]) for (kernelPos = 0; kernelPos < filterSize; kernelPos++)
										avg += out[count + posLUT[kernelPos]] * kernel1[kernelPos];
									else
									{
										kernelPos = 0;

										it.setPosition( x, 0 );
										it.setPosition( y, 1 ); 
										it.setPosition( z - filterSizeHalf - 1, 2 );
										
										for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
										{
											it.fwd( 2 );
											avg += it.get().get() * kernel1[kernelPos++];
										}
									}
									tempOut[z] = (float) avg;

									count += inc;
								}

								count = getPos( x, y, 0, width, height );

								for (int z = 0; z < depth; z++)
								{
									out[count] = tempOut[z];
									count += inc;
								}
							}					
				}
			});
		SimpleMultiThreading.startAndJoin(threads);
	}	
}
