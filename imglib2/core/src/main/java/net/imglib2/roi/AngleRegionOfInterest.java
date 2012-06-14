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
public class AngleRegionOfInterest extends AbstractRegionOfInterest {

	// -- declarations --
	
	private double[] ctr, end1, end2;
	private final LineRegionOfInterest line1, line2;
	
	// -- constructors --
	
	public AngleRegionOfInterest() {
		super( 2 );
		this.ctr = new double[2];
		this.end1 = new double[2];
		this.end2 = new double[2];
		this.line1 = new LineRegionOfInterest(ctr, end1);
		this.line2 = new LineRegionOfInterest(ctr, end2);
		invalidateCachedState();
	}
	
	public AngleRegionOfInterest( double[] ctr, double[] e1, double[] e2 )
	{
		super( ctr.length );
		assert ctr.length == e1.length;
		assert ctr.length == e2.length;
		this.ctr = ctr;
		this.end1 = e1;
		this.end2 = e2;
		this.line1 = new LineRegionOfInterest(ctr, end1);
		this.line2 = new LineRegionOfInterest(ctr, end2);
		invalidateCachedState();
	}

	// -- AngleRegionOfInterest methods --
	

	public void getPoint1(double[] pt) {
		System.arraycopy(end1, 0, pt, 0, end1.length);
	}
	
	public void getPoint2(double[] pt) {
		System.arraycopy(end2, 0, pt, 0, end2.length);
	}
	
	public void getCenter(double[] pt) {
		System.arraycopy(ctr, 0, pt, 0, ctr.length);
	}
	
	public void setPoint1(double[] pt) {
		System.arraycopy(pt, 0, end1, 0, end1.length);
		invalidateCachedState();
	}
	
	public void setPoint2(double[] pt) {
		System.arraycopy(pt, 0, end2, 0, end2.length);
		invalidateCachedState();
	}

	public void setCenter(double[] pt) {
		System.arraycopy(pt, 0, ctr, 0, ctr.length);
		invalidateCachedState();
	}

	// -- RegionOfInterest methods --
	
	@Override
	public void move(double displacement, int d) {
		ctr[d] += displacement;
		end1[d] += displacement;
		end2[d] += displacement;
		line1.move(displacement, d);
		line2.move(displacement, d);
		invalidateCachedState();
	}

	@Override
	public boolean contains(double[] position) {
		return line1.contains(position) || line2.contains(position);
	}

	// -- AbstractRegionOfInterest methods --
	
	@Override
	protected void getRealExtrema(double[] minima, double[] maxima) {
		for (int i = 0; i < ctr.length; i++) {
			minima[i] = Math.min( Math.min(end1[i], end2[i]) , ctr[i]);
			maxima[i] = Math.max( Math.max(end1[i], end2[i]) , ctr[i]);
		}
	}
}
