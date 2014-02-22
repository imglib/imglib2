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

import net.imglib2.ops.function.Function;
import net.imglib2.type.numeric.RealType;

// TODO - An example implementation of a real integral function.
// Note that it does a very simple approximation. Modify to use the
// multidimensional equivalent of a trapezoidal interpolation rule
// or simpson's or some appropriate integration fit.

/**
 * Does a numerical integration of another function over a user specified region.
 *  
 * @author Barry DeZonia
 */
public class RealContinuousIntegralFunction<T extends RealType<T>>
	implements Function<double[],T>
{
	// -- instance variables --
	
	private final Function<double[],T> otherFunc;
	private final double[] deltas;
	private final double[] ranges;
	private final double cellSize;
	private final T variable;
	private final double[] position;
	
	// -- constructor --

	/**
	 * Creates a Function that can compute the integral of another function. One
	 * computes the integral at a point over the limits of integration by the
	 * given per dimension step sizes.
	 * 
	 * @param otherFunc The function to integrate.
	 * @param ranges The per dimension min and max limits of integration.
	 * @param deltas The per dimension step sizes that divide the space between
	 *          the limits of integration.
	 */
	public RealContinuousIntegralFunction(
		Function<double[],T> otherFunc, double[] ranges, double[] deltas)
	{
		this.otherFunc = otherFunc;
		this.ranges = ranges.clone();
		this.deltas = deltas.clone();
		this.variable = createOutput();
		this.cellSize = cellSize(deltas);
		this.position = new double[deltas.length];
	}
	
	// -- Function methods --
	
	/**
	 * Compute the integral of a prespecified neighborhood anchored at the given
	 * point. The neighborhood is specified at construction time as the limits of
	 * integration.
	 */
	@Override
	public void compute(double[] point, T output) {
		
		for (int i = 0; i < position.length; i++)
			position[i] = point[i];

		double sum = 0;
		
		boolean done = false;
		while (!done) {
			otherFunc.compute(position, variable);
			sum += variable.getRealDouble() * cellSize;
			done = !nextPosition(position, point);
		}
		output.setReal(sum);
	}

	@Override
	public RealContinuousIntegralFunction<T> copy() {
		return new RealContinuousIntegralFunction<T>(otherFunc.copy(), ranges, deltas);
	}

	@Override
	public T createOutput() {
		return otherFunc.createOutput();
	}

	// -- private helpers --
	
	private double cellSize(double[] sizes) {
		if (sizes.length == 0) return 0;
		double totalSize = 1;
		for (double size : sizes)
			totalSize *= size;
		return totalSize;
	}
	
	private boolean nextPosition(double[] pos, double[] startPt) {
		for (int i = 0; i < pos.length; i++) {
			pos[i] += deltas[i];
			if (pos[i] <= startPt[i] + ranges[i])
				return true;
			pos[i] = startPt[i];
		}
		return false;
	}
}
