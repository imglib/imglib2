package net.imglib2.ops.sandbox.types;

// TODO - some subtyping not enforced here. See definition at:
// http://www.haskell.org/ghc/docs/latest/html/libraries/base/Data-Complex.html

public interface Complex<T> extends Equable<Complex<T>>,
	Floating<Complex<T>>
// , Factory<Complex<T>>
{

	void realPart(T result);

	void imagPart(T result);

	void mkPolar(RealFloat<T> a, RealFloat<T> b, Complex<T> result);

	void cis(RealFloat<T> a, Complex<T> result);

	void polar(T magnitudeResult, T phaseResult);

	void magnitude(T result);

	void phase(T result);

	void conjugate(Complex<T> result);
}
