/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.LongType;

/**
 * This class represents an n-dimensional set of counters. Histogram
 * implementations use these for tracking value counts.
 * 
 * @author Barry DeZonia
 */
public class DiscreteFrequencyDistribution implements Img< LongType >
{

	// -- instance variables --

	private final Img< LongType > counts;

	private final RandomAccess< LongType > accessor;

	private long totalValues;

	// -- public api --

	/**
	 * Construct an n-dimensional counter with the given number of bins
	 */
	public DiscreteFrequencyDistribution( final long[] binCounts )
	{
		// check inputs for issues

		for ( int i = 0; i < binCounts.length; i++ )
		{
			if ( binCounts[ i ] <= 0 ) { throw new IllegalArgumentException( "invalid bin count (<= 0)" ); }
		}

		// then build object

		counts = new ArrayImgFactory< LongType >().create( binCounts, new LongType() );

		accessor = counts.randomAccess();

		totalValues = 0;
	}

	/**
	 * Construct an n-dimensional counter using a provided Img<LongType> to
	 * store counts.
	 */
	public DiscreteFrequencyDistribution( final Img< LongType > img )
	{
		counts = img;
		accessor = counts.randomAccess();
		resetCounters();
	}

	/**
	 * Resets all frequency counts to zero.
	 */
	public void resetCounters()
	{
		final Cursor< LongType > cursor = counts.cursor();
		while ( cursor.hasNext() )
		{
			cursor.next().setZero();
		}
		totalValues = 0;
	}

	/**
	 * Returns the frequency count associated with a given bin.
	 */
	public long frequency( final long[] binPos )
	{
		for ( int i = 0; i < accessor.numDimensions(); i++ )
		{
			if ( binPos[ i ] < 0 || binPos[ i ] >= dimension( i ) )
				return 0;
		}
		accessor.setPosition( binPos );
		return accessor.get().get();
	}

	/**
	 * Sets the frequency count associated with a given bin.
	 */
	public void setFrequency( final long[] binPos, final long value )
	{
		if ( value < 0 ) { throw new IllegalArgumentException( "frequency count must be >= 0" ); }
		accessor.setPosition( binPos );
		final long currentValue = accessor.get().get();
		totalValues += ( value - currentValue );
		accessor.get().set( value );
	}

	/**
	 * Returns the relative frequency (0 <= f <= 1) associated with a given bin.
	 */
	public double relativeFrequency( final long[] binPos )
	{
		if ( totalValues == 0 )
			return 0;
		return 1.0 * frequency( binPos ) / totalValues;
	}

	/**
	 * Increments the frequency count of a specified bin.
	 */
	public void increment( final long[] binPos )
	{
		accessor.setPosition( binPos );
		accessor.get().inc();
		totalValues++;
	}

	/**
	 * Decrements the frequency count of a specified bin.
	 */
	public void decrement( final long[] binPos )
	{
		accessor.setPosition( binPos );
		accessor.get().dec();
		totalValues--;
	}

	/**
	 * Returns the total number of values counted by this distribution.
	 */
	public long totalValues()
	{
		return totalValues;
	}

	/**
	 * Returns the highest frequency count found within the bins.
	 */
	public long modeCount()
	{
		final List< long[] > modes = modePositions();
		return frequency( modes.get( 0 ) );
	}

	/**
	 * Returns a list of bin positions of the highest frequency bins.
	 */
	public List< long[] > modePositions()
	{
		long commonValue = 0;
		final List< long[] > modePositions = new ArrayList< long[] >();
		final Cursor< LongType > cursor = localizingCursor();
		while ( cursor.hasNext() )
		{
			final long val = cursor.next().get();
			if ( val > commonValue )
			{
				commonValue = val;
				modePositions.clear();
				final long[] pos = new long[ numDimensions() ];
				cursor.localize( pos );
				modePositions.add( pos );
			}
			else if ( val == commonValue )
			{
				final long[] pos = new long[ numDimensions() ];
				cursor.localize( pos );
				modePositions.add( pos );
			}
		}
		return modePositions;
	}

	// -- Img methods --

	@Override
	public RandomAccess< LongType > randomAccess()
	{
		return counts.randomAccess();
	}

	@Override
	public RandomAccess< LongType > randomAccess( final Interval interval )
	{
		return counts.randomAccess( interval );
	}

	@Override
	public int numDimensions()
	{
		return counts.numDimensions();
	}

	@Override
	public long min( final int d )
	{
		return counts.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		counts.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		counts.min( min );
	}

	@Override
	public long max( final int d )
	{
		return counts.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		counts.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		counts.max( max );
	}

	@Override
	public double realMin( final int d )
	{
		return counts.realMin( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		counts.realMin( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		counts.realMin( min );
	}

	@Override
	public double realMax( final int d )
	{
		return counts.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		counts.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		counts.realMax( max );
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		counts.dimensions( dimensions );
	}

	@Override
	public long dimension( final int d )
	{
		return counts.dimension( d );
	}

	@Override
	public Cursor< LongType > cursor()
	{
		return counts.cursor();
	}

	@Override
	public Cursor< LongType > localizingCursor()
	{
		return counts.localizingCursor();
	}

	@Override
	public long size()
	{
		return counts.size();
	}

	@Override
	public LongType firstElement()
	{
		return counts.firstElement();
	}

	@Override
	public Object iterationOrder()
	{
		return counts.iterationOrder();
	}

	@Override
	public Iterator< LongType > iterator()
	{
		return counts.iterator();
	}

	@Override
	public ImgFactory< LongType > factory()
	{
		return counts.factory();
	}

	@Override
	public DiscreteFrequencyDistribution copy()
	{
		return new DiscreteFrequencyDistribution( counts.copy() );
	}

}
