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

import net.imglib2.type.numeric.IntegerType;

/**
 * @author Barry DeZonia
 * @param <T>
 */
public class Integer1dBinMapper<T extends IntegerType<T>> implements
	BinMapper<T>
{

	// -- instance variables --

	private final long bins;
	private final long[] binDimensions;
	private final long minVal, maxVal;
	private final boolean tailBins;

	// -- constructor --

	// TODO - numBins? or numBins and maxVal also?

	public Integer1dBinMapper(long minVal, long numBins, boolean tailBins) {
		this.bins = numBins;
		this.binDimensions = new long[] { numBins };
		this.tailBins = tailBins;
		this.minVal = minVal;
		if (tailBins) {
			this.maxVal = minVal + numBins - 1 - 2;
		}
		else {
			this.maxVal = minVal + numBins - 1;
		}
		if (bins <= 0) {
			throw new IllegalArgumentException(
				"invalid IntegerBinMapper: nonpositive dimension");
		}
	}

	// -- BinMapper methods --

	@Override
	public int numDimensions() {
		return binDimensions.length;
	}

	@Override
	public void getBinDimensions(long[] dims) {
		for (int i = 0; i < binDimensions.length; i++)
			dims[i] = binDimensions[i];
	}

	@Override
	public void getBinPosition(List<T> values, long[] binPos) {
		long val = values.get(0).getIntegerLong();
		long pos;
		if (val < minVal) pos = 0;
		else if (val > maxVal) pos = bins - 1;
		else {
			if (tailBins) pos = val - minVal + 1;
			else pos = val - minVal;
		}
		binPos[0] = pos;
	}

	@Override
	public void getCenterValues(long[] binPos, List<T> values) {
		long pos = binPos[0];
		long val;
		if (tailBins) {
			if (pos == 0) val = minVal - 1; // HACK - what is best to return?
			else if (pos == bins - 1) val = maxVal + 1; // same HACK
			else val = minVal + pos - 1;
		}
		else { // no tail bins
			val = minVal + pos;
		}
		values.get(0).setInteger(val);
	}

	@Override
	public void getMinValues(long[] binPos, List<T> values) {
		getCenterValues(binPos, values);
	}

	@Override
	public void getMaxValues(long[] binPos, List<T> values) {
		getCenterValues(binPos, values);
	}

	@Override
	public boolean includesMinValues(long[] binPos) {
		return true;
	}

	@Override
	public boolean includesMaxValues(long[] binPos) {
		return true;
	}
}
