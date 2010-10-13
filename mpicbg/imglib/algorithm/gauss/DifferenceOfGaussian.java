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
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.ImageCalculatorInPlace;
import mpicbg.imglib.algorithm.math.function.Converter;
import mpicbg.imglib.algorithm.math.function.Function;
import mpicbg.imglib.algorithm.math.function.SubtractNorm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;

public class DifferenceOfGaussian < A extends Type<A>, B extends NumericType<B> > implements Algorithm, MultiThreaded, Benchmark
{
	public static enum SpecialPoint { INVALID, MIN, MAX };
	
	final Image<A> image;
	final ImageFactory<B> factory;
	final OutOfBoundsStrategyFactory<B> outOfBoundsFactory;
	
	final double sigma1, sigma2;
	final B normalizationFactor, minPeakValue, negMinPeakValue, zero, one, minusOne;
	
	final ArrayList<DifferenceOfGaussianPeak<B>> peaks = new ArrayList<DifferenceOfGaussianPeak<B>>();
	final Converter<A, B> converter;
	
	long processingTime;
	int numThreads;
	String errorMessage = "";
	
	public DifferenceOfGaussian( final Image<A> img, final ImageFactory<B> factory, final Converter<A, B> converter,
			    final OutOfBoundsStrategyFactory<B> outOfBoundsFactory, 
			    final double sigma1, final double sigma2, final B minPeakValue, final B normalizationFactor )
	{
		this.processingTime = -1;
		setNumThreads();
	
		this.image = img;
		this.factory = factory;
		this.outOfBoundsFactory = outOfBoundsFactory;
		this.converter = converter;
		
		this.sigma1 = sigma1;
		this.sigma2 = sigma2;
		this.normalizationFactor = normalizationFactor;
		this.minPeakValue = minPeakValue;
		
		this.zero = factory.createType();
		this.zero.setZero();
		this.one = factory.createType();
		this.one.setOne();
		this.minusOne = factory.createType();
		this.minusOne.setZero();
		this.minusOne.sub( one );
		
		this.negMinPeakValue = minPeakValue.clone();
		this.negMinPeakValue.mul( minusOne );
	}
	
	public ArrayList<DifferenceOfGaussianPeak<B>> getPeaks() { return peaks; }
	
	/**
	 * This method returns the {@link OutputAlgorithm} that will compute the Gaussian Convolutions, more efficient versions can override this method
	 * 
	 * @param sigma - the sigma of the convolution
	 * @param numThreads - the number of threads for this convolution
	 * @return
	 */
	protected OutputAlgorithm<B> getGaussianConvolution( final double sigma, final int numThreads )
	{
		final GaussianConvolution2<A, B> gauss = new GaussianConvolution2<A, B>( image, factory, outOfBoundsFactory, converter, sigma );
		
		return gauss;
	}
	
	/**
	 * Returns the function that does the normalized subtraction of the gauss images, more efficient versions can override this method
	 * @return - the Subtraction Function
	 */
	protected Function<B, B, B> getNormalizedSubtraction()
	{
		return new SubtractNorm<B>( normalizationFactor );
	}

	/**
	 * Checks if the absolute value of the current peak is high enough, more efficient versions can override this method
	 * @param value - the current value
	 * @return true if the absoluted value is high enough, otherwise false 
	 */
	protected boolean isPeakHighEnough( final B value )
	{
		if ( value.compareTo( zero ) >= 0 )
		{
			// is a positive extremum
			if ( value.compareTo( minPeakValue ) >= 0 )
				return true;
			else 
				return false;
		}
		else
		{
			// is a negative extremum
			if ( value.compareTo( negMinPeakValue ) <= 0 )
				return true;
			else
				return false;
		}
	}

	/**
	 * Checks if the current position is a minima or maxima in a 3^n neighborhood, more efficient versions can override this method
	 *  
	 * @param neighborhoodCursor - the {@link LocalNeighborhoodCursor}
	 * @param centerValue - the value in the center which is tested
	 * @return - if is a minimum, maximum or nothig
	 */
	protected SpecialPoint isSpecialPoint( final LocalNeighborhoodCursor<B> neighborhoodCursor, final B centerValue )
	{
		boolean isMin = true;
		boolean isMax = true;

		while ( (isMax || isMin) && neighborhoodCursor.hasNext() )
		{			
			neighborhoodCursor.fwd();
			
			final B value = neighborhoodCursor.getType(); 
			
			// it can still be a minima if the current value is bigger/equal to the center value
			isMin &= ( value.compareTo( centerValue ) >= 0 );
			
			// it can still be a maxima if the current value is smaller/equal to the center value
			isMax &= ( value.compareTo( centerValue ) <= 0 );
		}	
		
		// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
		if ( isMin )
			return SpecialPoint.MAX;
		else if ( isMax )
			return SpecialPoint.MIN;
		else
			return SpecialPoint.INVALID;
	}
	
	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		// perform the gaussian convolutions transferring it to the new (potentially higher precision) type T				
		final OutputAlgorithm<B> conv1 = getGaussianConvolution( sigma1, Math.max( 1, getNumThreads() / 2 ) );
		final OutputAlgorithm<B> conv2 = getGaussianConvolution( sigma2, Math.max( 1, getNumThreads() / 2 ) );
		        
