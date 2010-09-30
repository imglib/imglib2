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
import mpicbg.imglib.container.array.Array3D;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class GaussianConvolution2< S extends RealType<S>, T extends RealType<T> > implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{	
	final Image<S> image;
	final Image<T> convolved;
	final OutOfBoundsStrategyFactory<S> outOfBoundsFactoryInput;
	final OutOfBoundsStrategyFactory<T> outOfBoundsFactoryOutput;
	final int numDimensions;
	final double[] sigma;
    final double[][] kernel;

	long processingTime;
	int numThreads;
	String errorMessage = "";

	public GaussianConvolution2( final Image<S> image, final ImageFactory<T> imageFactory, 
								 final OutOfBoundsStrategyFactory<S> outOfBoundsFactoryInput, final OutOfBoundsStrategyFactory<T> outOfBoundsFactoryOutput, 
								 final double[] sigma )
	{
		this.image = image;
		
		if ( imageFactory != null )
			this.convolved = imageFactory.createImage( image.getDimensions() );
		else
			this.convolved = null;
		
		this.sigma = sigma;
		this.processingTime = -1;
		setNumThreads();
		
		this.outOfBoundsFactoryInput = outOfBoundsFactoryInput;
		this.outOfBoundsFactoryOutput = outOfBoundsFactoryOutput;
		this.numDimensions = image.getNumDimensions();

		this.kernel = new double[ numDimensions ][];
		
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = MathLib.createGaussianKernel1DDouble( sigma[ d ], true );
	}

	public GaussianConvolution2( final Image<S> image, final ImageFactory<T> imageFactory, 
								 final OutOfBoundsStrategyFactory<S> outOfBoundsFactoryInput, final OutOfBoundsStrategyFactory<T> outOfBoundsFactoryOutput,
								 final double sigma )
	{
		this ( image, imageFactory, outOfBoundsFactoryInput, outOfBoundsFactoryOutput, createArray(image, sigma));
	}
	
	protected static double[] createArray( final Image<?> image, final double sigma )
	{
		final double[] sigmas = new double[ image.getNumDimensions() ];
		
		for ( int d = 0; d < image.getNumDimensions(); ++d )
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
			errorMessage = "GaussianConvolution: [Image<S> img] is null.";
			return false;
		}
		else if ( convolved == null )
		{
			errorMessage = "DifferenceOfGaussian: [ImageFactory<T> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactoryInput == null )
		{
			errorMessage = "GaussianConvolution: [OutOfBoundsStrategyFactory<S>] is null.";
			return false;
		}
		else if ( outOfBoundsFactoryOutput == null )
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
	
		if ( Array3D.class.isInstance( image.getContainer() ) && Array3D.class.isInstance( convolved.getContainer() ) && FloatType.class.isInstance( image.createType() ) && FloatType.class.isInstance( convolved.createType() ) )
		{
			computeGaussFloat3D();
    		processingTime = System.currentTimeMillis() - startTime;
    		return true;
		}
    	
        final Image<T> temp = convolved.createNewImage();        
    	final long imageSize = image.getNumPixels();

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

	                	final LocalizableByDimCursor<? extends RealType<?>> inputIterator;
	                	final LocalizableCursor<? extends RealType<?>> outputIterator;
	                	
	                	if ( numDimensions % 2 == 0 ) // even number of dimensions ( 2d, 4d, 6d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the temporary image
	                		{
			                	inputIterator = image.createLocalizableByDimCursor( outOfBoundsFactoryInput );
			                    outputIterator = temp.createLocalizableCursor();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output image, because that might be the last convolution  
	                		{
			                	inputIterator = temp.createLocalizableByDimCursor( outOfBoundsFactoryOutput );
			                    outputIterator = convolved.createLocalizableCursor();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp image, it is not the last convolution for sure
	                		{
			                	inputIterator = convolved.createLocalizableByDimCursor( outOfBoundsFactoryOutput );
			                    outputIterator = temp.createLocalizableCursor();
	                		}	                		
	                	}
	                	else // ( numDimensions % 2 != 0 ) // even number of dimensions ( 1d, 3d, 5d, ... )
	                	{
	                		if ( currentDim == 0 ) // first dimension convolve to the output image, in the 1d case we are done then already
	                		{
			                	inputIterator = image.createLocalizableByDimCursor( outOfBoundsFactoryInput );
			                    outputIterator = convolved.createLocalizableCursor();	                			
	                		}
	                		else if ( currentDim % 2 == 1 ) // for odd dimension ids we convolve to the output image, because that might be the last convolution  
	                		{
			                	inputIterator = convolved.createLocalizableByDimCursor( outOfBoundsFactoryOutput );
			                    outputIterator = temp.createLocalizableCursor();
	                		}
	                		else //if ( currentDim % 2 == 0 ) // for even dimension ids we convolve to the temp image, it is not the last convolution for sure
	                		{
			                	inputIterator = temp.createLocalizableByDimCursor( outOfBoundsFactoryOutput );
			                    outputIterator = convolved.createLocalizableCursor();
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
	
	protected void convolve( final LocalizableByDimCursor<? extends RealType<?>> inputIterator, final LocalizableCursor<? extends RealType<?>> outputIterator, final int dim, final float[] kernel, final long startPos, final long loopSize )
	{		
		// move to the starting position of the current thread
		outputIterator.fwd( startPos );
		
		final int filterSize = kernel.length;
		final int filterSizeMinus1 = filterSize - 1;
		final int filterSizeHalf = filterSize / 2;
		final int filterSizeHalfMinus1 = filterSizeHalf - 1;
		final int numDimensions = inputIterator.getImage().getNumDimensions();
		
		final int iteratorPosition = filterSizeHalf;
		
		final int[] to = new int[ numDimensions ];
		
		// do as many pixels as wanted by this thread
		for ( long j = 0; j < loopSize; ++j )
		{
			outputIterator.fwd();			                			                	
			
			// set the sum to zero
			double sum = 0, tmp = 0;
			
			//
			// we move filtersize/2 of the convolved pixel in the input image
			//
			
			// get the current positon in the output image
			outputIterator.getPosition( to );
			
			// position in the input image is filtersize/2 to the left
			to[ dim ] -= iteratorPosition;
			
			// set the input cursor to this very position
			inputIterator.setPosition( to );
			
			// iterate over the kernel length across the input image
			for ( int f = -filterSizeHalf; f <= filterSizeHalfMinus1; ++f )
			{
				// get value from the input image
				tmp = inputIterator.getType().getRealDouble();
				
				// multiply the kernel
				tmp *= kernel[ f + filterSizeHalf ];
				
				// add up the sum
				sum += tmp;
				
				// move the cursor forward for the next iteration
				inputIterator.fwd( dim );
			}
			
			//
			// for the last pixel we do not move forward
			//
			
			// get value from the input image
			tmp = inputIterator.getType().getRealDouble();
			
			// multiply the kernel
			tmp *= kernel[ filterSizeMinus1 ];
			
			// add up the sum
			sum += tmp;
			
			outputIterator.getType().setReal( sum );
		}
	}	
	
	@SuppressWarnings("unchecked")
	protected void computeGaussFloat3D()
	{
		/* inconvertible types due to javac bug 6548436: final OutOfBoundsStrategyFactory<FloatType> outOfBoundsFactoryFloat = (OutOfBoundsStrategyFactory<FloatType>)outOfBoundsFactory;  */
		final OutOfBoundsStrategyFactory<FloatType> outOfBoundsFactoryFloat = (OutOfBoundsStrategyFactory)outOfBoundsFactoryInput;
		
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> imageFloat = (Image<FloatType>) image; */
		final Image<FloatType> imageFloat = (Image)image;
		/* inconvertible types due to javac bug 6548436: final Image<FloatType> convolvedFloat = (Image<FloatType>) convolved; */
		final Image<FloatType> convolvedFloat = (Image)convolved;
		
		GaussianConvolution.computeGaussFloatArray3D( imageFloat, convolvedFloat, outOfBoundsFactoryFloat, kernel, numThreads );
	}
}
