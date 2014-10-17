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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.algorithm.function.Function;
import net.imglib2.algorithm.function.SubtractNormReal;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.algorithm.region.localneighborhood.old.LocalNeighborhoodCursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

public class DifferenceOfGaussian < A extends RealType<A> > implements Algorithm, MultiThreaded, Benchmark
{
	public static enum SpecialPoint { INVALID, MIN, MAX }

	protected final RandomAccessibleInterval<A> image;
	protected Img<FloatType> dogImg;
	protected final ImgFactory<A> factory;
	protected final OutOfBoundsFactory<A, RandomAccessibleInterval<A>> outOfBoundsFactory;

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

	/** Calls the DifferenceOfGaussian constructor with the given sigmas copied into double[] arrays,
	 * one entry per {@param img} dimension. */
	public DifferenceOfGaussian( final RandomAccessibleInterval<A> img, final ImgFactory<A> factory,
		    final OutOfBoundsFactory<A, RandomAccessibleInterval<A>> outOfBoundsFactory,
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
	public DifferenceOfGaussian( final RandomAccessibleInterval<A> img, final ImgFactory<A> factory,
			    final OutOfBoundsFactory<A, RandomAccessibleInterval<A>> outOfBoundsFactory,
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
		final FloatType type = new FloatType();
		Img< FloatType > target = null;
		try
		{
			target = this.factory.imgFactory( type ).create( image, type );
			Gauss3.gauss( sigma, Views.extend( image, outOfBoundsFactory ), target );
		}
		catch ( final IncompatibleTypeException e )
		{
			e.printStackTrace();
		}
		return target;
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

	/**
	 * Checks if the current position is a minima or maxima in a 3^n neighborhood, more efficient versions can override this method
	 *
	 * @param neighborhoodCursor - the {@link LocalNeighborhoodCursor}
	 * @param centerValue - the value in the center which is tested
	 * @return - if is a minimum, maximum or nothig
	 */
	public static final < T extends Comparable< T > > SpecialPoint isSpecialPoint( final Neighborhood< T > neighborhood, final T centerValue )
	{
		final Cursor< T > c = neighborhood.cursor();
		while( c.hasNext() )
		{
			final int comp = centerValue.compareTo( c.next() );
			if ( comp < 0 )
			{
				// it can only be a minimum
				while ( c.hasNext() )
					if ( centerValue.compareTo( c.next() ) > 0 )
						return SpecialPoint.INVALID;
				// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
				return SpecialPoint.MAX;
			}
			else if ( comp > 0 )
			{
				// it can only be a maximum
				while ( c.hasNext() )
					if ( centerValue.compareTo( c.next() ) < 0 )
						return SpecialPoint.INVALID;
				// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
				return SpecialPoint.MIN;
			}
		}
		return SpecialPoint.MIN; // all neighboring pixels have the same value. count it as MIN.
	}

	/**
	 * Checks if the current position is a minima or maxima in a 3^n neighborhood, more efficient versions can override this method
	 *
	 * @param neighborhoodCursor - the {@link LocalNeighborhoodCursor}
	 * @param centerValue - the value in the center which is tested
	 * @return - if is a minimum, maximum or nothig
	 */
	private final SpecialPoint isSpecialPointFloat( final Neighborhood< FloatType > neighborhood, final float centerValue )
	{
		final Cursor< FloatType > c = neighborhood.cursor();
		while( c.hasNext() )
		{
			final float v = c.next().get();
			if ( centerValue < v )
			{
				// it can only be a minimum
				while ( c.hasNext() )
					if ( centerValue > c.next().get() )
						return SpecialPoint.INVALID;
				// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
				return SpecialPoint.MAX;
			}
			else if ( centerValue > v )
			{
				// it can only be a maximum
				while ( c.hasNext() )
					if ( centerValue < c.next().get() )
						return SpecialPoint.INVALID;
				// this mixup is intended, a minimum in the 2nd derivation is a maxima in image space and vice versa
				return SpecialPoint.MIN;
			}
		}
		return SpecialPoint.MIN; // all neighboring pixels have the same value. count it as MIN.
	}

	public ArrayList<DifferenceOfGaussianPeak<FloatType>> findPeaks( final Img<FloatType> laPlace )
	{
		final int numThreads = getNumThreads();

		final ArrayList< ArrayList< DifferenceOfGaussianPeak< FloatType >> > threadPeaksList = new ArrayList< ArrayList< DifferenceOfGaussianPeak< FloatType >> >();

	    final Interval full = Intervals.expand( laPlace, -1 );
	    final int n = laPlace.numDimensions();
	    final int splitd = n - 1;
		final int numTasks = numThreads <= 1 ? 1 : (int) Math.min( full.dimension( splitd ), numThreads * 20 );
	    final long dsize = full.dimension( splitd ) / numTasks;
	    final long[] min = new long[ n ];
	    final long[] max = new long[ n ];
	    full.min( min );
	    full.max( max );

	    final RectangleShape shape = new RectangleShape( 1, true );

		final ExecutorService ex = Executors.newFixedThreadPool( numThreads );
		for ( int taskNum = 0; taskNum < numTasks; ++taskNum )
		{
			min[ splitd ] = full.min( splitd ) + taskNum * dsize;
			max[ splitd ] = ( taskNum == numTasks - 1 ) ? full.max( splitd ) : min[ splitd ] + dsize - 1;
			final RandomAccessibleInterval< FloatType > source = Views.interval( laPlace, new FinalInterval( min, max ) );
			final ArrayList< DifferenceOfGaussianPeak< FloatType >> myPeaks = new ArrayList< DifferenceOfGaussianPeak< FloatType >>();
			threadPeaksList.add( myPeaks );
			final Runnable r = new Runnable()
			{
				@Override
				public void run()
				{
					final Cursor< FloatType > center = Views.iterable( source ).cursor();
					for ( final Neighborhood< FloatType > neighborhood : shape.neighborhoods( source ) )
					{
						final float centerValue = center.next().get();
						if ( isPeakHighEnough( centerValue ) )
						{
							final SpecialPoint specialPoint = isSpecialPointFloat( neighborhood, centerValue );
							if ( specialPoint != SpecialPoint.INVALID )
								myPeaks.add( new DifferenceOfGaussianPeak< FloatType >( center, center.get(), specialPoint ) );
						}
					}
				}
			};
			ex.execute( r );
		}
		ex.shutdown();
		try
		{
			ex.awaitTermination( 1000, TimeUnit.DAYS );
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}

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
			errorMessage = "DifferenceOfGaussian: [Img<A> img] is null.";
			return false;
		}
		else if ( factory == null )
		{
			errorMessage = "DifferenceOfGaussian: [ImgFactory<A> img] is null.";
			return false;
		}
		else if ( outOfBoundsFactory == null )
		{
			errorMessage = "DifferenceOfGaussian: [OutOfBoundsStrategyFactory<A>] is null.";
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
