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

package net.imglib2.algorithm.stats;

import java.util.ArrayList;
import java.util.Arrays;

import net.imglib2.RealCursor;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.img.Img;
import net.imglib2.type.Type;

/**
 * Implements a Histogram over an Image.
 * 
 * @author 2011 Larry Lindsey
 * @author Larry Lindsey
 */
public class Histogram< T > implements Algorithm, Benchmark
{
	/**
	 * Processing time, milliseconds.
	 */
	private long pTime = 0;

	/**
	 * Hold the histogram itself.
	 */
	private final int[] histogram;

	/**
	 * The Cursor from which the histogram is to be calculated.
	 */
	private final RealCursor< T > cursor;

	/**
	 * The HistogramBinMapper, used to map Type values to histogram bin indices.
	 */
	private final HistogramBinMapper< T > binMapper;

	/**
	 * Create a Histogram using the given mapper, calculating from the given
	 * Cursor.
	 * 
	 * @param mapper
	 *            the HistogramBinMapper used to map Type values to histogram
	 *            bin indices.
	 * @param c
	 *            a Cursor corresponding to the Image from which the Histogram
	 *            will be calculated
	 * 
	 */
	public Histogram( final HistogramBinMapper< T > mapper,
			final RealCursor< T > c )
	{
		cursor = c;
		binMapper = mapper;
		histogram = new int[ binMapper.getNumBins() ];
	}

	/**
	 * Create a Histogram using the given mapper, calculating from the given
	 * Image.
	 * 
	 * @param mapper
	 *            the HistogramBinMapper used to map Type values to histogram
	 *            bin indices.
	 * @param image
	 *            an Image from which the Histogram will be calculated
	 * 
	 */
	public Histogram( final HistogramBinMapper< T > mapper,
			final Img< T > image )
	{
		this( mapper, image.cursor() );
	}

	/**
	 * Resets the histogram array and the Cursor.
	 */
	public void reset()
	{
		Arrays.fill( histogram, 0 );
		cursor.reset();
	}

	/**
	 * Returns the bin count corresponding to a given {@link Type}.
	 * 
	 * @param t
	 *            the Type corresponding to the requested
	 * @return The requested bin count.
	 */
	public int getBin( final T t )
	{
		return getHistogram()[ binMapper.map( t ) ];
	}

	/**
	 * Returns the bin count given by the indicated bin index.
	 * 
	 * @param i
	 *            the index of the requested bin
	 * @return the bin count at the given index
	 */
	public int getBin( final int i )
	{
		return getHistogram()[ i ];
	}

	/**
	 * Returns this Histogram's HistogramBinMapper.
	 * 
	 * @return the HistogramBinMapper associated with this Histogram.
	 */
	public HistogramBinMapper< T > getBinMapper()
	{
		return binMapper;
	}

	/**
	 * Returns the histogram array.
	 * 
	 * @return the histogram array.
	 */
	public int[] getHistogram()
	{
		return histogram;
	}

	/**
	 * Creates and returns the a Type whose value corresponds to the center of
	 * the bin indexed by i.
	 * 
	 * @param i
	 *            the requested bin index.
	 * @return a Type whose value corresponds to the requested bin center.
	 */
	public T getBinCenter( final int i )
	{
		return getBinMapper().invMap( i );
	}

	/**
	 * Creates and returns a List containing Types that correspond to the
	 * centers of the histogram bins.
	 * 
	 * @return a List containing Types that correspond to the centers of the
	 *         histogram bins.
	 */
	public ArrayList< T > getBinCenters()
	{
		final ArrayList< T > binCenters = new ArrayList< T >( histogram.length );
		for ( int i = 0; i < histogram.length; ++i )
		{
			binCenters.add( i, getBinMapper().invMap( i ) );
		}

		return binCenters;
	}

	/**
	 * Returns the number of bins in this Histogram.
	 * 
	 * @return the number of bins in this Histogram
	 * 
	 */
	public int getNumBins()
	{
		return getBinMapper().getNumBins();
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return null;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		int index;

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			index = binMapper.map( cursor.get() );
			/*
			 * The following check makes this run for IntegerTypes at 3 to 4
			 * longer than the manual case on my machine. This is a necessary
			 * check, but if this takes too long, it might be worthwhile to
			 * separate out an UncheckedHistogram, which would instead throw an
			 * ArrayOutOfBoundsException.
			 */
			if ( index >= 0 && index < histogram.length )
			{
				++histogram[ index ];
			}
		}

		pTime = System.currentTimeMillis() - startTime;
		return true;
	}

	@Override
	public long getProcessingTime()
	{
		return pTime;
	}

}
