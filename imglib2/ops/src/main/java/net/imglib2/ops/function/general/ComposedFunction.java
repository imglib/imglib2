/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.function.general;

import java.util.ArrayList;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;

// TODO - this is a simple implementation. It only works from a fixed point
// along a axis. Ideally in the future we'd have different shaped functions
// that would have regions within their domains stitched together.

// NOTE you cannot change dimensionality of functions. i.e. this might be
// a 3d function made up of other 3d functions. No stacks of 2d functions
// making a 3d function. Fix? Or make a stacked implementation too. Maybe
// a function can be lifted from 2d to N-d with each new dimension size==1.

// Limitation : requires long[]'s. what about double[]'s?

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ComposedFunction<T> implements Function<long[],T> {

	private ArrayList<Function<long[],T>> functions;
	private ArrayList<Long> widths;
	private long startIndex;
	private long[] relativePosition;
	private Neighborhood<long[]> localNeigh;
	private int dimension;
	
	public ComposedFunction(int dim, long startPoint) {
		functions = new ArrayList<Function<long[],T>>();
		widths = new ArrayList<Long>();
		dimension = dim;
		startIndex = startPoint;
		relativePosition = null;
		localNeigh = null;
	}

	public void add(Function<long[],T> function, long width) {
		functions.add(function);
		widths.add(width);
	}
	
	@Override
	public void evaluate(Neighborhood<long[]> neigh, long[] point, T output) {
		if (relativePosition == null) {
			relativePosition = new long[point.length];
			localNeigh = neigh.duplicate();
		}
		for (int i = 0; i < relativePosition.length; i++)
			relativePosition[i] = point[i];
		long indexVal = point[dimension];
		long currSpot = startIndex;
		for (int i = 0; i < functions.size(); i++) {
			long functionWidth = widths.get(i);
			if (indexVal < currSpot + functionWidth) {
				localNeigh.moveTo(relativePosition);
				functions.get(i).evaluate(localNeigh, relativePosition, output);
				return;
			}
			currSpot += functionWidth;
			relativePosition[dimension] -= functionWidth;
		}
		throw new IllegalArgumentException(
				"ComposedFunction::evaluate() - given point is out of bounds");
	}

	@Override
	public T createOutput() {
		if (functions.size() > 0)
			return functions.get(0).createOutput();
		throw new IllegalArgumentException(
				"ComposedFunction has not been initialized yet.");
	}
}
