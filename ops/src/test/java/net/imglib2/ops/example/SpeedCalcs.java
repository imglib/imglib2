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

package net.imglib2.ops.example;

import net.imglib2.ops.operation.complex.binary.ComplexDivide;
import net.imglib2.ops.operation.complex.unary.ComplexCos;
import net.imglib2.ops.operation.complex.unary.ComplexReciprocal;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

/**
 * TODO
 *
 */
public class SpeedCalcs {

	private static final ComplexCos<ComplexDoubleType,ComplexDoubleType>
		cosFunc =	new ComplexCos<ComplexDoubleType, ComplexDoubleType>();
	private static final ComplexReciprocal<ComplexDoubleType,ComplexDoubleType>
		recipFunc = new ComplexReciprocal<ComplexDoubleType, ComplexDoubleType>();
	private static final ComplexDivide<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>
		divFunc = new ComplexDivide<ComplexDoubleType, ComplexDoubleType, ComplexDoubleType>();
	private static final ComplexDoubleType one = new ComplexDoubleType(1,0);
		
	static void method1(ComplexDoubleType in, ComplexDoubleType out) {
		divFunc.compute(one, in, out);
	}
	
	static void method2(ComplexDoubleType in, ComplexDoubleType out) {
		recipFunc.compute(in, out);
	}

	static boolean veryClose(double v1, double v2) {
		return Math.abs(v1-v2) < 0.00001;
	}
	
	public static void main(String[] args) {
		System.out.println("Listing differences to screen");
		ComplexDoubleType cos = new ComplexDoubleType();
		ComplexDoubleType out1 = new ComplexDoubleType();
		ComplexDoubleType out2 = new ComplexDoubleType();
		for (double x = -100; x <= 100; x += 0.5) {
			for (double y = -100; y <= 100; y += 0.3) {
				ComplexDoubleType input = new ComplexDoubleType(x,y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
				method2(cos, out2);
				if (!veryClose(out1.getRealDouble(), out2.getRealDouble()) ||
						!veryClose(out1.getImaginaryDouble(), out2.getImaginaryDouble()))
					System.out.println("Methods differ!");
			}
		}
		System.out.println("Done listing differences to screen");
		System.out.println("Running speed tests");
		
		ComplexDoubleType input = new ComplexDoubleType();
		long pt1 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
			}
		}
		long pt2 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method2(cos, out2);
			}
		}
		long pt3 = System.currentTimeMillis();
		
		System.out.println("Total milleseconds");
		System.out.println("  trial 1 of 2");
		System.out.println("    method1 (divide(1,z))   = "+(pt2-pt1));
		System.out.println("    method2 (reciprocal(z)) = "+(pt3-pt2));
	
		pt1 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
			}
		}
		pt2 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method2(cos, out2);
			}
		}
		pt3 = System.currentTimeMillis();
		
		System.out.println("  trial 2 of 2");
		System.out.println("    method1 (divide(1,z))   = "+(pt2-pt1));
		System.out.println("    method2 (reciprocal(z)) = "+(pt3-pt2));
	}
	
}
