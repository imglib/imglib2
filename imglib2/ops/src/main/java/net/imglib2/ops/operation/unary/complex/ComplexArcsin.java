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
import net.imglib2.ops.operation.binary.complex.ComplexMultiply;
import net.imglib2.ops.operation.binary.complex.ComplexPower;
import net.imglib2.ops.operation.binary.complex.ComplexSubtract;
import net.imglib2.type.numeric.ComplexType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * 
 * @author Barry DeZonia
 * 
 */
public final class ComplexArcsin<T extends ComplexType<T>, U extends ComplexType<U>>
		implements UnaryOperation<T, U> {

	private final ComplexCopy<T, U> copyFunc;
	private final ComplexMultiply<U, U, U> mulFunc;
	private final ComplexSubtract<U, U, U> diffFunc;
	private final ComplexPower<U, U, U> powFunc;
	private final ComplexAdd<U, U, U> addFunc;
	private final ComplexLog<U, U> logFunc;

	private final U I;
	private final U MINUS_I;
	private final U ONE;
	private final U ONE_HALF;

	private final U z;
	private final U iz;
	private final U zSquared;
	private final U miniSum;
	private final U root;
	private final U sum;
	private final U logSum;

	private U type;

	public ComplexArcsin(U type) {
		this.type = type;

		copyFunc = new ComplexCopy<T, U>();
		mulFunc = new ComplexMultiply<U, U, U>();
		diffFunc = new ComplexSubtract<U, U, U>();
		powFunc = new ComplexPower<U, U, U>(type);
		addFunc = new ComplexAdd<U, U, U>();
		logFunc = new ComplexLog<U, U>();

		I = type.createVariable();
		MINUS_I = type.createVariable();
		ONE = type.createVariable();
		ONE_HALF = type.createVariable();

		z = type.createVariable();
		iz = type.createVariable();
		zSquared = type.createVariable();
		miniSum = type.createVariable();
		root = type.createVariable();
		sum = type.createVariable();
		logSum = type.createVariable();

		I.setComplexNumber(0, 1);
		MINUS_I.setComplexNumber(0, -1);
		ONE.setComplexNumber(1, 0);
		ONE_HALF.setComplexNumber(0.5, 0);
	}

	@Override
	public U compute(T in, U output) {
		copyFunc.compute(in, z);
		mulFunc.compute(I, z, iz);
		mulFunc.compute(z, z, zSquared);
		diffFunc.compute(ONE, zSquared, miniSum);
		powFunc.compute(miniSum, ONE_HALF, root);
		addFunc.compute(iz, root, sum);
		logFunc.compute(sum, logSum);
		mulFunc.compute(MINUS_I, logSum, output);
		return output;
	}

	@Override
	public ComplexArcsin<T, U> copy() {
		return new ComplexArcsin<T, U>(type);
	}
}
