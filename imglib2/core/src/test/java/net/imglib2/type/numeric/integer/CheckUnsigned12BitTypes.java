package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;

import org.junit.Test;

public class CheckUnsigned12BitTypes
{

	final long[] values = new long[100];
	{
		for (int k=0; k<values.length; ++k) {
			values[k] = (long)(Math.random() * 1023);
		}
	}
	
	@Test
	public void testUnsigned12BitType2() {
		test(new Unsigned12BitType2());
	}
	
	@Test
	public void testUnsignedBit64TypeWith12() {
		test(new UnsignedBit64Type(12));
	}
	
	@Test
	public void testUnsigned12BitType() {
		test(new Unsigned12BitType());
	}
	
	public <T extends AbstractIntegerType<T> & NativeType<T>> void test(final T u1) {
		long[] dims = new long[]{values.length};
		Img<T> img1 = u1.createSuitableNativeImg(new ArrayImgFactory<T>(), dims);
		Cursor<T> c1 = img1.cursor();
		
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
