/*

Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld.
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

import java.util.ArrayList;
import java.util.Collections;

import net.imglib2.ops.DiscreteIterator;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class RealMedianFunction implements Function<DiscreteNeigh,Real> {

	private DiscreteNeigh region;
	private Function<DiscreteNeigh,Real> otherFunc;
	private Real variable;
	private ArrayList<Double> values;
	
	public RealMedianFunction(DiscreteNeigh region,
		Function<DiscreteNeigh,Real> otherFunc)
	{
		this.region = region.duplicate();
		this.otherFunc = otherFunc;
		this.variable = createVariable();
		this.values = new ArrayList<Double>();
		int numNeighs = getNeighborhoodSize(region);
		for (int i = 0; i < numNeighs; i++)
			this.values.add(0.0);
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}

	@Override
	public void evaluate(DiscreteNeigh input, Real output) {
		DiscreteIterator iter = input.getIterator();
		iter.reset();
		int numElements = 0;
		while (iter.hasNext()) {
			iter.fwd();
			region.moveTo(iter.getPosition());
			otherFunc.evaluate(region, variable);
			values.set(numElements++, variable.getReal());
		}
		Collections.sort(values);
		if (numElements == 0)
			output.setReal(0);
		else if ((numElements % 2) == 1)  // odd number of elements
			output.setReal( values.get(numElements/2) );
		else { // even number of elements
			double value1 = values.get((numElements/2) - 1); 
			double value2 = values.get((numElements/2));
			output.setReal((value1 + value2) / 2);
		}
	}

	private int getNeighborhoodSize(DiscreteNeigh neigh) {
		long[] negOffs = neigh.getNegativeOffsets();
		long[] posOffs = neigh.getPositiveOffsets();
		int size = 1;
		for (int i = 0; i < neigh.getNumDims(); i++) {
			size *= 1 + negOffs[i] + posOffs[i];
		}
		return size;
	}
}
