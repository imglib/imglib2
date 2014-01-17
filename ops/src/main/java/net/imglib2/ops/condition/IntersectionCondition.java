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

import java.util.ArrayList;
import java.util.List;

/**
* A {@link Condition} that aggregates a list of other Conditions as a group.
* This Condition is true when all of the other Conditions are true.
* 
* @author Barry DeZonia
*
*/
public class IntersectionCondition<T> implements Condition<T> {
	
	// -- instance variables --
	
	private final Condition<T> condition;
	
	// -- constructor --
	
	public IntersectionCondition(List<Condition<T>> conditions) {
		if (conditions.size() == 0)
			throw new IllegalArgumentException("no conditions provided");
		else if (conditions.size() == 1)
			condition = conditions.get(0);
		else {
			AndCondition<T> and =
					new AndCondition<T>(conditions.get(0), conditions.get(1));
			for (int i = 2; i < conditions.size(); i++)
				and = new AndCondition<T>(and, conditions.get(i));
			condition = and;
		}
	}

	// -- Condition methods --
	
	@Override
	public boolean isTrue(T val) {
		return condition.isTrue(val);
	}

	@Override
	public IntersectionCondition<T> copy() {
		final List<Condition<T>> conditions = new ArrayList<Condition<T>>();
		conditions.add(condition.copy());
		return new IntersectionCondition<T>(conditions);
	}
}

