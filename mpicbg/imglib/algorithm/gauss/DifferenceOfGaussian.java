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

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.math.ImageCalculatorInPlace;
import mpicbg.imglib.algorithm.math.function.Function;
import mpicbg.imglib.algorithm.math.function.SubtractNormReal;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

public class DifferenceOfGaussian<S extends RealType<S>, T extends RealType<T> > implements Algorithm, MultiThreaded, Benchmark
{
	static enum SpecialPoint { NONE, MIN, MAX };
	
	final Image<S> image;
	final ImageFactory<T> factory;
	final OutOfBoundsStrategyFactory<S> outOfBoundsFactoryIn;
	final OutOfBoundsStrategyFactory<T> outOfBoundsFactoryOut;
	
	final double sigma1, sigma2, normalizationFactor, minPeakValue;
	
	final ArrayList<DifferenceOfGaussianPeak> peaks = new ArrayList<DifferenceOfGaussianPeak>();
	
	long processingTime;
	int numThreads;
	String errorMessage = "";
	
	public DifferenceOfGaussian( final Image<S> img, final ImageFactory<T> factory,  
								 final OutOfBoundsStrategyFactory<S> outOfBoundsFactoryIn, final OutOfBoundsStrategyFactory<T> outOfBoundsFactoryOut, 
								 final double sigma1, final double sigma2, final double minPeakValue, final double normalizationFactor )
	{
		this.processingTime = -1;
		setNumThreads();
	
		this.image = img;
		this.factory = factory;
		this.outOfBoundsFactoryIn = outOfBoundsFactoryIn;
		this.outOfBoundsFactoryOut = outOfBoundsFactoryOut;
		
		this.sigma1 = sigma1;
		this.sigma2 = sigma2;
		this.normalizationFactor = normalizationFactor;
		this.minPeakValue = minPeakValue;
	}
	
	public ArrayList<DifferenceOfGaussianPeak> getPeaks() { return peaks; }

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		// perform the gaussian convolutions transferring it to the new (potentially higher precision) type T
        final GaussianConvolution2<S,T> conv1 = new GaussianConvolution2<S,T>( image, factory, outOfBoundsFactoryIn, outOfBoundsFactoryOut, sigma1 );
        final GaussianConvolution2<S,T> conv2 = new GaussianConvolution2<S,T>( image, factory, outOfBoundsFactoryIn, outOfBoundsFactoryOut, sigma2 );
        
        final Image<T> gauss1, gauss2;
        
        if ( conv1.checkInput() && conv2.checkInput() )
        {
        	// split up the number of threads
        	int numThreads = getNumThreads() / 2;
        	if ( numThreads < 1 )
        		numThreads = 1;
        	
        	conv1.setNumThreads( numThreads );
        	conv2.setNumThreads( numThreads );
        	
            final AtomicInteger ai = new AtomicInteger(0);					
            Thread[] threads = SimpleMultiThreading.newThreads( 2 );

        	for (int ithread = 0; ithread < threads.length; ++ithread)
                threads[ithread] = new Thread(new Runnable()
                {
                    public void run()
                    {
                    	final int myNumber = ai.getAndIncrement();
                    	if ( myNumber == 0 )
                    	{
                    		if ( !conv1.process() )
                            	System.out.println( "Cannot compute gaussian convolution 1: " + conv1.getErrorMessage() );                    		
                    	}
                    	else
                    	{
                    		if ( !conv2.process() )
                    			System.out.println( "Cannot compute gaussian convolution 2: " + conv2.getErrorMessage() );
                    	}                    	
                    }
                });
        	
    		SimpleMultiThreading.startAndJoin( threads );       	
        }
        else
        {
        	System.out.println( "Cannot compute gaussian convolutions: " + conv1.getErrorMessage() + " & " + conv2.getErrorMessage() );
        
        	gauss1 = gauss2 = null;
        	return false;
        }
                
        if ( conv1.getErrorMessage().length() == 0 && conv2.getErrorMessage().length() == 0 )
        {
	        gauss1 = conv1.getResult();
	        gauss2 = conv2.getResult();
        }
        else
        {
        	gauss1 = gauss2 = null;
        	return false;        	
        }

        /*
        gauss1.setName( "gauss1" );
        gauss2.setName( "gauss2" );
        
        gauss1.getDisplay().setMinMax();
        gauss2.getDisplay().setMinMax();
        
        ImageJFunctions.copyToImagePlus( gauss1 ).show();
        ImageJFunctions.copyToImagePlus( gauss2 ).show();
        */
        
        final Function<T, T, T> function = new SubtractNormReal<T, T, T>( normalizationFactor );        
        final ImageCalculatorInPlace<T, T> imageCalc = new ImageCalculatorInPlace<T, T>( gauss2, gauss1, function );
        
