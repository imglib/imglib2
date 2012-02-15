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

import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

//Formula taken from MATLAB documentation

/**
 * Sets an output complex number to the hyperbolic secant of an input
 * complex number.
 * 
 * @author Barry DeZonia
 * 
 */
public final class ComplexSech<I extends ComplexType<I>, O extends ComplexType<O>>
	implements ComplexUnaryOperation<I,O>
{
	private final ComplexCosh<I,ComplexDoubleType>
		coshFunc = new ComplexCosh<I,ComplexDoubleType>();
	private final ComplexReciprocal<ComplexDoubleType,O>
		recipFunc = new ComplexReciprocal<ComplexDoubleType,O>();

	private final ComplexDoubleType cosh = new ComplexDoubleType();

	@Override
	public O compute(I z, O output) {
		coshFunc.compute(z, cosh);
		recipFunc.compute(cosh, output);
		return output;
	}

	@Override
	public ComplexSech<I,O> copy() {
		return new ComplexSech<I,O>();
	}
}
