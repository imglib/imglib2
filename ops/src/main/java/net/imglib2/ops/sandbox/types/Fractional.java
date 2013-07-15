package net.imglib2.ops.sandbox.types;

public interface Fractional<T> extends Number<T> {

	void divide(T b, T result);

	void recip(T result);

	void fromRational(Rational a, T result);

	/**
	 * From Functions. Raise a Fractional to an integer power (negative power
	 * okay)
	 * 
	 * @param b
	 * @param result
	 */
	<Z> void powI(Integral<Z> b, T result);

}