        if ( !imageCalc.checkInput() || !imageCalc.process() )
        {
        	System.out.println( "Cannot subtract images: " + imageCalc.getErrorMessage() );
        	
        	gauss1.close();
        	gauss2.close();
        	
        	return false;
        }

        gauss1.close();
                
        //
        // Now we find minima and maxima in the DoG image
        //        
	    final AtomicInteger ai = new AtomicInteger( 0 );					
	    final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );
	    final int numThreads = threads.length;
	    final int numDimensions = gauss2.getNumDimensions();
	    
	    final Vector<ArrayList<DifferenceOfGaussianPeak>> threadPeaksList = new Vector<ArrayList<DifferenceOfGaussianPeak>>();
	    
	    for ( int i = 0; i < numThreads; ++i )
	    	threadPeaksList.add( new ArrayList<DifferenceOfGaussianPeak>() );

		for (int ithread = 0; ithread < threads.length; ++ithread)
	        threads[ithread] = new Thread(new Runnable()
	        {
	            public void run()
	            {
	            	final int myNumber = ai.getAndIncrement();
            	
	            	final ArrayList<DifferenceOfGaussianPeak> myPeaks = threadPeaksList.get( myNumber );	
	            	final LocalizableByDimCursor<T> cursor = gauss2.createLocalizableByDimCursor();	            	
	            	final LocalNeighborhoodCursor<T> neighborhoodCursor = LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( cursor );
	            	
	            	final int[] position = new int[ numDimensions ];
	            	final int[] dimensionsMinus2 = gauss2.getDimensions();

            		for ( int d = 0; d < numDimensions; ++d )
            			dimensionsMinus2[ d ] -= 2;
	            	
MainLoop:           while ( cursor.hasNext() )
	                {
	                	cursor.fwd();
	                	cursor.getPosition( position );
	                	
	                	if ( position[ 0 ] % numThreads == myNumber )
	                	{
	                		for ( int d = 0; d < numDimensions; ++d )
	                		{
	                			final int pos = position[ d ];
	                			
	                			if ( pos < 1 || pos > dimensionsMinus2[ d ] )
	                				continue MainLoop;
	                		}

	                		final double currentValue = cursor.getType().getRealDouble();
	                		
	                		// it can never be a desired peak as it is too low
                			if ( Math.abs( currentValue ) < minPeakValue )
                				continue;

                			// update to the current position
                			neighborhoodCursor.update();

                			// we have to compare for example 26 neighbors in the 3d case (3^3 - 1) relative to the current position
                			final SpecialPoint specialPoint = isSpecialPoint( neighborhoodCursor, currentValue ); 
                			if ( specialPoint != SpecialPoint.NONE )
                				myPeaks.add( new DifferenceOfGaussianPeak( position, currentValue, specialPoint ) );
                			
                			// reset the position of the parent cursor
                			neighborhoodCursor.reset();	                				                		
	                	}
	                }
                
	                cursor.close();
            }
        });
	
		SimpleMultiThreading.startAndJoin( threads );				

		/*
        gauss2.setName( "laplace" );
        gauss2.getDisplay().setMinMax();
        ImageJFunctions.copyToImagePlus( gauss2 ).show();
		*/
		
		gauss2.close(); 
		
		// put together the list from the various threads	
		peaks.clear();
		
		for ( final ArrayList<DifferenceOfGaussianPeak> peakList : threadPeaksList )
			peaks.addAll( peakList );
        		
        processingTime = System.currentTimeMillis() - startTime;
		
		return true;
	}
	
	final protected SpecialPoint isSpecialPoint( final LocalNeighborhoodCursor<T> neighborhoodCursor, final double centerValue )
	{
		boolean isMin = true;
		boolean isMax = true;
		
		while ( (isMax || isMin) && neighborhoodCursor.hasNext() )
		{			
			neighborhoodCursor.fwd();
			
			final double value = neighborhoodCursor.getType().getRealDouble(); 
			
			// it can still be a minima if the current value is bigger/equal to the center value
			isMin &= (value >= centerValue);
			
			// it can still be a maxima if the current value is smaller/equal to the center value
			isMax &= (value <= centerValue);
		}		
		
		// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
		if ( isMin )
			return SpecialPoint.MAX;
		else if ( isMax )
			return SpecialPoint.MIN;
		else
			return SpecialPoint.NONE;
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
			errorMessage = "DifferenceOfGaussian: [Image<S> img] is null.";
			return false;
		}
		else if ( factory == null )
		{
			errorMessage = "DifferenceOfGaussian: [ImageFactory<T> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactoryIn == null )
		{
			errorMessage = "DifferenceOfGaussian: [OutOfBoundsStrategyFactory<S>] is null.";
			return false;
		}
		else if ( outOfBoundsFactoryOut == null )
		{
			errorMessage = "DifferenceOfGaussian: [OutOfBoundsStrategyFactory<T>] is null.";
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
