/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.type.numeric.RealType;

/**
 * Maps real values into a 1-d set of bins. Though this class will work with
 * integral data types it is really more appropriate to do so using a
 * {@link Integer1dBinMapper}.
 * 
 * @author Barry DeZonia
 */
public class Real1dBinMapper< T extends RealType< T >> implements BinMapper1d< T >
{

	// -- instance variables --

	private final long bins;

	private final double minVal, maxVal;

	private final boolean tailBins;

	private final double binWidth;

	private final long interiorBins;

	// -- constructor --

	/**
	 * Specify a mapping of real data from a user defined range into a specified
	 * number of bins. If tailBins is true then there will be two bins that
	 * count values outside the user specified ranges. If false then values
	 * outside the range fail to map to any bin.
	 * 
	 * @param minVal
	 *            The first data value of interest.
	 * @param maxVal
	 *            The last data value of interest.
	 * @param numBins
	 *            The total number of bins to create.
	 * @param tailBins
	 *            A boolean specifying whether to have a bin in each tail to
	 *            count values outside the user defined range.
	 */
	public Real1dBinMapper( final double minVal, final double maxVal, final long numBins,
			final boolean tailBins )
	{
		this.bins = numBins;
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.tailBins = tailBins;
		if ( numBins <= 0 || ( tailBins && numBins <= 2 ) ) { throw new IllegalArgumentException(
				"invalid Real1dBinMapper: no data bins specified" ); }
		if ( minVal > maxVal ) { throw new IllegalArgumentException(
				"invalid Real1dBinMapper: invalid data range specified (min > max)" ); }
		if ( tailBins )
		{
			this.interiorBins = bins - 2;
		}
		else
		{
			this.interiorBins = bins;
		}
		if ( minVal == maxVal )
			this.binWidth = 1.0 / interiorBins;
		else
			this.binWidth = ( maxVal - minVal ) / ( interiorBins );
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
		final double val = value.getRealDouble();
		long pos;
		if ( val >= minVal && val <= maxVal )
		{
			final double bin = ( val - minVal ) / binWidth;
			pos = ( long ) Math.floor( bin );
			// catch out of bounds which can happen for edge values and roundoff
			// errs
			if ( pos >= interiorBins )
				pos = interiorBins - 1;
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
		value.setReal( center( binPos ) );
	}

	@Override
	public void getLowerBound( final long binPos, final T value )
	{
		value.setReal( min( binPos ) );
	}

	@Override
	public void getUpperBound( final long binPos, final T value )
	{
		value.setReal( max( binPos ) );
	}

	@Override
	public boolean includesLowerBound( final long binPos )
	{
		if ( tailBins && binPos == bins - 1 )
			return false;
		return true;
	}

	@Override
	public boolean includesUpperBound( final long binPos )
	{
		if ( tailBins )
		{
			if ( binPos >= bins - 2 )
				return true;
		}
		else
		{ // no tail bins
			if ( binPos == bins - 1 )
				return true;
		}
		return false;
	}

	@Override
	public boolean hasTails()
	{
		return tailBins;
	}

	@Override
	public Real1dBinMapper< T > copy()
	{
		return new Real1dBinMapper< T >( minVal, maxVal, bins, tailBins );
	}

	/**
	 * This is a convenience method for creating a {@link HistogramNd} from
	 * inputs that describe a set of real 1-d based bin mappers. The inputs
	 * should all have n entries for an n-d set of mappers.
	 * 
	 * @param minVals
	 *            The minimum bin values for each dimension
	 * @param maxVals
	 *            The maximum bin values for each dimension
	 * @param numBins
	 *            The total bin count for each dimension
	 * @param tailBins
	 *            Flags per dimension for whether to include tail bins
	 * @return An unpopulated HistogramNd
	 */
	public static < K extends RealType< K >> HistogramNd< K > histogramNd(
			final double[] minVals, final double[] maxVals, final long[] numBins, final boolean[] tailBins )
	{
		if ( ( minVals.length != numBins.length ) ||
				( minVals.length != tailBins.length ) ) { throw new IllegalArgumentException(
				"multiDimMappers: differing input array sizes" ); }
		final List< BinMapper1d< K >> binMappers = new ArrayList< BinMapper1d< K >>();
		for ( int i = 0; i < minVals.length; i++ )
		{
			final Real1dBinMapper< K > mapper =
					new Real1dBinMapper< K >( minVals[ i ], maxVals[ i ], numBins[ i ], tailBins[ i ] );
			binMappers.add( mapper );
		}
		return new HistogramNd< K >( binMappers );
	}

	// -- helpers --

	private double min( final long pos )
	{
		if ( pos < 0 || pos > bins - 1 ) { throw new IllegalArgumentException( "invalid bin position specified" ); }
		if ( tailBins )
		{
			if ( pos == 0 )
				return Double.NEGATIVE_INFINITY;
			if ( pos == bins - 1 )
				return maxVal;
			return minVal + ( 1.0 * ( pos - 1 ) / ( bins - 2 ) ) * ( maxVal - minVal );
		}
		return minVal + ( 1.0 * pos / ( bins ) ) * ( maxVal - minVal );
	}

	private double max( final long pos )
	{
		if ( pos < 0 || pos > bins - 1 ) { throw new IllegalArgumentException( "invalid bin position specified" ); }
		if ( tailBins )
		{
			if ( pos == 0 )
				return minVal;
			if ( pos == bins - 1 )
				return Double.POSITIVE_INFINITY;
			return minVal + ( 1.0 * pos / ( bins - 2 ) ) * ( maxVal - minVal );
		}
		return minVal + ( 1.0 * ( pos + 1 ) / ( bins ) ) * ( maxVal - minVal );
	}

	private double center( final long pos )
	{
		return ( min( pos ) + max( pos ) ) / 2;
	}

}
