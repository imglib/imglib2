package net.imglib2.ops;

public final class Real implements Comparable<Real> {
	private double real;
	
	public Real() { real = 0; }
	public Real(double r) { real = r; }
	public void setReal(double r) { real = r; }
	public double getReal() { return real; }

	@Override
	public int compareTo(Real other) {
		double myVal = real;
		double otherVal = other.getReal();
		if (myVal < otherVal)
			return -1;
		if (myVal == otherVal)
			return 0;
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Real) {
			Real other = (Real) obj;
			return real == other.getReal();
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		int result = 17;
		long tmp = Double.doubleToLongBits(real);
		result = result * 31 + (int) (tmp ^ (tmp >>> 32));
		return result;
	}
}

