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

import java.util.ArrayList;
import java.util.List;

import net.imglib2.type.numeric.IntegerType;

/**
 * An n-dimensional integral BinMapper. Composed of 1-dimensional integral
 * BinMappers internally.
 * 
 * @author Barry DeZonia
 */
public class IntegerNdBinMapper<T extends IntegerType<T>> implements
	BinMapper<T>
{

	// -- instance variables --

	private List<Integer1dBinMapper<T>> binMappers;
	private List<T> tmpVals;
	private long[] tmpPos;

	// -- constructor --

	public IntegerNdBinMapper(long[] minVals, long[] numBins, boolean[] tailBins)
	{
		if ((minVals.length != numBins.length) ||
			(minVals.length != tailBins.length))
		{
			throw new IllegalArgumentException(
				"IntegerNdBinMapper: differing input array sizes");
		}
		binMappers = new ArrayList<Integer1dBinMapper<T>>();
		for (int i = 0; i < minVals.length; i++) {
			Integer1dBinMapper<T> mapper =
				new Integer1dBinMapper<T>(minVals[i], numBins[i], tailBins[i]);
			binMappers.add(mapper);
		}
		tmpVals = new ArrayList<T>();
		tmpVals.add(null);
		tmpPos = new long[1];
	}

	// -- BinMapper methods --

	@Override
	public int numDimensions() {
		return binMappers.size();
	}

	@Override
	public long getBinCount() {
		if (binMappers.size() == 0) return 0;
		long size = 1;
		for (Integer1dBinMapper<T> mapper : binMappers) {
			size *= mapper.getBinCount();
		}
		return size;
	}

	@Override
	public void getBinDimensions(long[] dims) {
		for (int i = 0; i < binMappers.size(); i++) {
			dims[i] = binMappers.get(i).getBinCount();
		}
	}

	@Override
	public boolean getBinPosition(List<T> values, long[] binPos) {
		boolean isInBounds = true;
		for (int i = 0; i < binMappers.size(); i++) {
			tmpVals.set(0, values.get(i));
			binMappers.get(i).getBinPosition(tmpVals, tmpPos);
			binPos[i] = tmpPos[0];
			isInBounds &= binPos[i] >= 0;
		}
		return isInBounds;
	}

	@Override
	public void getCenterValues(long[] binPos, List<T> values) {
		for (int i = 0; i < binMappers.size(); i++) {
			tmpPos[0] = binPos[i];
			tmpVals.set(0, values.get(i));
			binMappers.get(i).getCenterValues(tmpPos, tmpVals);
		}
	}

	@Override
	public void getLowerBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < binMappers.size(); i++) {
			tmpPos[0] = binPos[i];
			tmpVals.set(0, values.get(i));
			binMappers.get(i).getLowerBounds(tmpPos, tmpVals);
		}
	}

	@Override
	public void getUpperBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < binMappers.size(); i++) {
			tmpPos[0] = binPos[i];
			tmpVals.set(0, values.get(i));
			binMappers.get(i).getUpperBounds(tmpPos, tmpVals);
		}
	}

	@Override
	public boolean includesLowerBounds(long[] binPos) {
		for (int i = 0; i < binMappers.size(); i++) {
			tmpPos[0] = binPos[i];
			if (!binMappers.get(i).includesLowerBounds(tmpPos)) return false;
		}
		return true;
	}

	@Override
	public boolean includesUpperBounds(long[] binPos) {
		for (int i = 0; i < binMappers.size(); i++) {
			tmpPos[0] = binPos[i];
			if (!binMappers.get(i).includesUpperBounds(tmpPos)) return false;
		}
		return true;
	}

}