        final Image<B> gauss1, gauss2;
        
        if ( conv1.checkInput() && conv2.checkInput() )
        {
        	
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
        
        final Function<B, B, B> function = getNormalizedSubtraction();        
        final ImageCalculatorInPlace<B, B> imageCalc = new ImageCalculatorInPlace<B, B>( gauss2, gauss1, function );
        
        if ( !imageCalc.checkInput() || !imageCalc.process() )
        {
        	System.out.println( "Cannot subtract images: " + imageCalc.getErrorMessage() );
        	
        	gauss1.close();
        	gauss2.close();
        	
        	return false;
        }

        gauss1.close();
        
        /*
        gauss2.setName( "laplace" );
        gauss2.getDisplay().setMinMax();
        ImageJFunctions.copyToImagePlus( gauss2 ).show();
        */
        
        //
        // Now we find minima and maxima in the DoG image
        //        
	    final AtomicInteger ai = new AtomicInteger( 0 );					
	    final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );
	    final int numThreads = threads.length;
	    final int numDimensions = gauss2.getNumDimensions();
	    
	    final Vector<ArrayList<DifferenceOfGaussianPeak<B>>> threadPeaksList = new Vector<ArrayList<DifferenceOfGaussianPeak<B>>>();
	    
	    for ( int i = 0; i < numThreads; ++i )
	    	threadPeaksList.add( new ArrayList<DifferenceOfGaussianPeak<B>>() );

		for (int ithread = 0; ithread < threads.length; ++ithread)
	        threads[ithread] = new Thread(new Runnable()
	        {
	            public void run()
	            {
	            	final int myNumber = ai.getAndIncrement();
            	
	            	final ArrayList<DifferenceOfGaussianPeak<B>> myPeaks = threadPeaksList.get( myNumber );	
	            	final LocalizableByDimCursor<B> cursor = gauss2.createLocalizableByDimCursor();	            	
	            	final LocalNeighborhoodCursor<B> neighborhoodCursor = LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( cursor );
	            	
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

	                		// if we do not clone it here, it might be moved along with the cursor
	                		// depending on the container type used
	                		final B currentValue = cursor.getType().clone();
	                		
	                		// it can never be a desired peak as it is too low
	                		if ( !isPeakHighEnough( currentValue ) )
                				continue;
	                		
                			//if ( Math.abs( currentValue ) < minPeakValue )
                			//	continue;

                			// update to the current position
                			neighborhoodCursor.update();

                			// we have to compare for example 26 neighbors in the 3d case (3^3 - 1) relative to the current position
                			final SpecialPoint specialPoint = isSpecialPoint( neighborhoodCursor, currentValue ); 
                			if ( specialPoint != SpecialPoint.INVALID )
                				myPeaks.add( new DifferenceOfGaussianPeak<B>( position, currentValue, specialPoint ) );
                			
                			// reset the position of the parent cursor
                			neighborhoodCursor.reset();	                				                		
	                	}
	                }
                
	                cursor.close();
            }
        });
	
		SimpleMultiThreading.startAndJoin( threads );		
		
		gauss2.close(); 
		
		// put together the list from the various threads	
		peaks.clear();
		
		for ( final ArrayList<DifferenceOfGaussianPeak<B>> peakList : threadPeaksList )
			peaks.addAll( peakList );
        		
        processingTime = System.currentTimeMillis() - startTime;
		
		return true;
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
			errorMessage = "DifferenceOfGaussian: [Image<A> img] is null.";
			return false;
		}
		else if ( factory == null )
		{
			errorMessage = "DifferenceOfGaussian: [ImageFactory<B> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactory == null )
		{
			errorMessage = "DifferenceOfGaussian: [OutOfBoundsStrategyFactory<B>] is null.";
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