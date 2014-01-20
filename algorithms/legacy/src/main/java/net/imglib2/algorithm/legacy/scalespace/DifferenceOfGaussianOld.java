/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.legacy.scalespace;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.function.Function;
import net.imglib2.algorithm.function.SubtractNormReal;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.algorithm.legacy.scalespace.DifferenceOfGaussian.SpecialPoint;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhoodCursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class DifferenceOfGaussianOld < A extends RealType<A> > implements Algorithm, MultiThreaded, Benchmark
{
	protected final Img<A> image;
	protected Img<FloatType> dogImg;
	protected final ImgFactory<FloatType> factory;
	protected final OutOfBoundsFactory<FloatType, RandomAccessibleInterval<FloatType>> outOfBoundsFactory;

	final double[] sigma1, sigma2;
	double normalizationFactor, minPeakValue, negMinPeakValue;

	protected final ArrayList<DifferenceOfGaussianPeak<FloatType>> peaks = new ArrayList<DifferenceOfGaussianPeak<FloatType>>();

	boolean computeConvolutionsParalell, keepDoGImg;
	long processingTime;
	int numThreads;
	String errorMessage = "";

	static private final double[] asArray( final int nDim, final double sigma )
	{
		final double[] s = new double[ nDim ];
		for (int i=0; i<nDim; ++i)
			s[ i ] = sigma;
		return s;
	}

	/** Calls the DifferenceOfGaussianOld constructor with the given sigmas copied into double[] arrays,
	 * one entry per {@param img} dimension. */
	public DifferenceOfGaussianOld( final Img<A> img, final ImgFactory<FloatType> factory,
		    final OutOfBoundsFactory<FloatType, RandomAccessibleInterval<FloatType>> outOfBoundsFactory,
		    final double sigma1, final double sigma2, final double minPeakValue, final double normalizationFactor )
	{
		this( img, factory, outOfBoundsFactory, asArray(img.numDimensions(), sigma1),
				asArray(img.numDimensions(), sigma2), minPeakValue, normalizationFactor );
	}

	/**
	 * Extracts local minima and maxima of a certain size. It therefore computes the difference of gaussian
	 * for an {@link Img} and detects all local minima and maxima in 3x3x3x....3 environment, which is returned
	 * as an {@link ArrayList} of {@link DifferenceOfGaussianPeak}s. The two sigmas define the scale on which
	 * extrema are identified, it correlates with the size of the object.
	 * Note that not only extrema of this size are found, but they will have the higher absolute values. Note as
	 * well that the values of the difference of gaussian image is also defined by the distance between the two
	 * sigmas. A normalization if necessary can be found in the {@link ScaleSpace} class.
	 *
	 * Also note a technical detail, the method findPeaks(Img<FloatType> img) can be called on any image if the image
	 * from where the extrema should be computed already exists.
	 *
	 * @param img - The input {@link Img}<A>
	 * @param factory - The {@link ImgFactory}<FloatType> which defines the datatype in which the computation is performed
	 * @param outOfBoundsFactory - The {@link OutOfBoundsStrategyFactory} necessary for the {@link GaussianConvolution}
	 * @param sigma1 - The lower sigma
	 * @param sigma2 - The higher sigma
	 * @param minPeakValue -
	 * @param normalizationFactor
	 */
	public DifferenceOfGaussianOld( final Img<A> img, final ImgFactory<FloatType> factory,
			    final OutOfBoundsFactory<FloatType, RandomAccessibleInterval<FloatType>> outOfBoundsFactory,
			    final double[] sigma1, final double[] sigma2, final double minPeakValue, final double normalizationFactor )
	{
		this.processingTime = -1;
		this.computeConvolutionsParalell = true;
		setNumThreads();

		this.image = img;
		this.factory = factory;
		this.outOfBoundsFactory = outOfBoundsFactory;

		this.sigma1 = sigma1;
		this.sigma2 = sigma2;
		this.normalizationFactor = normalizationFactor;
		this.minPeakValue = minPeakValue;

		this.dogImg = null;
		this.keepDoGImg = false;
	}

	public void setMinPeakValue( final double value ) { this.minPeakValue = value; }
	public double getMinPeakValue() { return minPeakValue; }
	public Img<FloatType> getDoGImg() { return dogImg; }
	public void setKeepDoGImg( final boolean keepDoGImg ) { this.keepDoGImg = keepDoGImg; }
	public boolean getKeepDoGImg() { return keepDoGImg; }
	public ArrayList<DifferenceOfGaussianPeak<FloatType>> getPeaks() { return peaks; }
	public void setComputeConvolutionsParalell( final boolean paralell ) { this.computeConvolutionsParalell = paralell; }
	public boolean getComputeConvolutionsParalell() { return computeConvolutionsParalell; }

	protected Img< FloatType > computeGaussianConvolution( final double[] sigma )
	{
		return Gauss.toFloat( sigma, image, outOfBoundsFactory );
	}

	/**
	 * Returns the function that does the normalized subtraction of the gauss images, more efficient versions can override this method
	 * @return - the Subtraction Function
	 */
	protected Function<FloatType, FloatType, FloatType> getNormalizedSubtraction()
	{
		return new SubtractNormReal<FloatType, FloatType, FloatType >(normalizationFactor);
	}

	/**
	 * Checks if the absolute value of the current peak is high enough, more efficient versions can override this method
	 * @param value - the current value
	 * @return true if the absoluted value is high enough, otherwise false
	 */
	protected boolean isPeakHighEnough( final double value )
	{
		return Math.abs( value ) >= minPeakValue;
	}

	/**
	 * Checks if the current position is a minima or maxima in a 3^n neighborhood, more efficient versions can override this method
	 *
	 * @param neighborhoodCursor - the {@link LocalNeighborhoodCursor}
	 * @param centerValue - the value in the center which is tested
	 * @return - if is a minimum, maximum or nothig
	 */
	protected SpecialPoint isSpecialPoint( final LocalNeighborhoodCursor<FloatType> neighborhoodCursor, final FloatType centerValue )
	{
		boolean isMin = true;
		boolean isMax = true;

		final double centerValueReal = centerValue.getRealDouble();

		while ( (isMax || isMin) && neighborhoodCursor.hasNext() )
		{
			neighborhoodCursor.fwd();

			final double value = neighborhoodCursor.get().getRealDouble();

			// it can still be a minima if the current value is bigger/equal to the center value
			isMin &= (value >= centerValueReal);

			// it can still be a maxima if the current value is smaller/equal to the center value
			isMax &= (value <= centerValueReal);
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

		//
		// perform the gaussian convolutions transferring it to the new (potentially higher precision) type T
		//
        final Img<FloatType> gauss1 = computeGaussianConvolution( sigma1 );
        final Img<FloatType> gauss2 = computeGaussianConvolution( sigma2 );

        //
        // subtract the images to get the LaPlace image
        //
        final Function<FloatType, FloatType, FloatType> function = getNormalizedSubtraction();
        final ImageCalculatorInPlace<FloatType, FloatType> imageCalc = new ImageCalculatorInPlace<FloatType, FloatType>( gauss2, gauss1, function );

        imageCalc.process();

        //
        // Now we find minima and maxima in the DoG image
        //
		peaks.clear();
		peaks.addAll( findPeaks( gauss2 ) );

		if ( keepDoGImg )
			dogImg = gauss2;

        processingTime = System.currentTimeMillis() - startTime;

		return true;
	}

	public ArrayList<DifferenceOfGaussianPeak<FloatType>> findPeaks( final Img<FloatType> laPlace )
	{
	    final AtomicInteger ai = new AtomicInteger( 0 );
	    final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );
	    final int nThreads = threads.length;
	    final int numDimensions = laPlace.numDimensions();

	    final Vector< ArrayList<DifferenceOfGaussianPeak<FloatType>> > threadPeaksList = new Vector< ArrayList<DifferenceOfGaussianPeak<FloatType>> >();

	    for ( int i = 0; i < nThreads; ++i )
	    	threadPeaksList.add( new ArrayList<DifferenceOfGaussianPeak<FloatType>>() );

		for (int ithread = 0; ithread < threads.length; ++ithread)
	        threads[ithread] = new Thread(new Runnable()
	        {
	            @Override
				public void run()
	            {
	            	final int myNumber = ai.getAndIncrement();

	            	final ArrayList<DifferenceOfGaussianPeak<FloatType>> myPeaks = threadPeaksList.get( myNumber );
	            	final Cursor<FloatType> cursor = laPlace.localizingCursor();
	            	final LocalNeighborhoodCursor<FloatType> neighborhoodCursor = new LocalNeighborhoodCursor<FloatType>( laPlace, cursor );

	            	final long[] position = new long[ numDimensions ];
	            	final long[] dimensionsMinus2 = new long[ laPlace.numDimensions() ];
	            	laPlace.dimensions( dimensionsMinus2 );

            		for ( int d = 0; d < numDimensions; ++d )
            			dimensionsMinus2[ d ] -= 2;

MainLoop:           while ( cursor.hasNext() )
	                {
	                	cursor.fwd();
	                	cursor.localize( position );

	                	if ( position[ 0 ] % nThreads == myNumber )
	                	{
	                		for ( int d = 0; d < numDimensions; ++d )
	                		{
	                			final long pos = position[ d ];

	                			if ( pos < 1 || pos > dimensionsMinus2[ d ] )
	                				continue MainLoop;
	                		}

	                		// if we do not clone it here, it might be moved along with the cursor
	                		// depending on the container type used
	                		final FloatType currentValue = cursor.get().copy();

	                		// it can never be a desired peak as it is too low
	                		if ( !isPeakHighEnough( currentValue.get() ) )
                				continue;

                			// update to the current position
                			neighborhoodCursor.updateCenter( cursor );

                			// we have to compare for example 26 neighbors in the 3d case (3^3 - 1) relative to the current position
                			final SpecialPoint specialPoint = isSpecialPoint( neighborhoodCursor, currentValue );
                			if ( specialPoint != SpecialPoint.INVALID )
                				myPeaks.add( new DifferenceOfGaussianPeak<FloatType>( position, currentValue, specialPoint ) );

                			// reset the position of the parent cursor
                			neighborhoodCursor.reset();
	                	}
	                }
            }
        });

		SimpleMultiThreading.startAndJoin( threads );

		// put together the list from the various threads
		final ArrayList<DifferenceOfGaussianPeak<FloatType>> dogPeaks = new ArrayList<DifferenceOfGaussianPeak<FloatType>>();

		for ( final ArrayList<DifferenceOfGaussianPeak<FloatType>> peakList : threadPeaksList )
			dogPeaks.addAll( peakList );

		return dogPeaks;
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
			errorMessage = "DifferenceOfGaussianOld: [Img<A> img] is null.";
			return false;
		}
		else if ( factory == null )
		{
			errorMessage = "DifferenceOfGaussianOld: [ImgFactory<FloatType> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactory == null )
		{
			errorMessage = "DifferenceOfGaussianOld: [OutOfBoundsStrategyFactory<FloatType>] is null.";
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
