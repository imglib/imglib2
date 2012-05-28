package script.imglib.test;

import net.imglib2.Cursor;
import net.imglib2.algorithm.integral.IntegralImg;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.integral.FastIntegralImg;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Util;

public class CompareIntegralImages
{
	static public final void main(String[] arg) {
		final String src = "/home/albert/lab/TEM/abd/microvolumes/Seg/180-220-int/180-220-int-00.tif";
		try {
			final Img<UnsignedByteType> img = new ImgOpener().openImg(src);
			
			// Test as byte
			test(img, new LongType());
			test(img, new IntType());
			
			// Test as short
			Img<UnsignedShortType> copy = new UnsignedShortType().createSuitableNativeImg(new ArrayImgFactory<UnsignedShortType>(), Util.intervalDimensions(img));
			Cursor<UnsignedByteType> c1 = img.cursor();
			Cursor<UnsignedShortType> c2 = copy.cursor();
			while (c1.hasNext()) {
				c2.next().setInteger(c1.next().get());
			}
			test(img, new LongType());
			test(img, new IntType());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public final <T extends IntegerType<T>, I extends IntegerType<I> & NativeType<I>> void test(
			final Img<T> img,
			final I integralType)
	{
		try {
			
			final Converter<T, I> converter = new Converter<T, I>() {
				@Override
				public final void convert(final T input, final I output) {
					output.setInteger(input.getIntegerLong());
				}
			};
			
			long t0 = System.currentTimeMillis();
			
			final IntegralImg<T, I> oa = new IntegralImg<T, I>(img, integralType.createVariable(), converter);
			oa.process();
			final Img<I> ii1 = oa.getResult();
			
			long t1 = System.currentTimeMillis();
			
			System.out.println("IntegralImg: " + (t1 - t0) + " ms");
			
			long t2 = System.currentTimeMillis();
			
			final FastIntegralImg<T, I> fii = new FastIntegralImg<T, I>(img, integralType.createVariable(), converter);
			
			long t3 = System.currentTimeMillis();
			
			System.out.println("FastIntegralImg: " + (t3 - t2) + " ms");
			
			final Cursor<I> c1 = ii1.cursor();
			final Cursor<I> c2 = fii.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				if (0 != c1.get().compareTo(c2.get())) {
					System.out.println("Different values at " + Util.printCoordinates(c1) + " :: " + c1.get() + ", " + c2.get());
					break;
				}
			}
			
			ImgLib.wrap(ii1).show();
			ImgLib.wrap(fii).show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
