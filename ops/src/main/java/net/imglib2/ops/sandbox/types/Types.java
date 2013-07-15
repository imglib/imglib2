package net.imglib2.ops.sandbox.types;

import java.math.BigInteger;

// A port of the Haskell numeric types to Java. Just signatures below. 
// Mostly from:
// http://www.haskell.org/ghc/docs/latest/html/libraries/base/Prelude.html

/*
 
A criticism of the Haskell Type system I found online:

The Prelude for Haskell 98 offers a well-considered set of numeric classes
which covers the standard numeric types
('Integer', 'Int', 'Rational', 'Float', 'Double', 'Complex') quite well.
But they offer limited extensibility and have a few other flaws.
In this proposal we will revisit these classes, addressing the following concerns:
.
[1] The current Prelude defines no semantics for the fundamental operations.
    For instance, presumably addition should be associative
    (or come as close as feasible),
    but this is not mentioned anywhere.
.
[2] There are some superfluous superclasses.
    For instance, 'Eq' and 'Show' are superclasses of 'Num'.
    Consider the data type
    @   data IntegerFunction a = IF (a -> Integer) @
    One can reasonably define all the methods of 'Algebra.Ring.C' for
    @IntegerFunction a@ (satisfying good semantics),
    but it is impossible to define non-bottom instances of 'Eq' and 'Show'.
    In general, superclass relationship should indicate
    some semantic connection between the two classes.
.
[3] In a few cases, there is a mix of semantic operations and
    representation-specific operations.
    'toInteger', 'toRational',
    and the various operations in 'RealFloating' ('decodeFloat', ...)
    are the main examples.
*/

public class Types {

	private interface Factory<T> {

		T create();

		T copy();
	}

	// primitive boolean type
	// TODO - make it a class

	private interface Bool extends Factory<Bool> {

		boolean getValue();

		void getValue(Bool result);

		void setValue(boolean b);

		void setValue(Bool b);
	}

	// primitive integer type
	// TODO - make it a class

	private interface Int extends Bounded<Int>, Integral<Int>, Factory<Int> {

		int getValue();

		void getValue(Int result);

		void setValue(int i);

		void setValue(Int i);
	}

	// unbounded integer type
	// TODO - make it a class

	private interface Integer extends Integral<Integer>, Factory<Integer> {

		BigInteger getValue();

		void getValue(Integer result);

		void setValue(BigInteger i);

		void setValue(Integer i);
	}

	private interface Equable<T> {

		boolean isEqual(T b, Bool result);

		boolean isNotEqual(T b, Bool result);
	}

	private interface Ordered<T> extends Equable<T> {

		int compare(T b);

		boolean isLess(T b, Bool result);

		boolean isLessEqual(T b, Bool result);

		boolean isGreater(T b, Bool result);

		boolean isGreaterEqual(T b, Bool result);

		void max(T b, T result);

		void min(T b, T result);
	}

	private interface Enumerable<T> {

		void succ(T result);

		void pred(T result);
	}

	private interface Bounded<T> {

		void minBound(T result);

		void maxBound(T result);
	}

	private interface Number<T> {

		void add(T b, T result);

		void subtract(T b, T result);

		void multiply(T b, T result);

		<Z> void pow(Integral<Z> b, T result);

		void negate(T result); // optional

		void abs(T result);

		void signum(T result);

		void fromInteger(Integer i, T result);

	}

	private interface Integral<T> extends Real<T>, Enumerable<T> {

		void quotient(T b, T result);

		void remainder(T b, T result);

		void quotRem(T b, T quotResult, T remResult);

		void div(T b, T result);

		void mod(T b, T result);

		void divMod(T b, T divResult, T modResult);

		void toInteger(Integer result);

		boolean even(Bool result);

		boolean odd(Bool result);

		void gcd(T b, T result);

		void lcm(T b, T result);

		<Z> void fromIntegral(Number<Z> result);
	}

	private interface Real<T> extends Number<T>, Ordered<T> {

		void toRational(Rational result);

		<Z> void realToFrac(Fractional<Z> result);
	}

	private interface Ratio<T> {

		Ratio<T> create(Integral<T> numer, Integral<T> denom);

		void numerator(T result);

		void denominator(T result);

		void approxRational(RealFrac<T> a, RealFrac<T> b, Rational result);
	}

	private interface Rational extends Ratio<Integer> {
		// nothing to declare
	}

	private interface Fractional<T> extends Number<T> {

		void divide(T b, T result);

		void recip(T result);

		void fromRational(Rational a, T result);

		<Z> void pow(Integral<Z> b, T result);
	}

	private interface Floating<T> extends Fractional<T> {

		void PI(T result);

		void E(T result);

		void exp(T result);

		void sqrt(T result);

		void log(T result);

		void pow(T b, T result);

		void sin(T result);

		void cos(T result);

		void tan(T result);

		void asin(T result);

		void acos(T result);

		void atan(T result);

		void sinh(T result);

		void cosh(T result);

		void tanh(T result);

		void asinh(T result);

		void acosh(T result);

		void atanh(T result);
	}

	private interface RealFrac<T> extends Real<T>, Fractional<T> {

		<Z> void properFraction(Integral<Z> intResult, T fracResult);

		<Z> void truncate(Integral<Z> result);

		<Z> void round(Integral<Z> result);

		<Z> void ceiling(Integral<Z> result);

		<Z> void floor(Integral<Z> result);
	}

	private interface RealFloat<T> extends RealFrac<T>, Floating<T> {

		void floatRadix(Integer result);

		void floatDigits(Int result);

		void floatRange(Int minResult, Int maxResult);

		void decodeFloat(Integer significandResult, Int exponentResult);

		void encodeFloat(Integer significand, Int exponent, T result);

		void exponent(Int result);

		void significand(T result);

		void scaleFloat(Int power, T result);

		boolean isNaN(Bool result);

		boolean isInfinite(Bool result);

		boolean isDenormalized(Bool result);

		boolean isNegativeZero(Bool result);

		boolean isIEEE(Bool result);

		void atan2(T x, T y, T result);
	}

	// TODO - some subtyping not enforced here. See definition at:
	// http://www.haskell.org/ghc/docs/latest/html/libraries/base/Data-Complex.html

	private interface Complex<T> extends Equable<Complex<T>>,
		Floating<Complex<T>>, Factory<Complex<T>>
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
}
