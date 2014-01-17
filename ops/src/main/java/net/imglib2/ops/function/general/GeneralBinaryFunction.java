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

package net.imglib2.ops.function.general;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.type.numeric.ComplexType;

/**
 * Combines the output of two other {@link Function}s into an output type.
 * Combines the two outputs using a {@link BinaryOperation}.
 *  
 * @author Barry DeZonia
 */
public class GeneralBinaryFunction<INPUT,
		C1 extends ComplexType<C1>,
		C2 extends ComplexType<C2>,
		OUTPUT extends ComplexType<OUTPUT>>
	implements Function<INPUT, OUTPUT>
{
	// -- instance variables --
	
	private final Function<INPUT, C1> f1;
	private final Function<INPUT, C2> f2;
	private final C1 input1;
	private final C2 input2;
	private final BinaryOperation<C1,C2,OUTPUT> operation;
	private final OUTPUT type;

	// -- constructor --
	
	public GeneralBinaryFunction(Function<INPUT,C1> f1,
			Function<INPUT,C2> f2,
			BinaryOperation<C1,C2,OUTPUT> operation,
			OUTPUT type)
	{
		this.type = type;
		this.f1 = f1;
		this.f2 = f2;
		this.input1 = f1.createOutput();
		this.input2 = f2.createOutput();
		this.operation = operation;
	}

	// -- Function methods --
	
	@Override
	public void compute(INPUT input, OUTPUT output) {
		f1.compute(input, input1);
		f2.compute(input, input2);
		operation.compute(input1, input2, output);
	}

	@Override
	public GeneralBinaryFunction<INPUT, C1, C2, OUTPUT> copy()
	{
		return new GeneralBinaryFunction<INPUT, C1, C2, OUTPUT>(
				f1.copy(), f2.copy(), operation.copy(), type.createVariable());
	}

	@Override
	public OUTPUT createOutput() {
		return type.createVariable();
	}
}
