import java.lang.StringBuilder;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An experimental numeric type hierarchy.
 * 
 * @author Barry DeZonia
 */
public class Numerics2 {

/* motivation: grown out of frustrations with limitations of Imglib2
 * type system
 * 
 * Imglib2 types were incorporated into ImageJ2 with reliance on RealType.
 * However this reliance causes a few problems. For instance each
 * RealType assumes all values can be represented as doubles (and thus
 * the LongType class loses precision). Also since code relies on
 * RealType and doubles for generality it might miss the efficiency for
 * the integer types. However it is not general enough and it might
 * make sense to have IJ2 rely on ComplexTypes instead. But that would
 * make all algorithms set real and set imag components when often it
 * may not make sense. Ideally you'd instead work in the native type of
 * the container.
 * 
 * Also in Imglib2 type system the real types are subclasses of complex
 * types. But this allows nonsense of sending a real type into an algo
 * that wants a complex type but also wants to set the imaginary values
 * of the complex type. The key need is the ability to treat reals as
 * inputs to complex values.
 * 
 * In the past I mocked up some stuff of the Haskell type system in
 * imglib2 ops sandbox on the types-experiment branch. That code should
 * be used to enhance this code. I copied the Types.java and Types2.java
 * classes from that project into the subdirectory of this file but have
 * hardly used it.
 * 
 * I'm hoping this type system can return an Img<?> from a file open
 * process and using that image of unknown type do all the mathematical
 * operations available to it.
 * 
 * One idea is that all algorithms are designed to use Objects as input
 * and they cast dynamically to their desired input types. Failures
 * report a runtime error. Such an approach is like C++ dynamic casts
 * or Ruby's unknown method approach. I have mocked up such an approach
 * near the bottom of this file.
 * 
 * More on the casting approach. The data structures of an image store
 * specifically (i.e. List<Float64>). However it returns a facade in a
 * very generic type (i.e. List<GroupMember>) to outside observers. This
 * is what gets passed around to plugins. The plugins cast to the most
 * restrictive class they handle (i.e. List<Float64>, or List<Real<?>>
 * etc.).
 * 
 * Some things to note:
 * 1) this hierarchy is algebraically correct (as far as I can tell
 *    after reading a book on abstract algebra by Pinter).
 * 2) it supports various number systems (integer, rational, real,
 *    complex, quaternion, matrix) and is extensible
 * 3) More sophisticated values (like complex variables) can be set from
 *    less sophisticated values (like reals, rationals, etc.)
 * 4) TODO document the algebraic hierarchy
 * 5) TODO represent types efficiently using indexes into the primitive
 *    based container classes of Imglib2 rather than this toy approach
 *    of Objects for every number
 * 6) TODO write tests
 */

// TODO
//  add rootOp() and powerOp()
//    note that for some types powers can only be >= 0 and some can
//    handle neg pows and some can handle fractional pows and some can
//    handle complex pows.
//    roots might not be like this. their power may always be >= 1. But
//    some groups do not support roots at all. locate in hierarchy
//    carefully. (note that any number has multiple complex roots)
//  2x2 mats are a certain kind of group/integral domain/??. Support
//    them especially because Quaternions would use them.
//  Migrate Haskell Type class code into this code so more operators are
//    supported. sin(), cos(), round(), trunc(), etc.

// (from an older version):
// How is my approach of a group having shared operators any better than
// a group based hierarchy with direct manipulation methods (i.e. a more
// traditional approach)? for one thing the non-group approach does not
// support a two output approach that the group op approach can. For
// instance the in place method assumes a.div(b) or a.add(b). Maybe
// a.add(b,c). But how to do a.divMod(b)? or a.sineAndCosine(angle)?
// The op method allows op.divMod(a, d, m) or opSAndC.(a, s, c).
// Still could try the other approach in new file and instead of
// Ring<T> you do RingMember<T> etc. Note my op approach has nice built
// in safeguards to support constant values.

// (and now porting from the old version to this one):
// Note this version used to have groups as separate types (as previous
// item discussed). But this approach here now supports easily operations
// of arbitrary complexity (i.e. many params of mixed types) with a
// friendly user api. However one cannot easily define new ops that
// extend the hierarchy.

// do norm, conjugate, transpose end up being methods in the algebraic
// hierarchy?

// algebra hierarchy should be enhanced such that a vector class can be
// implemented with addition of vectors and multiplication by scalars.
// The multiply is trickier in that it involves two different types.
// Note also that there are a number of things that support
// multiplication by scalars (vectors, matrix, complex num, quat, etc.).
// Use this fact to improve the hierarchy.

// fix quaternions so that like matrices we can use recursive types
// but code overridden in subclasses. Later note: this was a failure
// and I pulled it out of the Matrix interface too. Note that this is
// disappointing as we really do want to define methods that take
// generic matrices and generic quats as input and do stuff. Right now
// those interfaces are totally inept. Same with RationalMember,
// RealMember, and ComplexMember.

// Define polynomials that take a RingWithUnity type as coeffs and
// represents an equation. Then support quot/rem and other ops. It is
// thus maybe an IntegralDomainMember.

// quats can be represented as four numbers but require eight floats to
// do math with. Think of best way to implement: tradeoff of mem use
// vs. clarity of code.

// can any complex number be represented by a quaternion? like how every
// real number can be represented by a complex num w/ imag part == 0. If
// so then we need set(ComplexMember<?>) for the quaternion interface.

// some quaternion methods allocate objects when maybe they could be
// inlined. For instance invert() and one other. Investigate.

// note use of BigDecimals right now does not currently protect from
// generating repeating decimals.

// add functions of a real value: commutative ring w/ unity
//   not a field: x^2 is not invertible
//   not an integral domain: piecewise functions may be divisors of zero

	public interface GroupMember<T extends GroupMember<T>> {
		boolean isEqual(T other);
		boolean isNotEqual(T other);
		T variable(); // a default group member constructor
		T copy();
		void set(T other);
	}

	public interface AdditiveGroupMember<T extends AdditiveGroupMember<T>>
		extends GroupMember<T>
	{
		void zero(); // setZero()
		void negate(T a);  // additive inverse
		void add(T a, T b);
		void subtract(T a, T b);
	}

	public interface RingMember<T extends RingMember<T>>
		extends AdditiveGroupMember<T>
	{
		void multiply(T a, T b);
		void power(T a, int power);  // implementations decide if arg can be negative
	}
	
	public interface InvertibleMember<T extends InvertibleMember<T>> {
		void invert(T a); // multiplicative inverse
		void divide(T a, T b);
	}
	
	public interface OrderedMember<T extends OrderedMember<T>> {
		boolean isLess(T other); 
		boolean isLessEqual(T other); 
		boolean isGreater(T other); 
		boolean isGreaterEqual(T other);
		int compare(T other);
		int signum();
	}

	public interface Bounded<T extends Bounded<T>> {
		void max();  // set self to max value of type
		void min();  // set self to min value of type
	}

	// TODO
	// 1) get mathematical name here
	// 2) incorporate

	public interface Trigonometric<T extends Trigonometric<T>> {
		void sin(T other);
		void cos(T other);
		void tan(T other);
		void sinh(T other);
		void cosh(T other);
		void tanh(T other);
	}
	
	public interface Exponential<T extends Exponential<T>> {
		void log(T other);
		void exp(T other);
		void root(T other, int power);
		//void sqrt(T other);
		//void cbrt(T other);
	}
	
	// TODO
	// 1) get mathematical name here
	// 2) incorporate

	public interface InverseTrigonometric<T extends InverseTrigonometric<T,U>,U> {
		U arcsin(T other);
		U arccos(T other);
		U arctan(T other);
		U arcsinh(T other);
		U arccosh(T other);
		U arctanh(T other);
		U atan2(T a, T b);
	}
	
	// TODO
	// 1) get mathematical name here
	// 2) incorporate

	public interface RealBoundStuff {
		boolean isNan();
		boolean isPositiveInfinity();
		boolean isNegativeInfinity();
	}

	// TODO
	// 1) get mathematical name here
	// 2) incorporate

	public interface RealRoundingStuff {
		void trunc();
		void round();
		void ceil();
		void floor();
	}
	
	public interface CommutativeRingMember<T extends CommutativeRingMember<T>>
		extends RingMember<T>
	{
		// multiplication commutes
	}
	
	public interface RingWithUnityMember<T extends RingWithUnityMember<T>>
		extends RingMember<T>
	{
		void unity();  // setOne()
	}
	
	public interface CommutativeRingWithUnityMember<T extends CommutativeRingWithUnityMember<T>>
		extends CommutativeRingMember<T>, RingWithUnityMember<T>
	{
	}
	
