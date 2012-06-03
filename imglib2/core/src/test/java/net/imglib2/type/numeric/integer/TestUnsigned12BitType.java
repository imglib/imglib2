package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.Test;

public class TestUnsigned12BitType {

	@Test
	public void test() {
		Unsigned12BitType u1 = new Unsigned12BitType();
		long[] dims = new long[]{5};
		Img<Unsigned12BitType> img1 = u1.createSuitableNativeImg(new ArrayImgFactory<Unsigned12BitType>(), dims);
		Cursor<Unsigned12BitType> c1 = img1.cursor();
		
		long[] values = new long[(int)img1.size()];
		
		for (int k=0; k<values.length; ++k) {
			values[k] = (long)(Math.random() * 1023);
		}
		
		int k = 0;
		
		while (c1.hasNext()) {
			c1.next().setInteger(values[k]);
			// All fine:
			assertTrue(c1.get().getIntegerLong() == values[k]);
			++k;
		}

		c1.reset();
		k = 0;
		while (c1.hasNext()) {
			// Fails:
			assertTrue(c1.next().getIntegerLong() == values[k]);
			++k;
		}
	}
}
