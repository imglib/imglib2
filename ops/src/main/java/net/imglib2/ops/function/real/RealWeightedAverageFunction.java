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

// TODO - this class suffers from the weakness that one needs to
// correctly build the weights without maybe knowing the order of points in
// the PointSet. We will need helper methods in that build a PointSet
// compatible set of weights from a PointSet and some other hints.

import net.imglib2.ops.function.Function;
import net.imglib2.type.numeric.RealType;

/**
 * Computes a weighted average of the values of another function over a region.
 * The weights are specified in the constructor.
 * 
 * @author Barry DeZonia
 */
public class RealWeightedAverageFunction<T extends RealType<T>>
 extends
	AbstractRealStatFunction<T>
{
	// -- instance variables --
	
	private final double[] weights;
	
	// -- constructor --
	
	public RealWeightedAverageFunction(Function<long[],T> otherFunc, double[] weights)
	{
		super(otherFunc);
		this.weights = weights;
	}

	// -- abstract method overrides --

	@Override
	protected double value(StatCalculator<T> calc) {
		return calc.weightedAverage(weights);
	}

	// -- Function methods --
	
	@Override
	public RealWeightedAverageFunction<T> copy() {
		return new RealWeightedAverageFunction<T>(otherFunc.copy(), weights.clone());
	}

}
