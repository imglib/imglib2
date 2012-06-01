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

package net.imglib2.roi;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class LineRegionOfInterest extends AbstractRegionOfInterest {

	// -- declarations --
	private static final double TOLERANCE = 0.5;
	private final double[] p1, p2, min, max;
	private final double[] coeffs;
	
	// -- constructors --
	
	public LineRegionOfInterest( double[] pt1, double[] pt2 )
	{
		super( pt1.length );
		assert pt1.length == pt2.length;
		this.p1 = pt1.clone();
		this.p2 = pt2.clone();
		this.min = new double[pt1.length];
		this.max = new double[pt1.length];
		this.coeffs = new double[pt1.length];
		invalidateCachedState();
		setupAuxVariables();
	}

	// -- LineRegionOfInterest methods --
	
	public void getPoint1(double[] pt) {
		System.arraycopy(p1, 0, pt, 0, p1.length);
	}
	
	public void getPoint2(double[] pt) {
		System.arraycopy(p2, 0, pt, 0, p2.length);
	}
	
	public void setPoint1(double[] pt) {
		System.arraycopy(pt, 0, p1, 0, p1.length);
		invalidateCachedState();
		setupAuxVariables();
	}
	
	public void setPoint2(double[] pt) {
		System.arraycopy(pt, 0, p2, 0, p2.length);
		invalidateCachedState();
		setupAuxVariables();
	}

	// -- RegionOfInterest methods --
	
	@Override
	public void move(double displacement, int d) {
		p1[d] += displacement;
		p2[d] += displacement;
		invalidateCachedState();
		setupAuxVariables();
	}

	// Could just calc the N-dim line equation, calc the value and compare it to
	// zero. This would be easy and efficient. But it causes some trickiness in
	// determining when a point near an end point is on the line (i.e. when the
	// line is rotated). End point checking could see if pt is within a distance
	// of an end point. This might be the best way to go.
	//
	// Other approaches
	// - calc a rotation/translation matrix from original line. transform given
	//   point and compare it to transformed end points. simple tests. hard to
	//   setup n-dim rotation/translation matrix
	// - create two circle ROIS and a polygon ROI that encompass the valid space.
	//   simple tests at runtime but a lot of object overhead and maybe slow. Also
	//   might only be 2 dimensional.
	// - calc the perpendicular distance of the point from the line. need to
	//   derive an n-dim equation though.
	
	@Override
	protected boolean isMember(double[] position) {
		double sum = 0;
		for (int i = 0; i < coeffs.length; i++) {
			sum += position[i] * coeffs[i];
		}
		if (sum >= TOLERANCE) return false;
		// else its near the line
		realMin(min);
		realMax(max);
		if (!outOfBounds(min,max,position)) return true;
		// somewhere outside narrow bounds of p1 and p2
		// close enough to one endpoint?
		if (dist(p1, position) < TOLERANCE) return true;
		// close enough to other endpoint?
		if (dist(p2, position) < TOLERANCE) return true;
		// else its too far away
		return false;
	}

	// -- AbstractRegionOfInterest methods --
	
	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		for (int i = 0; i < p1.length; i++) {
			minima[i] = Math.min(p1[i], p2[i]);
			maxima[i] = Math.max(p1[i], p2[i]);
		}
	}

	// -- private helpers --
	
	private void setupAuxVariables() {
		// TODO - calc coeffs from two endpoints
		throw new UnsupportedOperationException("must implement");
	}

	private double dist(double[] pt1, double[] pt2) {
		double sum = 0;
		for (int i = 0; i < p1.length; i++) {
			double term = pt2[i] - pt1[i];
			sum += term * term;
		}
		return Math.sqrt(sum);
	}
	
	private boolean outOfBounds(double[] mn, double[] mx, double[] pt) {
		
		for (int i = 0; i < p1.length; i++) {
			if (pt[i] < mn[i]) return true;
			if (pt[i] > mx[i]) return true;
		}
		return false;
	}
	
}
