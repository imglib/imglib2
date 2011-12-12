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

package net.imglib2.ops.function.real;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.RegionIndexIterator;
import net.imglib2.type.numeric.RealType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class RealSumFunction<T extends RealType<T>> implements Function<long[],T> {

	private final Function<long[],T> otherFunc;
	private final T variable;
	private RegionIndexIterator iter;
	
	public RealSumFunction(Function<long[],T> otherFunc) {
		this.otherFunc = otherFunc;
		this.variable = createOutput();
		this.iter = null;
	}
	
	@Override
	public void evaluate(Neighborhood<long[]> region, long[] point, T output) {
		if (iter == null)
			iter = new RegionIndexIterator(region);
		else
			iter.relocate(region.getKeyPoint());
		iter.reset();
		double sum = 0;
		while (iter.hasNext()) {
			iter.fwd();
			otherFunc.evaluate(region, iter.getPosition(), variable);
			sum += variable.getRealDouble();
		}
		output.setReal(sum);
	}

	@Override
	public RealSumFunction<T> duplicate() {
		return new RealSumFunction<T>(otherFunc.duplicate());
	}

	@Override
	public T createOutput() {
		return otherFunc.createOutput();
	}
}
