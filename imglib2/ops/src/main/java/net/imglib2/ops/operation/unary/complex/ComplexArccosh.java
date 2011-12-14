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
public final class ComplexArccosh<T extends ComplexType<T>, U extends ComplexType<U>>
	implements UnaryOperation<T,U> {

	private final ComplexCopy<T,U> copyFunc;
	private final ComplexMultiply<U,U,U> mulFunc;
	private final ComplexSubtract<U,U,U> diffFunc;
	private final ComplexPower<U,U,U> powFunc;
	private final ComplexAdd<U,U,U> addFunc;
	private final ComplexLog<U,U> logFunc;
	
	private final U ONE;
	private final U ONE_HALF;

	private final U z;
	private final U zSquared;
	private final U miniSum;
	private final U root;
	private final U sum;
	
	private U type;

	public ComplexArccosh(U type) {
		this.type = type;

		copyFunc = new ComplexCopy<T,U>(type);
		mulFunc = new ComplexMultiply<U,U,U>(type);
		diffFunc = new ComplexSubtract<U,U,U>(type);
		powFunc = new ComplexPower<U,U,U>(type);
		addFunc = new ComplexAdd<U,U,U>(type);
		logFunc = new ComplexLog<U,U>(type);

		ONE = type.createVariable();
		ONE_HALF = type.createVariable();
		z = type.createVariable();
		zSquared = type.createVariable();
		miniSum = type.createVariable();
		root = type.createVariable();
		sum = type.createVariable();

		ONE.setComplexNumber(1, 0);
		ONE_HALF.setComplexNumber(0.5, 0);
	}
	
	@Override
	public void compute(T in, U output) {
		copyFunc.compute(in,z);
		mulFunc.compute(z, z, zSquared);
		diffFunc.compute(zSquared, ONE, miniSum);
		powFunc.compute(miniSum, ONE_HALF, root);
		addFunc.compute(z, root, sum);
		logFunc.compute(sum, output);
	}
	
	@Override
	public ComplexArccosh<T,U> copy() {
		return new ComplexArccosh<T,U>(type);
	}

	@Override
	public U createOutput(T dataHint) {
		return type.createVariable();
	}
}
