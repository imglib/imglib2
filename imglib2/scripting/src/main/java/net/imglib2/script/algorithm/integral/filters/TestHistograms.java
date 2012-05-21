package net.imglib2.script.algorithm.integral.filters;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.integral.IntegralHistogram;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class TestHistograms {

	static public final void main(String[] arg) {
		new ImageJ();
		TestHistograms t = new TestHistograms();
		t.testFeatures();
		//t.testHistogramOf3x3Img();
	}
	
	public void testCorners() {
		long[] window = new long[]{10, 10, 10};
		int numDimensions = window.length;
		Point[] offsets = new Point[(int)Math.pow(2, numDimensions)];
		for (int i=0; i<offsets.length; ++i) offsets[i] = new Point(numDimensions);
		int d = 0;
		while (d < numDimensions) {
			final int flip = (int)Math.pow(2, d);
			int sign = -1;
			for (int i=0; i<offsets.length;) {
				offsets[i].setPosition(sign * window[d] / 2, d);
				++i;
				if (0 == i % flip) sign *= -1;
			}
			++d;
		}
		for (int i=0; i<offsets.length; ++i) {
			System.out.println(offsets[i].toString());
		}
	}
	
	public void testFeatures() {
		try {
			Img<UnsignedByteType> img = new ImgOpener().openImg("/home/albert/Desktop/t2/bridge-crop.tif");
			ImgLib.wrap(img, "Original").show();
			long[] window = new long[]{1, 1};
			HistogramFeatures<UnsignedByteType> features = new HistogramFeatures<UnsignedByteType>(img, 0, 255, 32, window);
			ImgLib.wrap(features, "Features for " + window[0] + "x" + window[1]).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 1 2 3 = 6
	 * 2 4 6 = 12
	 * 3 6 9 = 18
	 * 
	 * In the histogram, the bottom right should have:
	 * {1:1, 2:2, 3:2, 4:1, 5:0, 6:2, 7:0, 8:0, 9:0}
	 * 
	 */
	@Test
	public void testHistogramOf3x3Img() {
		Img<UnsignedByteType> img =
				new UnsignedByteType().createSuitableNativeImg(
						new ArrayImgFactory<UnsignedByteType>(),
						new long[]{3, 3});
		Cursor<UnsignedByteType> c = img.cursor();
		long[] position = new long[3];
		int sum = 0;
		while (c.hasNext()) {
			c.fwd();
			c.localize(position);
			int v = 1;
			for (int i=0; i<position.length; ++i) v *= position[i] + 1;
			c.get().set(v);
			sum += v;
		}
		assertTrue("Test image isn't right", sum == 36);
		//
		try {
			ImgLib.wrap(img, "image").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
		// Histogram
		Img<? extends IntegerType<?>> h = IntegralHistogram.create(img, 1, 9, 9);
		
		// Expected cummulative:
		final int[] expected = new int[]{1, 2, 2, 1, 0, 2, 0, 0, 1};
		
		RandomAccess<? extends IntegerType<?>> ra = h.randomAccess();
		StringBuilder sb = new StringBuilder("{");
		ra.setPosition(3, 0);
		ra.setPosition(3, 1);
		for (int i=0; i<9; ++i) {
			ra.setPosition(i, 2);
			if (i > 0) sb.append(',').append(' ');
			sb.append(i+1).append(':').append(ra.get().getInteger());
			assertTrue("Cummulative histogram isn't right", expected[i] == ra.get().getInteger());
		}
		sb.append('}');
		System.out.println("Cummulative Histogram: " + sb.toString());
		
		try {
			ImgLib.wrap((Img)h, "histogram").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
}
