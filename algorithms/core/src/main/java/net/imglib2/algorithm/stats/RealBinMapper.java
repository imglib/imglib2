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

import net.imglib2.type.numeric.RealType;

/**
 * A HistogramBinMapper over RealType, using arbitrary-width histogram bins.
 * 
 * @author 2011 Larry Lindsey
 * @author Larry Lindsey
 */
public class RealBinMapper< T extends RealType< T >>
		implements HistogramBinMapper< T >
{

	private final int numBins;

	private final T minBin;

	private final T maxBin;

	private final double binWidth;

	private final double halfBinWidth;

	private final double minVal;

	/**
	 * Creates a RealBinMapper for a histogram with the given minimum bin
	 * center, maximum bin center, and number of bins.
	 * 
	 * @param minBin
	 *            the minimal bin center.
	 * @param maxBin
	 *            the maximal bin center.
	 * @param numBins
	 *            the number of histogram bins to use.
	 */
	public RealBinMapper( final T minBin, final T maxBin, final int numBins )
	{
		this.numBins = numBins;
		this.minBin = minBin;
		this.maxBin = maxBin;

		// Save a little computation time by calculating these only once.
		binWidth = ( 1 + maxBin.getRealDouble() - minBin.getRealDouble() ) /
				( numBins );
		halfBinWidth = binWidth / 2;

		minVal = minBin.getRealDouble();
	}

	@Override
	public T getMaxBin()
	{
		return maxBin;
	}

	@Override
	public T getMinBin()
	{
		return minBin;
	}

	@Override
	public int getNumBins()
	{
		return numBins;
	}

	@Override
	public T invMap( final int i )
	{
		final T out = minBin.createVariable();
		double t = i;

		t *= binWidth;
		t += minVal;
		out.setReal( t );
		return out;
	}

	@Override
	public int map( final T type )
	{
		double tVal = type.getRealDouble();
		tVal -= minVal;
		tVal += halfBinWidth;
		tVal /= binWidth;

		return ( int ) tVal;
	}

	public double getBinWidth()
	{
		return binWidth;
	}

}
