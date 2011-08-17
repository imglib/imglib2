package net.imglib2.ops;

public final class Complex implements Comparable<Complex>{
	private double real;
	private double imag;

	public Complex() {
		real = 0;
		imag = 0;
	}
	
	public Complex(double r, double i) {
		real = r;
		imag = i;
	}
	
	public void setReal(double r) {
		real = r;
	}

	public void setImag(double i) {
		imag = i;
	}

	public void setRealImag(double r, double i) {
		real = r;
		imag = i;
	}
	
	public double getReal() { return real; }
	public double getImag() { return imag; }

	@Override
	public int compareTo(Complex other) {
		if (real == other.getReal())
			if (imag == other.getImag())
				return 0;

		double myVal = magnitudeSquared();
		double otherVal = other.magnitudeSquared();
		
		if (myVal < otherVal)
			return -1;
		
		if (myVal > otherVal)
			return 1;
		
		// magnitudes are equal

		// TODO I'm discriminating here by spatial layout of r & i
		// rather than returning 0 all the time. This may not be
		// mathematically valid. Investigate.
		
		if (real < other.getReal())
			return -1;

		if (real > other.getReal())
			return 1;

		// real == other.getReal()
		
		if (imag < other.getImag())
			return -1;
		
		// imag must be greater than other.getImag()
		return 1;
	}

	// TODO - verify compareTo() is consistent with equals(). I think it is.
	//   See Java API docs for Comparable and also compareTo() therein.
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Complex) {
			Complex other = (Complex) obj;
			return ((real == other.getReal()) && (imag == other.getImag()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 17;
		long tmp = Double.doubleToLongBits(real);
		result = 31 * result + (int) (tmp ^ (tmp >>> 32));
		tmp = Double.doubleToLongBits(imag);
		result = 31 * result + (int) (tmp ^ (tmp >>> 32));
		return result;
	}

	private double magnitudeSquared() {
		return real*real + imag*imag;
	}
}

