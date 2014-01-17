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
import net.imglib2.ops.operation.complex.binary.ComplexMultiply;
import net.imglib2.ops.operation.complex.binary.ComplexPower;
import net.imglib2.ops.operation.complex.binary.ComplexSubtract;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * Sets an output complex number to the inverse cosine of an input complex
 * number.
 * 
 * @author Barry DeZonia
 */
public final class ComplexArccos<I extends ComplexType<I>, O extends ComplexType<O>>
	implements ComplexUnaryOperation<I,O>
{
	private final ComplexMultiply<I,I,ComplexDoubleType>
		mulFunc1 = new ComplexMultiply<I,I,ComplexDoubleType>();
	private final ComplexSubtract<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>
		diffFunc = new ComplexSubtract<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private final ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>
		powFunc = new ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private final ComplexAdd<I,ComplexDoubleType,ComplexDoubleType>
		addFunc = new ComplexAdd<I,ComplexDoubleType,ComplexDoubleType>();
	private final ComplexLog<ComplexDoubleType,ComplexDoubleType>
		logFunc = new ComplexLog<ComplexDoubleType,ComplexDoubleType>();
	private final ComplexMultiply<ComplexDoubleType,ComplexDoubleType,O>
		mulFunc2 = new ComplexMultiply<ComplexDoubleType,ComplexDoubleType,O>();

	private static final ComplexDoubleType MINUS_I = new ComplexDoubleType(0,-1);
	private static final ComplexDoubleType ONE = new ComplexDoubleType(1,0);
	private static final ComplexDoubleType ONE_HALF = new ComplexDoubleType(0.5,0);

	private final ComplexDoubleType zSquared = new ComplexDoubleType();
	private final ComplexDoubleType miniSum = new ComplexDoubleType();
	private final ComplexDoubleType root = new ComplexDoubleType();
	private final ComplexDoubleType sum = new ComplexDoubleType();
	private final ComplexDoubleType logSum = new ComplexDoubleType();

	@Override
	public O compute(I z, O output) {
		mulFunc1.compute(z, z, zSquared);
		diffFunc.compute(zSquared, ONE, miniSum);
		powFunc.compute(miniSum, ONE_HALF, root);
		addFunc.compute(z, root, sum);
		logFunc.compute(sum, logSum);
		mulFunc2.compute(MINUS_I, logSum, output);
		return output;
	}

	@Override
	public ComplexArccos<I,O> copy() {
		return new ComplexArccos<I,O>();
	}

}
