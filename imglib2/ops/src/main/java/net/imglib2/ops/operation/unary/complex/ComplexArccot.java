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
import net.imglib2.ops.operation.binary.complex.ComplexSubtract;
import net.imglib2.type.numeric.ComplexType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * 
 * @author Barry DeZonia
 * 
 */
public final class ComplexArccot<T extends ComplexType<T>, U extends ComplexType<U>>
		implements UnaryOperation<T, U> {

	private final ComplexCopy<T, U> copyFunc;
	private final ComplexMultiply<U, U, U> mulFunc;
	private final ComplexAdd<U, U, U> addFunc;
	private final ComplexSubtract<U, U, U> subFunc;
	private final ComplexDivide<U, U, U> divFunc;
	private final ComplexLog<U, U> logFunc;

	private final U ONE;
	private final U I;
	private final U I_OVER_TWO;

	private final U z;
	private final U iz;
	private final U sum;
	private final U diff;
	private final U quotient;
	private final U log;

	private final U type;

	public ComplexArccot(U type) {

		this.type = type;

		copyFunc = new ComplexCopy<T, U>();
		mulFunc = new ComplexMultiply<U, U, U>();
		addFunc = new ComplexAdd<U, U, U>();
		subFunc = new ComplexSubtract<U, U, U>();
		divFunc = new ComplexDivide<U, U, U>();
		logFunc = new ComplexLog<U, U>();

		ONE = type.createVariable();
		I = type.createVariable();
		I_OVER_TWO = type.createVariable();

		z = type.createVariable();
		iz = type.createVariable();
		sum = type.createVariable();
		diff = type.createVariable();
		quotient = type.createVariable();
		log = type.createVariable();

		ONE.setComplexNumber(1, 0);
		I.setComplexNumber(0, 1);
		I_OVER_TWO.setComplexNumber(0, 0.5);
	}

	@Override
	public U compute(T in, U output) {
		copyFunc.compute(in, z);
		mulFunc.compute(I, z, iz);
		addFunc.compute(iz, ONE, sum);
		subFunc.compute(iz, ONE, diff);
		divFunc.compute(sum, diff, quotient);
		logFunc.compute(quotient, log);
		mulFunc.compute(I_OVER_TWO, log, output);
		return output;
	}

	@Override
	public ComplexArccot<T, U> copy() {
		return new ComplexArccot<T, U>(type);
	}
}
