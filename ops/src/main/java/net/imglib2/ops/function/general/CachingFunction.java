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

import java.util.Arrays;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.util.DataCopier;

/**
 * A CachingFunction returns a cached value when the same input data is passed
 * to the evaluate() method. Currently it caches the last value only. Imagine 
 * you have a ConditionalFunction that evaluates a function twice (once as part
 * of the condition test and once as part of the assignment of value). This
 * class can be used to improve the performance of function evaluation if the
 * cost of computing the function is high. (The test for input equality is
 * relatively expensive in its own right)
 *  
 * @author Barry DeZonia
 */
public class CachingFunction<T extends DataCopier<T>> implements Function<long[],T> {

	// -- instance variables --
	
	private final Function<long[],T> otherFunc;
	private long[] lastPoint;
	private final T lastValue;

	// -- constructor --
	
	public CachingFunction(Function<long[],T> otherFunc) {
		this.otherFunc = otherFunc;
		lastValue = createOutput();
		lastPoint = null;
	}
	
	// -- public interface --
	
	@Override
	public void compute(long[] point, T output) {
		if (lastPoint == null) {
			lastPoint = point.clone();
			otherFunc.compute(point, lastValue);
		}
		else if (!Arrays.equals(point,lastPoint)) {
			recordInput(point);
			otherFunc.compute(point, lastValue);
		}
		output.setValue(lastValue);
	}

	@Override
	public T createOutput() {
		return otherFunc.createOutput();
	}

	
	@Override
	public CachingFunction<T> copy() {
		return new CachingFunction<T>(otherFunc.copy());
	}

	// -- private helpers --
	
	private void recordInput(long[] point) {
		for (int i = 0; i < lastPoint.length; i++) {
			lastPoint[i] = point[i];
		}
	}
}
