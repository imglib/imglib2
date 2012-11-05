/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.sandbox.sampling;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.RealCursor;
import net.imglib2.Sampler;


/**
 * A real coordinate based sampling of space. The sampling is defined as a
 * regular grid between two points (along the orthogonal axes).
 * 
 * @author Barry DeZonia
 */
public class GridSampling extends AbstractSampling {

	// -- instance variables --

	private final double[] pt1;
	private final double[] pt2;
	private final double[] increments;

	// -- constructors --

	/**
	 * Set up a grid sampling starting at point one and iterating towards point
	 * two. The increments specify the granularity of movement along each axis.
	 * The increments are expressed as positive values (even though depending upon
	 * input point orientations the grid points may move in negative directions).
	 * 
	 * @param pt1 The first point (the origin of the grid)
	 * @param pt2 The second point (the extremal value of the grid)
	 * @param increments The per axis positive step values between grid points
	 */
	public GridSampling(double pt1[], double[] pt2, double[] increments) {
		super(pt1.length);
		this.pt1 = pt1.clone();
		this.pt2 = pt2.clone();
		this.increments = increments.clone();
		if (pt2.length != n) {
			throw new IllegalArgumentException("points have mismatching sizes");
		}
		if (increments.length != n) {
			throw new IllegalArgumentException("incorrect number of increments");
		}
		for (double increment : increments) {
			if (increment <= 0) {
				throw new IllegalArgumentException("increments must all be positive");
			}
		}
		// now set signs of increments
		for (int i = 0; i < n; i++) {
			if (pt1[i] > pt2[i]) this.increments[i] *= -1;
		}
	}

	/**
	 * Set up a grid sampling starting at point one and iterating towards point
	 * two in increments of 1 unit per axis.
	 * 
	 * @param pt1 The first point (the origin of the grid)
	 * @param pt2 The second point (the extremal value of the grid)
	 */
	public GridSampling(double[] pt1, double[] pt2) {
		this(pt1, pt2, ones(pt1.length));
	}

	/**
	 * Set up a grid sampling starting at the origin and iterating towards a point
	 * in increments of 1 unit per axis.
	 * 
	 * @param pt The end point of the grid
	 */
	public GridSampling(double[] pt) {
		this(new double[pt.length], pt);
	}

	// -- IterableRealInterval methods --

	@Override
	public SamplingIterator iterator() {
		return new GridSamplingIterator();
	}
	
	@Override
	public int numDimensions() {
		return n;
	}

	@Override
	public double realMin(int d) {
		return Math.min(pt1[d], pt2[d]);
	}

	@Override
	public double realMax(int d) {
		return Math.max(pt1[d], pt2[d]);
	}

	@Override
	public long size() {
		if (n == 0) return 0;
		long prod = 1;
		for (int i = 0; i < n; i++) {
			double span = realMax(i) - realMin(i);
			double increment = increments[i];
			double numSteps = (span / increment) + 1;
			prod *= Math.floor(numSteps);
		}
		return prod;
	}


	// -- private helpers --

	private static double[] ones(int numD) {
		double[] ones = new double[numD];
		for (int i = 0; i < numD; i++)
			ones[i] = 1;
		return ones;
	}

	private class GridSamplingIterator extends AbstractSamplingIterator {

		private boolean outOfBounds;

		public GridSamplingIterator() {
			super(n);
			outOfBounds = true;
		}

		@Override
		public boolean hasNext() {
			if (outOfBounds) return true;
			for (int i = 0; i < n; i++) {
				double c = curr[i];
				double p1 = pt1[i];
				double p2 = pt2[i];
				double inc = increments[i];
				if (c >= p1 && c + inc <= p2) return true;
				if (c <= p1 && c + inc >= p2) return true;
			}
			return false;
		}

		@Override
		public void fwd() {
			if (outOfBounds) {
				outOfBounds = false;
				for (int i = 0; i < n; i++)
					curr[i] = pt1[i];
				return;
			}
			for (int i = 0; i < n; i++) {
				curr[i] += increments[i];
				double c = curr[i];
				double p1 = pt1[i];
				double p2 = pt2[i];
				if (c > p1) {
					if (c <= p2) return;
				}
				else { // c < p1
					if (c >= p2) return;
				}
				curr[i] = p1;
			}
			throw new IllegalArgumentException("cannot fwd() beyond end");
		}

		@Override
		public void reset() {
			outOfBounds = true;
		}

		@Override
		public Sampler<double[]> copy() {
			return iterator();
		}

		@Override
		public Cursor<double[]> copyCursor() {
			return iterator();
		}

	}

	public static void main(String[] args) {
		/*
		Sampling s = new GridSampling(new double[] { 0, 0 }, new double[] { 1, 1 });

		Sampling s =
			new GridSampling(new double[] { 0, 0 }, new double[] { 1, 1 },
				new double[] { 0.3, 0.2 });
		*/
		Sampling s =
			new GridSampling(new double[] { 1, 0 }, new double[] { 0, 1 },
				new double[] { 0.2, 0.3 });
		RealCursor<double[]> cursor = s.cursor();
		while (cursor.hasNext()) {
			System.out.println(Arrays.toString(cursor.next()));
		}
	}
}