	public interface IntegralDomainMember<T extends IntegralDomainMember<T>>
		extends CommutativeRingWithUnityMember<T>
	{
		// has cancellation property
	}
	
	public interface EuclideanRingMember<T extends EuclideanRingMember<T>>
		extends RingMember<T> // or is it a ring with unity member?
	{
		void div(T a, T b);
		void mod(T a, T b);
		void divMod(T a, T b, T r);
		void gcd(T a, T b);
		void lcm(T a, T b);
		T norm();
		boolean isEven();
		boolean isOdd();
	}
	
	public interface EuclideanDomainMember<T extends EuclideanDomainMember<T>>
		extends IntegralDomainMember<T>, EuclideanRingMember<T>
	{
	}

	public interface OrderedIntegralDomainMember<T extends OrderedIntegralDomainMember<T>>
		extends IntegralDomainMember<T>, OrderedMember<T>
	{
		void abs(T other);
	}

	// note: in a skew field multiplication does not commute
	public interface SkewFieldMember<T extends SkewFieldMember<T>>
		extends RingWithUnityMember<T>, InvertibleMember<T>
	{
	}
	
	// note: while in a field multiplication does commute
	public interface FieldMember<T extends FieldMember<T>>
		extends IntegralDomainMember<T>, InvertibleMember<T>
	{
	}

	public interface OrderedFieldMember<T extends OrderedFieldMember<T>>
		extends FieldMember<T>, OrderedMember<T>
	{
	}

	// not sure if the U def is right yet. An IntegralDomain? A EuclideanDomain? Or less likely a Field?
	// Note that integers are not really part of a euclidean ring as q/r uniqueness is not guaranteed
	// for neg vs. pos numbers. I can still relax uniqueness of result in my implementations?
	public interface PolynomialRingMember<T extends PolynomialRingMember<T,U>, U extends IntegralDomainMember<U>>
		extends EuclideanRingMember<T>, RingWithUnityMember<T>
	{
		List<U> coefficients();
		List<Integer> powers();
	}
	
	// rings: general real matrices? complex matrices too?
	
	// TODO: should V extend FieldMember?
	public interface Matrix<U extends Matrix<U,V>,V extends RingWithUnityMember<V>> {
		int rows();
		int cols();
		V value(int r, int c);
		void setValue(int r, int c, V src);
		void transpose(); // somewhere else in hierarchy???? i.e. conjugate op perhaps?
		void scale(Matrix<U,V> other, V value);
	}
	
	// rings with unity: all square matrices, F(R) is commut ring unity

	// skew fields: quaternions
	//   how does norm and conjugate of quats fit into hierarchy?

	public interface Quaternion<U extends Quaternion<U,V>, V extends RingWithUnityMember<V>> {
		void setA(V val);
		void setB(V val);
		void setC(V val);
		void setD(V val);
		V a();
		V b();
		V c();
		V d();
		V norm();
		// TODO
		//void conjugate(Quaternion<T> other);
		void set(Quaternion<?,?> val);
		void set(RealMember<?> val);
		void set(RationalMember<?> val);
		void set(IntegerMember<?> val);
		
		UnboundedInt aIntPart();
		UnboundedInt bIntPart();
		UnboundedInt cIntPart();
		UnboundedInt dIntPart();
		int decPower();
	}

	// integral domains: Z

	public interface IntegerMember<T extends IntegerMember<T>>
		extends OrderedIntegralDomainMember<T>, EuclideanDomainMember<T>
	{
		void pred(T a);
		void succ(T a);

		UnboundedInt intPart();
		void set(IntegerMember<?> val);
	}
	
	// fields: q, r, c

	public interface RationalMember<T> {
		void set(RationalMember<?> val);
		void set(IntegerMember<?> val);
		
		UnboundedInt numerator();
		UnboundedInt denominator();
	}
	
	public interface RealMember<T> {
		void set(RealMember<?> val);
		void set(RationalMember<?> val);
		void set(IntegerMember<?> val);
		
		UnboundedInt intPart();
		int decPower();
	}
	
	public interface ComplexMember<T> {
		void set(ComplexMember<?> val);
		void set(RealMember<?> val);
		void set(RationalMember<?> val);
		void set(IntegerMember<?> val);

		UnboundedInt realIntPart();
		UnboundedInt imagIntPart();
		int decPower();
	}

	// Want to define RealMatrix and ComplexMatrix? Useful?

	public static class Signed32BitInt
		implements IntegerMember<Signed32BitInt>, Bounded<Signed32BitInt>
	{
		private int v;

		private static Signed32BitInt ZERO = new Signed32BitInt(0);
		private static Signed32BitInt ONE = new Signed32BitInt(1);
		
		public Signed32BitInt() {
			v = 0;
		}
		
		public Signed32BitInt(int v) {
			this();
			set(v);
		}
		
		public Signed32BitInt(Signed32BitInt other) {
			this(other.v);
		}
		
		public void set(int v) {
			this.v = v;
		}
		
		public void set(Signed32BitInt other) {
			set(other.v);
		}
		
		public Signed32BitInt variable() {
			return new Signed32BitInt();
		}
		
		public boolean isEqual(Signed32BitInt other) {
			return v == other.v;
		}
		
		public boolean isNotEqual(Signed32BitInt other) {
			return v != other.v;
		}
		
		public boolean isLess(Signed32BitInt other) {
			return v < other.v;
		}
		
		public boolean isLessEqual(Signed32BitInt other) {
			return v <= other.v;
		}
		
		public boolean isGreater(Signed32BitInt other) {
			return v > other.v;
		}
		
		public boolean isGreaterEqual(Signed32BitInt other) {
			return v >= other.v;
		}
		
		public int compare(Signed32BitInt other) {
			if (v < other.v) return -1;
			if (v > other.v) return  1;
			return 0;
		}
		
		public int signum() {
			return compare(ZERO);
		}
		
		public Signed32BitInt copy() {
			return new Signed32BitInt(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(Signed32BitInt other) {
			v = -other.v;
		}
		
		public void add(Signed32BitInt a, Signed32BitInt b) {
			v = a.v + b.v;
		}
		
		public void subtract(Signed32BitInt a, Signed32BitInt b) {
			v = a.v - b.v;
		}
		
		public void multiply(Signed32BitInt a, Signed32BitInt b) {
			v = a.v * b.v;
		}
		
		public void power(Signed32BitInt input, int power) {
			v = 1;
			for (int i = 0; i < power; i++) {
				v *= input.v;
			}
		}
		
		public void abs(Signed32BitInt other) {
			v = v < 0 ? -v : v;
		}
		
		public void max() {
			set(Integer.MAX_VALUE);
		}
		
		public void min() {
			set(Integer.MIN_VALUE);
		}
		
		public void div(Signed32BitInt a, Signed32BitInt b) {
			v = a.v / b.v;
		}
		
		public void mod(Signed32BitInt a, Signed32BitInt b) {
			v = a.v % b.v;
		}
		
		public void divMod(Signed32BitInt a, Signed32BitInt b, Signed32BitInt r) {
			v = a.v / b.v;
			r.v = a.v % b.v;
		}
		
		public void gcd(Signed32BitInt a, Signed32BitInt b) {
			v = gcdHelper(a, b);
		}
		
		public void lcm(Signed32BitInt a, Signed32BitInt b) {
			int n = Math.abs(a.v * b.v);
			int d = gcdHelper(a, b);
			v = n / d;
		}
		
		private int gcdHelper(Signed32BitInt a, Signed32BitInt b) {
			int av = a.v, bv = b.v;
			while (bv != 0 ) {
				int t = bv;
				bv = av % bv;
				av = t;
			}
			return av;
		}
		
		public void pred(Signed32BitInt a) {
			v = a.v - 1;
		}
		
		public void succ(Signed32BitInt a) {
			v = a.v + 1;
		}
		
		public UnboundedInt intPart() {
			return new UnboundedInt(BigInteger.valueOf(v));
		}
		
		public void set(IntegerMember<?> val) {
			v = val.intPart().v.intValue();
		}
		
		public String toString() {
			return Integer.toString(v);
		}
		
		public Signed32BitInt norm() {
			return new Signed32BitInt(v < 0 ? -v : v);
		}
		
		public boolean isEven() {
			return v % 2 == 0;
		}

		public boolean isOdd() {
			return v % 2 == 1;
		}
	}
	
	public static class Unsigned16BitInt
		implements IntegerMember<Unsigned16BitInt>, Bounded<Unsigned16BitInt>
	{
		private short v;

		private static Unsigned16BitInt ZERO = new Unsigned16BitInt(0);
		private static Unsigned16BitInt ONE = new Unsigned16BitInt(1);
		
		public Unsigned16BitInt() {
			v = 0;
		}
		
		public Unsigned16BitInt(int v) {
			this();
			set(v);
		}
		
		public Unsigned16BitInt(Unsigned16BitInt other) {
			this(other.v & 0xffff);
		}
	
		public void set(int v) {
			if (v < 0 || v > 0xffff)
				throw new IllegalArgumentException("value out of range");
			this.v = (short) (v & 0xffff);
		}
		
		public void set(Unsigned16BitInt other) {
			set(other.v & 0xffff);
		}
		
		public Unsigned16BitInt variable() {
			return new Unsigned16BitInt();
		}
		
		public boolean isEqual(Unsigned16BitInt other) {
			return compare(other) == 0;
		}
		
		public boolean isNotEqual(Unsigned16BitInt other) {
			return compare(other) != 0;
		}
		
		public boolean isLess(Unsigned16BitInt other) {
			return compare(other) < 0;
		}
		
		public boolean isLessEqual(Unsigned16BitInt other) {
			return compare(other) <= 0;
		}
		
		public boolean isGreater(Unsigned16BitInt other) {
			return compare(other) > 0;
		}
		
		public boolean isGreaterEqual(Unsigned16BitInt other) {
			return compare(other) >= 0;
		}
		
		public int compare(Unsigned16BitInt other) {
			int thisI = v & 0xffff;
			int otherI = other.v & 0xffff;
			if (thisI < otherI) return -1;
			if (thisI > otherI) return  1;
			return 0;
		}
		
		public int signum() {
			return compare(ZERO);
		}

		public Unsigned16BitInt copy() {
			return new Unsigned16BitInt(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(Unsigned16BitInt other) {
			v = other.v; // negate unsupported
		}
		
		public void add(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v + b.v);
		}
		
		public void subtract(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v - b.v);
		}
		
		public void multiply(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v * b.v);
		}
		
		public void power(Unsigned16BitInt input, int power) {
			// TODO: okay via two's complement or translate to integers?
			v = 1;
			for (int i = 0; i < power; i++) {
				v *= input.v;
			}
		}
		
		public void abs(Unsigned16BitInt other) {
			v = other.v;
		}
		
		public void max() {
			set(0xffff);
		}
		
		public void min() {
			set(0);
		}
		
		public void div(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v / b.v);
		}
		
		public void mod(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v % b.v);
		}
		
