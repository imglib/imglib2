/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
 * #L%
 */

package net.imglib2.histogram;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.integer.LongType;

/**
 * A Histogram1d is a histogram that tracks up to four kinds of values: 1)
 * values in the center of the distribution 2) values to the left of the center
 * of the distribution (lower tail) 3) values to the right of the center of the
 * distribution (upper tail) 4) values outside the other areas
 * <p>
 * Note: the last three classifications may not be present depending upon the
 * makeup of the input data.
 * 
 * @author Barry DeZonia
 */
public class Histogram1d< T > implements Img< LongType >
{

	// -- instance variables --

	private T firstValue;

	private BinMapper1d< T > mapper;

	private DiscreteFrequencyDistribution distrib;

	private long[] pos;

	private long ignoredCount;

	// -- constructor --

	/**
	 * Construct a histogram from a bin mapping algorithm. Use countData() to
	 * populate it.
	 * 
	 * @param mapper
	 *            The algorithm used to map values to bins
	 */
	public Histogram1d( final BinMapper1d< T > mapper )
	{
		this.mapper = mapper;
		this.distrib =
				new DiscreteFrequencyDistribution( new long[] { mapper.getBinCount() } );
		this.pos = new long[ 1 ];
		this.ignoredCount = 0;
	}

	/**
	 * Construct a histogram whose bin mappings match another histogram. After
	 * this construction the histogram bins are unpopulated.
	 * 
	 * @param other
	 *            The histogram to copy.
	 */
	public Histogram1d( final Histogram1d< T > other )
	{
		mapper = other.mapper.copy();
		distrib = other.distrib.copy();
		pos = other.pos.clone();
		// TODO - is reset what we really want? or copy the exact counts too?
		reset();
	}

	/**
	 * Construct a histogram from an iterable set of data and a bin mapping
	 * algorithm.
	 * 
	 * @param data
	 *            The iterable set of values to calculate upon
	 * @param mapper
	 *            The algorithm used to map values to bins
	 */
	public Histogram1d( final Iterable< T > data, final BinMapper1d< T > mapper )
	{
		this( mapper );
		init( data );
	}

	// -- public api --

	/**
	 * Returns the first data value of the input iteration.
	 */
	public T firstDataValue()
	{
		return firstValue;
	}

	/**
	 * Returns true if the histogram has tail bins at both ends which count
	 * extreme values.
	 */
	public boolean hasTails()
	{
		return mapper.hasTails();
	}

	/**
	 * Returns the frequency count of values in the lower tail bin (if any).
	 */
	public long lowerTailCount()
	{
		if ( !hasTails() )
			return 0;
		pos[ 0 ] = 0;
		return distrib.frequency( pos );
	}

	/**
	 * Returns the frequency count of values in the upper tail bin (if any).
	 */
	public long upperTailCount()
	{
		if ( !hasTails() )
			return 0;
		pos[ 0 ] = mapper.getBinCount() - 1;
		return distrib.frequency( pos );
	}

	/**
	 * Returns the frequency count of all values in the middle of the
	 * distribution.
	 */
	public long valueCount()
	{
		return distributionCount() - lowerTailCount() - upperTailCount();
	}

	/**
	 * Returns the frequency count of all values in the distribution: lower tail
	 * + middle + upper tail. Does not include ignored values.
	 */
	public long distributionCount()
	{
		return distrib.totalValues();
	}

	/**
	 * Returns the frequency count of values that were ignored because they
	 * could not be mapped to any bin.
	 */
	public long ignoredCount()
	{
		return ignoredCount;
	}

	/**
	 * Returns the total count of all values observed; both within and without
	 * the entire distribution. Thus it includes ignored values. One should
	 * decide carefully between using distributionCount() and totalCount().
	 */
	public long totalCount()
	{
		return distributionCount() + ignoredCount();
	}

	/**
	 * Returns the frequency count of values within a bin using a representative
	 * value. Note that multiple values can be mapped to one bin so this is NOT
	 * the frequency count of this exact value in the distribution.
	 * 
	 * @param value
	 *            A representative value of interest
	 */
	public long frequency( final T value )
	{
		final long bin = mapper.map( value );
		return frequency( bin );
	}

	/**
	 * Returns the frequency count of the values within a bin.
	 */
	public long frequency( final long binPos )
	{
		if ( binPos < 0 || binPos >= mapper.getBinCount() )
			return 0;
		pos[ 0 ] = binPos;
		return distrib.frequency( pos );
	}

	/**
	 * Returns the relative frequency of values within a bin using a
	 * representative value. Note that multiple values can be mapped to one bin
	 * so this is NOT the relative frequency of this exact value in the
	 * distribution.
	 * <p>
	 * This calculation is of the number of values in the bin divided by either
	 * the number of values in the distribution or the number of values in the
	 * center of the distribution (tails ignored).
	 * <p>
	 * One can devise other ways to count relative frequencies that consider
	 * ignored values also. If needed one can use the various count methods and
	 * frequency methods to calculate any relative frequency desired.
	 * 
	 * @param value
	 *            A representative value of interest
	 * @param includeTails
	 *            Flag for determining whether to include tails in calculation.
	 */
	public double relativeFrequency( final T value, final boolean includeTails )
	{
		final long bin = mapper.map( value );
		return relativeFrequency( bin, includeTails );
	}

