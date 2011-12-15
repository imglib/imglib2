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

package net.imglib2.ops.operation.unary.complex;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.type.numeric.ComplexType;

// Formula taken from MATLAB documentation

/**
 * 
 * @author Barry DeZonia
 *
 */
public final class ComplexArcsech<T extends ComplexType<T>,U extends ComplexType<U>>
	implements UnaryOperation<T,U> {

	private final U ONE;

	private final ComplexCopy<T,U> copyFunc; 
	private final ComplexArccosh<U,U> arccoshFunc;
	private final ComplexDivide<U,U,U> divFunc;
	
	private final U z;
	private final U recipZ;

	private final U type;
	
	public ComplexArcsech(U type) {
		this.type = type;

		copyFunc = new ComplexCopy<T,U>(type);
		arccoshFunc = new ComplexArccosh<U,U>(type);
		divFunc = new ComplexDivide<U,U,U>(type);
		
		ONE = type.createVariable();
		
		z = type.createVariable();
		recipZ = type.createVariable();
		
		ONE.setComplexNumber(1, 0);
	}
	
	@Override
	public void compute(T in, U output) {
		copyFunc.compute(in,z);
		divFunc.compute(ONE, z, recipZ);
		arccoshFunc.compute(recipZ, output);
	}
	
	@Override
	public ComplexArcsech<T,U> copy() {
		return new ComplexArcsech<T,U>(type);
	}

	@Override
	public U createOutput(T dataHint) {
		return type.createVariable();
	}
}
