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

import static org.junit.Assert.assertEquals;
import net.imglib2.ops.operation.complex.unary.ComplexExp;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexExpTest {

	private ComplexExp<ComplexDoubleType,ComplexDoubleType> op =
		new ComplexExp<ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		
		// try power of 0
		doCase(0,0,1,0);

		// try real integer 1 & -1
		doCase(1,0,Math.E,0);
		doCase(-1,0,0.367879,0);
		
		// try imaginary integer 1 & -1
		doCase(0,1,0.540302,0.841470);
		doCase(0,-1,0.540302,-0.841470);

		// try multiple integral powers
		for (int i = 0; i < 20; i++)
			doCase(i,0,Math.pow(Math.E, i),0);
		
		// try some random values
		doCase(1,2,-1.131204,2.471726);
		doCase(2.4,-2.6,-9.44564,-5.68246);
		doCase(1.5,4.3,-1.79626,-4.10597);
		doCase(-6.2,3.2,-0.002025,-0.000118);
		doCase(-4.4,-2.7,-0.011099,-0.005247);
		doCase(Math.E,Math.E,-13.816654,6.225087);
	}

	private void doCase(double r1, double i1, double expR, double expI) {
		input1.setComplexNumber(r1, i1);
		op.compute(input1, output);
		assertEquals(expR, output.getRealDouble(), 0.00001);
		assertEquals(expI, output.getImaginaryDouble(), 0.00001);
	}
}
