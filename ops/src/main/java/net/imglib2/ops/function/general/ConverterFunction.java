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
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.NumericType;

// NOTE - this class would not support a ConditionalFunction as that
// implementation uses neigh info to calc one value or another. This could
// take a Bool to a Real but without neighborhood info. Might want another
// kind of operation/function to generalize a ConditionalFunction.

/**
 * Couples a {@link Function} and a {@link UnaryOperation}. The Function can
 * return values of any type. This type is an intermediate type. The
 * UnaryOperation converts values from the intermediate type to the final type
 * of this Function. The final type must be some kind of {@link NumericType}.
 *   
 * @author Barry DeZonia
 */
public class ConverterFunction<
		INPUT,
		INTERMEDIATE_TYPE,
		FINAL_TYPE extends NumericType<FINAL_TYPE>>
	implements Function<INPUT, FINAL_TYPE>
{
	// -- instance variables --
	
	private final Function<INPUT, INTERMEDIATE_TYPE> intermediateFunc;
	private final UnaryOperation<INTERMEDIATE_TYPE, FINAL_TYPE> operation;
	private final INTERMEDIATE_TYPE variable;
	private final FINAL_TYPE type;
	
	// -- constructor --
	
	public ConverterFunction(Function<INPUT, INTERMEDIATE_TYPE> func,
			UnaryOperation<INTERMEDIATE_TYPE, FINAL_TYPE> operation,
			FINAL_TYPE type)
	{
		this.type = type;
		this.intermediateFunc = func;
		this.operation = operation;
		this.variable = func.createOutput();
	}

	// -- Function methods --
	
	@Override
	public void compute(INPUT input, FINAL_TYPE output) {
		intermediateFunc.compute(input, variable);
		operation.compute(variable, output);
	}

	@Override
	public FINAL_TYPE createOutput() {
		return type.createVariable();
	}

	@Override
	public ConverterFunction<INPUT, INTERMEDIATE_TYPE, FINAL_TYPE> copy() {
		return new ConverterFunction<INPUT, INTERMEDIATE_TYPE, FINAL_TYPE>(
				intermediateFunc.copy(), operation.copy(), type.copy());
	}
}