	/**
	 * Returns the relative frequency of values within a bin.
	 * <p>
	 * This calculation is of the number of values in the bin divided by either
	 * the number of values in the distribution or the number of values in the
	 * center of the distribution (tails ignored).
	 * <p>
	 * One can devise other ways to count relative frequencies that consider
	 * ignored values also. If needed one can use the various count methods and
	 * frequency methods to calculate any relative frequency desired.
	 * 
	 * @param binPos
	 *            The position of the bin of interest
	 * @param includeTails
	 *            Flag for determining whether to include tails in calculation.
	 */
	public double relativeFrequency( final long binPos, final boolean includeTails )
	{
		final double numer = frequency( binPos );
		final long denom = includeTails ? distributionCount() : valueCount();
		return numer / denom;
	}

	/**
	 * Returns the number of bins contained in the histogram.
	 */
	public long getBinCount()
	{
		return mapper.getBinCount();
	}

	/**
	 * Returns a bin position by mapping from a representative value.
	 */
	public long map( final T value )
	{
		return mapper.map( value );
	}

	/**
	 * Gets the value associated with the center of a bin.
	 * 
	 * @param binPos
	 *            The bin number of interest
	 * @param value
	 *            The output to fill with the center value
	 */
	public void getCenterValue( final long binPos, final T value )
	{
		mapper.getCenterValue( binPos, value );
	}

	/**
	 * Gets the value associated with the left edge of a bin.
	 * 
	 * @param binPos
	 *            The bin number of interest
	 * @param value
	 *            The output to fill with the left edge value
	 */
	public void getLowerBound( final long binPos, final T value )
	{
		mapper.getLowerBound( binPos, value );
	}

	/**
	 * Gets the value associated with the right edge of the bin.
	 * 
	 * @param binPos
	 *            The bin number of interest
	 * @param value
	 *            The output to fill with the right edge value
	 */
	public void getUpperBound( final long binPos, final T value )
	{
		mapper.getUpperBound( binPos, value );
	}

	/**
	 * Returns true if the given bin interval is closed on the right
	 * 
	 * @param binPos
	 *            The bin number of the interval of interest
	 */
	public boolean includesUpperBound( final long binPos )
	{
		return mapper.includesUpperBound( binPos );
	}

	/**
	 * Returns true if the given bin interval is closed on the left
	 * 
	 * @param binPos
	 *            The bin number of the interval of interest
	 */
	public boolean includesLowerBound( final long binPos )
	{
		return mapper.includesLowerBound( binPos );
	}

	/**
	 * Returns true if a given value is mapped to the lower tail of the
	 * distribution.
	 * 
	 * @param value
	 *            The value to determine the location of
	 */
	public boolean isInLowerTail( final T value )
	{
		if ( !hasTails() )
			return false;
		final long bin = mapper.map( value );
		return bin == 0;
	}

	/**
	 * Returns true if a given value is mapped to the upper tail of the
	 * distribution.
	 * 
	 * @param value
	 *            The value to determine the location of
	 */
	public boolean isInUpperTail( final T value )
	{
		if ( !hasTails() )
			return false;
		final long bin = mapper.map( value );
		return bin == getBinCount() - 1;
	}

	/**
	 * Returns true if a given value is mapped to the middle of the
	 * distribution.
	 * 
	 * @param value
	 *            The value to determine the location of
	 */
	public boolean isInMiddle( final T value )
	{
		if ( !hasTails() )
			return true;
		final long bin = mapper.map( value );
		return ( bin > 0 ) && ( bin < getBinCount() - 1 );
	}

	/**
	 * Returns true if a given value is outside the distribution.
	 * 
	 * @param value
	 *            The value to determine the location of
	 */
	public boolean isOutside( final T value )
	{
		final long bin = mapper.map( value );
		return ( bin == Long.MIN_VALUE ) || ( bin == Long.MAX_VALUE );
	}

	/**
	 * Get the discrete frequency distribution associated with this histogram.
	 */
	public DiscreteFrequencyDistribution dfd()
	{
		return distrib;
	}

	/**
	 * Counts the data contained in the given data source using the underlying
	 * bin distribution.
	 * 
	 * @param data
	 *            The total data to count
	 */
	public void countData( final Iterable< T > data )
	{
		init( data );
	}

	/**
	 * Counts additional data contained in a given iterable collection. One can
	 * use this to update an existing histogram with a subset of values.
	 * 
	 * @param data
	 *            The new data to count
	 */
	public void addData( final Iterable< T > data )
	{
		add( data );
	}

