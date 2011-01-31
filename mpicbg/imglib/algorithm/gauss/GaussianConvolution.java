/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Preibisch
 */
package mpicbg.imglib.algorithm.gauss;

import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outofbounds.RasterOutOfBoundsFactory;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class GaussianConvolution< T extends NumericType<T>> implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{	
	final Image<T> image, convolved;
	final RasterOutOfBoundsFactory<T> outOfBoundsFactory;
	final int numDimensions;
	final double[] sigma;
    final double[][] kernel;

	long processingTime;
	int numThreads;
	String errorMessage = "";

	public GaussianConvolution( final Image<T> image, final RasterOutOfBoundsFactory<T> outOfBoundsFactory, final double[] sigma )
	{
		this.image = image;
		this.convolved = image.createNewImage();
		this.sigma = sigma;
		this.processingTime = -1;
		setNumThreads();
		
		this.outOfBoundsFactory = outOfBoundsFactory;
		this.numDimensions = image.numDimensions();

		this.kernel = new double[ numDimensions ][];
		
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = MathLib.createGaussianKernel1DDouble( sigma[ d ], true );
	}

	public GaussianConvolution( final Image<T> image, final RasterOutOfBoundsFactory<T> outOfBoundsFactory, final double sigma )
	{
		this ( image, outOfBoundsFactory, createArray(image, sigma));
	}
	
	protected static double[] createArray( final Image<?> image, final double sigma )
	{
		final double[] sigmas = new double[ image.numDimensions() ];
		
		for ( int d = 0; d < image.numDimensions(); ++d )
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
	 * The sigma the image was convolved with
	 * @return - double sigma
	 */
	public double[] getSigmas() { return sigma; }
	
	public int getKernelSize( final int dim ) { return kernel[ dim ].length; }
	
	@Override
	public Image<T> getResult() { return convolved;	}

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
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
	
		if ( image.numDimensions() == 3 && Array.class.isInstance( image.getContainer() ) && FloatType.class.isInstance( image.createType() ))
		{
    		//System.out.println( "GaussianConvolution: Input is instance of Image<Float> using an Array3D, fast forward algorithm");
    		computeGaussFloatArray3D();
    		
    		processingTime = System.currentTimeMillis() - startTime;
    		
    		return true;
		}
    	
        final Image<T> temp = image.createNewImage();        
    	final long imageSize = image.numPixels();

        //
        // Folding loop
        //
        for ( int dim = 0; dim < numDimensions; dim++ )
        {
         	final int currentDim = dim;
        	
			final AtomicInteger ai = new AtomicInteger(0);					
	        final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
	        
	        final long threadChunkSize = imageSize / threads.length;
	        final long threadChunkMod = imageSize % threads.length;
	
	        for (int ithread = 0; ithread < threads.length; ++ithread)
	            threads[ithread] = new Thread(new Runnable()
	            {
	                public void run()
	                {
	                	// Thread ID
	                	final int myNumber = ai.getAndIncrement();

	                	//System.out.println("Thread " + myNumber + " folds in dimension " + currentDim);

	                	final PositionableRasterSampler<T> inputIterator;
	                	final RasterIterator<T> outputIterator;
	                	
	                	if ( numDimensions % 2 == 0 ) // even number of dimensions ( 2d, 4d, 6d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the temporary image
	                		{
			                	inputIterator = image.createPositionableRasterSampler( outOfBoundsFactory );
			                    outputIterator = temp.createLocalizingRasterIterator();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output image, because that might be the last convolution  
	                		{
			                	inputIterator = temp.createPositionableRasterSampler( outOfBoundsFactory );
			                    outputIterator = convolved.createLocalizingRasterIterator();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp image, it is not the last convolution for sure
	                		{
			                	inputIterator = convolved.createPositionableRasterSampler( outOfBoundsFactory );
			                    outputIterator = temp.createLocalizingRasterIterator();
	                		}	                		
	                	}
	                	else // ( numDimensions % 2 != 0 ) // even number of dimensions ( 1d, 3d, 5d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the output image, in the 1d case we are done then already
	                		{
			                	inputIterator = image.createPositionableRasterSampler( outOfBoundsFactory );
			                    outputIterator = convolved.createLocalizingRasterIterator();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output image, because that might be the last convolution  
	                		{
			                	inputIterator = convolved.createPositionableRasterSampler( outOfBoundsFactory );
			                    outputIterator = temp.createLocalizingRasterIterator();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp image, it is not the last convolution for sure
	                		{
			                	inputIterator = temp.createPositionableRasterSampler( outOfBoundsFactory );
			                    outputIterator = convolved.createLocalizingRasterIterator();
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
	                	
	                    // convolve the image in the current dimension using the given cursors
	                    float[] kernelF = new float[ kernel[ currentDim ].length ];
	                    
	                    for ( int i = 0; i < kernelF.length; ++i )
	                    	kernelF[ i ] = (float)kernel[ currentDim ][ i ];
	                    
	                    convolve( inputIterator, outputIterator, currentDim, kernelF, startPosition, loopSize );
		                
		                inputIterator.close();
		                outputIterator.close();		               
	                }
	            });
	        SimpleMultiThreading.startAndJoin(threads);
        }

        // close temporary datastructure
        temp.close();
        
        processingTime = System.currentTimeMillis() - startTime;
        
        return true;
	}
	
	protected void convolve( final PositionableRasterSampler<T> inputIterator, final RasterIterator<T> outputIterator, 
															   final int dim, final float[] kernel,
															   final long startPos, final long loopSize )
	{		
    	// move to the starting position of the current thread
    	outputIterator.jumpFwd( startPos );
   	 
        final int filterSize = kernel.length;
        final int filterSizeMinus1 = filterSize - 1;
        final int filterSizeHalf = filterSize / 2;
        final int filterSizeHalfMinus1 = filterSizeHalf - 1;
        final int numDimensions = inputIterator.getImage().numDimensions();
        
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
        	// we move filtersize/2 of the convolved pixel in the input image
        	//
        	
        	// get the current positon in the output image
    		outputIterator.localize( to );
    		
    		// position in the input image is filtersize/2 to the left
    		to[ dim ] -= iteratorPosition;
    		
    		// set the input cursor to this very position
    		inputIterator.setPosition( to );

    		// iterate over the kernel length across the input image
        	for ( int f = -filterSizeHalf; f <= filterSizeHalfMinus1; ++f )
    		{
        		// get value from the input image
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
        	    		
    		// get value from the input image
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
	 * This class does the gaussian filtering of an image. On the edges of
	 * the image it does mirror the pixels. It also uses the seperability of
	 * the gaussian convolution.
	 *
	 * @param input FloatProcessor which should be folded (will not be touched)
	 * @param sigma Standard Derivation of the gaussian function
	 * @return FloatProcessor The folded image
	 *
	 * @author   Stephan Preibisch
	 */

	@SuppressWarnings("unchecked")
	public void computeGaussFloatArray3D()
	{
		/* inconvertible types due to javac bug 6548436: final OutOfBoundsStrategyFactory<FloatType> outOfBoundsFactoryFloat = (OutOfBoundsStrategyFactory<FloatType>)outOfBoundsFactory;  */
		final RasterOutOfBoundsFactory<FloatType> outOfBoundsFactoryFloat = (RasterOutOfBoundsFactory)outOfBoundsFactory;
		
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> imageFloat = (Image<FloatType>) image; */
		final Image<FloatType> imageFloat = (Image)image;
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> convolvedFloat = (Image<FloatType>) convolved; */
		final Image<FloatType> convolvedFloat = (Image)convolved;
		
		final FloatArray inputArray = (FloatArray) ( (DirectAccessContainer<FloatType, FloatAccess>) imageFloat.getContainer() ).update( null );
		final FloatArray outputArray = (FloatArray) ( (DirectAccessContainer<FloatType, FloatAccess>) convolvedFloat.getContainer() ).update( null );
		
  		final int width = imageFloat.getDimension( 0 );
		final int height = imageFloat.getDimension( 1 );
		final int depth = imageFloat.getDimension( 2 );

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
					
					final PositionableRasterSampler<FloatType> it = imageFloat.createPositionableRasterSampler( outOfBoundsFactoryFloat );

					// fold in x
					int kernelPos, count;

					// precompute direct positions inside image data when multiplying with kernel
					final int posLUT[] = new int[kernel1.length];
					for (int f = -filterSizeHalf; f <= filterSizeHalf; f++)
						posLUT[f + filterSizeHalf] = f;

					// precompute wheater we have to use mirroring or not (mirror when kernel goes out of image bounds)
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
					it.close();
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
					final PositionableRasterSampler<FloatType> it = convolvedFloat.createPositionableRasterSampler( outOfBoundsFactoryFloat );
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
					
					it.close();
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
					final PositionableRasterSampler<FloatType> it = convolvedFloat.createPositionableRasterSampler( outOfBoundsFactoryFloat );

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
					it.close();
				}
			});
		SimpleMultiThreading.startAndJoin(threads);
	}	
}
