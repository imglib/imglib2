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

package net.imglib2.ops.function.real;

import net.imglib2.img.Img;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.parse.RealEquationFunctionParser;
import net.imglib2.ops.util.Tuple2;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Builds a function from a text specification. A typical specification might
 * look like "[x,y] , 2*x^2 - 4*y + log(y)". In the specification x and y map
 * to axis indices in long[] position passed in to compute(). x is mapped
 * to the first axis index and y to the second axis index. Axis names are
 * strings of letters that do not match reserved words (like sin, cos, abs,
 * etc.). All axis names must be declared before being referenced.
 * 
 * @author Barry DeZonia
 */
public class RealEquationFunction<T extends RealType<T>>
	implements Function<long[], T>
{
	// -- instance variables --
	
	private final String origSpec;
	private final Function<long[], DoubleType> eqnFunc;
	private final DoubleType tmp;
	private final T factory;
	private final Img<T> img;
	
	// -- constructor --
	
	public RealEquationFunction(String specification, T type, Img<T> img) {
		final RealEquationFunctionParser parser =
				new RealEquationFunctionParser();
		final Tuple2<Function<long[],DoubleType>,String> result =
				parser.parse(specification, img);
		if (result.get2() != null)
			throw new IllegalArgumentException(result.get2());
		this.eqnFunc = result.get1();
		this.origSpec = specification;
		this.tmp = new DoubleType();
		this.factory = type;
		this.img = img;
	}

	public RealEquationFunction(String specification, T type) {
		this(specification, type, null);
	}

	// -- Function methods --
	
	@Override
	public void compute(long[] input, T output) {
		eqnFunc.compute(input, tmp);
		output.setReal(tmp.getRealDouble());
	}

	@Override
	public T createOutput() {
		return factory.createVariable();
	}

	@Override
	public RealEquationFunction<T> copy() {
		return new RealEquationFunction<T>(origSpec, createOutput(), img);
	}

}