	/**
	 * Uncounts some original data contained in a given iterable collection. One
	 * can use this to update an existing histogram with a subset of values.
	 * 
	 * @param data
	 *            The old data to uncount
	 */
	public void subtractData( final Iterable< T > data )
	{
		subtract( data );
	}

	/**
	 * Directly increment a bin by position.
	 * 
	 * @param binPos
	 *            The 1-d index of the bin
	 */
	public void increment( final long binPos )
	{
		pos[ 0 ] = binPos;
		distrib.increment( pos );
	}

	/**
	 * Directly decrement a bin by position.
	 * 
	 * @param binPos
	 *            The 1-d index of the bin
	 */
	public void decrement( final long binPos )
	{
		pos[ 0 ] = binPos;
		distrib.decrement( pos );
	}

	/**
	 * Directly increment a bin by value.
	 * 
	 * @param value
	 *            The value to map to a bin position
	 */
	public void increment( final T value )
	{
		final long bin = mapper.map( value );
		if ( bin == Long.MIN_VALUE || bin == Long.MAX_VALUE )
		{
			ignoredCount++;
		}
		else
		{
			pos[ 0 ] = bin;
			distrib.increment( pos );
		}
	}

	/**
	 * Directly decrement a bin by value,
	 * 
	 * @param value
	 *            The value to map to a bin position
	 */
	public void decrement( final T value )
	{
		final long bin = mapper.map( value );
		if ( bin == Long.MIN_VALUE || bin == Long.MAX_VALUE )
		{
			ignoredCount--;
		}
		else
		{
			pos[ 0 ] = bin;
			distrib.decrement( pos );
		}
	}

	/**
	 * Resets all data counts to 0.
	 */
	public void resetCounters()
	{
		reset();
	}

	/**
	 * Returns a bare long[] histogram with the same bin counts as this
	 * histogram.
	 */
	public long[] toLongArray()
	{
		final long[] result = new long[ ( int ) getBinCount() ];
		final Cursor< LongType > cursor = cursor();
		int i = 0;
		while ( cursor.hasNext() )
		{
			result[ i++ ] = cursor.next().get();
		}
		return result;
	}

	// -- delegated Img methods --

	/**
	 * Return the number of dimensions of the frequency distribution of this
	 * histogram.
	 */
	@Override
	public int numDimensions()
	{
		return distrib.numDimensions();
	}

	/**
	 * Return the size of the given dimension of the frequency distribution of
	 * this histogram.
	 */
	@Override
	public long dimension( final int d )
	{
		return distrib.dimension( d );
	}

	/**
	 * Fill the provided long[] with the sizes of all dimensions of the
	 * frequency distribution of this histogram.
	 */
	@Override
	public void dimensions( final long[] dims )
	{
		distrib.dimensions( dims );
	}

	@Override
	public RandomAccess< LongType > randomAccess()
	{
		return distrib.randomAccess();
	}

	@Override
	public RandomAccess< LongType > randomAccess( final Interval interval )
	{
		return distrib.randomAccess( interval );
	}

	public long min()
	{
		return min( 0 );
	}

	@Override
	public long min( final int d )
	{
		return distrib.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		distrib.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		distrib.min( min );
	}

	public long max()
	{
		return max( 0 );
	}

	@Override
	public long max( final int d )
	{
		return distrib.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		distrib.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		distrib.max( max );
	}

	public double realMin()
	{
		return realMin( 0 );
	}

	@Override
	public double realMin( final int d )
	{
		return distrib.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		distrib.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		distrib.realMin( min );
	}

	public double realMax()
	{
		return realMax( 0 );
	}

	@Override
	public double realMax( final int d )
	{
		return distrib.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		distrib.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		distrib.realMax( max );
	}

	@Override
	public Cursor< LongType > cursor()
	{
		return distrib.cursor();
	}

	@Override
	public Cursor< LongType > localizingCursor()
	{
		return distrib.localizingCursor();
	}

	@Override
	public long size()
	{
		return distrib.size();
	}

	@Override
	public LongType firstElement()
	{
		return distrib.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return distrib.iterationOrder();
	}

	@Override
	public Iterator< LongType > iterator()
	{
		return distrib.iterator();
	}

	@Override
	public ImgFactory< LongType > factory()
	{
		return distrib.factory();
	}

	@Override
	public Histogram1d< T > copy()
	{
		return new Histogram1d< T >( this );
	}

	// -- helpers --

	private void reset()
	{
		distrib.resetCounters();
		ignoredCount = 0;
		firstValue = null;
	}

	private void init( final Iterable< T > data )
	{
		reset();

		// record the first element
		final Iterator<T> iter = data.iterator();
		if ( iter.hasNext() )
		{
			firstValue = iter.next();
			increment( firstValue );
		}

		// record the rest of the elements
		while ( iter.hasNext() )
		{
			increment ( iter.next() );
		}
	}

	private void add( final Iterable< T > data )
	{
		for ( final T value : data )
		{
			increment( value );
		}
	}

	private void subtract( final Iterable< T > data )
	{
		for ( final T value : data )
		{
			decrement( value );
		}
	}

}
