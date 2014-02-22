/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.ops.operation.unary.complex;

import static org.junit.Assert.assertTrue;
import net.imglib2.ops.operation.complex.binary.ComplexPower;
import net.imglib2.ops.operation.complex.unary.ComplexIntegerPower;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexIntegerPowerTest {

	private ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> opFull =
		new ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output1 = new ComplexDoubleType();
	private ComplexDoubleType output2 = new ComplexDoubleType();

	@Test
	public void test() {
		
		// NOTE - this test makes sure that ComplexIntegerPower matches
		// ComplexPower when you pass it integer valued complex numbers.
		// ComplexPower needs to be tested thoroughly.
		
		doCase(1,2);
		doCase(4,-3);
		doCase(-Math.PI,Math.E);
		doCase(-2,-3.2);
		doCase(.1,.1);
		doCase(6.1,0.5);
		doCase(-0.3,4.6);
	}

	private void doCase(double r1, double i1) {
		for (int i = 0; i < 26; i++) {  // NOTE - diverges at 27 or higher
			ComplexDoubleType power = new ComplexDoubleType();
			power.setComplexNumber(i, 0);
			ComplexIntegerPower<ComplexDoubleType,ComplexDoubleType> op =
					new ComplexIntegerPower<ComplexDoubleType,ComplexDoubleType>(i);
			input1.setComplexNumber(r1, i1);
			input2.setComplexNumber(r1, i1);
			op.compute(input1, output1);
			opFull.compute(input2, power, output2);
			assertTrue(near(output1.getRealDouble(), output2.getRealDouble(), 0.000001));
			assertTrue(near(output1.getImaginaryDouble(), output2.getImaginaryDouble(), 0.000001));
		}
	}
	
	private boolean near(double d1, double d2, double tol) {
		if (d1 == 0 || d2 == 0) {
			return Math.abs(d1-d2) < tol;
		}

		double ratio = d1 / d2;
		return (Math.abs(ratio) - 1) < tol;
	}
}
