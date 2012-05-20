package net.imglib2.script.algorithm.integral.filters;

import ij.ImageJ;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.ImgLib;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class TestHistograms {

	static public final void main(String[] arg) {
		new ImageJ();
		new TestHistograms().testFeatures();
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
			long[] window = new long[]{50, 50};
			HistogramFeatures<UnsignedByteType> features = new HistogramFeatures<UnsignedByteType>(img, 0, 255, 32, window);
			ImgLib.wrap(features).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
