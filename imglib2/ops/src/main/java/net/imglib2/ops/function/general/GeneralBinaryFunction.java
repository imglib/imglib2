/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.function.general;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Function;
import net.imglib2.type.numeric.ComplexType;

/**
 * 
 * @author Barry DeZonia
 */
public class GeneralBinaryFunction<INDEX,
		INPUT1_TYPE extends ComplexType<INPUT1_TYPE>,
		INPUT2_TYPE extends ComplexType<INPUT2_TYPE>,
		OUTPUT_TYPE extends ComplexType<OUTPUT_TYPE>>
	implements Function<INDEX, OUTPUT_TYPE>
{
	private final Function<INDEX, INPUT1_TYPE> f1;
	private final Function<INDEX, INPUT2_TYPE> f2;
	private final INPUT1_TYPE input1;
	private final INPUT2_TYPE input2;
	private final BinaryOperation<INPUT1_TYPE,INPUT2_TYPE,OUTPUT_TYPE> operation;
	private final OUTPUT_TYPE type;

	public GeneralBinaryFunction(Function<INDEX, INPUT1_TYPE> f1,
			Function<INDEX, INPUT2_TYPE> f2,
			BinaryOperation<INPUT1_TYPE,INPUT2_TYPE,OUTPUT_TYPE> operation,
			OUTPUT_TYPE type) {
		this.type = type;
		this.f1 = f1;
		this.f2 = f2;
		this.input1 = f1.createOutput();
		this.input2 = f2.createOutput();
		this.operation = operation;
	}

	@Override
	public void compute(INDEX point, OUTPUT_TYPE output) {
		f1.compute(point, input1);
		f2.compute(point, input2);
		operation.compute(input1, input2, output);
	}

	@Override
	public GeneralBinaryFunction<INDEX, INPUT1_TYPE, INPUT2_TYPE, OUTPUT_TYPE> copy() {
		return new GeneralBinaryFunction<INDEX, INPUT1_TYPE, INPUT2_TYPE, OUTPUT_TYPE>(
				f1.copy(), f2.copy(), operation.copy(), type.createVariable());
	}

	@Override
	public OUTPUT_TYPE createOutput() {
		return type.createVariable();
	}
}
