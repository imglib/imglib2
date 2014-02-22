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
 * Base class for some simple conditions based upon values of functions.
 * 
 * @author Barry DeZonia
 */
public abstract class AbstractFunctionCondition<T extends RealType<T>>
	implements Condition<long[]>
{

	// -- instance variables --

	protected Function<long[], T> func;
	protected double value;
	private final T var;

	// -- abstract methods --

	abstract boolean relationTrue(double fVal);

	// -- constructor --

	public AbstractFunctionCondition(Function<long[], T> func, double value) {
		this.func = func;
		this.value = value;
		var = func.createOutput();
	}

	// -- Condition methods --

	@Override
	public boolean isTrue(long[] val) {
		func.compute(val, var);
		return relationTrue(var.getRealDouble());
	}

	// -- AbstractFunctionCondition methods --

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Function<long[], T> getFunction() {
		return func;
	}

	public void setFunction(Function<long[], T> func) {
		this.func = func;
	}
}

