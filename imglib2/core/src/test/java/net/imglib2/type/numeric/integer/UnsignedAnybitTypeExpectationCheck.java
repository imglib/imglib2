package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;

import org.junit.Test;

/**
 * @author Albert Cardona
 *
 *
 * Test on a dual-core 2.8 GHz i7 with hyperthreading
 * 

Creating images...
Filling images...
Compare iteration speed...
Elapset time (5 iterations):
 Unsigned12BitType: 726 ms
 UnsignedAnyBitType: 2013 ms
Creating images...
Filling images...
Compare iteration speed...
Elapset time (5 iterations):
 BitType: 258 ms
 UnsignedAnyBitType: 364 ms
Creating images...
Filling images...
Compare iteration speed...
Elapset time (5 iterations):
 UnsignedShortType: 252 ms
 UnsignedAnyBitType: 3457 ms
 *
 *
 *
 * As expected, the larger the number of bits, the worse the performance.
 */
public class UnsignedAnybitTypeExpectationCheck
{
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UnsignedAnybitTypeExpectationCheck u = new UnsignedAnybitTypeExpectationCheck();
		/*
		u.comparePerformanceWith(new Unsigned12BitType(), 12);
		u.comparePerformanceWith(new BitType(), 1);
		u.comparePerformanceWith(new UnsignedShortType(), 16);
		*/
		u.testGetBigInteger();
		u.testSetBigInteger();
		//u.comparePerformanceOfSetBigIntegerMethods();
		//u.comparePerformanceOfGetBigIntegerMethods();
	}
	
	@Test
	public void testValues() {
		
		for (int i=1; i<101; ++i) {
			// Create a Type support i bits
			System.out.println("New type with " + i + " bits");
			UnsignedAnyBitType a = new UnsignedAnyBitType(i);
			// Compute the maximum value for that type
			long v = (long) Math.pow(2, i) - 1;
			// .. and set all values from 0 to that max value
			for (int k=0; k<=v; ++k) {
				a.set( k );
				// ... and test that the returned value is the same
				assertTrue("Incorrect max value for " + k, a.get() == k);
			}
		}
	}

	@Test
	public <T extends AbstractIntegerType<T> & NativeType<T>> void comparePerformanceWith(final T t1, final int nBits) {
		System.out.println("Creating images...");
		long[] dims = new long[]{2048, 2048};
		// Create other Img
		Img<T> img1 = t1.createSuitableNativeImg(new ArrayImgFactory<T>(), dims);
		// Create 12-bit Img with UnsignedAnyBitType
		UnsignedAnyBitType t2 = new UnsignedAnyBitType(nBits);
		Img<UnsignedAnyBitType> img2 = t2.createSuitableNativeImg(new ArrayImgFactory<UnsignedAnyBitType>(), dims);
		// Fill both images with random 12-bit numbers, and check that they are equal
		System.out.println("Filling images...");
		final double max = t1.getMaxValue();
		final Cursor<T> c1 = img1.cursor();
		final Cursor<UnsignedAnyBitType> c2 = img2.cursor();
		while (c1.hasNext()) {
			c1.fwd();
			c2.fwd();
			c1.get().setInteger((long)(Math.random() * max));
			c2.get().set(c1.get().getIntegerLong());
			assertTrue("Missmatch!", c1.get().getIntegerLong() == c2.get().get());
		}
		// Compare iteration speed
		System.out.println("Compare iteration speed...");
		final int nIterations = 5;
		long time0 = System.currentTimeMillis();
		for (int i=0; i<nIterations; ++i) {
			c1.reset();
			while (c1.hasNext()) {
				c1.fwd();
				c1.get().getIntegerLong();
			}
		}
		long time1 = System.currentTimeMillis();
		for (int i=0; i<nIterations; ++i) {
			c2.reset();
			while (c2.hasNext()) {
				c2.fwd();
				c2.get().getIntegerLong();
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("Elapsed time (" + nIterations + " iterations):\n " + t1.getClass().getSimpleName() + ": " + (time1 - time0) + " ms\n UnsignedAnyBitType: " + (time2 - time1) + " ms");
	}

	@Test
	public void testGetBigInteger() {
		for (int i=0; i<1025; ++i) {
			UnsignedAnyBitType u = new UnsignedAnyBitType( i, 11);
			System.out.println(i + " : " + u.getBigInteger().toString());
			assertTrue("BigInteger fails", 0 == u.getBigInteger().compareTo(BigInteger.valueOf(i)));
		}
		System.out.println("Get BigInteger passed!");
	}
	
	@Test
	public void testSetBigInteger() {
		// Fill in an image with incrementing values
		Img<UnsignedAnyBitType> img = new UnsignedAnyBitType(0, 11).createSuitableNativeImg( new ArrayImgFactory<UnsignedAnyBitType>(), new long[]{1024, 2});
		long val = -1;
		for (UnsignedAnyBitType u : img) {
			u.setBigInteger( BigInteger.valueOf( ++val ) );
		}
		val = -1;
		for (UnsignedAnyBitType u : img) {
			assertTrue("BigInteger value is wrong", u.getBigInteger().longValue() == (++val) );
		}
		System.out.println("Set and get BigInteger passed!");
	}
	
	/*
	private final void setMethod1(final Img<UnsignedAnyBitType> img) {
		long val = -1;
		for (UnsignedAnyBitType u : img) {
			u.setBigInteger( BigInteger.valueOf( ++val ) );
		}
	}
	private final void setMethod2(final Img<UnsignedAnyBitType> img) {
		long val = -1;
		for (UnsignedAnyBitType u : img) {
			u.setBigInteger2( BigInteger.valueOf( ++val ) );
		}
	}

	@Test
	public void comparePerformanceOfSetBigIntegerMethods() {
		Img<UnsignedAnyBitType> img = new UnsignedAnyBitType(0, 16).createSuitableNativeImg( new ArrayImgFactory<UnsignedAnyBitType>(), new long[]{(long) Math.pow(2, 16)});

		long t0 = System.currentTimeMillis();
		for (int i=0; i<10; ++i) {
			setMethod1(img);
		}
		long t1 = System.currentTimeMillis();
		for (int i=0; i<10; ++i) {
			setMethod2(img);
		}
		
		long t2 = System.currentTimeMillis();
		System.out.println("setBigInteger method 1: " + (t1 - t0) + " ms\nsetBigInteger method 2: " + (t2 - t1) + " ms");
	}
	*/
	
	/*
	private final void getMethod1(final Img<UnsignedAnyBitType> img) {
		for (UnsignedAnyBitType u : img) {
			u.getBigInteger(); // with byte[]
		}
	}
	private final void getMethod2(final Img<UnsignedAnyBitType> img) {
		for (UnsignedAnyBitType u : img) {
			u.getBigInteger2(); // with char[]
		}
	}
	
	@Test
	public void comparePerformanceOfGetBigIntegerMethods() {
		Img<UnsignedAnyBitType> img = new UnsignedAnyBitType(0, 16).createSuitableNativeImg( new ArrayImgFactory<UnsignedAnyBitType>(), new long[]{(long) Math.pow(2, 16)});
		long val = -1;
		for (UnsignedAnyBitType u : img) {
			u.setBigInteger( BigInteger.valueOf( ++val ) );
		}
		
		long t0 = System.currentTimeMillis();
		for (int i=0; i<10; ++i) {
			getMethod1(img);
		}
		long t1 = System.currentTimeMillis();
		for (int i=0; i<10; ++i) {
			getMethod2(img); // twice as slow with String !!!
		}
		
		long t2 = System.currentTimeMillis();
		System.out.println("getBigInteger method 1: " + (t1 - t0) + " ms\ngetBigInteger method 2: " + (t2 - t1) + " ms");
	}
	*/
}
