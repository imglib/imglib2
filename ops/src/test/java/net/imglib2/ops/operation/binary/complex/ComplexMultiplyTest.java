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

package net.imglib2.ops.operation.binary.complex;

import static org.junit.Assert.assertEquals;
import net.imglib2.ops.operation.complex.binary.ComplexMultiply;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexMultiplyTest {

	private ComplexMultiply<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
		new ComplexMultiply<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		double[] values = new double[]{.1,.375,12,23.9,100,1250};
		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(r1,i1,r2,i2);

		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(r1,i1,-r2,-i2);

		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(-r1,-i1,r2,i2);

		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(-r1,-i1,-r2,-i2);

		// multiply 0 by something
		doCase(0,0,1,0);
		
		// multiply something by 0
		doCase(1,0,0,0);
		
		// multiply 0 by 0
		doCase(0,0,0,0);
	}

	private void doCase(double r1, double i1, double r2, double i2) {
		input1.setComplexNumber(r1, i1);
		input2.setComplexNumber(r2, i2);
		op.compute(input1, input2, output);
		double real = r1*r2 - i1*i2;
		double imag = r1*i2 + i1*r2;
		// if textbook implementation of algorithm
		assertEquals(real, output.getRealDouble(), 0);
		assertEquals(imag, output.getImaginaryDouble(), 0);
		// if optimized algorithm to minimize multiplications
		//assertEquals(real, output.getRealDouble(), 0.0001);
		//assertEquals(imag, output.getImaginaryDouble(), 0.0001);
	}
}
