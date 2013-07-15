package net.imglib2.ops.sandbox.types;

import net.imglib2.ops.parse.token.Int;

public interface RealFloat<T> extends RealFrac<T>,
	Floating<T>
{

	void floatRadix(Integer result);

	void floatDigits(Int result);

	void floatRange(Int minResult, Int maxResult);

	void decodeFloat(Integer significandResult, Int exponentResult);

	void encodeFloat(Integer significand, Int exponent, T result);

	void exponent(Int result);

	void significand(T result);

	void scaleFloat(Int power, T result);

	boolean isNaN();

	boolean isInfinite();

	boolean isDenormalized();

	boolean isNegativeZero();

	boolean isIEEE();

	// TODO - eliminate x? no. this should be a static method of any RealFloat?
	void atan2(T x, T y, T result);
}
