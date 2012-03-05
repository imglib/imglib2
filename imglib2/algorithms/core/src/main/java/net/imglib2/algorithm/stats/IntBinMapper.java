/**
 * Copyright (c) 2010, 2011 Larry Lindsey
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Larry Lindsey
 */
package net.imglib2.algorithm.stats;

import net.imglib2.type.numeric.IntegerType;

/**
 * A HistogramBinMapper over IntegerTypes, using the values themselves as the
 * histogram bin centers.
 * 
 */
public class IntBinMapper <T extends IntegerType<T>>
    implements HistogramBinMapper<T>{

    /**
     * Given an IntegerType, returns a new Type containing its minimum possible
     * value.
     * @param <R> the IntegerType in question.
     * @param r a representative Type object.
     * @return a new Type containing its minimum possible
     * value.
     */
	private static <R extends IntegerType<R>> R minType(R r)
	{
		R type = r.createVariable();
		type.setReal(r.getMinValue());
		return type;
	}
	
	/**
     * Given an IntegerType, returns a new Type containing its maximum possible
     * value.
     * @param <R> the IntegerType in question.
     * @param r a representative Type object.
     * @return a new Type containing its maximum possible
     * value.
     */
	private static <R extends IntegerType<R>> R maxType(R r)
	{
		R type = r.createVariable();
		type.setReal(r.getMaxValue());
		return type;
	}
	
	private final T minType, maxType;
	private final int numBins;
	private final int minVal;
	
	/**
	 * Create an IntBinMapper with the given minimum and maximum bins.
	 * @param min the minimum bin center.
	 * @param max the maximum bin center.
	 */
	public IntBinMapper(final T min, final T max)
	{
		minType = min;
		maxType = max;
		numBins = max.getInteger() - min.getInteger() + 1;
		minVal = min.getInteger();
	}
	
	/**
	 * Create an IntBinMapper with minimum and maximum bin centers
	 * corresponding to the minimal and maximal Type values.
	 * @param type a representative Type object.
	 */
	public IntBinMapper(final T type)
	{
		this(minType(type), maxType(type));
	}
	
	@Override
	public T getMaxBin() {		
		return maxType;
	}

	@Override
	public T getMinBin() {
		return minType;
	}

	@Override
	public int getNumBins() {		
		return numBins;
	}

	@Override
	public T invMap(final int i) {
		T out = minType.createVariable();
		out.setInteger(i + minVal);
		return out;
	}

	@Override
	public int map(final T type) {
		return type.getInteger() - minVal;
	}
}
