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

package net.imglib2.histogram.rev2;

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
	BinMapperNd<T>
{

	// -- instance variables --

	private List<Integer1dBinMapper<T>> binMappers;

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
	}

	// -- BinMapper methods --

	@Override
	public int numDimensions() {
		return binMappers.size();
	}

	@Override
	public long getBinCount(int dim) {
		return binMappers.get(dim).getBinCount();
	}

	@Override
	public long getBinCount() {
		if (binMappers.size() == 0) return 0;
		long size = 1;
		for (int i = 0; i < binMappers.size(); i++) {
			size *= getBinCount(i);
		}
		return size;
	}

	@Override
	public void map(List<T> values, long[] binPos) {
		for (int i = 0; i < binMappers.size(); i++) {
			binPos[i] = binMappers.get(i).map(values.get(i));
		}
	}

	@Override
	public void getCenterValues(long[] binPos, List<T> values) {
		for (int i = 0; i < binMappers.size(); i++) {
			binMappers.get(i).getCenterValue(binPos[i], values.get(i));
		}
	}

	@Override
	public void getLowerBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < binMappers.size(); i++) {
			binMappers.get(i).getLowerBound(binPos[i], values.get(i));
		}
	}

	@Override
	public void getUpperBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < binMappers.size(); i++) {
			binMappers.get(i).getUpperBound(binPos[i], values.get(i));
		}
	}

	@Override
	public boolean includesLowerBounds(long[] binPos) {
		for (int i = 0; i < binMappers.size(); i++) {
			if (!binMappers.get(i).includesLowerBound(binPos[i])) return false;
		}
		return true;
	}

	@Override
	public boolean includesUpperBounds(long[] binPos) {
		for (int i = 0; i < binMappers.size(); i++) {
			if (!binMappers.get(i).includesUpperBound(binPos[i])) return false;
		}
		return true;
	}

	@Override
	public boolean hasTails() {
		for (int i = 0; i < binMappers.size(); i++) {
			if (hasTails(i)) return true;
		}
		return false;
	}

	@Override
	public boolean hasTails(int dim) {
		return binMappers.get(dim).hasTails();
	}
}
