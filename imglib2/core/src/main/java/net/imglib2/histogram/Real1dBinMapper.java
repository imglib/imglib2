/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.histogram;

import java.util.List;

import net.imglib2.type.numeric.RealType;

/**
 * Maps real values into a 1-d set of bins. Though this class will work with
 * integral data types it is really more appropriate to do so using a
 * {@link Integer1dBinMapper}.
 * 
 * @author Barry DeZonia
 */
public class Real1dBinMapper<T extends RealType<T>> implements BinMapper<T> {

	// -- instance variables --

	private final long bins;
	private final long[] binDimensions;
	private final double minVal, maxVal;
	private final boolean tailBins;

	// -- constructor --

	// TODO - do we just ignore values outside bins when tailbins == false?

	/**
	 * Specify a mapping of real data from a user defined range into a specified
	 * number of bins. If tailBins is true then there will be two bins that count
	 * values outside the user specified ranges. If false then any values outside
	 * the range are counted in the two outermost bins.
	 * 
	 * @param minVal The first data value of interest.
	 * @param maxVal The last data value of interest.
	 * @param numBins The total number of bins to create.
	 * @param tailBins A boolean specifying whether to have catch all bins for
	 *          values outside the user defined range.
	 */
	public Real1dBinMapper(double minVal, double maxVal, long numBins,
		boolean tailBins)
	{
		this.bins = numBins;
		this.binDimensions = new long[] { numBins };
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.tailBins = tailBins;
		if (bins <= 0) {
			throw new IllegalArgumentException(
				"invalid RealBinMapper: nonpositive dimension");
		}
		if (minVal >= maxVal) {
			throw new IllegalArgumentException(
				"invalid RealBinMapper: nonpositive bin width");
		}
	}

	// -- BinMapper methods --

	@Override
	public int numDimensions() {
		return binDimensions.length;
	}
	
	@Override
	public long getBinCount() {
		if (binDimensions.length == 0) return 0;
		long tot = 1;
		for (long dim : binDimensions)
			tot *= dim;
		return tot;
	}

	@Override
	public void getBinDimensions(long[] dims) {
		for (int i = 0; i < binDimensions.length; i++)
			dims[i] = binDimensions[i];
	}

	@Override
	public void getBinPosition(List<T> values, long[] binPos) {
		long pos;
		double val = values.get(0).getRealDouble();
		if (val < minVal) pos = 0;
		else if (val > maxVal) pos = bins - 1;
		else {
			double relPos = (val - minVal) / (maxVal - minVal);
			if (tailBins) {
				pos = 1 + Math.round(relPos * (bins - 2));
			}
			else {
				pos = Math.round(relPos * bins);
			}
		}
		binPos[0] = pos;
	}

	@Override
	public void getCenterValues(long[] binPos, List<T> values) {
		values.get(0).setReal(center(binPos[0]));
	}

	@Override
	public void getMinValues(long[] binPos, List<T> values) {
		values.get(0).setReal(min(binPos[0]));
	}

	@Override
	public void getMaxValues(long[] binPos, List<T> values) {
		values.get(0).setReal(max(binPos[0]));
	}

	@Override
	public boolean includesMinValues(long[] binPos) {
		if (tailBins && binPos[0] == bins - 1) return false;
		return true;
	}

	@Override
	public boolean includesMaxValues(long[] binPos) {
		if (tailBins) {
			if (binPos[0] >= bins - 2) return true;
		}
		else { // no tail bins
			if (binPos[0] == bins - 1) return true;
		}
		return false;
	}

	// -- helpers --

	private double min(long pos) {
		if (pos < 0 || pos > bins - 1) {
			throw new IllegalArgumentException("invalid bin position specified");
		}
		if (tailBins) {
			if (pos == 0) return Double.NEGATIVE_INFINITY;
			if (pos == bins - 1) return maxVal;
			return minVal + (1.0 * (pos - 1) / (bins - 2)) * (maxVal - minVal);
		}
		return minVal + (1.0 * pos / (bins)) * (maxVal - minVal);
	}

	private double max(long pos) {
		if (pos < 0 || pos > bins - 1) {
			throw new IllegalArgumentException("invalid bin position specified");
		}
		if (tailBins) {
			if (pos == 0) return minVal;
			if (pos == bins - 1) return Double.POSITIVE_INFINITY;
			return minVal + (1.0 * pos / (bins - 2)) * (maxVal - minVal);
		}
		return minVal + (1.0 * (pos + 1) / (bins)) * (maxVal - minVal);
	}
	
	private double center(long pos) {
		return (min(pos) + max(pos)) / 2;
	}

}
