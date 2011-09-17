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

import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.Function;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class ContinuousTranslationFunction<T> implements Function<double[],T> {

	private final Function<double[],T> otherFunc;
	private final double[] deltas;
	private final double[] localCtr;
	private final Neighborhood<double[]> localRegion;
	
	public ContinuousTranslationFunction(Function<double[],T> otherFunc,
		Neighborhood<double[]> region, double[] deltas)
	{
		this.otherFunc = otherFunc;
		this.deltas = deltas;
		this.localRegion = region.duplicate();
		this.localCtr = new double[deltas.length];
	}
	
	@Override
	public void evaluate(Neighborhood<double[]> region, double[] point, T output) {
		double[] keyPt = region.getKeyPoint();
		for (int i = 0; i < localCtr.length; i++)
			localCtr[i] = keyPt[i] + deltas[i];
		localRegion.moveTo(localCtr);
		otherFunc.evaluate(localRegion, point, output);
	}

	@Override
	public T createOutput() {
		return otherFunc.createOutput();
	}

}
