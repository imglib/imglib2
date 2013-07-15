package net.imglib2.ops.sandbox.types;

import java.math.BigInteger;


// implements an unbounded integer class

public class Integer implements Integral<Integer> {

	private static final Integer ZERO = new Integer();
	private static final Integer TWO = new Integer(2);

	private BigInteger val;
	private Integer tmp = new Integer();

	public Integer(BigInteger val) {
		this.val = val;
	}

	public Integer() {
		this(BigInteger.ZERO);
	}

	public Integer(long val) {
		this(BigInteger.valueOf(val));
	}

	void setValue(long val) {
		this.val = BigInteger.valueOf(val);
	}

	void setValue(BigInteger val) {
		this.val = val;
	}

	@Override
	public void toRational(Rational result) {
		// TODO Auto-generated method stub

	}

	@Override
	public <Z> void realToFrac(Fractional<Z> result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(Integer b, Integer result) {
		result.val = val.add(b.val);
	}

	@Override
	public void subtract(Integer b, Integer result) {
		result.val = val.subtract(b.val);
	}

	@Override
	public void multiply(Integer b, Integer result) {
		result.val = val.multiply(b.val);
	}

	@Override
	public void negate(Integer result) {
		result.val = val.negate();
	}

	@Override
	public void abs(Integer result) {
		result.val = val.abs();
	}

	@Override
	public void signum(Integer result) {
		result.setValue(val.signum());
	}

	@Override
	public void fromInteger(Integer i, Integer result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(Integer src) {
		val = src.val;
	}

	@Override
	public void getValue(Integer dest) {
		dest.val = val;
	}

	@Override
	public Integer create() {
		return new Integer();
	}

	@Override
	public Integer copy() {
		return new Integer(val);
	}

	@Override
	public int compare(Integer b) {
		return val.compareTo(b.val);
	}

	@Override
	public boolean isLess(Integer b) {
		return compare(b) < 0;
	}

	@Override
	public boolean isLessEqual(Integer b) {
		return compare(b) <= 0;
	}

	@Override
	public boolean isGreater(Integer b) {
		return compare(b) > 0;
	}

	@Override
	public boolean isGreaterEqual(Integer b) {
		return compare(b) >= 0;
	}

	@Override
	public void max(Integer b, Integer result) {
		if (compare(b) > 0) result.setValue(this);
		else result.setValue(b);
	}

	@Override
	public void min(Integer b, Integer result) {
		if (compare(b) < 0) result.setValue(this);
		else result.setValue(b);
	}

	@Override
	public boolean isEqual(Integer b) {
		return val.equals(b.val);
	}

	@Override
	public boolean isNotEqual(Integer b) {
		return !isEqual(b);
	}

	@Override
	public void succ(Integer result) {
		result.val = val.add(BigInteger.ONE);
	}

	@Override
	public void pred(Integer result) {
		result.val = val.subtract(BigInteger.ONE);
	}

	@Override
	public void div(Integer b, Integer result) {
		result.val = val.divide(b.val);

	}

	@Override
	public void mod(Integer b, Integer result) {
		result.val = val.mod(b.val);
	}

	@Override
	public void divMod(Integer b, Integer divResult, Integer modResult) {
		BigInteger[] result = val.divideAndRemainder(b.val);
		divResult.setValue(result[0]);
		modResult.setValue(result[1]);
	}

	@Override
	public void toInteger(Integer result) {
		result.setValue(this);
	}

	@Override
	public boolean isEven() {
		mod(TWO, tmp);
		return tmp.compare(ZERO) == 0;
	}

	@Override
	public boolean isOdd() {
		return !isEven();
	}

	@Override
	public <Z> void fromIntegral(Number<Z> result) {
		// TODO Auto-generated method stub

	}

	@Override
	public <Z> void powNNI(Integral<Z> b, Integer result) {
		BigInteger value = BigInteger.valueOf(1);
		/* TODO - doesn't yet compile but correct
		while (b.compare(ZERO) > 0) {
			value = value.multiply(val);
			b.pred(b);
		}
		*/
		result.setValue(value);
	}
}
