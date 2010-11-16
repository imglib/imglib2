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

package mpicbg.imglib.algorithm.histogram.discrete;

import mpicbg.imglib.algorithm.histogram.HistogramBin;
import mpicbg.imglib.algorithm.histogram.HistogramBinFactory;
import mpicbg.imglib.algorithm.histogram.HistogramKey;
import mpicbg.imglib.type.numeric.IntegerType;

/**
 * A HistogramBinFactory to be used to create a discrete Histogram over
 * integer-valued Type's.
 * @author LarryLindsey
 *
 * @param <T> the type of {@link Type} corresponding to this factory, implementing IntegerType.
 */
public class DiscreteIntHistogramBinFactory<T extends IntegerType<T>> implements HistogramBinFactory<T>
{
	public class DiscreteIntHistogramBin extends HistogramBin<T>
	{

		public DiscreteIntHistogramBin(T t, HistogramKey<T> k) {
			super(t, k);
		}

		@Override
		public T getLowerBound() {
			return getCenter();
		}

		@Override
		public T getUpperBound() {
			return getCenter();
		}
		
	}
	
	@Override
	public HistogramBin<T> createBin(T type) {		
		return new DiscreteIntHistogramBin(type, createKey(type));
	}

	@Override
	public HistogramKey<T> createKey(T type) {
		return new HistogramKey<T>((new Double(type.getIntegerLong())).hashCode(),
				type.copy(), this);
	}

	@Override
	public boolean equivalent(T type1, T type2) {
		return type1.getIntegerLong() == type2.getIntegerLong();
	}
	
}
