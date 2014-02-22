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
import net.imglib2.ops.operation.complex.binary.ComplexPower;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexPowerTest {

	private ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
		new ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		// values taken from Wolfram Alpha online calculator
		doCase(4,0,2,0,16,0);
		doCase(0,4,2,0,-16,0);
		doCase(1,1,1,1,0.273957,0.583701);
		doCase(-1,-1,-1,-1,-0.028475,0.060669);
		doCase(1,2,3,4,0.129009,0.033924);
	}

	private void doCase(double r1, double i1, double r2, double i2, double expR, double expI) {
		input1.setComplexNumber(r1, i1);
		input2.setComplexNumber(r2, i2);
		op.compute(input1, input2, output);
		assertEquals(expR, output.getRealDouble(), 0.000001);
		assertEquals(expI, output.getImaginaryDouble(), 0.000001);
	}
}
