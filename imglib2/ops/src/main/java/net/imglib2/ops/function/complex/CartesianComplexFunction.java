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

package net.imglib2.ops.function.complex;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.TempComplexType;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.type.numeric.RealType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class CartesianComplexFunction<INDEX,T extends RealType<T>>
	implements Function<INDEX,TempComplexType> {

	private final Function<INDEX,T> realFunc1;
	private final Function<INDEX,T> realFunc2;
	private final T real1;
	private final T real2;
	
	public CartesianComplexFunction(Function<INDEX,T> realFunc) {
		this.realFunc1 = realFunc;
		this.realFunc2 = new ConstantRealFunction<INDEX,T>(realFunc.createOutput(),0);
		this.real1 = realFunc.createOutput();
		this.real2 = realFunc.createOutput();
	}
	
	public CartesianComplexFunction(Function<INDEX,T> realFunc1, Function<INDEX,T> realFunc2) {
		this.realFunc1 = realFunc1;
		this.realFunc2 = realFunc2;
		this.real1 = realFunc1.createOutput();
		this.real2 = realFunc1.createOutput();
	}
	
	@Override
	public void evaluate(Neighborhood<INDEX> neigh, INDEX point, TempComplexType value) {
		realFunc1.evaluate(neigh, point, real1);
		realFunc2.evaluate(neigh, point, real2);
		value.setComplexNumber(real1.getRealDouble(),real2.getRealDouble());
	}
	
	@Override
	public CartesianComplexFunction<INDEX,T> copy() {
		return new CartesianComplexFunction<INDEX,T>(realFunc1.copy(), realFunc2.copy());
	}

	@Override
	public TempComplexType createOutput() {
		return new TempComplexType();
	}
}
