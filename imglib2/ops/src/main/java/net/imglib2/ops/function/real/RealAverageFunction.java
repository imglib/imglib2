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

import net.imglib2.ops.DiscreteIterator;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class RealAverageFunction implements Function<DiscreteNeigh,Real> {

	private DiscreteNeigh region;
	private Function<DiscreteNeigh,Real> otherFunc;
	private Real variable;
	
	public RealAverageFunction(DiscreteNeigh region,
		Function<DiscreteNeigh,Real> otherFunc)
	{
		this.region = region.duplicate();
		this.otherFunc = otherFunc;
		this.variable = createVariable();
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}

	@Override
	public void evaluate(DiscreteNeigh input, Real output) {
		DiscreteIterator iter = input.getIterator();
		iter.reset();
		double sum = 0;
		double numElements = 0;
		while (iter.hasNext()) {
			iter.fwd();
			region.moveTo(iter.getPosition());
			otherFunc.evaluate(region, variable);
			sum += variable.getReal();
			numElements++;
		}
		if (numElements == 0)
			output.setReal(0);
		else
			output.setReal(sum / numElements);
	}

}
