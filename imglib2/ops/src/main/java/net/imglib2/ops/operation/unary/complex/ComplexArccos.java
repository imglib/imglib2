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

import net.imglib2.ops.ComplexOutput;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.Complex;
import net.imglib2.ops.operation.binary.complex.ComplexAdd;
import net.imglib2.ops.operation.binary.complex.ComplexMultiply;
import net.imglib2.ops.operation.binary.complex.ComplexPower;
import net.imglib2.ops.operation.binary.complex.ComplexSubtract;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * 
 * @author Barry DeZonia
 *
 */
public final class ComplexArccos extends ComplexOutput implements UnaryOperation<Complex,Complex> {

	private static final Complex MINUS_I = Complex.createCartesian(0, -1);
	private static final Complex ONE = Complex.createCartesian(1, 0);
	private static final Complex ONE_HALF = Complex.createCartesian(0.5, 0);

	private static final ComplexMultiply mulFunc = new ComplexMultiply();
	private static final ComplexSubtract diffFunc = new ComplexSubtract();
	private static final ComplexPower powFunc = new ComplexPower();
	private static final ComplexAdd addFunc = new ComplexAdd();
	private static final ComplexLog logFunc = new ComplexLog();
	
	private final Complex zSquared = new Complex();
	private final Complex miniSum = new Complex();
	private final Complex root = new Complex();
	private final Complex sum = new Complex();
	private final Complex logSum = new Complex();
	
	@Override
	public void compute(Complex z, Complex output) {
		mulFunc.compute(z, z, zSquared);
		diffFunc.compute(zSquared, ONE, miniSum);
		powFunc.compute(miniSum, ONE_HALF, root);
		addFunc.compute(z, root, sum);
		logFunc.compute(sum, logSum);
		mulFunc.compute(MINUS_I, logSum, output);
	}
	
	@Override
	public ComplexArccos duplicate() {
		return new ComplexArccos();
	}
}
