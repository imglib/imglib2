package net.imglib2.type.numeric.integer;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedBit64Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.img.basictypeaccess.array.LongArray;

public class CheckUnsignedAnyBitType64 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		checkAccuracy();
		checkPerformance();
	}
	
	public static void checkPerformance() {
		final long[] dims = new long[]{1024, 1024};
		final UnsignedByteType ub = new UnsignedByteType();
		final Img<UnsignedByteType> imgub = ub.createSuitableNativeImg(new ArrayImgFactory<UnsignedByteType>(), dims);
		final UnsignedShortType us = new UnsignedShortType();
		final Img<UnsignedShortType> imgus = us.createSuitableNativeImg(new ArrayImgFactory<UnsignedShortType>(), dims);
		final Unsigned12BitType u12 = new Unsigned12BitType();
		final Img<Unsigned12BitType> imgu12 = u12.createSuitableNativeImg(new ArrayImgFactory<Unsigned12BitType>(), dims);
		final UnsignedBit64Type ua12 = new UnsignedBit64Type(12);
		final Img<UnsignedBit64Type> imgua12 = ua12.createSuitableNativeImg(new ArrayImgFactory<UnsignedBit64Type>(), dims);
		final UnsignedBit64Type ua16 = new UnsignedBit64Type(16);
		final Img<UnsignedBit64Type> imgua16 = ua16.createSuitableNativeImg(new ArrayImgFactory<UnsignedBit64Type>(), dims);
		final UnsignedBit64Type ua50 = new UnsignedBit64Type(50);
		final Img<UnsignedBit64Type> imgua50 = ua50.createSuitableNativeImg(new ArrayImgFactory<UnsignedBit64Type>(), dims);
		
		final int nIterations = 5;
		
		for (int i=0; i<nIterations; ++i) {
			timeSet(imgub);
			timeGet(imgub);
		}
		for (int i=0; i<nIterations; ++i) {
			timeSet(imgus);
			timeGet(imgus);
		}
		for (int i=0; i<nIterations; ++i) {
			timeSet(imgu12);
			timeGet(imgu12);
		}
		System.out.println("ua12");
		for (int i=0; i<nIterations; ++i) {
			timeSet(imgua12);
			timeGet(imgua12);
		}
		System.out.println("ua16");
		for (int i=0; i<nIterations; ++i) {
			timeSet(imgua16);
			timeGet(imgua16);
		}
		System.out.println("ua50");
		for (int i=0; i<nIterations; ++i) {
			timeSet(imgua50);
			timeGet(imgua50);
		}
	}
	
	private static final <T extends IntegerType<T>> void timeSet(final Img<T> img) {
		final Cursor<T> c = img.cursor();
		long t0 = System.currentTimeMillis();
		final long max = (long) img.firstElement().getMaxValue();
		while (c.hasNext()) {
			c.fwd();
			c.get().setInteger(max);
		}
		long t1 = System.currentTimeMillis();
		System.out.println(img.firstElement().getClass().getSimpleName() + ": SET took " + (t1 - t0));
	}

	private static final <T extends IntegerType<T>> void timeGet(final Img<T> img) {
		final Cursor<T> c = img.cursor();
		long t0 = System.currentTimeMillis();
		while (c.hasNext()) {
			c.fwd();
			long v = c.get().getIntegerLong();
		}
		long t1 = System.currentTimeMillis();
		System.out.println(img.firstElement().getClass().getSimpleName() + ": GET took " + (t1 - t0));
	}

	public static void checkAccuracy() {
		final int nBits = 7;
		final UnsignedBit64Type u = new UnsignedBit64Type(nBits);
		final Img<UnsignedBit64Type> img = u.createSuitableNativeImg(
				new ArrayImgFactory<UnsignedBit64Type>(), new long[]{(long) u.getMaxValue()});
		int i = 0;
		RandomAccess<UnsignedBit64Type> r = img.randomAccess();
		r.setPosition(9, 0);
		r.get().set(9);
		long[] array = ((ArrayImg<UnsignedBit64Type,LongArray>)img).update(null).getCurrentStorageArray();
		System.out.println("[0]: " + bits(array[0]));
		System.out.println("[1]: " + bits(array[1]));
		System.out.println("[2]: " + bits(array[2]));
		System.out.println("get 9: " + r.get().get());
		
		//System.out.println(bits(1023) + ", " + Long.toString(1023, 2) + ", " + Long.toString(1L << 63, 2));

		
		Cursor<UnsignedBit64Type> c = img.cursor();
		while (c.hasNext()) {
			c.fwd();
			c.get().set(i);
			long v = c.get().get();
			System.out.println(i + ": " + v + ", EQUAL: " + (i == v));
			++i;
		}
		System.out.println("Read after having written to all:");
		c.reset();
		i = 0;
		while (c.hasNext()) {
			c.fwd();
			long v = c.get().get();
			System.out.println(i + ": " + v + ", EQUAL: " + (i == v));
			++i;
		}
		c.reset();
		while (c.hasNext()) {
			c.next().inc();
		}
		System.out.println("Read +1 after having inc() all:");
		c.reset();
		i = 1;
		while (c.hasNext()) {
			c.fwd();
			long v = c.get().get();
			System.out.println(i + ": " + v + ", EQUAL: " + (i == v));
			++i;
		}
	}
	
	static private final String bits(final long v) {
		return Long.toString(v, 2);
	}

}
