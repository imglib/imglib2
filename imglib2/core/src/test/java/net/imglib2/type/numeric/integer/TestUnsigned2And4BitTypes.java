package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;

public class TestUnsigned2And4BitTypes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		checkAccuracy(new Unsigned2BitType());
		checkAccuracy(new Unsigned4BitType());
		checkMaxDimensions(new Unsigned2BitType());
		checkMaxDimensions(new Unsigned4BitType());
	}

	@Test
	public void check2Bit() {
		checkAccuracy(new Unsigned2BitType());
		checkAccuracy2(new Unsigned2BitType());
	}
	
	@Test
	public void check4Bit() {
		checkAccuracy(new Unsigned4BitType());
		checkAccuracy2(new Unsigned4BitType());
	}
	
	/*
	@Test
	public void check2BitMaxImgSize() {
		checkMaxDimensions(new Unsigned2BitType());
	}
	
	@Test
	public void check4BitMaxImgSize() {
		checkMaxDimensions(new Unsigned4BitType());
	}
	*/
	
	private static <T extends IntegerType<T> & NativeType<T>> void checkMaxDimensions(final T u) {
		// VERY IMPORTANT: 64L, notice the L to make it a long, otherwise the math is done with 32-bit int.
		final long[] dims = new long[]{Integer.MAX_VALUE * (64L / u.getBitsPerPixel())};
		System.out.println(Integer.MAX_VALUE + " :: " + Integer.MAX_VALUE * (64L / u.getBitsPerPixel()));
		final Img<T> img = u.createSuitableNativeImg(
				new ArrayImgFactory<T>(), dims);
		System.out.println("Created Img<" + u.getClass().getSimpleName() + "> of size " + img.size()
				+ ", which is " + (Integer.MAX_VALUE / img.size()) + " * Integer.MAX_VALUE");
	}
	
	private static <T extends IntegerType<T> & NativeType<T>> void checkAccuracy(final T u) {
		final Img<T> img = u.createSuitableNativeImg(
				new ArrayImgFactory<T>(), new long[]{(long) u.getMaxValue()});
		
		long[] array = ((ArrayImg<T,LongArray>)img).update(null).getCurrentStorageArray();
		System.out.println(u.getClass().getSimpleName() + ": stored in long[" + array.length + "]");
		
		final Cursor<T> c = img.cursor();
		long i = 0;
		while (c.hasNext()) {
			c.next().setInteger(i);
			assertTrue(u.getClass().getSimpleName() + "; i: " + i + ", val: " + c.get().getIntegerLong(), c.get().getIntegerLong() == i);
			++i;
		}
		c.reset();
		i = 0;
		while (c.hasNext()) {
			assertTrue(c.next().getIntegerLong() == i);
			++i;
		}
		c.reset();
		while (c.hasNext()) {
			c.next().inc();
		}
		c.reset();
		i = 1;
		while (c.hasNext()) {
			assertTrue(c.next().getIntegerLong() == i);
			++i;
		}
		System.out.println("OK " + u.getClass().getSimpleName());
	}
	private static <T extends IntegerType<T> & NativeType<T>> void checkAccuracy2(final T u) {
		final Img<T> img = u.createSuitableNativeImg(
				new ArrayImgFactory<T>(), new long[]{1000});
		
		long[] array = ((ArrayImg<T,LongArray>)img).update(null).getCurrentStorageArray();
		System.out.println(u.getClass().getSimpleName() + ": stored in long[" + array.length + "]");
		
		long[] values = new long[1000];
		long max = (long) u.getMaxValue();
		for (int i=0; i<1000; ++i) {
			values[i] = i % max;
		}
		
		final Cursor<T> c = img.cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next().setInteger(values[i]);
			assertTrue(u.getClass().getSimpleName() + "; values[i]: " + values[i] + ", val: " + c.get().getIntegerLong(), c.get().getIntegerLong() == values[i]);
			++i;
		}
		c.reset();
		i = 0;
		while (c.hasNext()) {
			assertTrue(c.next().getIntegerLong() == values[i]);
			++i;
		}
		c.reset();
		while (c.hasNext()) {
			c.next().inc();
		}
		c.reset();
		i = 0;
		while (c.hasNext()) {
			assertTrue(c.next().getIntegerLong() == values[i] + 1);
			++i;
		}
		System.out.println("OK " + u.getClass().getSimpleName());
	}

}
