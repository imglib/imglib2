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
import net.imglib2.ops.Real;
import net.imglib2.ops.RealOutput;

// TODO - An example implementation of a real integral function.
// Note that it does a very simple approximation. Modify to use the
// multidimensional equivalent of a trapezoidal interpolation rule.

/**
 * 
 * @author Barry DeZonia
 *
 */
public class RealContinuousIntegralFunction extends RealOutput implements Function<double[],Real> {

	// -- instance variables --
	
	private final Function<double[],Real> otherFunc;
	private final double[] deltas;
	private final double cellSize;
	private final Real variable;
	private double[] position;
	
	// -- constructor --
	
	public RealContinuousIntegralFunction(Function<double[],Real> otherFunc, double[] deltas) {
		this.otherFunc = otherFunc;
		this.deltas = deltas.clone();
		this.variable = createOutput();
		this.cellSize = cellSize(deltas);
		this.position = new double[deltas.length];
	}
	
	// -- public interface --
	
	@Override
	public void evaluate(Neighborhood<double[]> region, double[] point, Real output) {
		
		for (int i = 0; i < position.length; i++)
			position[i] = point[i] - region.getNegativeOffsets()[i];

		double sum = 0;
		
		boolean done = false;
		while (!done) {
			otherFunc.evaluate(region, position, variable);
			sum += variable.getReal() * cellSize;
			done = !nextPosition(position, region);
		}
		output.setReal(sum);
	}

	@Override
	public RealContinuousIntegralFunction duplicate() {
		return new RealContinuousIntegralFunction(otherFunc.duplicate(), deltas);
	}

	// -- private helpers --
	
	private double cellSize(double[] sizes) {
		if (sizes.length == 0) return 0;
		double totalSize = 1;
		for (double size : sizes)
			totalSize *= size;
		return totalSize;
	}
	
	private boolean nextPosition(double[] pos, Neighborhood<double[]> region) {
		for (int i = 0; i < pos.length; i++) {
			pos[i] += deltas[i];
			if (pos[i] <= region.getKeyPoint()[i] + region.getPositiveOffsets()[i])
				return true;
			pos[i] = region.getKeyPoint()[i] - region.getNegativeOffsets()[i];
		}
		return false;
	}
}
