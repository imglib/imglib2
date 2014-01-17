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

package net.imglib2.meta;

import java.util.HashMap;

import net.imglib2.RealInterval;
import net.imglib2.RealPositionable;

/**
 * A {@code CombinedRealInterval} is a {@link RealInterval} (specifically a
 * {@link TypedRealInterval}) which is a union of other
 * {@link TypedRealInterval}s. Dimensions with the same {@link AxisType} are
 * combined; see {@link CombinedSpace} for further details.
 * <p>
 * The {@link #realMin} of a dimension will be the lower bound of all
 * constituent interval {@link #realMin}s for dimensions of that type.
 * Similarly, the {@link #realMax} of a dimension will be the upper bound of all
 * constituent interval {@link #realMax}s.
 * </p>
 * <p>
 * In the case of {@link RealInterval}s which also implement
 * {@link CalibratedSpace}, no reconciliation is done to ensure that overlapping
 * axes have equal units or calibrations; it is assumed that each axis has
 * already been standardized to a common calibration via the
 * {@link CalibratedViews#recalibrate} method.
 * </p>
 * 
 * @author Curtis Rueden
 */
public class CombinedRealInterval<A extends TypedAxis, S extends TypedRealInterval<A>>
	extends CombinedSpace<A, S> implements TypedRealInterval<A>
{

	/** Combined min and max values for each axis. */
	private final HashMap<AxisType, MinMax> minMax =
		new HashMap<AxisType, MinMax>();

	// -- CombinedRealInterval methods --

	@Override
	public void update() {
		synchronized(this) {
			super.update();
			minMax.clear();
			for (final TypedRealInterval<A> interval : this) {
				for (int d = 0; d < interval.numDimensions(); d++) {
					final AxisType axisType = interval.axis(d).type();
					if (!minMax.containsKey(axisType)) {
						// new axis; add to the hash
						minMax.put(axisType, new MinMax());
					}
					final MinMax mm = minMax.get(axisType);
					mm.expand(interval.realMin(d), interval.realMax(d));
				}
			}
		}
	}

	// -- RealInterval methods --

	@Override
	public double realMin(final int d) {
			return (minMax().get(axis(d).type())).min();
	}

	@Override
	public void realMin(final double[] min) {
		for (int i = 0; i < min.length; i++)
			min[i] = realMin(i);
	}

	@Override
	public void realMin(final RealPositionable min) {
		for (int i = 0; i < min.numDimensions(); i++)
			min.setPosition(realMin(i), i);
	}

	@Override
	public double realMax(final int d) {
		return (minMax().get(axis(d).type())).max();
	}

	@Override
	public void realMax(final double[] max) {
		for (int i = 0; i < max.length; i++)
			max[i] = realMax(i);
	}

	@Override
	public void realMax(final RealPositionable max) {
		for (int i = 0; i < max.numDimensions(); i++)
			max.setPosition(realMax(i), i);
	}

	// -- Helper classes --

	protected class MinMax {

		private double minMin = Double.POSITIVE_INFINITY;
		private double maxMax = Double.NEGATIVE_INFINITY;

		public void expand(final double min, final double max) {
			if (min < minMin) minMin = min;
			if (max > maxMax) maxMax = max;
		}

		public double min() {
			return minMin;
		}

		public double max() {
			return maxMax;
		}

	}
	
	// -- Helper methods --

	/**
	 * Helper method to return min max map in a threadsafe way, so as not to
	 * conflict with {@link #update()}
	 * 
	 * @return map of axis types to MinMax for this interval
	 */
	private HashMap<AxisType, MinMax> minMax() {
		synchronized (this) {
			return minMax;
		}
	}

}
