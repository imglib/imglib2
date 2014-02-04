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

package net.imglib2.ops.operation.complex.binary;

import net.imglib2.type.numeric.ComplexType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * Sets a complex number output to the product of two complex number inputs.
 * 
 * @author Barry DeZonia
 */
public final class ComplexMultiply<
		I1 extends ComplexType<I1>,
		I2 extends ComplexType<I2>,
		O extends ComplexType<O>>
	implements ComplexBinaryOperation<I1,I2,O>
{
	@Override
	public O compute(I1 z1, I2 z2, O output) {
		/*
		  optimized version (fewer multiplies) : from mathworld.com
		    seems to exhibit some rounding differences from textbook algorithm
		    
		double a = z1.getRealDouble();
		double b = z1.getImaginaryDouble();
		double c = z2.getRealDouble();
		double d = z2.getImaginaryDouble();
		
		double ac = a*c;
		double bd = b*d;

		double x = ac - bd;
		double y = (a + b)*(c + d) - ac - bd;
		*/
		
		// textbook version : works
		double x = z1.getRealDouble() * z2.getRealDouble()
				- z1.getImaginaryDouble() * z2.getImaginaryDouble();
		double y = z1.getImaginaryDouble() * z2.getRealDouble()
				+ z1.getRealDouble() * z2.getImaginaryDouble();
		
		output.setComplexNumber(x, y);
		return output;
	}

	@Override
	public ComplexMultiply<I1,I2,O> copy() {
		return new ComplexMultiply<I1,I2,O>();
	}

}
