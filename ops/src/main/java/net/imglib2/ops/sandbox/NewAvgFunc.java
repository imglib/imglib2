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

package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class NewAvgFunc<U extends RealType<U>,V extends RealType<V>>
	implements NewFunc<U, V>
{
	private Cursor<U> crs = null;
	private V outType = null;
	
	public NewAvgFunc(V outType) {
		this.outType = outType;
	}

	@Override
	public void evaluate(NewIterableInterval<U> i, V output) {
		if (crs == null) crs = i.cursor();
		crs.reset();
		double sum = 0;
		long numElements = 0;
		while (crs.hasNext()) {
			sum += crs.next().getRealDouble();
			numElements++;
		}
		if (numElements == 0)
			output.setZero();
		else
			output.setReal(sum / numElements);
	}
	
	// MIGHT BE INCONVENIENT - works in RealIntervals (might be useful too)
	public void evaluate(RealRandomAccessibleRealInterval<U> interval, V output) {
		// NB - can't use any RegionOfInterest because it is always of type
		// BitType and no ops of ours will get the right numeric data. Use next
		// lower one in chain
		
		// CAN'T RELOCATE THE INTERVAL
		// CAN'T CREATE A CURSOR

		// CAN DO THIS BUT BACK AT SQUARE ONE
		interval.realRandomAccess();
	}

	// NB - attempt1 - FAILURE
	public void evaluate(Cursor<U> cursor, V output) {
		// need to make a copy of the cursor as we will change its position and
		// parent might hate that. (MAYBE I'M WRONG HERE). But any function that
		// evals another function needs its own cursor
		if (crs == null) {
			crs = cursor.copyCursor();
			//position = new long[cursor.numDimensions()];
		}
		//cursor.localize(position);  // EXPENSIVE as setPosition() call from pure method
		//cursor.relocate(position);  // NOPE - can't do this type of needed op
		crs.reset();
		double sum = 0;
		long numElements = 0;
		while (crs.hasNext()) {
			sum += crs.next().getRealDouble();
			numElements++;
		}
		if (numElements == 0)
			output.setZero();
		else
			output.setReal(sum / numElements);
	}

	@Override
	public V createOutput() {
		return outType.createVariable();
	}
	
	@Override
	public NewAvgFunc<U,V> copy() {
		return new NewAvgFunc<U,V>(outType);
	}
}
