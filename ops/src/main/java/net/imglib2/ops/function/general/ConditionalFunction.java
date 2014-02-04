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

import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.function.Function;

/**
 * A {@link Function} that takes on one of two Function values depending upon
 * the value of a {@link Condition} at an input region.
 * 
 * @author Barry DeZonia
 */
public class ConditionalFunction<INPUT, T>
	implements Function<INPUT,T>
{
	// -- instance variables --
	
	private final Condition<INPUT> condition;
	private final Function<INPUT,T> f1;
	private final Function<INPUT,T> f2;
	
	// -- constructor --
	
	/**
	 * Constructs a ConditionalFunction from inputs.
	 * 
	 * @param condition
	 * The condition used to determine which function to choose
	 * @param f1
	 * The function to pull values from if the condition is true
	 * @param f2
	 * The function to pull values from if the condition is false
	 */
	public ConditionalFunction(
			Condition<INPUT> condition,
			Function<INPUT,T> f1,
			Function<INPUT,T> f2)
	{
		this.condition = condition;
		this.f1 = f1;
		this.f2 = f2;
	}
	
	// -- Function methods --
	
	@Override
	public void compute(INPUT input, T output) {
		if (condition.isTrue(input))
			f1.compute(input, output);
		else
			f2.compute(input, output);
	}

	@Override
	public T createOutput() {
		return f1.createOutput();
	}
	
	@Override
	public ConditionalFunction<INPUT,T> copy() {
		return new ConditionalFunction<INPUT, T>(condition.copy(), f1.copy(), f2.copy());
	}
}
