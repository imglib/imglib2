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

/**
 * Helper class for tracking bounds of a region. Used by some {@link PointSet}
 * implementations.
 * 
 * @author Barry DeZonia
 */
public class BoundsCalculator {
	
	// -- instance variables --
	
	private long[] min, max;

	// -- constructor --
	
	public BoundsCalculator() {
	}
	
	// -- BoundsCalculator methods --
	
	public long[] getMin() {
		return min;
	}
	
	public long[] getMax() {
		return max;
	}
	
	public void calc(PointSet ps) {
		PointSetIterator iter = ps.iterator();
		boolean invalid = true;
		while (iter.hasNext()) {
			long[] point = iter.next();
			if (invalid) {
				invalid = false;
				setMax(point);
				setMin(point);
			}
			else {
				updateMax(point);
				updateMin(point);
			}
		}
	}

	// -- private helpers --

	private void setMin(long[] p) {
		min = p.clone();
	}
	
	private void setMax(long[] p) {
		max = p.clone();
	}
	
	private void updateMin(long[] p) {
		for (int i = 0; i < min.length; i++) {
			if (p[i] < min[i]) min[i] = p[i];
		}
	}
	
	private void updateMax(long[] p) {
		for (int i = 0; i < max.length; i++) {
			if (p[i] > max[i]) max[i] = p[i];
		}
	}
}
