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

import net.imglib2.ops.util.DataCopier;

/**
 * 
 * @author Barry DeZonia
 */
public final class Complex implements Comparable<Complex>, DataCopier<Complex> {
	private double x;
	private double y;
	private boolean polarInvalid;
	private double r;
	private double theta;

	public Complex() {
		x = 0;
		y = 0;
		polarInvalid = true;
	}
	
	public static double findPrincipleArgument(double angle) {
		double arg = angle;
		while (arg <= -Math.PI) arg += 2*Math.PI;
		while (arg > Math.PI) arg -= 2*Math.PI;
		return arg;
	}
	
	public static Complex createCartesian(double x, double y) {
		Complex c = new Complex();
		c.setCartesian(x,y);
		return c;
	}
	
	public static Complex createPolar(double r, double theta) {
		Complex c = new Complex();
		c.setPolar(r,theta);
		return c;
	}
	
	public void setCartesian(double x, double y) {
		this.x = x;
		this.y = y;
		polarInvalid = true;
	}
	
	public void setCartesianX(double x) {
		this.x = x;
		polarInvalid = true;
	}

	public void setCartesianY(double y) {
		this.y = y;
		polarInvalid = true;
	}

	public void setPolar(double r, double theta) {
		this.r = r;
		this.theta = theta;
		calcXY();
		polarInvalid = false;
	}

	public void setPolarR(double r) {
		if (polarInvalid)
			calcTheta();
		this.r = r;
		calcXY();
		polarInvalid = false;
	}

	public void setPolarTheta(double theta) {
		if (polarInvalid)
			calcR();
		this.theta = theta;
		calcXY();
		polarInvalid = false;
	}

	public double getX() { return x; }
	public double getY() { return y; }
	public double getModulus() { if (polarInvalid) calcRTheta(); return r; }
	public double getArgument() { if (polarInvalid) calcRTheta(); return theta; }

	public double getPrincipleArgument() {
		return findPrincipleArgument(getArgument());
	}
	
	@Override
	public int compareTo(Complex other) {
		if (x == other.getX())
			if (y == other.getY())
				return 0;

		double myVal = magnitudeSquared();
		double otherVal = other.magnitudeSquared();
		
		if (myVal < otherVal)
			return -1;
		
		if (myVal > otherVal)
			return 1;
		
		// magnitudes are equal

		// TODO I'm discriminating here by spatial layout of x & y
		// rather than returning 0 all the time. This may not be
		// mathematically valid but compareTo maybe less strict.
		// Investigate.
		
		if (x < other.getX())
			return -1;

		if (x > other.getX())
			return 1;

		// x == other.getX()
		
		if (y < other.getY())
			return -1;
		
		// y must be greater than other.getY()
		return 1;
	}

	// TODO - verify compareTo() is consistent with equals(). I think it is.
	//   See Java API docs for Comparable and also compareTo() therein.
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			Complex other = (Complex) obj;
			return ((x == other.getX()) && (y == other.getY()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		long tmp = Double.doubleToLongBits(x);
		result = 31 * result + (int) (tmp ^ (tmp >>> 32));
		tmp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (tmp ^ (tmp >>> 32));
		return result;
	}

	@Override
	public void setValue(Complex fromValue) {
		setCartesian(fromValue.getX(), fromValue.getY());
	}

	// -- private helpers --
	
	private double magnitudeSquared() {
		return x*x + y*y;
	}
	
	private void calcXY() {
		x = r * Math.cos(theta);
		y = r * Math.sin(theta);
	}
	
	private void calcRTheta() {
		calcR();
		calcTheta();
		polarInvalid = false;
	}
	
	private void calcR() {
		r = Math.sqrt(magnitudeSquared());
	}

	private void calcTheta() {
		if (x == 0) {
			if (y > 0)
				theta = Math.PI / 2;  // looks fine
			else if (y < 0)
				theta = -Math.PI / 2;  // looks fine
			else // y == 0 : theta indeterminate
				theta = Double.NaN;
		}
		else if (y == 0) {
			if (x > 0)
				theta = 0;  // looks fine
			else // (x < 0)
				theta = Math.PI;  // looks fine
		}
		else // x && y both != 0
			theta = Math.atan2(y,x);
	}
}

