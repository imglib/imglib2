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

import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.ops.operation.binary.complex.ComplexMultiply;
import net.imglib2.ops.operation.binary.complex.ComplexSubtract;
import net.imglib2.ops.operation.unary.complex.ComplexExp;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * Sets an output complex number to the sine of an input complex number.
 * 
 * @author Barry DeZonia
 * 
 */
public final class ComplexSin<I extends ComplexType<I>, O extends ComplexType<O>>
	implements ComplexUnaryOperation<I,O>
{
	private final ComplexMultiply<I,ComplexDoubleType,ComplexDoubleType>
		mulFunc = new ComplexMultiply<I,ComplexDoubleType,ComplexDoubleType>();
	private final ComplexExp<ComplexDoubleType,ComplexDoubleType>
		expFunc = new ComplexExp<ComplexDoubleType,ComplexDoubleType>();
	private final ComplexSubtract<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>
		subFunc = new ComplexSubtract<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private final ComplexDivide<ComplexDoubleType,ComplexDoubleType,O>
		divFunc = new ComplexDivide<ComplexDoubleType,ComplexDoubleType,O>();

	private static final ComplexDoubleType TWO_I = new ComplexDoubleType(0,2);
	private static final ComplexDoubleType I = new ComplexDoubleType(0,1);
	private static final ComplexDoubleType MINUS_I = new ComplexDoubleType(0,-1);

	private final ComplexDoubleType IZ = new ComplexDoubleType();
	private final ComplexDoubleType minusIZ = new ComplexDoubleType();
	private final ComplexDoubleType expIZ = new ComplexDoubleType();
	private final ComplexDoubleType expMinusIZ = new ComplexDoubleType();
	private final ComplexDoubleType diff = new ComplexDoubleType();

	@Override
	public O compute(I z, O output) {
		mulFunc.compute(z, I, IZ);
		mulFunc.compute(z, MINUS_I, minusIZ);
		expFunc.compute(IZ, expIZ);
		expFunc.compute(minusIZ, expMinusIZ);
		subFunc.compute(expIZ, expMinusIZ, diff);
		divFunc.compute(diff, TWO_I, output);
		return output;
	}

	@Override
	public ComplexSin<I,O> copy() {
		return new ComplexSin<I,O>();
	}

}
