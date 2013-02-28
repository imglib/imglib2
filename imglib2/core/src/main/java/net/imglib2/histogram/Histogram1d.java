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

/**
 * A one dimensional histogram implementation.
 * 
 * @author Barry DeZonia
 */
public class Histogram1d<T> {

	// -- instance variables --

	private final HistogramNd<T> histN;
	private final long[] tmpPos;
	private final List<Iterable<T>> iterables;
	private final List<T> vals;

	// -- public api --
	
	/**
	 * Constructs a histogram on an Iterable set of data. The algorithm for
	 * mapping values to bins must be provided. A scratch variable must be
	 * provided also.
	 * 
	 * @param iterable The set of data to iterate and count.
	 * @param binMapper The algorithm used to map values to bins.
	 * @param val A variable that can be used internally for temporary work.
	 */
	public Histogram1d(Iterable<T> iterable, BinMapper<T> binMapper, T val)
	{
		if (binMapper.numDimensions() != 1) {
			throw new IllegalArgumentException(
				"This histogram requires a 1d bin mapper");
		}
		iterables = new ArrayList<Iterable<T>>();
		iterables.add(iterable);
		vals = new ArrayList<T>();
		vals.add(val);
		histN = new HistogramNd<T>(iterables, binMapper, vals);
		tmpPos = new long[1];
	}

	/**
	 * Returns the total number of bins defined for this histogram.
	 */
	public long getBinCount() {
		return histN.getBinCount();
	}

	/**
	 * Returns the bin number associated with the given value.
	 */
	public long getBinPosition(T value) {
		vals.set(0, value);
		histN.getBinPosition(vals, tmpPos);
		return tmpPos[0];
	}

	/**
	 * Returns the count of values mapped to a specified bin number.
	 */
	public long frequency(long binPos) {
		tmpPos[0] = binPos;
		return histN.frequency(tmpPos);
	}

	/**
	 * Returns the count of values in the bin mapped from a given value.
	 */
	public long frequency(T value) {
		vals.set(0, value);
		histN.getBinPosition(vals, tmpPos);
		return histN.frequency(tmpPos);
	}

	/**
	 * Returns the relative frequency of values mapped to a specified bin number.
	 */
	public double relativeFrequency(long binPos) {
		tmpPos[0] = binPos;
		return histN.relativeFrequency(tmpPos);
	}

	/**
	 * Returns the relative frequency of values within the bin containing a
	 * specified value.
	 */
	public double relativeFrequency(T value) {
		vals.set(0, value);
		histN.getBinPosition(vals, tmpPos);
		return histN.relativeFrequency(tmpPos);
	}

	/**
	 * Returns the count of the values contained in the histogram bins.
	 */
	public long totalValues() {
		return histN.totalValues();
	}

	/**
	 * Recalculates the frequency distribution of the earlier specified Iterable
	 * data source.
	 */
	public void recalc() {
		histN.recalc();
	}

	/**
	 * Gets the data value of the center of a given bin number.
	 */
	public void getCenterValue(long binPos, T value) {
		tmpPos[0] = binPos;
		vals.set(0, value);
		histN.getCenterValues(tmpPos, vals);
	}

	/**
	 * Gets the data value of the left edge of a given bin number.
	 */
	public void getLowerBound(long binPos, T value) {
		tmpPos[0] = binPos;
		vals.set(0, value);
		histN.getLowerBounds(tmpPos, vals);
	}

	/**
	 * Gets the data value of the right edge of a given bin number.
	 */
	public void getUpperBound(long binPos, T value) {
		tmpPos[0] = binPos;
		vals.set(0, value);
		histN.getUpperBounds(tmpPos, vals);
	}

	/**
	 * Returns true if the bin specified by bin position includes data points
	 * matching the bin's maximum edge value. Otherwise the max value is
	 * considered just outside the bin.
	 */
	public boolean includesUpperBound(long binPos) {
		tmpPos[0] = binPos;
		return histN.includesUpperBounds(tmpPos);
	}

	/**
	 * Returns true if the bin specified by bin position includes data points
	 * matching the bin's minimum edge value. Otherwise the min value is
	 * considered just outside the bin.
	 */
	public boolean includesLowerBound(long binPos) {
		tmpPos[0] = binPos;
		return histN.includesLowerBounds(tmpPos);
	}
}
