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

package net.imglib2.ops.pointset;

import net.imglib2.roi.AbstractRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

// TODO - when the efforts of people working with SciJava resolves Roi
// implementations (real and integer, RegionOfInterest and PointSet)
// this class can go away.

/**
* Wraps a (integer based) {@link PointSet} as a (real based)
* {@link RegionOfInterest}. An adapter class that brings the functionality
* of RegionOfInterests to PointSets.
* 
* @author Barry DeZonia
*
*/
public class PointSetRegionOfInterest extends AbstractRegionOfInterest implements RegionOfInterest {

	private final PointSet points;
	private final long[] pos;
	
	public PointSetRegionOfInterest(PointSet points) {
		super(points.numDimensions());
		this.points = points;
		this.pos = new long[nDimensions];
	}
	
	@Override
	synchronized public boolean contains(double[] position) {
		if (position.length != nDimensions) return false;
		for (int i = 0; i < position.length; i++) {
			pos[i] = (long) position[i];
			if (pos[i] != position[i]) { // input point was truncated and not integral
				return false;
			}
		}
		return points.includes(pos);
	}

	@Override
	public void move(double displacement, int d) {
		if (Math.floor(displacement) != Math.ceil(displacement)) {
			throw new IllegalArgumentException(
				"PointSetRegionsOfInterest can only move in increments of 1.0.");
		}
		long[] deltas = new long[pos.length];
		deltas[d] = (long) displacement;
		points.translate(deltas);
	}

	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		for (int i = 0; i < minima.length; i++)
			minima[i] = points.min(i);
		for (int i = 0; i < maxima.length; i++)
			maxima[i] = points.max(i);
	}
	
}