		public void divMod(Unsigned16BitInt a, Unsigned16BitInt b, Unsigned16BitInt r) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v / b.v);
			r.v = (short) (a.v % b.v);
		}
		
		public void gcd(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			int val = gcdHelper(a, b);
			if (val < 0) val =  0;
			if (val > 0xffff) val = 0xffff;
			v = (short) (val & 0xffff);
		}
		
		public void lcm(Unsigned16BitInt a, Unsigned16BitInt b) {
			// TODO: okay via two's complement or translate to integers?
			int n = Math.abs(a.v * b.v);
			int d = gcdHelper(a, b);
			int val = n / d;
			if (val < 0) val =  0;
			if (val > 0xffff) val = 0xffff;
			v = (short) (val & 0xffff);
		}
		
		private int gcdHelper(Unsigned16BitInt a, Unsigned16BitInt b) {
			int av = a.v & 0xffff, bv = b.v & 0xffff;
			while (bv != 0 ) {
				int t = bv;
				bv = av % bv;
				av = t;
			}
			return av;
		}
		
		public void pred(Unsigned16BitInt a) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v - 1);
		}
		
		public void succ(Unsigned16BitInt a) {
			// TODO: okay via two's complement or translate to integers?
			v = (short) (a.v + 1);
		}
		
		public UnboundedInt intPart() {
			return new UnboundedInt(BigInteger.valueOf(v));
		}
		
		public void set(IntegerMember<?> val) {
			set(val.intPart().v.intValue());
		}
		
		public String toString() {
			return Integer.toString(v & 0xffff);
		}
		
		public Unsigned16BitInt norm() {
			return new Unsigned16BitInt(v);
		}
		
		public boolean isEven() {
			return v % 2 == 0;
		}

		public boolean isOdd() {
			return v % 2 == 1;
		}
	}
	
	public static class UnboundedInt
		implements IntegerMember<UnboundedInt>
	{
		private BigInteger v;

		private static UnboundedInt ZERO = new UnboundedInt(BigInteger.ZERO);
		private static UnboundedInt ONE = new UnboundedInt(BigInteger.ONE);
		
		public UnboundedInt() {
			v = BigInteger.ZERO;
		}
		
		public UnboundedInt(BigInteger v) {
			this();
			set(v);
		}
		
		public UnboundedInt(UnboundedInt other) {
			this(other.v);
		}
		
		public void set(BigInteger v) {
			this.v = v;
		}
		
		public void set(UnboundedInt other) {
			set(other.v);
		}
		
		public UnboundedInt variable() {
			return new UnboundedInt();
		}
		
		public boolean isEqual(UnboundedInt other) {
			return compare(other) == 0;
		}
		
		public boolean isNotEqual(UnboundedInt other) {
			return compare(other) != 0;
		}
		
		public boolean isLess(UnboundedInt other) {
			return compare(other) < 0;
		}
		
		public boolean isLessEqual(UnboundedInt other) {
			return compare(other) <= 0;
		}
		
		public boolean isGreater(UnboundedInt other) {
			return compare(other) > 0;
		}
		
		public boolean isGreaterEqual(UnboundedInt other) {
			return compare(other) >= 0;
		}
		
		public int compare(UnboundedInt other) {
			return v.compareTo(other.v);
		}		
		
		public int signum() {
			return compare(ZERO);
		}
		
		public UnboundedInt copy() {
			return new UnboundedInt(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(UnboundedInt other) {
			subtract(ZERO,other);
		}
		
		public void add(UnboundedInt a, UnboundedInt b) {
			v = a.v.add(b.v);
		}
		
		public void subtract(UnboundedInt a, UnboundedInt b) {
			v = a.v.subtract(b.v);
		}
		
		public void multiply(UnboundedInt a, UnboundedInt b) {
			v = a.v.multiply(b.v);
		}
		
		public void power(UnboundedInt input, int power) {
			v = BigInteger.ONE;
			for (int i = 0; i < power; i++) {
				v = v.multiply(input.v);
			}
		}
		
		public void abs(UnboundedInt other) {
			if (other.compare(ZERO) < 0) negate(other);
			else v = other.v;
		}
		
		public void div(UnboundedInt a, UnboundedInt b) {
			v = a.v.divide(b.v);
		}
		
		public void mod(UnboundedInt a, UnboundedInt b) {
			v = a.v.mod(b.v);
		}
		
		public void divMod(UnboundedInt a, UnboundedInt b, UnboundedInt r) {
			v = a.v.divide(b.v);
			r.v = a.v.mod(b.v);
		}
		
		public void gcd(UnboundedInt a, UnboundedInt b) {
			v = gcdHelper(a, b);
		}
		
		public void lcm(UnboundedInt a, UnboundedInt b) {
			BigInteger n = a.v.multiply(b.v);
			n = n.abs();
			BigInteger d = gcdHelper(a, b);
			v = n.divide(d);
		}

		private BigInteger gcdHelper(UnboundedInt a, UnboundedInt b) {
			BigInteger av = a.v, bv = b.v;
			while (bv.compareTo(BigInteger.ZERO) != 0 ) {
				BigInteger t = bv;
				bv = av.mod(bv);
				av = t;
			}
			return av;
		}
		
		public void pred(UnboundedInt a) {
			v = a.v.subtract(BigInteger.ONE);
		}
		
		public void succ(UnboundedInt a) {
			v = a.v.add(BigInteger.ONE);
		}
		
		public UnboundedInt intPart() {
			return copy();
		}
		
		public void set(IntegerMember<?> val) {
			v = val.intPart().v;
		}
		
		public String toString() {
			return v.toString();
		}
		
		public UnboundedInt norm() {
			UnboundedInt i = new UnboundedInt();
			i.abs(this);
			return i;
		}
		
		public boolean isEven() {
			BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
			return v.mod(two).equals(BigInteger.ZERO);
		}

		public boolean isOdd() {
			BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
			return v.mod(two).equals(BigInteger.ONE);
		}
	} 
	
	public interface UnboundedRational
		extends OrderedFieldMember<UnboundedRational>, RationalMember<UnboundedRational>
	{
		UnboundedInt numerator(UnboundedInt dest);
		UnboundedInt denominator(UnboundedInt dest);
	}

	public static class Float32
		implements RealMember<Float32>, OrderedFieldMember<Float32> , Bounded<Float32>
	{
		private float v;

		private static Float32 ZERO = new Float32(0);
		private static Float32 ONE = new Float32(1);
		
		public Float32() {
			v = 0;
		}
		
		public Float32(float v) {
			this();
			set(v);
		}
		
		public Float32(Float32 other) {
			this();
			set(other);
		}

		public void set(float v) {
			this.v = v;
		}
		
		public void set(Float32 other) {
			set(other.v);
		}
		
		public Float32 variable() {
			return new Float32();
		}
		
		public boolean isEqual(Float32 other) {
			return v == other.v;
		}
		
		public boolean isNotEqual(Float32 other) {
			return v != other.v;
		}
		
		public boolean isLess(Float32 other) {
			return v < other.v;
		}
		
		public boolean isLessEqual(Float32 other) {
			return v <= other.v;
		}
		
		public boolean isGreater(Float32 other) {
			return v > other.v;
		}
		
		public boolean isGreaterEqual(Float32 other) {
			return v >= other.v;
		}
		
		public int compare(Float32 other) {
			if (v < other.v) return -1;
			if (v > other.v) return  1;
			return 0;
		}
		
		public int signum() {
			return compare(ZERO);
		}
		
		public Float32 copy() {
			return new Float32(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(Float32 other) {
			v = -other.v;
		}
		
		public void add(Float32 a, Float32 b) {
			v = a.v + b.v;
		}
		
		public void subtract(Float32 a, Float32 b) {
			v = a.v - b.v;
		}
		
		public void multiply(Float32 a, Float32 b) {
			v = a.v * b.v;
		}
		
		public void divide(Float32 a, Float32 b) {
			v = a.v / b.v;
		}
		
		public void invert(Float32 other) {
			divide(ONE, other);
		}
		
		public void power(Float32 input, int power) {
			v = (float) Math.pow(input.v, power);
		}
		
		public void abs(Float32 other) {
			v = v < 0 ? -v : v;
		}
		
		public void max() {
			set(Float.MAX_VALUE);
		}
		
		public void min() {
			set(-Float.MAX_VALUE);
		}
		
		public UnboundedInt intPart() {
			BigDecimal tmp = BigDecimal.valueOf(v);
			tmp = tmp.movePointRight(decPower());
			return new UnboundedInt(tmp.toBigInteger());
		}
		
		public int decPower() {
			return 10; // 7 might be enough to represent all floats
		}

		public void set(RationalMember val) {
			BigDecimal n = new BigDecimal(val.numerator().v);
			BigDecimal d = new BigDecimal(val.denominator().v);
			BigDecimal tmp = n.divide(d);
			v = tmp.floatValue();
		}
		
		public void set(IntegerMember<?> val) {
			BigDecimal n = new BigDecimal(val.intPart().v);
			v = n.floatValue();
		}
		
		public void set(RealMember<?> val) {
			BigDecimal tmp = new BigDecimal(val.intPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			v = tmp.floatValue();
		}
		
		public String toString() {
			return Float.toString(v);
		}
	}
	
	public static class Float64
		implements RealMember<Float64>, OrderedFieldMember<Float64>, Bounded<Float64>
	{
		private double v;
		
		private static Float64 ZERO = new Float64(0);
		private static Float64 ONE = new Float64(1);
		
		public Float64() {
			v = 0;
		}
		
		public Float64(double v) {
			this();
			set(v);
		}
		
		public Float64(Float64 other) {
			this();
			set(other);
		}
		
		public void set(double v) {
			this.v = v;
		}
		
		public void set(Float64 other) {
			set(other.v);
		}
		
		public Float64 variable() {
			return new Float64();
		}
		
		public boolean isEqual(Float64 other) {
			return v == other.v;
		}
		
		public boolean isNotEqual(Float64 other) {
			return v != other.v;
		}
		
		public boolean isLess(Float64 other) {
			return v < other.v;
		}
		
		public boolean isLessEqual(Float64 other) {
			return v <= other.v;
		}
		
		public boolean isGreater(Float64 other) {
			return v > other.v;
		}
		
		public boolean isGreaterEqual(Float64 other) {
			return v >= other.v;
		}
		
		public int compare(Float64 other) {
			if (v < other.v) return -1;
			if (v > other.v) return  1;
			return 0;
		}
		
		public int signum() {
			return compare(ZERO);
		}
		
		public Float64 copy() {
			return new Float64(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(Float64 other) {
			v = -other.v;
		}
		
		public void add(Float64 a, Float64 b) {
			v = a.v + b.v;
		}
		
		public void subtract(Float64 a, Float64 b) {
			v = a.v - b.v;
		}
		
		public void multiply(Float64 a, Float64 b) {
			v = a.v * b.v;
		}
		
		public void divide(Float64 a, Float64 b) {
			v = a.v / b.v;
		}
		
		public void invert(Float64 other) {
			divide(ONE, other);
		}
		
		public void power(Float64 input, int power) {
			v = Math.pow(input.v, power);
		}
		
		public void abs(Float64 other) {
			v = v < 0 ? -v : v;
		}
		
		public void max() {
			set(Double.MAX_VALUE);
		}
		
		public void min() {
			set(-Double.MAX_VALUE);
		}
		
		public UnboundedInt intPart() {
			BigDecimal tmp = BigDecimal.valueOf(v);
			tmp = tmp.movePointRight(decPower());
			return new UnboundedInt(tmp.toBigInteger());
		}
		
		public int decPower() {
			return 20; // 17 might be enough to represent all doubles
		}

		public void set(RationalMember val) {
			BigDecimal n = new BigDecimal(val.numerator().v);
			BigDecimal d = new BigDecimal(val.denominator().v);
			BigDecimal tmp = n.divide(d);
			v = tmp.doubleValue();
		}
		
		public void set(IntegerMember<?> val) {
			BigDecimal n = new BigDecimal(val.intPart().v);
			v = n.doubleValue();
		}
		
		public void set(RealMember<?> val) {
			BigDecimal tmp = new BigDecimal(val.intPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			v = tmp.doubleValue();
		}
		
		public String toString() {
			return Double.toString(v);
		}
	}

	public static class ComplexFloat32
		implements ComplexMember<ComplexFloat32>, FieldMember<ComplexFloat32>
	{
		private Float32 r;
		private Float32 i;
		
		private static ComplexFloat32 ZERO = new ComplexFloat32(Float32.ZERO,Float32.ZERO);
		private static ComplexFloat32 ONE = new ComplexFloat32(Float32.ONE,Float32.ZERO);
		
		public ComplexFloat32() {
			r = Float32.ZERO.copy();
			i = Float32.ZERO.copy();
		}
		
		public ComplexFloat32(float r, float i) {
			this();
			set(r, i);
		}
		
		public ComplexFloat32(Float32 r, Float32 i) {
			this(r.v, i.v);
		}
		
		public ComplexFloat32(ComplexFloat32 other) {
			this(other.r, other.i);
		}

		public void set(float r, float i) {
			this.r.set(r);
			this.i.set(i);
		}

		public void set(Float32 r, Float32 i) {
			set(r.v, i.v);
		}
		
		public void set(ComplexFloat32 other) {
			set(other.r, other.i);
		}
		
		public Float32 r() {
			return r;
		}
		
		public Float32 i() {
			return i;
		}

		public ComplexFloat32 variable() {
			return new ComplexFloat32();
		}
		
		public boolean isEqual(ComplexFloat32 other) {
			return r.isEqual(other.r) && i.isEqual(other.i);
		}
		
		public boolean isNotEqual(ComplexFloat32 other) {
			return !isEqual(other);
		}
		
		public ComplexFloat32 copy() {
			return new ComplexFloat32(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(ComplexFloat32 other) {
			r.v = -r.v;
			i.v = -i.v;
		}
		
		public void add(ComplexFloat32 a, ComplexFloat32 b) {
			r.v = a.r.v + b.r.v;
			i.v = a.i.v + b.i.v;
		}
		
		public void subtract(ComplexFloat32 a, ComplexFloat32 b) {
			r.v = a.r.v - b.r.v;
			i.v = a.i.v - b.i.v;
		}
		
		public void multiply(ComplexFloat32 a, ComplexFloat32 b) {
			r.v = a.r.v * b.r.v - a.i.v * b.i.v;
			i.v = a.i.v * b.r.v + a.r.v * b.i.v;
		}
		
		public void divide(ComplexFloat32 a, ComplexFloat32 b) {
			float mod2 = b.r.v * b.r.v + b.i.v * b.i.v;
			r.v = (a.r.v * b.r.v + a.i.v * b.i.v) / mod2;
			i.v = (a.i.v * b.r.v - a.r.v * b.i.v) / mod2;
		}
		
		public void invert(ComplexFloat32 other) {
			divide(ONE, other);
		}
		
		public void power(ComplexFloat32 input, int power) {
			set(ONE);
			for (int i = 0; i < power; i++) {
				multiply(this,input);
			}
		}
		
		public UnboundedInt realIntPart() {
			return r.intPart();
		}
		
		public UnboundedInt imagIntPart() {
			return i.intPart();
		}

		public int decPower() {
			return r.decPower();
		}
		
		public void set(RationalMember val) {
			r.set(val);
			i.zero();
		}
		
		public void set(IntegerMember<?> val) {
			r.set(val);
			i.zero();
		}
		
		public void set(RealMember<?> val) {
			r.set(val);
			i.zero();
		}
		
		public void set(ComplexMember<?> val) {
			BigDecimal tmp = new BigDecimal(val.realIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			r.set(tmp.floatValue());
			tmp = new BigDecimal(val.imagIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			i.set(tmp.floatValue());
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(r.v);
			b.append(i.v < 0 ? "-" : "+");
			b.append(Math.abs(i.v));
			b.append("i");
			return b.toString();
		}
	}
	
	public static class ComplexFloat64
		implements ComplexMember<ComplexFloat64>, FieldMember<ComplexFloat64>
	{
		private Float64 r;
		private Float64 i;
		
		private static ComplexFloat64 ZERO = new ComplexFloat64(Float64.ZERO,Float64.ZERO);
		private static ComplexFloat64 ONE = new ComplexFloat64(Float64.ONE,Float64.ZERO);
		
		public ComplexFloat64() {
			r = Float64.ZERO.copy();
			i = Float64.ZERO.copy();
		}
		
		public ComplexFloat64(double r, double i) {
			this();
			set(r, i);
		}
		
		public ComplexFloat64(Float64 r, Float64 i) {
			this(r.v, i.v);
		}
		
		public ComplexFloat64(ComplexFloat64 other) {
			this(other.r, other.i);
		}

		public void set(double r, double i) {
			this.r.set(r);
			this.i.set(i);
		}
		
		public void set(Float64 r, Float64 i) {
			set(r.v, i.v);
		}
		
		public void set(ComplexFloat64 other) {
			set(other.r, other.i);
		}
		
		public Float64 r() {
			return r;
		}
		
		public Float64 i() {
			return i;
		}

		public ComplexFloat64 variable() {
			return new ComplexFloat64();
		}
		
		public boolean isEqual(ComplexFloat64 other) {
			return r.isEqual(other.r) && i.isEqual(other.i);
		}
		
		public boolean isNotEqual(ComplexFloat64 other) {
			return !isEqual(other);
		}
		
		public ComplexFloat64 copy() {
			return new ComplexFloat64(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(ComplexFloat64 other) {
			r.v = -r.v;
			i.v = -i.v;
		}
		
		public void add(ComplexFloat64 a, ComplexFloat64 b) {
			r.v = a.r.v + b.r.v;
			i.v = a.i.v + b.i.v;
		}
		
		public void subtract(ComplexFloat64 a, ComplexFloat64 b) {
			r.v = a.r.v - b.r.v;
			i.v = a.i.v - b.i.v;
		}
		
		public void multiply(ComplexFloat64 a, ComplexFloat64 b) {
			r.v = a.r.v * b.r.v - a.i.v * b.i.v;
			i.v = a.i.v * b.r.v + a.r.v * b.i.v;
		}
		
		public void divide(ComplexFloat64 a, ComplexFloat64 b) {
			double mod2 = b.r.v * b.r.v + b.i.v * b.i.v;
			r.v = (a.r.v * b.r.v + a.i.v * b.i.v) / mod2;
			i.v = (a.i.v * b.r.v - a.r.v * b.i.v) / mod2;
		}
		
		public void invert(ComplexFloat64 other) {
			divide(ONE, other);
		}
		
		public void power(ComplexFloat64 input, int power) {
			set(ONE);
			for (int i = 0; i < power; i++) {
				multiply(this,input);
			}
		}
		
		public UnboundedInt realIntPart() {
			return r.intPart();
		}
		
		public UnboundedInt imagIntPart() {
			return i.intPart();
		}
		
		public int decPower() {
			return r.decPower();
		}
		
		public void set(RationalMember val) {
			r.set(val);
			i.zero();
		}
		
		public void set(IntegerMember<?> val) {
			r.set(val);
			i.zero();
		}
		
		public void set(RealMember<?> val) {
			r.set(val);
			i.zero();
		}
		
		public void set(ComplexMember<?> val) {
			BigDecimal tmp = new BigDecimal(val.realIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			r.set(tmp.doubleValue());
			tmp = new BigDecimal(val.imagIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			i.set(tmp.doubleValue());
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(r.v);
			b.append(i.v < 0 ? "-" : "+");
			b.append(Math.abs(i.v));
			b.append("i");
			return b.toString();
		}
	}
	
	// composite classes
	
	// TODO: should V extend FieldMember?
	public static abstract class AbstractMatrix<U extends Matrix<U,V>, V extends RingWithUnityMember<V>>
		implements Matrix<U,V>
	{
		private V type;
		private int rows, cols;
		private ArrayList<V> values;

		protected void allocate(int r, int c) {
			if (r*c != rows*cols) {
				values = new ArrayList<V>();
				for (int i = 0; i < rows*cols; i++) {
					values.add(type.variable());
				}
			}
			rows = r;
			cols = c;
		}

		protected void copyValues(Matrix<U,V> other) {
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					setValue(r, c, other.value(r, c));
				}
			}
		}

		public AbstractMatrix(V type) {
			this.type = type;
		}
		
		public void setValue(int r, int c, V val) {
			int index = r * cols + c;
			values.get(index).set(val);
		}
		
		public V value(int r, int c) {
			int index = r * cols + c;
			return values.get(index);
		}
		
		public int cols() {
			return cols;
		}
		
		public int rows() {
			return rows;
		}
		
		public void set(Matrix<U,V> other) {
			allocate(other.rows(), other.cols());
			copyValues(other);
		}
		
		public boolean isEqual(Matrix<U,V> other) {
			if (rows != other.rows()) return false;
			if (cols != other.cols()) return false;
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if (value(r,c).isNotEqual(other.value(r,c)))
						return false;
				}
			}
			return true;
		}
		
		public boolean isNotEqual(Matrix<U,V> other) {
			return !isEqual(other);
		}
		
		public void zero() {
			for (int i = 0; i < values.size(); i++) {
				values.get(i).zero();
			}
		}
		
		public void add(Matrix<U,V> a, Matrix<U,V> b) {
			if (a.rows() != b.rows() || a.cols() != b.cols()) {
				throw new IllegalArgumentException(
					"matrices are of different size");
			}
			allocate(a.rows(), a.cols());
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					value(r, c).add(a.value(r, c), b.value(r, c));
				}
			}
		}
		
		public void subtract(Matrix<U,V> a, Matrix<U,V> b) {
			if (a.rows() != b.rows() || a.cols() != b.cols()) {
				throw new IllegalArgumentException(
					"matrices are of different size");
			}
			allocate(a.rows(), a.cols());
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					value(r, c).subtract(a.value(r, c), b.value(r, c));
				}
			}
		}
		
		public void multiply(Matrix<U,V> a, Matrix<U,V> b) {
			if (a.cols() != b.rows()) {
				throw new IllegalArgumentException(
					"cannot multiply matrices of unmatching shape");
			}
			allocate(a.rows(), b.cols());
			V sum = type.variable();
			V tmp = type.variable();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					sum.zero();
					for (int i = 0; i < a.cols(); i++) {
						tmp.multiply(a.value(r,i), b.value(i,c));
						sum.add(sum, tmp);
					}
					setValue(r, c, sum);
				}
			}
		}
		
		public void power(Matrix<U,V> input, int power) {
			if (power < 0) {
				throw new IllegalArgumentException(
					"negative power not allowed");
			}
			if (power == 1) {
				set(input);
				return;
			}
			if (input.rows() != input.cols()) {
				throw new IllegalArgumentException(
					"power method requires a square matrix");
			}
			if (power == 0) {
				allocate(input.rows(), input.cols());
				zero();
				for (int i = 0; i < input.rows(); i++) {
					value(i, i).unity();
				}
			}
			else { // power >= 2
				set(input);
				for (int i = 1; i < power; i++) {
					multiply(this, input);
				}
			} 
		}

		public void transpose() { // TODO: test
			/*
			[ 0,  1,  2,  3,
			  4,  5,  6,  7,
			  8,  9, 10, 11]
			transposes to
			[ 0, 4, 8,
			  1, 5, 9,
			  2, 6, 10,
			  3, 7, 11]
			*/
			V tmp = type.variable();
			for (int r = 0; r < rows; r++) {
				for (int c = r; c < cols; c++) {
					int currPos = r * cols + c;
					int newPos = c * rows + r;
					tmp.set(values.get(newPos));
					values.get(newPos).set(values.get(currPos));
					values.get(currPos).set(tmp);
				}
			}
			int swp = rows;
			rows = cols;
			cols = swp;
		}
		
		public void negate(Matrix<U,V> other) {
			set(other);
			for (int i = 0; i < values.size(); i++) {
				V val = values.get(i);
				val.negate(val);
			}
		}
		
		public void scale(Matrix<U,V> other, V scale) {
			set(other);
			for (int i = 0; i < values.size(); i++) {
				V val = values.get(i);
				val.multiply(val, scale);
			}
		}
	}
	
	public static class RealFloat32Matrix
		extends AbstractMatrix<RealFloat32Matrix,Float32>
		implements RingMember<RealFloat32Matrix>
 	{
		private static Float32 type = new Float32();
		
		public RealFloat32Matrix(int r, int c) {
			super(type);
			allocate(r, c);
		}
		
		public RealFloat32Matrix(RealFloat32Matrix other) {
			super(type);
			set(other);
		}
		
		public RealFloat32Matrix variable() {
			return new RealFloat32Matrix(1,1);
		}
		
		public RealFloat32Matrix copy() {
			return new RealFloat32Matrix(this);
		}

		public void set(RealFloat32Matrix other) {
			super.set(other);
		}
		
		public boolean isEqual(RealFloat32Matrix other) {
			return super.isEqual(other);
		}
		
		public boolean isNotEqual(RealFloat32Matrix other) {
			return super.isNotEqual(other);
		}
		
		public void add(RealFloat32Matrix a, RealFloat32Matrix b) {
			super.add(a, b);
		}
		
		public void subtract(RealFloat32Matrix a, RealFloat32Matrix b) {
			super.subtract(a, b);
		}
		
		public void multiply(RealFloat32Matrix a, RealFloat32Matrix b) {
			super.multiply(a, b);
		}
		
		public void power(RealFloat32Matrix other, int power) {
			super.power(other, power);
		}
		
		public void negate(RealFloat32Matrix other) {
			super.negate(other);
		}
	}
	
	public static class RealFloat64Matrix
		extends AbstractMatrix<RealFloat64Matrix,Float64>
		implements RingMember<RealFloat64Matrix>
	{
		private static Float64 type = new Float64();

		public RealFloat64Matrix(int r, int c) {
			super(type);
			allocate(r, c);
		}
		
		public RealFloat64Matrix(RealFloat64Matrix other) {
			super(type);
			set(other);
		}
			
		public RealFloat64Matrix variable() {
			return new RealFloat64Matrix(1,1);
		}
		
		public RealFloat64Matrix copy() {
			return new RealFloat64Matrix(this);
		}
		
		public void set(RealFloat64Matrix other) {
			super.set(other);
		}
		
		public boolean isEqual(RealFloat64Matrix other) {
			return super.isEqual(other);
		}
		
		public boolean isNotEqual(RealFloat64Matrix other) {
			return super.isNotEqual(other);
		}
		
		public void add(RealFloat64Matrix a, RealFloat64Matrix b) {
			super.add(a, b);
		}
		
		public void subtract(RealFloat64Matrix a, RealFloat64Matrix b) {
			super.subtract(a, b);
		}
		
		public void multiply(RealFloat64Matrix a, RealFloat64Matrix b) {
			super.multiply(a, b);
		}
		
		public void power(RealFloat64Matrix other, int power) {
			super.power(other, power);
		}
		
		public void negate(RealFloat64Matrix other) {
			super.negate(other);
		}
	}
	
	public static class ComplexFloat32Matrix
		extends AbstractMatrix<ComplexFloat32Matrix,ComplexFloat32>
		implements RingMember<ComplexFloat32Matrix>
	{
		private static ComplexFloat32 type = new ComplexFloat32();

		public ComplexFloat32Matrix(int r, int c) {
			super(type);
			allocate(r, c);
		}
		
		public ComplexFloat32Matrix(ComplexFloat32Matrix other) {
			super(type);
			set(other);
		}
		
		public ComplexFloat32Matrix variable() {
			return new ComplexFloat32Matrix(1,1);
		}
		
		public ComplexFloat32Matrix copy() {
			return new ComplexFloat32Matrix(this);
		}
		
		public void set(ComplexFloat32Matrix other) {
			super.set(other);
		}
		
		public boolean isEqual(ComplexFloat32Matrix other) {
			return super.isEqual(other);
		}
		
		public boolean isNotEqual(ComplexFloat32Matrix other) {
			return super.isNotEqual(other);
		}
		
		public void add(ComplexFloat32Matrix a, ComplexFloat32Matrix b) {
			super.add(a, b);
		}
		
		public void subtract(ComplexFloat32Matrix a, ComplexFloat32Matrix b) {
			super.subtract(a, b);
		}
		
		public void multiply(ComplexFloat32Matrix a, ComplexFloat32Matrix b) {
			super.multiply(a, b);
		}
		
		public void power(ComplexFloat32Matrix other, int power) {
			super.power(other, power);
		}
		
		public void negate(ComplexFloat32Matrix other) {
			super.negate(other);
		}
	}
	
	public static class ComplexFloat64Matrix
		extends AbstractMatrix<ComplexFloat64Matrix,ComplexFloat64>
		implements RingMember<ComplexFloat64Matrix>
	{
		private static ComplexFloat64 type = new ComplexFloat64();

		public ComplexFloat64Matrix(int r, int c) {
			super(type);
			allocate(r, c);
		}
		
		public ComplexFloat64Matrix(ComplexFloat64Matrix other) {
			super(type);
			set(other);
		}
		
		public ComplexFloat64Matrix variable() {
			return new ComplexFloat64Matrix(1,1);
		}
		
		public ComplexFloat64Matrix copy() {
			return new ComplexFloat64Matrix(this);
		}
		
		public void set(ComplexFloat64Matrix other) {
			super.set(other);
		}
		
		public boolean isEqual(ComplexFloat64Matrix other) {
			return super.isEqual(other);
		}
		
		public boolean isNotEqual(ComplexFloat64Matrix other) {
			return super.isNotEqual(other);
		}
		
		public void add(ComplexFloat64Matrix a, ComplexFloat64Matrix b) {
			super.add(a, b);
		}
		
		public void subtract(ComplexFloat64Matrix a, ComplexFloat64Matrix b) {
			super.subtract(a, b);
		}
		
		public void multiply(ComplexFloat64Matrix a, ComplexFloat64Matrix b) {
			super.multiply(a, b);
		}
		
		public void power(ComplexFloat64Matrix other, int power) {
			super.power(other, power);
		}
		
		public void negate(ComplexFloat64Matrix other) {
			super.negate(other);
		}
	}
	
	public static abstract class AbstractQuaternion<U extends Quaternion<U,V>,V extends RingWithUnityMember<V>>
		implements Quaternion<U,V>
	{
		private V type;
		
		public AbstractQuaternion(V type) {
			this.type = type;
		}
		
		public V norm() {
			V norm = type.variable();
			V t2 = type.variable();
			t2.multiply(a(), a());
			norm.add(norm, t2);
			t2.multiply(b(), b());
			norm.add(norm, t2);
			t2.multiply(c(), c());
			norm.add(norm, t2);
			t2.multiply(d(), d());
			norm.add(norm, t2);
			return norm;
			// TODO: from reading I can't tell if norm is sum or sqrt of
			// sum. It might be that either is fine if you are
			// consistent. Investigate.
		}
		
	}

	// I think this quaternion implementation is called a biquaternion.
	// This might not be what is desired.
	
	public static class QuaternionFloat32
		extends AbstractQuaternion<QuaternionFloat32, Float32>
		implements Quaternion<QuaternionFloat32, Float32>, SkewFieldMember<QuaternionFloat32>
	{
		private static Float32 type = new Float32();
		private ComplexFloat32Matrix value = new ComplexFloat32Matrix(2,2);
		private static QuaternionFloat32 ZERO =
			new QuaternionFloat32(Float32.ZERO, Float32.ZERO, Float32.ZERO, Float32.ZERO);
		private static QuaternionFloat32 ONE =
			new QuaternionFloat32(Float32.ONE, Float32.ZERO, Float32.ZERO, Float32.ZERO);
		
		public QuaternionFloat32() {
			super(type);
			set(ZERO);
		}

		public QuaternionFloat32(QuaternionFloat32 other) {
			super(type);
			set(other);
		}

		public QuaternionFloat32(Float32 a, Float32 b, Float32 c, Float32 d) {
			super(type);
			setA(a);
			setB(b);
			setC(c);
			setD(d);
		}
		
		public void set(QuaternionFloat32 other) {
			setA(other.a());
			setB(other.b());
			setC(other.c());
			setD(other.d());
		}
		
		public Float32 a() { return value.value(0,0).r(); }
		public Float32 b() { return value.value(0,0).i(); }
		public Float32 c() { return value.value(0,1).r(); }
		public Float32 d() { return value.value(0,1).i(); }
		
		public void setA(Float32 val) {
			value.value(0,0).r().set(val);
			value.value(1,1).r().set(val);
		}
		
		public void setB(Float32 val) {
			value.value(0,0).i().set(val);
			value.value(1,1).i().negate(val);
		}
		
		public void setC(Float32 val) {
			value.value(0,1).r().set(val);
			value.value(1,0).r().negate(val);
		}
		
		public void setD(Float32 val) {
			value.value(0,1).i().set(val);
			value.value(1,0).i().set(val);
		}
		
		public QuaternionFloat32 variable() {
			return new QuaternionFloat32();
		}
		
		public boolean isEqual(QuaternionFloat32 other) {
			return value.isEqual(other.value);
		}
		
		public boolean isNotEqual(QuaternionFloat32 other) {
			return !isEqual(other);
		}
		
		public QuaternionFloat32 copy() {
			return new QuaternionFloat32(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(QuaternionFloat32 other) {
			subtract(ZERO, other);
		}
		
		public void add(QuaternionFloat32 a, QuaternionFloat32 b) { 
			value.add(a.value, b.value);
		}
		
		public void subtract(QuaternionFloat32 a, QuaternionFloat32 b) {
			value.subtract(a.value, b.value);
		}
		
		public void multiply(QuaternionFloat32 a, QuaternionFloat32 b) {
			value.multiply(a.value, b.value);
		}
		
		public void divide(QuaternionFloat32 a, QuaternionFloat32 b) {
			QuaternionFloat32 ib = new QuaternionFloat32();
			ib.invert(b);
			value.multiply(a.value, ib.value);
		}
		
		public void invert(QuaternionFloat32 other) {
			QuaternionFloat32 scale = new QuaternionFloat32();
			scale.setA(other.norm()); // or norm()^2 depending upon def: see norm()
			QuaternionFloat32 c = new QuaternionFloat32();
			c.conjugate(other);
			multiply(scale, c);
		}

		public void conjugate(QuaternionFloat32 other) {
			ComplexFloat32 v;
			v = value.value(0,0);
			v.i().negate(other.value.value(0,0).i());
			v = value.value(0,1);
			v.r().negate(other.value.value(0,1).r());
			v.i().negate(other.value.value(0,1).i());
			v = value.value(1,0);
			v.r().negate(other.value.value(1,0).r());
			v.i().negate(other.value.value(1,0).i());
			v = value.value(1,1);
			v.i().negate(other.value.value(1,1).i());
		}
		
		public void power(QuaternionFloat32 other, int power) {
			unity();
			for (int i = 0; i < power; i++) {
				multiply(this, other);
			}
		}
		
		public void set(IntegerMember<?> val) {
			BigInteger n = val.intPart().v;
			Float32 f = new Float32(n.floatValue());
			zero();
			setA(f);
		}
		
		public void set(RationalMember<?> val) {
			BigDecimal n = new BigDecimal(val.numerator().v);
			BigDecimal d = new BigDecimal(val.denominator().v);
			BigDecimal ratio = n.divide(d);
			Float32 f = new Float32(ratio.floatValue());
			zero();
			setA(f);
		}
		
		public void set(RealMember<?> val) {
			BigDecimal n = new BigDecimal(val.intPart().v);
			n = n.movePointLeft(val.decPower());
			Float32 f = new Float32(n.floatValue());
			zero();
			setA(f);
		}
		
		public void set(Quaternion<?,?> val) {
			BigDecimal tmp;
			Float32 f = new Float32();
			tmp = new BigDecimal(val.aIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.floatValue());
			setA(f);
			tmp = new BigDecimal(val.bIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.floatValue());
			setB(f);
			tmp = new BigDecimal(val.cIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.floatValue());
			setC(f);
			tmp = new BigDecimal(val.dIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.floatValue());
			setD(f);
		}
		
		public UnboundedInt aIntPart() {
			return a().intPart();
		}
		
		public UnboundedInt bIntPart() {
			return b().intPart();
		}
		
		public UnboundedInt cIntPart() {
			return c().intPart();
		}
		
		public UnboundedInt dIntPart() {
			return d().intPart();
		}
		
		public int decPower() {
			return type.decPower();
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(a().v);
			b.append(b().v < 0 ? "-" : "+");
			b.append(Math.abs(b().v));
			b.append("i");
			b.append(c().v < 0 ? "-" : "+");
			b.append(Math.abs(c().v));
			b.append("j");
			b.append(d().v < 0 ? "-" : "+");
			b.append(Math.abs(d().v));
			b.append("k");
			return b.toString();
		}
	}
	
	// I think this quaternion implementation is called a biquaternion.
	// This might not be what is desired.
	
	public static class QuaternionFloat64
		extends AbstractQuaternion<QuaternionFloat64, Float64>
		implements Quaternion<QuaternionFloat64, Float64>, SkewFieldMember<QuaternionFloat64>
	{
		private static Float64 type = new Float64();
		private ComplexFloat64Matrix value = new ComplexFloat64Matrix(2,2);
		private static QuaternionFloat64 ZERO =
			new QuaternionFloat64(Float64.ZERO, Float64.ZERO, Float64.ZERO, Float64.ZERO);
		private static QuaternionFloat64 ONE =
			new QuaternionFloat64(Float64.ONE, Float64.ZERO, Float64.ZERO, Float64.ZERO);
		
		public QuaternionFloat64() {
			super(type);
			set(ZERO);
		}

		public QuaternionFloat64(QuaternionFloat64 other) {
			super(type);
			set(other);
		}

		public QuaternionFloat64(Float64 a, Float64 b, Float64 c, Float64 d) {
			super(type);
			setA(a);
			setB(b);
			setC(c);
			setD(d);
		}
		
		public void set(QuaternionFloat64 other) {
			setA(other.a());
			setB(other.b());
			setC(other.c());
			setD(other.d());
		}
		
		public Float64 a() { return value.value(0,0).r(); }
		public Float64 b() { return value.value(0,0).i(); }
		public Float64 c() { return value.value(0,1).r(); }
		public Float64 d() { return value.value(0,1).i(); }
		
		public void setA(Float64 val) {
			value.value(0,0).r().set(val);
			value.value(1,1).r().set(val);
		}
		
		public void setB(Float64 val) {
			value.value(0,0).i().set(val);
			value.value(1,1).i().negate(val);
		}
		
		public void setC(Float64 val) {
			value.value(0,1).r().set(val);
			value.value(1,0).r().negate(val);
		}
		
		public void setD(Float64 val) {
			value.value(0,1).i().set(val);
			value.value(1,0).i().set(val);
		}
		
		public QuaternionFloat64 variable() {
			return new QuaternionFloat64();
		}
		
		public boolean isEqual(QuaternionFloat64 other) {
			return value.isEqual(other.value);
		}
		
		public boolean isNotEqual(QuaternionFloat64 other) {
			return !isEqual(other);
		}
		
		public QuaternionFloat64 copy() {
			return new QuaternionFloat64(this);
		}
		
		public void zero() {
			set(ZERO);
		}
		
		public void unity() {
			set(ONE);
		}
		
		public void negate(QuaternionFloat64 other) {
			subtract(ZERO,other);
		}
		
		public void add(QuaternionFloat64 a, QuaternionFloat64 b) {
			value.add(a.value, b.value);
		}
		
		public void subtract(QuaternionFloat64 a, QuaternionFloat64 b) {
			value.subtract(a.value, b.value);
		}
		
		public void multiply(QuaternionFloat64 a, QuaternionFloat64 b) {
			value.multiply(a.value, b.value);
		}
		
		public void divide(QuaternionFloat64 a, QuaternionFloat64 b) {
			QuaternionFloat64 ib = new QuaternionFloat64();
			ib.invert(b);
			value.multiply(a.value, ib.value);
		}
		
		public void invert(QuaternionFloat64 other) {
			QuaternionFloat64 scale = new QuaternionFloat64();
			scale.setA(other.norm()); // or norm()^2 depending upon def: see norm()
			QuaternionFloat64 c = new QuaternionFloat64();
			c.conjugate(other);
			multiply(scale, c);
		}

		public void conjugate(QuaternionFloat64 other) {
			ComplexFloat64 v;
			v = value.value(0,0);
			v.i().negate(other.value.value(0,0).i());
			v = value.value(0,1);
			v.r().negate(other.value.value(0,1).r());
			v.i().negate(other.value.value(0,1).i());
			v = value.value(1,0);
			v.r().negate(other.value.value(1,0).r());
			v.i().negate(other.value.value(1,0).i());
			v = value.value(1,1);
			v.i().negate(other.value.value(1,1).i());
		}

		public void power(QuaternionFloat64 other, int power) {
			unity();
			for (int i = 0; i < power; i++) {
				multiply(this, other);
			}
		}
		
		public void set(IntegerMember<?> val) {
			BigInteger n = val.intPart().v;
			Float64 f = new Float64(n.doubleValue());
			zero();
			setA(f);
		}
		
		public void set(RationalMember<?> val) {
			BigDecimal n = new BigDecimal(val.numerator().v);
			BigDecimal d = new BigDecimal(val.denominator().v);
			BigDecimal ratio = n.divide(d);
			Float64 f = new Float64(ratio.doubleValue());
			zero();
			setA(f);
		}
		
		public void set(RealMember<?> val) {
			BigDecimal n = new BigDecimal(val.intPart().v);
			n = n.movePointLeft(val.decPower());
			Float64 f = new Float64(n.doubleValue());
			zero();
			setA(f);
		}
		
		public void set(Quaternion<?,?> val) {
			BigDecimal tmp;
			Float64 f = new Float64();
			tmp = new BigDecimal(val.aIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.doubleValue());
			setA(f);
			tmp = new BigDecimal(val.bIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.doubleValue());
			setB(f);
			tmp = new BigDecimal(val.cIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.doubleValue());
			setC(f);
			tmp = new BigDecimal(val.dIntPart().v);
			tmp = tmp.movePointLeft(val.decPower());
			f.set(tmp.doubleValue());
			setD(f);
		}
		
		public UnboundedInt aIntPart() {
			return a().intPart();
		}
		
		public UnboundedInt bIntPart() {
			return b().intPart();
		}
		
		public UnboundedInt cIntPart() {
			return c().intPart();
		}
		
		public UnboundedInt dIntPart() {
			return d().intPart();
		}
		
		public int decPower() {
			return type.decPower();
		}
		
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(a().v);
			b.append(b().v < 0 ? "-" : "+");
			b.append(Math.abs(b().v));
			b.append("i");
			b.append(c().v < 0 ? "-" : "+");
			b.append(Math.abs(c().v));
			b.append("j");
			b.append(d().v < 0 ? "-" : "+");
			b.append(Math.abs(d().v));
			b.append("k");
			return b.toString();
		}
	}

	// a Data collection can always be viewed as a big list of items and
	// a multidim mapping of indices.
	 
	public static class Data {
		private int[] dims;
		private List<? extends GroupMember<?>> data;

		public Data(int[] dims, List<? extends GroupMember<?>> data) {
			long size = (dims.length == 0) ? 0 : 1;
			for (int i = 0; i < dims.length; i++) {
				size *= dims[i];
			}
			if (data.size() != size) {
				throw new IllegalArgumentException(
					"dims don't match size of input data");
			}
			this.dims = dims.clone();
			this.data = data; // TODO: copy data so list size can't change
		}
		
		public int numDimensions() {
			return dims.length;
		}
		
		public void dims(int[] dest) {
			if (dest.length != dims.length) {
				throw new IllegalArgumentException();
			}
			for (int i = 0; i < dims.length; i++) {
				dest[i] = dims[i];
			}
		}

		// Note: we expect all data to be monotyped.
		
		public GroupMember<?> type() {
			return data.get(0);
		}
		
		// TODO: return an UnmodifiableList so list size can't change
		
		public List<?> data() {
			return data;
		}
		
		public int index(int[] pos) {
			int p = 0;
			int inc = 1;
			for (int i = 0; i < pos.length; i++) {
				p += pos[i] * inc;
				inc *= dims[i];
			}
			return p;
		}
	}

	// note that though Plugin is typed in general we instantiate them
	// as raw types. This is similar to what IJ2 does now.
	
	public interface Plugin<T> {
		void setup(Data data); // just a toy. support multiple params later as needed.
		void run();
		String error();
		List<Object> outputs();
	}

	// example of a plugin implementation that works with any FieldMember derived class
	
	public static class MyFieldMemberPlugin<T extends FieldMember<T>> implements Plugin<T> {
		private List<FieldMember<T>> fieldData;
		private String err;
		private List<Object> outputs;
		
		@SuppressWarnings("unchecked")
		public void setup(Data data) {
			fieldData = null;
			try {
				if (FieldMember.class.isAssignableFrom(data.type().getClass()))
					fieldData = (List<FieldMember<T>>) data.data();
			} catch (Exception e) {
				// do nothing. if here likely a class cast exception
			}
			if (fieldData == null) {
				err = "Input data is not made of FieldMember objects.";
			}
			outputs = new ArrayList<Object>();
		}
		
		public void run() {
			if (fieldData == null) return;
			T val1 = fieldData.get(0).variable();
			T val2 = val1.variable();
			T two = val1.variable();
			val1.unity(); // val1 = 1
			val2.unity(); // val2 = 1
			two.add(val1, val2); // val1 + val2 = two
			T num = val1.variable();
			num.power(two, 5);
			// here num == 32 == 2^5
			outputs.add(num);
		}
		
		public String error() { return err; }
		
		public List<Object> outputs() { return outputs; }
	}
	
	// example of a plugin implementation that works with a specific
	// type of data. Note how isAssignableFrom() required to allow
	// runtime determination of type. Due to type erasure this is
	// required if not using compile checks.

	public static class MySumIntPlugin implements Plugin<Signed32BitInt> {
		
		private List<Signed32BitInt> data;
		private String err;
		private List<Object> outputs;

		@SuppressWarnings("unchecked")
		public void setup(Data data) {
			this.data = null;
			this.outputs = new ArrayList<Object>();
			try {
				if (Signed32BitInt.class.isAssignableFrom(data.type().getClass())) {
					this.data = (List<Signed32BitInt>) data.data();
				}
			} catch (Exception e) {
				// do nothing
			}
			if (this.data == null) {
				err = "Data is not made of Signed32BitInts";
			}
		}
		
		public void run() {
			if (data == null) return;
			Signed32BitInt sum = new Signed32BitInt();
			for (Signed32BitInt i : data) {
				sum.add(sum, i);
			}
			outputs.add(sum);
		}
		
		public String error() { return err; }
		
		public List<Object> outputs() { return outputs; }
	}
	
	// other plugin impls could test for all kinds of things in the
	// setup() method:
	//   has 3 channels, data is integral, all data values are > 10.0,
	//   input is made of Signed32BitInts, etc.
	
	// Data is returned untyped and plugins can shoehorn up to be
	// able to manipulate types using generics.
	
	public static class DataLoader {
		public static Data load(URL dataUrl) {
			// TODO: implement. For now hack to work for testing.
			List<GroupMember<?>> list = new ArrayList<GroupMember<?>>();
			list.add(null);
// TODO tweak these lines to test various behaviors
			list.set(0, new Float32(0.5f));
//			list.set(0, new UnboundedInt(BigInteger.ONE));
//			list.set(0, new Signed32BitInt(175));
			return new Data(new int[]{1}, list);
		}
	}

	// a typical sequence of operations ...
	
	public static void main(String[] args) {
		URL someUrl = null;
		Data data = DataLoader.load(someUrl);
		// this next line has no generics on purpose
		runPlugin(new MyFieldMemberPlugin(), "field member", data);
		runPlugin(new MySumIntPlugin(), "sum int", data);
	}
	
	public static void runPlugin(Plugin<?> plugin, String pluginName, Data data) {
		plugin.setup(data);
		plugin.run();
		String err = plugin.error();
		if (err == null)
		  System.out.println("Successfully ran "+pluginName+" plugin: " + plugin.outputs().get(0));
		else
		  System.out.println("Could not run "+pluginName+" plugin: " + err);
	}
}
