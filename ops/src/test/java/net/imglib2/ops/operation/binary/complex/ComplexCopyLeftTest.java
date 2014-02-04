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
import net.imglib2.ops.operation.complex.binary.ComplexCopyLeft;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexCopyLeftTest {

	private ComplexCopyLeft<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
		new ComplexCopyLeft<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		for (double r1 = -10.0; r1 <= 10.0; r1 += Math.PI / 10)
			for (double i1 = -10.0; i1 <= 10.0; i1 += Math.PI / 11)
				for (double r2 = -10.0; r2 <= 10.0; r2 += Math.PI / 12)
					for (double i2 = -10.0; i2 <= 10.0; i2 += Math.PI / 13)
						doCase(r1,i1,r2,i2);
			
	}
	
	private void doCase(double r1, double i1, double r2, double i2) {
		input1.setComplexNumber(r1, i1);
		input2.setComplexNumber(r2, i2);
		op.compute(input1, input2, output);
		assertEquals(r1, output.getRealDouble(), 0);
		assertEquals(i1, output.getImaginaryDouble(), 0);
	}

}
