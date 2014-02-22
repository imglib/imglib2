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

import java.util.ArrayList;

import net.imglib2.ops.function.Function;

// This is a proof of concept implementation that would allow one to interleave
// a number of input datasets or other functions.

/**
 * AlternatingFunction interleaves data from a set of input {@link Function}s.
 * 
 * @author Barry DeZonia
 */
public class AlternatingFunction<T>
	implements Function<long[],T>
{
	// -- instance variables --
	
	private final ArrayList<Function<long[],T>> functions;
	private long[] relativePosition;
	private final int dimension;
	
	// -- constructor --
	
	public AlternatingFunction(int dim) {
		functions = new ArrayList<Function<long[],T>>();
		dimension = dim;
		relativePosition = null;
	}

	// -- AlternatingFunction methods --
	
	public void add(Function<long[],T> function) {
		functions.add(function);
	}
	
	// -- Function methods --
	
	@Override
	public void compute(long[] point, T output) {
		if (relativePosition == null) {
			relativePosition = new long[point.length];
		}
		for (int i = 0; i < relativePosition.length; i++)
			relativePosition[i] = point[i];
		relativePosition[dimension] /= functions.size();  // TODO - assumes pos >= 0 here
		int funcNum = (int) (point[dimension] % functions.size());
		functions.get(funcNum).compute(relativePosition, output);
	}

	@Override
	public T createOutput() {
		if (functions.size() > 0)
			return functions.get(0).createOutput();
		throw new IllegalArgumentException(
				"AlternatingFunction has not been initialized yet.");
	}
	
	@Override
	public AlternatingFunction<T> copy() {
		AlternatingFunction<T> newFunc = new AlternatingFunction<T>(dimension);
		for (Function<long[],T> f : functions)
			newFunc.add(f.copy());
		return newFunc;
	}
}
