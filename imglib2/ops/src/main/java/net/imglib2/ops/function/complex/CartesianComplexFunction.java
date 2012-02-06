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
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class CartesianComplexFunction<INDEX,R1 extends RealType<R1>,R2 extends RealType<R2>,C extends ComplexType<C>>
	implements Function<INDEX,C> {

	private final Function<INDEX,R1> realFunc1;
	private final Function<INDEX,R2> realFunc2;
	private final R1 real1;
	private final R2 real2;
	private final C cType;
	
	public CartesianComplexFunction(Function<INDEX,R1> realFunc1, Function<INDEX,R2> realFunc2, C cType) {
		this.cType = cType;
		this.realFunc1 = realFunc1;
		this.realFunc2 = realFunc2;
		this.real1 = realFunc1.createOutput();
		this.real2 = realFunc2.createOutput();
	}
	
	@Override
	public void evaluate(Neighborhood<INDEX> neigh, INDEX point, C value) {
		realFunc1.evaluate(neigh, point, real1);
		realFunc2.evaluate(neigh, point, real2);
		value.setComplexNumber(real1.getRealDouble(),real2.getRealDouble());
	}
	
	@Override
	public CartesianComplexFunction<INDEX,R1,R2,C> copy() {
		return new CartesianComplexFunction<INDEX,R1,R2,C>(realFunc1.copy(), realFunc2.copy(), cType);
	}

	@Override
	public C createOutput() {
		return cType.createVariable();
	}
}
