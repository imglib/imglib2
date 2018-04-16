/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import java.util.List;

import net.imglib2.type.numeric.IntegerType;

/**
 * Maps integer values into a 1-d set of bins.
 * 
 * @author Barry DeZonia
 */
public class Integer1dBinMapper< T extends IntegerType< T >> implements
		BinMapper1d< T >
{

	// -- instance variables --

	private final long bins;

	private final long minVal, maxVal;

	private final boolean tailBins;

	// -- constructor --

	/**
	 * Specify a mapping of integral data from a user defined range into a
	 * specified number of bins. If tailBins is true then there will be two bins
	 * that count values outside the user specified ranges. If false then values
	 * outside the range fail to map to any bin.
	 * 
	 * @param minVal
	 *            The first data value of interest.
	 * @param numBins
	 *            The total number of bins to create.
	 * @param tailBins
	 *            A boolean specifying whether to have a bin in each tail to
	 *            count values outside the user defined range.
	 */
	public Integer1dBinMapper( final long minVal, final long numBins, final boolean tailBins )
	{
		this.bins = numBins;
		this.tailBins = tailBins;
		this.minVal = minVal;
		if ( tailBins )
		{
			this.maxVal = minVal + numBins - 1 - 2;
		}
		else
		{
			this.maxVal = minVal + numBins - 1;
		}
		if ( ( bins <= 0 ) || ( tailBins && bins <= 2 ) ) { throw new IllegalArgumentException(
				"invalid Integer1dBinMapper: no data bins specified" ); }
	}

	// -- BinMapper methods --

	@Override
	public long getBinCount()
	{
		return bins;
	}

	@Override
	public long map( final T value )
	{
		final long val = value.getIntegerLong();
		long pos;
		if ( val >= minVal && val <= maxVal )
		{
			pos = val - minVal;
			if ( tailBins )
				pos++;
		}
		else if ( tailBins )
		{
			if ( val < minVal )
				pos = 0;
			else
				pos = bins - 1;
		}
		else
		{ // no tail bins and we are outside
			if ( val < minVal )
				pos = Long.MIN_VALUE;
			else
				pos = Long.MAX_VALUE;
		}
		return pos;
	}

	@Override
	public void getCenterValue( final long binPos, final T value )
	{
		long val;
		if ( tailBins )
		{
			if ( binPos == 0 )
				val = minVal - 1; // TODO HACK - what is best to return?
			else if ( binPos == bins - 1 )
				val = maxVal + 1; // TODO same HACK
			else
				val = minVal + binPos - 1;
		}
		else
		{ // no tail bins
			val = minVal + binPos;
		}
		value.setInteger( val );
	}

	@Override
	public void getLowerBound( final long binPos, final T value )
	{
		if ( tailBins && ( binPos == 0 || binPos == bins - 1 ) )
		{
			if ( binPos == 0 )
				value.setInteger( Long.MIN_VALUE + 1 );
			else
				value.setInteger( maxVal + 1 );
		}
		else
		{
			getCenterValue( binPos, value );
		}
	}

	@Override
	public void getUpperBound( final long binPos, final T value )
	{
		if ( tailBins && ( binPos == 0 || binPos == bins - 1 ) )
		{
			if ( binPos == 0 )
				value.setInteger( minVal - 1 );
			else
				value.setInteger( Long.MAX_VALUE - 1 );
		}
		else
		{
			getCenterValue( binPos, value );
		}
	}

	@Override
	public boolean includesLowerBound( final long binPos )
	{
		return true;
	}

	@Override
	public boolean includesUpperBound( final long binPos )
	{
		return true;
	}

	@Override
	public boolean hasTails()
	{
		return tailBins;
	}

	@Override
	public Integer1dBinMapper< T > copy()
	{
		return new Integer1dBinMapper< T >( minVal, bins, tailBins );
	}

	/**
	 * This is a convenience method for creating a {@link HistogramNd} from
	 * inputs that describe a set of integer 1-d based bin mappers. The inputs
	 * should all have n entries for an n-d set of mappers.
	 * 
	 * @param minVals
	 *            The minimum bin values for each dimension
	 * @param numBins
	 *            The total bin count for each dimension
	 * @param tailBins
	 *            Flags per dimension for whether to include tail bins
	 * @return An unpopulated HistogramNd
	 */
	public static < K extends IntegerType< K >> HistogramNd< K > histogramNd(
			final long[] minVals, final long[] numBins, final boolean[] tailBins )
	{
		if ( ( minVals.length != numBins.length ) ||
				( minVals.length != tailBins.length ) ) { throw new IllegalArgumentException(
				"multiDimMapper: differing input array sizes" ); }
		final List< BinMapper1d< K >> binMappers = new ArrayList< BinMapper1d< K >>();
		for ( int i = 0; i < minVals.length; i++ )
		{
			final Integer1dBinMapper< K > mapper =
					new Integer1dBinMapper< K >( minVals[ i ], numBins[ i ], tailBins[ i ] );
			binMappers.add( mapper );
		}
		return new HistogramNd< K >( binMappers );
	}
}
