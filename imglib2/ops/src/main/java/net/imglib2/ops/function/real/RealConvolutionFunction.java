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
import net.imglib2.ops.RegionIndexIterator;

// NOTE : convolution and correlation are similar operations whose output is
//   rotated by 180 degrees for the same kernel. You can get one or the other
//   from the same function by rotating the input kernel by 180 degrees. As
//   implemented below this function is really a Correlation.

// TODO
//   A convolution is really a GeneralBinaryOperation between an input function
//   and a kernel function. For efficiency this class exists. For generality a
//   kernel function could calculate in evaluate(neigh,point,output) the relation
//   of neigh and point and choose the correct index into the kernel. By doing
//   this we could have more flexibility in the definitions of kernels (rather
//   than just an array of user supplied reals).

/**
 * 
 * @author Barry DeZonia
 *
 */
public class RealConvolutionFunction extends RealOutput implements Function<long[],Real> {

	private final Function<long[],Real> otherFunc;
	private final Real variable;
	private final double[] kernel;
	private RegionIndexIterator iter;
	
	public RealConvolutionFunction(Function<long[],Real> otherFunc, double[] kernel) {
		this.otherFunc = otherFunc;
		this.variable = createOutput();
		this.kernel = kernel;
		this.iter = null;
	}
	
	@Override
	public void evaluate(Neighborhood<long[]> region, long[] point, Real output) {
		if (iter == null)
			iter = new RegionIndexIterator(region);
		else
			iter.relocate(region.getKeyPoint());
		iter.reset();
		int cell = 0;
		double sum = 0;
		while (iter.hasNext()) {
			iter.fwd();
			otherFunc.evaluate(region, iter.getPosition(), variable);
			sum += variable.getReal() * kernel[cell++];
		}
		output.setReal(sum);
	}
}
