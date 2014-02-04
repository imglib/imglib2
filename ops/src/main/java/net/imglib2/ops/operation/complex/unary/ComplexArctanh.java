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

package net.imglib2.ops.operation.complex.unary;

import net.imglib2.ops.operation.complex.binary.ComplexAdd;
import net.imglib2.ops.operation.complex.binary.ComplexDivide;
import net.imglib2.ops.operation.complex.binary.ComplexMultiply;
import net.imglib2.ops.operation.complex.binary.ComplexSubtract;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * Sets an output complex number to the inverse hyperbolic tangent of an
 * input complex number.
 * 
 * @author Barry DeZonia
 */
public final class ComplexArctanh<I extends ComplexType<I>, O extends ComplexType<O>>
	implements ComplexUnaryOperation<I,O>
{
	private final ComplexAdd<ComplexDoubleType,I,ComplexDoubleType>
		addFunc = new ComplexAdd<ComplexDoubleType, I, ComplexDoubleType>();
	private final ComplexSubtract<ComplexDoubleType, I, ComplexDoubleType>
		subFunc = new ComplexSubtract<ComplexDoubleType, I, ComplexDoubleType>();
	private final ComplexDivide<ComplexDoubleType, ComplexDoubleType, ComplexDoubleType>
		divFunc = new ComplexDivide<ComplexDoubleType, ComplexDoubleType, ComplexDoubleType>();
	private final ComplexLog<ComplexDoubleType, ComplexDoubleType>
		logFunc = new ComplexLog<ComplexDoubleType, ComplexDoubleType>();
	private final ComplexMultiply<ComplexDoubleType, ComplexDoubleType, O>
		mulFunc = new ComplexMultiply<ComplexDoubleType, ComplexDoubleType, O>();

	private static final ComplexDoubleType ONE = new ComplexDoubleType(1,0);
	private static final ComplexDoubleType ONE_HALF = new ComplexDoubleType(0,0.5);

	private final ComplexDoubleType sum = new ComplexDoubleType();
	private final ComplexDoubleType diff = new ComplexDoubleType();
	private final ComplexDoubleType quotient = new ComplexDoubleType();
	private final ComplexDoubleType log = new ComplexDoubleType();

	@Override
	public O compute(I z, O output) {
		addFunc.compute(ONE, z, sum);
		subFunc.compute(ONE, z, diff);
		divFunc.compute(sum, diff, quotient);
		logFunc.compute(quotient, log);
		mulFunc.compute(ONE_HALF, log, output);
		return output;
	}

	@Override
	public ComplexArctanh<I,O> copy() {
		return new ComplexArctanh<I,O>();
	}

}
