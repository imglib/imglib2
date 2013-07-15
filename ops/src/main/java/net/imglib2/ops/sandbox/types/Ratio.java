package net.imglib2.ops.sandbox.types;

public interface Ratio<T> {

	Ratio<T> create(Integral<T> numer, Integral<T> denom);

	void numerator(T result);

	void denominator(T result);

	void approxRational(RealFrac<T> a, RealFrac<T> b, Rational result);

	// added by bdz

	void setValue(T numerator, T denominator);

	void getValue(T numerator, T denominator);
}
