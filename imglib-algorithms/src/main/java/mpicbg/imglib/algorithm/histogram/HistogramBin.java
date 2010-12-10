/**
 * Copyright (c) 2010, Larry Lindsey
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
package mpicbg.imglib.algorithm.histogram;

import mpicbg.imglib.type.Type;

/**
 * HistogramBin is used by {@Histogram} to represent the bins of a histogram.
 * This sentence is very helpful.
 * 
 * @author Larry Lindsey
 *
 * @param <T> the type of {@link Type} corresponding to this HistogramBin. 
 */
public abstract class HistogramBin<T extends Type<T>> {

	/**
	 * The {@link Type} corresponding to the center of this HistogramBin.
	 */
	private final T center;
	
	/**
	 * A HistogramKey that may be used to key this HistogramBin in a 
	 * hash table.
	 */
	private final HistogramKey<T> key;
	
	/**
	 * The count of how many things are binned into this HistogramBin.
	 */
	private long count;
	
	/**
	 * Create a HistogramBin centered at {@Type} t, and keyed by 
	 * HistogramKey k.
	 * @param t the new HistogramBin's center.
	 * @param k a HistogramKey that may be used to key this HistogramBin.
	 */
	public HistogramBin(T t, HistogramKey<T> k)
	{
		center = t;
		key = k;
		count = 0;
	}
	
	/**
	 * Increment the count.
	 */
	public void inc()
	{
		++count;
	}
		
	public long getCount()
	{
		return count;	
	}

	/**
	 * Gets a HistogramKey that may be used to key this HistogramBin into a 
	 * hash table. 
	 * @return a HistogramKey that may be used to key this HistogramBin into a 
	 * hash table.
	 */
	public HistogramKey<T> getKey()
	{
		return key;
	}

	/**
	 * Returns the norm of this bin.  For most discrete purposes, this should 
	 * just be equal to 1.  When a bin may have varying-width, this should 
	 * return the difference between the upper and lower bound.
	 * @return the norm, or effective width of this HistogramBin.
	 */
	public double getNorm()
	{
		return 1;
	}
		
	public T getCenter()
	{
		return center;
	}
	
	/**
	 * Returns the lower bound of this bin.  In the case that this bin
	 * represents a discrete value, the lower bound should be equal to 
	 * the bin center.
	 * @return this bin's lower bound.
	 */
	public abstract T getLowerBound();

	/**
	 * Returns the upper bound of this bin.  In the case that this bin
	 * represents a discrete value, the upper bound should be equal to 
	 * the bin center.
	 * @return this bin's upper bound.
	 */
	public abstract T getUpperBound();
}
