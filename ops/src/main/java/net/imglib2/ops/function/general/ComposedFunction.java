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
import net.imglib2.ops.pointset.PointSet;

// TODO - this is a simple implementation. It only works from a fixed point
// along a axis. Ideally in the future we'd have different shaped functions
// that would have regions within their domains stitched together.

// NOTE you cannot change dimensionality of functions. i.e. this might be
// a 3d function made up of other 3d functions. No stacks of 2d functions
// making a 3d function. Fix? Or make a stacked implementation too. Maybe
// a function can be lifted from 2d to N-d with each new dimension size==1.

// Limitation : requires long[]'s. what about double[]'s?

// NOTE - this function composed of a number of 0-based functions

// TODO - use a data structure that does not require O(n) function searches
// in the evaluate method. Can certainly find a O(log n) solution using
// some tagged tree structure.

// As defined there is a (serious?) limitation of ComposedFunction. Imagine
// we stitch together three 1x3 functions to make a 3x3 composed function.
// And then we query it passing in a 3x3 neighborhood. The evaluate() code
// dispatches to the 1x3 subfunctions at each point. If they look outside
// their 1x3 neighborhood (since their passed a 3x3) they won't be pulling
// values from the their neighboring functions. Perhaps we need to keep
// smaller local neighborhoods around (1 per function) and locate them so
// that their underlying functions don't go out of bounds. This limitation
// needs to be thought about more.

/**
 * <p>A {@link Function} that is composed of other Functions. Each Function
 * that makes up the ComposedFunction is associated with a region. Regions
 * do not have to be continuous and can overlap.
 * <p>
 * When a point is handed to a ComposedFunction it determines which region
 * the point falls in and hands the calculation off to the associated
 * Function. When a point falls within multiple regions the computation is
 * done by the first matching region as input to the add() method.
 * 
 * @author Barry DeZonia
 */
public class ComposedFunction<T>
	implements Function<long[],T>
{
	// -- instance variables --
	
	private final int numDims;
	private final ArrayList<PointSet> regions;
	private final ArrayList<Function<long[],T>> functions;
	
	// -- constructor --
	
	public ComposedFunction(int numDims) {
		this.numDims = numDims;
		this.regions = new ArrayList<PointSet>();
		this.functions = new ArrayList<Function<long[],T>>();
	}

	// -- ComposedFunction methods --
	
	public void add(PointSet region, Function<long[],T> function) {
		if (region.numDimensions() != numDims)
			throw new IllegalArgumentException(
				"ComposedFunction::add() - cannot add region with incompatible dimensions");
		regions.add(region);
		functions.add(function);
	}
	
	// -- Function methods --
	
	@Override
	public void compute(long[] point, T output) {
		if (point.length != numDims)
			throw new IllegalArgumentException(
				"input point does not match dimensionality of composed function");
		for (int i = 0; i < regions.size(); i++) {
			final PointSet region = regions.get(i);
			if (region.includes(point)) {
				functions.get(i).compute(point, output);
				return;
			}
		}
		throw new IllegalArgumentException(
				"ComposedFunction::compute() - given point is out of bounds");
	}

	@Override
	public T createOutput() {
		if (functions.size() > 0)
			return functions.get(0).createOutput();
		throw new IllegalArgumentException(
				"ComposedFunction has not been initialized yet.");
	}
	
	@Override
	public ComposedFunction<T> copy() {
		ComposedFunction<T> newFunc = new ComposedFunction<T>(numDims);
		for (int i = 0; i < functions.size(); i++)
			newFunc.add(regions.get(i).copy(), functions.get(i).copy());
		return newFunc;
	}
}
