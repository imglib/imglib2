package net.imglib2.ops.sandbox.types;

// I have taken some of the numeric functions of the prelude and added them to
// the interfaces directly. This may be a problem. Below is another approach to
// solving the problem of where these should go. They however may not work here
// either.

public class Functions {

	/*

	public static <T> void subtract(Number<T> a, Number<T> b, Number<T> result) {
		a.subtract(b, result);
	}

	public static <T> boolean isEven(Integral<T> i) {
		Integral<T> zero = null;
		Integral<T> two = null;
		Integral<T> result = null;
		i.div(two, result);
		return result.compare(zero) > 0;
	}
	
	public static void pow() {

	}

	public static void pow2() {

	}

	 */

	/*

	even :: Integral a => a -> Bool

			odd :: Integral a => a -> Bool

			gcd :: Integral a => a -> a -> a

			gcd x y is the non-negative factor of both x and y of which every common factor of x and y is also a factor; for example gcd 4 2 = 2, gcd (-4) 6 = 2, gcd 0 4 = 4. gcd 0 0 = 0. (That is, the common divisor that is "greatest" in the divisibility preordering.)

			Note: Since for signed fixed-width integer types, abs minBound < 0, the result may be negative if one of the arguments is minBound (and necessarily is if the other is 0 or minBound) for such types.

			lcm :: Integral a => a -> a -> a

			lcm x y is the smallest positive integer that both x and y divide.

			(^) :: (Num a, Integral b) => a -> b -> a

			raise a number to a non-negative integral power

			(^^) :: (Fractional a, Integral b) => a -> b -> a

			raise a number to an integral power

			fromIntegral :: (Integral a, Num b) => a -> b

			general coercion from integral types

			realToFrac :: (Real a, Fractional b) => a -> b

			general coercion to fractional types
	 */
}