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
import net.imglib2.ops.operation.binary.complex.ComplexAdd;
import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.ops.operation.binary.complex.ComplexMultiply;
import net.imglib2.ops.operation.unary.complex.ComplexExp;
import net.imglib2.type.numeric.ComplexType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * 
 * @author Barry DeZonia
 * 
 */
public final class ComplexCos<T extends ComplexType<T>, U extends ComplexType<U>>
		implements UnaryOperation<T, U> {

	private final ComplexCopy<T, U> copyFunc;
	private final ComplexExp<U, U> expFunc;
	private final ComplexAdd<U, U, U> addFunc;
	private final ComplexMultiply<U, U, U> mulFunc;
	private final ComplexDivide<U, U, U> divFunc;

	private final U TWO;
	private final U I;
	private final U MINUS_I;

	private final U z;
	private final U IZ;
	private final U minusIZ;
	private final U expIZ;
	private final U expMinusIZ;
	private final U sum;

	private U type;

	public ComplexCos(U type) {
		this.type = type;

		copyFunc = new ComplexCopy<T, U>();
		expFunc = new ComplexExp<U, U>();
		addFunc = new ComplexAdd<U, U, U>();
		mulFunc = new ComplexMultiply<U, U, U>();
		divFunc = new ComplexDivide<U, U, U>();

		TWO = type.createVariable();
		I = type.createVariable();
		MINUS_I = type.createVariable();

		z = type.createVariable();
		IZ = type.createVariable();
		minusIZ = type.createVariable();
		expIZ = type.createVariable();
		expMinusIZ = type.createVariable();
		sum = type.createVariable();

		TWO.setComplexNumber(2, 0);
		I.setComplexNumber(0, 1);
		MINUS_I.setComplexNumber(0, -1);
	}

	@Override
	public U compute(T in, U output) {
		copyFunc.compute(in, z);
		mulFunc.compute(z, I, IZ);
		mulFunc.compute(z, MINUS_I, minusIZ);
		expFunc.compute(IZ, expIZ);
		expFunc.compute(minusIZ, expMinusIZ);
		addFunc.compute(expIZ, expMinusIZ, sum);
		divFunc.compute(sum, TWO, output);
		return output;
	}

	@Override
	public ComplexCos<T, U> copy() {
		return new ComplexCos<T, U>(type);
	}

}
