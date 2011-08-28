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

package net.imglib2.ops;

/**
 * 
 * @author Barry DeZonia
 *
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
			calcRTheta();
		this.r = r;
		calcXY();
		polarInvalid = false;
	}

	public void setPolarTheta(double theta) {
		if (polarInvalid)
			calcRTheta();
		this.theta = theta;
		calcXY();
		polarInvalid = false;
	}

	public double getX() { return x; }
	public double getY() { return y; }
	public double getModulus() { if (polarInvalid) calcRTheta(); return r; }
	public double getArgument() { if (polarInvalid) calcRTheta(); return theta; }

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
	
	// TODO : enforce some convention for theta
	//   either -pi <= theta <= pi or 0 < theta < 2*pi
	
	private void calcRTheta() {
		r = Math.sqrt(magnitudeSquared());
		if (x == 0) {
			if (y > 0)
				theta = Math.PI / 2;
			else if (y < 0)
				theta = 3 * Math.PI / 2;
			else // y == 0 : theta indeterminate
				theta = 0;  // sensible default (?)
		}
		else if (y == 0) {
			if (x > 0)
				theta = 0;
			else if (x < 0)
				theta = Math.PI;
			else // x == 0 : theta indeterminate
				theta = 0;  // sensible default (?)
		}
		else { // x && y both != 0
			double angle = Math.atan2(x,y);
			if (x > 0) {
				if (y > 0)
					theta = angle;
				else // y < 0
					theta = angle + 2*Math.PI;
			}
			else // x < 0
				theta = angle + Math.PI;
		}
		polarInvalid = false;
	}
}

