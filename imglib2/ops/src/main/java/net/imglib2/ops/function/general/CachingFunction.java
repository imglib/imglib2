/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.function.general;

import net.imglib2.ops.DataCopier;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;


/**
 * A CachingFunction returns a cached value when the same input data is passed
 * to the evaluate() method. Currently it caches the last value only. Imagine 
 * you have a ConditionalFunction that evaluates a function twice (once as part
 * of the condition test and once as part of the assignment of value). This
 * class can be used to improve the performance of function evaluation if the
 * cost of computing the function is high. (The test for input equality is
 * relatively expensive in its own right)
 *  
 * @author Barry DeZonia
 */
public class CachingFunction<T extends DataCopier<T>> implements Function<long[],T> {

	// -- instance variables --
	
	private Function<long[],T> otherFunc;
	private long[] lastKeyPoint;
	private long[] lastPoint;
	private T lastValue;

	// -- constructor --
	
	public CachingFunction(Function<long[],T> otherFunc) {
		this.otherFunc = otherFunc;
		lastValue = createVariable();
	}
	
	// -- public interface --
	
	@Override
	public void evaluate(Neighborhood<long[]> region, long[] point, T output) {
		if (lastKeyPoint == null) {
			lastKeyPoint = region.getKeyPoint().clone();
			lastPoint = point.clone();
			otherFunc.evaluate(region, point, lastValue);
		}
		else if (!sameInput(region, point)) {
			recordInput(region, point);
			otherFunc.evaluate(region, point, lastValue);
		}
		output.setValue(lastValue);
	}

	@Override
	public T createVariable() {
		return otherFunc.createVariable();
	}

	// -- private helpers --
	
	private boolean sameInput(Neighborhood<long[]> region, long[] point) {
		// NOTE - we will expect that this code only ever called with same region
		// extents so we won't test negative and positive offsets
		long[] input, cache;
		input = region.getKeyPoint();
		cache = lastKeyPoint;
		for (int i = 0; i < cache.length; i++)
			if (input[i] != cache[i])
				return false;
		input = point;
		cache = lastPoint;
		for (int i = 0; i < cache.length; i++)
			if (input[i] != cache[i])
				return false;
		return true;
	}
	
	private void recordInput(Neighborhood<long[]> region, long[] point) {
		long[] keyPt = region.getKeyPoint();
		for (int i = 0; i < lastPoint.length; i++) {
			lastKeyPoint[i] = keyPt[i];
			lastPoint[i] = point[i];
		}
	}
}
