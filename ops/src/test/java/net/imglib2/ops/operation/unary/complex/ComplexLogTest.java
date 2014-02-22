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
import net.imglib2.ops.operation.complex.unary.ComplexLog;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class ComplexLogTest {

	private ComplexLog<ComplexDoubleType,ComplexDoubleType> op =
			new ComplexLog<ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		double rt2 = Math.sqrt(2);
		double rt32 = Math.sqrt(3.0)/2;
		
		// note: values taken from Wolfram Alpha online calculator
		
		// try 0 case
		
		doCase(0,0,Math.log(0),Double.NaN);

		// try all the cardinal directions
		
		doCase(1,0,Math.log(1),0);  // 0
		doCase(0,1,Math.log(1),Math.PI/2); // 90
		doCase(-1,0,Math.log(1),Math.PI);  // 180/-180
		doCase(0,-1,Math.log(1),-Math.PI/2);  // -90

		// try all 45 angles
		
		doCase(1,1,Math.log(rt2),Math.PI/4); // 45
		doCase(-1,1,Math.log(rt2),3*Math.PI/4);  // 135
		doCase(-1,-1,Math.log(rt2),-3*Math.PI/4); // -135
		doCase(1,-1,Math.log(rt2),-Math.PI/4);  // -45

		// try all 30/60 angles
		
		doCase(rt32,0.5,Math.log(1),Math.PI/6);  // 30
		doCase(0.5,rt32,Math.log(1),2*Math.PI/6);  // 60
		doCase(-0.5,rt32,Math.log(1),4*Math.PI/6);  // 120
		doCase(-rt32,0.5,Math.log(1),5*Math.PI/6);  // 150
		doCase(rt32,-0.5,Math.log(1),-Math.PI/6);  // -30
		doCase(0.5,-rt32,Math.log(1),-2*Math.PI/6);  // -60
		doCase(-0.5,-rt32,Math.log(1),-4*Math.PI/6);  // -120
		doCase(-rt32,-0.5,Math.log(1),-5*Math.PI/6);  // -150
		
		// try a few random values
		
		doCase(Math.E,Math.E,1.346573,0.785398);
		doCase(1,2,0.804718,1.107148);
		doCase(-3,-2,1.282474,-2.553590);
		doCase(4,-3,1.609437,-0.643501);
		doCase(-7,3,2.030221,2.736700);
	}

	private void doCase(double r1, double i1, double expR, double expI) {
		input1.setComplexNumber(r1, i1);
		op.compute(input1, output);
		assertEquals(expR, output.getRealDouble(), 0.000001);
		assertEquals(expI, output.getImaginaryDouble(), 0.000001);
	}
}
