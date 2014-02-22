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

package net.imglib2.ops.operation.iterableinterval.unary;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Point;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * @author Felix Schoenenberger (University of Konstanz)
 * @author Christian Dietz (University of Konstanz)
 */
public class CalculateDiameter implements
		UnaryOperation<IterableInterval<BitType>, DoubleType> {

	@Override
	public DoubleType compute(IterableInterval<BitType> input, DoubleType output) {
		double diameter = 0.0f;

		Cursor<BitType> cursor = input.localizingCursor();

		List<Point> points = new ArrayList<Point>((int) input.size());

		int[] position = new int[cursor.numDimensions()];
		while (cursor.hasNext()) {
			cursor.fwd();
			if (cursor.get().get()) {
				cursor.localize(position);
				points.add(new Point(position));
			}
		}

		for (Point p : points) {
			for (Point p2 : points) {
				double dist = 0.0f;
				for (int i = 0; i < p.numDimensions(); i++) {
					dist += (p.getIntPosition(i) - p2.getIntPosition(i))
							* (p.getIntPosition(i) - p2.getIntPosition(i));
				}
				diameter = Math.max(diameter, dist);
			}
		}

		// sqrt for euclidean
		diameter = Math.sqrt(diameter);

		output.set(diameter);
		return output;
	}

	@Override
	public UnaryOperation<IterableInterval<BitType>, DoubleType> copy() {
		return new CalculateDiameter();
	}

}
