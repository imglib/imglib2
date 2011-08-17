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
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;


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
public class CachingFunction<T extends DataCopier<T>> implements Function<DiscreteNeigh,T> {

	// -- instance variables --
	
	private Function<DiscreteNeigh,T> otherFunc;
	private long[] lastCtr;
	private long[] lastNegOffs;
	private long[] lastPosOffs;
	private T lastValue;

	// -- constructor --
	
	public CachingFunction(Function<DiscreteNeigh,T> otherFunc) {
		this.otherFunc = otherFunc;
		lastValue = createVariable();
	}
	
	// -- public interface --
	
	@Override
	public void evaluate(DiscreteNeigh input, T output) {
		if (lastCtr == null) {
			lastCtr = new long[input.getNumDims()];
			lastNegOffs = new long[input.getNumDims()];
			lastPosOffs = new long[input.getNumDims()];
			recordInput(input);
		}
		else if (!sameInput(input)) {
			recordInput(input);
			otherFunc.evaluate(input, lastValue);
		}
		output.setValue(lastValue);
	}

	@Override
	public T createVariable() {
		return otherFunc.createVariable();
	}

	// -- private helpers --
	
	private boolean sameInput(DiscreteNeigh input) {
		long[] tmp;
		tmp = input.getKeyPoint();
		for (int i = 0; i < tmp.length; i++)
			if (tmp[i] != lastCtr[i])
				return false;
		tmp = input.getNegativeOffsets();
		for (int i = 0; i < tmp.length; i++)
			if (tmp[i] != lastNegOffs[i])
				return false;
		tmp = input.getPositiveOffsets();
		for (int i = 0; i < tmp.length; i++)
			if (tmp[i] != lastPosOffs[i])
				return false;
		return true;
	}
	
	private void recordInput(DiscreteNeigh input) {
		long[] tmp;
		tmp = input.getKeyPoint();
		for (int i = 0; i < tmp.length; i++)
			lastCtr[i] = tmp[i];
		tmp = input.getNegativeOffsets();
		for (int i = 0; i < tmp.length; i++)
			lastNegOffs[i] = tmp[i];
		tmp = input.getPositiveOffsets();
		for (int i = 0; i < tmp.length; i++)
			lastPosOffs[i] = tmp[i];
	}
}
