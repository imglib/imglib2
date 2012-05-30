package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;

public class TestUnsigned2And4BitTypes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		checkAccuracy(new Unsigned2BitType());
		checkAccuracy(new Unsigned4BitType());
	}

	@Test
	public void check2Bit() {
		checkAccuracy(new Unsigned2BitType());
	}
	
	@Test
	public void check4Bit() {
		checkAccuracy(new Unsigned4BitType());
	}
	
	private static <T extends IntegerType<T> & NativeType<T>> void checkAccuracy(final T u) {
		final Img<T> img = u.createSuitableNativeImg(
				new ArrayImgFactory<T>(), new long[]{(long) u.getMaxValue()});
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
}
