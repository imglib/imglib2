package net.imglib2.ops.sandbox.types;

import java.math.BigInteger;


public class Signed32BitInteger implements Bounded<Signed32BitInteger>,
	Integral<Signed32BitInteger>
{

	// -- constants --

	static final Signed32BitInteger ZERO = new Signed32BitInteger(0);
	static final Signed32BitInteger ONE = new Signed32BitInteger(1);

	// -- fields --

	private int val;

	// -- public methods --

	public Signed32BitInteger(int i) {
		val = i;
	}

	public Signed32BitInteger() {
		this(0);
	}

	@Override
	public Signed32BitInteger copy() {
		return new Signed32BitInteger(val);
	}

	@Override
	public Signed32BitInteger create() {
		return new Signed32BitInteger();
	}

	@Override
	public void getValue(Signed32BitInteger dest) {
		dest.val = val;
	}

	@Override
	public void setValue(Signed32BitInteger src) {
		val = src.val;
	}

	public void setValue(int src) {
		val = src;
	}

	@Override
	public void toRational(Rational result) {
		Integer n = new Integer();
		Integer d = new Integer();
		this.toInteger(n);
		ONE.toInteger(d);
		result.setValue(n, d);
	}

	@Override
	public <Z> void realToFrac(Fractional<Z> result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void add(Signed32BitInteger b, Signed32BitInteger result) {
		result.val = val + b.val;
	}

	@Override
	public void subtract(Signed32BitInteger b, Signed32BitInteger result) {
		result.val = val - b.val;
	}

	@Override
	public void multiply(Signed32BitInteger b, Signed32BitInteger result) {
		result.val = val * b.val;
	}

	@Override
	public void negate(Signed32BitInteger result) {
		val = -val;
	}

	@Override
	public void abs(Signed32BitInteger result) {
		if (val < 0) result.val = -val;
		else result.val = val;
	}

	@Override
	public void signum(Signed32BitInteger result) {
		result.val = compare(result);
	}

	@Override
	public void fromInteger(Integer i, Signed32BitInteger result) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compare(Signed32BitInteger b) {
		if (val < 0) return -1;
		if (val > 0) return 1;
		return 0;
	}

	@Override
	public boolean isLess(Signed32BitInteger b) {
		return val < b.val;
	}

	@Override
	public boolean isLessEqual(Signed32BitInteger b) {
		return val <= b.val;
	}

	@Override
	public boolean isGreater(Signed32BitInteger b) {
		return val > b.val;
	}

	@Override
	public boolean isGreaterEqual(Signed32BitInteger b) {
		return val >= b.val;
	}

	@Override
	public void max(Signed32BitInteger b, Signed32BitInteger result) {
		if (val > b.val) result.val = val;
		else result.val = b.val;
	}

	@Override
	public void min(Signed32BitInteger b, Signed32BitInteger result) {
		if (val < b.val) result.val = val;
		else result.val = b.val;
	}

	@Override
	public boolean isEqual(Signed32BitInteger b) {
		return val == b.val;
	}

	@Override
	public boolean isNotEqual(Signed32BitInteger b) {
		return val != b.val;
	}

	@Override
	public void succ(Signed32BitInteger result) {
		result.val = val + 1;
	}

	@Override
	public void pred(Signed32BitInteger result) {
		result.val = val - 1;
	}

	/*
	@Override
	public void quotient(Signed32BitInteger b, Signed32BitInteger result) {
		// TODO Auto-generated method stub

		// how different than div?
	}

	@Override
	public void remainder(Signed32BitInteger b, Signed32BitInteger result) {
		// TODO Auto-generated method stub

		// how different than mod?
	}

	@Override
	public void quotRem(Signed32BitInteger b, Signed32BitInteger quotResult,
		Signed32BitInteger remResult)
	{
		// slow way for now
		quotient(b, quotResult);
		remainder(b, remResult);
	}
	*/

	@Override
	public void div(Signed32BitInteger b, Signed32BitInteger result) {
		result.val = val / b.val;
	}

	@Override
	public void mod(Signed32BitInteger b, Signed32BitInteger result) {
		result.val = val % b.val;
	}

	@Override
	public void divMod(Signed32BitInteger b, Signed32BitInteger divResult,
		Signed32BitInteger modResult)
	{
		// slow way for now
		div(b, divResult);
		mod(b, modResult);
	}

	@Override
	public void toInteger(Integer result) {
		result.setValue(BigInteger.valueOf(val));
	}

	@Override
	public boolean isEven() {
		return (val & 1) == 0;
	}

	@Override
	public boolean isOdd() {
		return (val & 1) == 1;
	}

	/*

	@Override
	public void gcd(Signed32BitInteger b, Signed32BitInteger result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lcm(Signed32BitInteger b, Signed32BitInteger result) {
		// TODO Auto-generated method stub

	}

	 */

	@Override
	public <Z> void fromIntegral(Number<Z> result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void minBound(Signed32BitInteger result) {
		result.val = java.lang.Integer.MIN_VALUE;
	}

	@Override
	public void maxBound(Signed32BitInteger result) {
		result.val = java.lang.Integer.MAX_VALUE;
	}

	@Override
	public <Z> void powNNI(Integral<Z> b, Signed32BitInteger result) {
		int value = 1;
		/* TODO - doesn't yet compile but correct
		while (b.compare(ZERO) > 0) {
			value = value.multiply(val);
			b.pred(b);
		}
		*/
		result.setValue(value);
	}

	/*

	@Override
	public void one(Signed32BitInteger result) {
		ONE.getValue(result);
	}

	@Override
	public void zero(Signed32BitInteger result) {
		ZERO.getValue(result);
	}

	 */

}
