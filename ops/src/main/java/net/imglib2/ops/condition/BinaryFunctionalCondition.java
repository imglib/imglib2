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
import net.imglib2.ops.relation.BinaryRelation;

/**
 * A {@link Condition} between two {@link Function}s. The Condition is true when
 * a given {@link BinaryRelation} is satisfied between the two Functions for a
 * specific input.
 * 
 * @author Barry DeZonia
 */
public class BinaryFunctionalCondition<INPUT,O1,O2> implements Condition<INPUT> {

	// -- instance variables --
	
	private final Function<INPUT,O1> f1;
	private final Function<INPUT,O2> f2;
	private final O1 f1Val;
	private final O2 f2Val;
	private final BinaryRelation<O1,O2> relation;

	// -- constructor --
	
	public BinaryFunctionalCondition(Function<INPUT,O1> f1, Function<INPUT,O2> f2, BinaryRelation<O1,O2> relation) {
		this.f1 = f1;
		this.f2 = f2;
		this.f1Val = f1.createOutput();
		this.f2Val = f2.createOutput();
		this.relation = relation;
	}
	
	// -- Condition methods --
	
	@Override
	public boolean isTrue(INPUT input) {
		f1.compute(input, f1Val);
		f2.compute(input, f2Val);
		return relation.holds(f1Val,f2Val);
	}
	
	@Override
	public BinaryFunctionalCondition<INPUT,O1,O2> copy() {
		return new BinaryFunctionalCondition<INPUT,O1,O2>(f1.copy(), f2.copy(), relation.copy());
	}

}
