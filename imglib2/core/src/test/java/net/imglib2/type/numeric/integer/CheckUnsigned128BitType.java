package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.Test;

public class CheckUnsigned128BitType {

	@Test
	public void test() {
		Unsigned128BitType u = new Unsigned128BitType();
		long[] dims = new long[]{512, 512};
		Img<Unsigned128BitType> img = u.createSuitableNativeImg(new ArrayImgFactory<Unsigned128BitType>(), dims);
		
		Cursor<Unsigned128BitType> c = img.cursor();
		BigInteger maxUnsigned128BitValue = u.getMaxBigIntegerValue();
		
		// Check variable
		Unsigned128BitType max = new Unsigned128BitType( maxUnsigned128BitValue );
		assertTrue(0 == max.get().compareTo(maxUnsigned128BitValue));
		
		// Fill with maximum values
		while (c.hasNext()) {
			c.next().set(maxUnsigned128BitValue);
			assertTrue(maxUnsigned128BitValue.toString() + " :: " + c.get().get().toString(),
					0 == c.get().get().compareTo(maxUnsigned128BitValue));
		}
		
		// Read the maximum values
		c.reset();
		while (c.hasNext()) {
			c.fwd();
			assertTrue(0 == c.get().get().compareTo(maxUnsigned128BitValue));
		}
		
		// Test the compare method
		c.reset();
		while (c.hasNext()) {
			c.fwd();
			assertTrue(0 == c.get().compareTo(max));
			break; // all values are the same
		}
		
		// Fill in with values larger than maxUnsigned128Bitvalue,
		// to test for correct unsigned overflow
		BigInteger last = maxUnsigned128BitValue.add(BigInteger.ONE);
		int i = 0;
		c.reset();
		while (c.hasNext()) {
			c.fwd();
			c.get().set( last );
			assertTrue(i + " :: " + c.get().get(),
					0 == c.get().get().compareTo(BigInteger.valueOf(i)));
			++i;
			last = last.add(BigInteger.ONE);
		}
		
		// Check values again, testing for overwriting issues
		c.reset();
		i = 0;
		while (c.hasNext()) {
			c.fwd();
			assertTrue(i + " :: " + c.get().get(),
					0 == c.get().get().compareTo(BigInteger.valueOf(i)));
			++i;
		}
		
		// Check for get/set random values between 0 and max
		BigInteger[] values = new BigInteger[(int) img.size()];
		Random random = new Random(69997);
		for (int k=0; k<values.length; ++k) {
			values[k] = new BigInteger(128, random);
		}
		i = 0;
		c.reset();
		while (c.hasNext()) {
			c.fwd();
			c.get().set(values[i]);
			assertTrue(values[i] + " :: " + c.get().get(),
					0 == values[i].compareTo(c.get().get()));
			++i;
		}

		// Check again, for overwriting issues
		c.reset();
		i = 0;
		while (c.hasNext()) {
			c.fwd();
			c.get().set(values[i]);
			assertTrue(0 == values[i].compareTo(c.get().get()));
			++i;
		}
		
		// Test inc(), dec() and compareTo
		c.reset();
		while (c.hasNext()) {
			c.fwd();
			// Set a value far lower than the maximum
			c.get().set(new BigInteger(120, random));
			Unsigned128BitType copy0 = c.get().copy();
			// Test inc()
			BigInteger b0 = c.get().get();
			c.get().inc();
			Unsigned128BitType copy1 = c.get().copy();
			BigInteger b1 = b0.add(BigInteger.ONE);
			assertTrue(0 == b1.compareTo(c.get().get()));
			// Test dec()
			c.get().dec();
			assertTrue(0 == b0.compareTo(c.get().get()));
			// Test compareTo
			assertTrue(0 == copy0.compareTo(c.get()));
			assertTrue(1 == copy1.compareTo(copy0));
			assertTrue(-1 == copy0.compareTo(copy1));
		}
		
		// Test inc() and dec() around the 64-bit boundary
		BigInteger boundary64 = new BigInteger("1111111111111111111111111111111111111111111111111111111111111111", 2);
		u.set(boundary64);
		u.inc();
		assertTrue(0 == u.get().compareTo(boundary64.add(BigInteger.ONE)));
		u.dec();
		assertTrue(0 == u.get().compareTo(boundary64));
		
		// Test inc() and dec() around the 128-bit boundary
		u.set(maxUnsigned128BitValue);
		u.inc();
		assertTrue(0 == u.get().compareTo(BigInteger.ZERO));
		u.dec();
		assertTrue(0 == u.get().compareTo(maxUnsigned128BitValue));
	}
}
