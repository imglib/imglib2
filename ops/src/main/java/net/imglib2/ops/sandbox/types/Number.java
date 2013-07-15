package net.imglib2.ops.sandbox.types;

public interface Number<T>
// added by BDZ
	extends Access<T>, Factory<T>
{

	void add(T b, T result);

	void subtract(T b, T result);

	void multiply(T b, T result);

	void negate(T result); // optional

	void abs(T result);

	void signum(T result);

	void fromInteger(Integer i, T result);

	/**
	 * From Functions. Raise a Number to a nonnegative integer power
	 * 
	 * @param b
	 * @param result
	 */
	<Z> void powNNI(Integral<Z> b, T result);

	// -- added by BDZ --

	// TODO - if one and zero are not in legal bounds of number we have a problem

//Later: 	void one(T result);

//Later: 	void zero(T result);
}
