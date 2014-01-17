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

package net.imglib2.ops.condition;

import net.imglib2.ops.function.Function;
import net.imglib2.type.numeric.RealType;

/**
 * A {@link Condition} that returns true when a given {@link Function} has a
 * value within a user specified range at a given point. The Function and the
 * value range are specified in the constructor.
 * 
 * @author Barry DeZonia
 */
public class WithinRangeCondition<T extends RealType<T>> implements Condition<long[]> {

	// -- instance variables --
	
	private Function<long[],T> valueFunc;
	private double min;
	private double max;
	private final T tmp;
	
	// -- WithinRangeCondition methods --
	
	public WithinRangeCondition(Function<long[],T> func, double min, double max) {
		this.valueFunc = func;
		this.min = min;
		this.max = max;
		tmp = func.createOutput();
	}
	
	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public Function<long[], T> getFunction() {
		return valueFunc;
	}
	
	public void setFunction(Function<long[], T> func) {
		valueFunc = func;
	}
	
	// -- Condition methods --
	
	@Override
	public boolean isTrue(long[] input) {
		valueFunc.compute(input, tmp);
		double val = tmp.getRealDouble();
		return (min <= val) && (val <= max);
	}

	@Override
	public WithinRangeCondition<T> copy() {
		return new WithinRangeCondition<T>(valueFunc, min, max);
	}
	
}
