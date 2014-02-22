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
import net.imglib2.ops.operation.complex.binary.ComplexDivide;
import net.imglib2.ops.operation.complex.unary.ComplexReciprocal;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexReciprocalTest {

	@Test
	public void test() {
		
		doCase(0,0);
		doCase(1,0);
		doCase(0,1);
		doCase(-1,0);
		doCase(0,-1);
		doCase(Math.E,-Math.PI);
		doCase(-Math.PI,Math.E);
		doCase(3,4);
		doCase(-5,7);
		doCase(2,-6);
		doCase(-7,-3);
		doCase(22.5,97.1);
		doCase(1000,1000);
	}

	// this method tests that taking the reciprocal of a number is the same as
	// dividing one by that number.
	
	private void doCase(double r, double i) {
		ComplexDoubleType one = new ComplexDoubleType();
		ComplexDoubleType input = new ComplexDoubleType();
		ComplexDoubleType output1 = new ComplexDoubleType();
		ComplexDoubleType output2 = new ComplexDoubleType();
		one.setOne();
		input.setComplexNumber(r, i);
		ComplexReciprocal<ComplexDoubleType,ComplexDoubleType> opRecip =
				new ComplexReciprocal<ComplexDoubleType,ComplexDoubleType>();
		ComplexDivide<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> opDiv =
				new ComplexDivide<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
		opRecip.compute(input, output1);
		opDiv.compute(one, input, output2);
		assertEquals(output1.getRealDouble(), output2.getRealDouble(), 0.000001);
		assertEquals(output1.getImaginaryDouble(), output2.getImaginaryDouble(), 0.000001);
	}
}
