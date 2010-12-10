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
package mpicbg.imglib.algorithm.histogram.arbitrary;

import mpicbg.imglib.algorithm.histogram.HistogramBin;
import mpicbg.imglib.algorithm.histogram.HistogramBinFactory;
import mpicbg.imglib.algorithm.histogram.HistogramKey;
import mpicbg.imglib.type.numeric.RealType;

public class RealHistogramBinFactory<T extends RealType<T>> implements HistogramBinFactory<T>
{
	
	public class RealHistogramBin extends HistogramBin<T>
	{

		public RealHistogramBin(T t, HistogramKey<T> k) {
			super(t, k);
		}

		@Override
		public T getLowerBound() {
			T lower = getCenter().createVariable();
			lower.setReal(getCenter().getRealDouble() - width / 2);
			return lower;
		}

		@Override
		public T getUpperBound() {
			T upper = getCenter().createVariable();
			upper.setReal(getCenter().getRealDouble() - width / 2);
			return upper;
		}
		
		@Override
		public double getNorm()
		{
			return width;
		}
		
	}
	
	
	private final double width;
	private final double offset;
	
	public RealHistogramBinFactory(double w, double d)
	{
		width = w;
		offset = d;
	}
	
	private double mapToCenter(double val)
	{
		/*
		 * This way, bins are closed at the lower bound and open at the upper.
		 * To make them closed at the upper bound and open at the lower, do
		 * r = Math.ceil((val - offset) / width) - .5;
		 */
		double r = Math.floor((val - offset) / width) + .5;
		return r * width + offset;
	}
	
	private T centerType(T type)
	{
		double center = mapToCenter(type.getRealDouble());
		T out = type.createVariable();
		out.setReal(center);
		return out;
	}
	
	@Override
	public HistogramBin<T> createBin(T type) {
		T center = centerType(type);
		return new RealHistogramBin(center, createKey(center));		
	}

	@Override
	public HistogramKey<T> createKey(T type) {
		HistogramKey<T> key;
		T center = centerType(type);
		int hc = (new Double(center.getRealDouble())).hashCode();
		
		key = new HistogramKey<T>(hc, center, this);
		
		return key;
	}

	@Override
	public boolean equivalent(T type1, T type2) {		
		return mapToCenter(type1.getRealDouble()) ==
			mapToCenter(type2.getRealDouble());
	}

}
