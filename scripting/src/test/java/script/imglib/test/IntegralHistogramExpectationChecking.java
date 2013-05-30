package script.imglib.test;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogram;
import net.imglib2.script.algorithm.integral.histogram.LinearHistogram;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedAnyBitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

import org.junit.Test;

public class IntegralHistogramExpectationChecking {
	
	static public final void main(String[] arg) {
		IntegralHistogramExpectationChecking I = new IntegralHistogramExpectationChecking();
		//I.testIntegralHistogram2dB();
		//I.testIntegralHistogram1d();
		I.testIntegralHistogram2d();
		//I.testUnsignedAnyBitImg();
	}

	@Test
	public void testIntegralHistogram1d() {
		// Create a 1d image with values 0,0,1,1,2,2,3,3,4,4,5,5,6,6,7,7
		Img<UnsignedByteType> img =
				new UnsignedByteType().createSuitableNativeImg(
						new ArrayImgFactory<UnsignedByteType>(), new long[]{16});
		Cursor<UnsignedByteType> c = img.cursor();
		int i = 0;
		int next = 0;
		while (c.hasNext()) {
			c.fwd();
			c.get().set(next);
			++i;
			if (0 == i % 2) ++next;
		}
		//
		LinearHistogram<UnsignedByteType> lh = new LinearHistogram<UnsignedByteType>(16, 1, new UnsignedByteType(0), new UnsignedByteType(7));
		Img<? extends RealType<?>> ih = IntegralHistogram.create(img, lh);
		
		new ImageJ();
		try {
			ImgLib.wrap((Img)ih, "histogram").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
		
		RandomAccess<? extends RealType<?>> ra = ih.randomAccess();
		long[] position = new long[2];
		StringBuilder sb = new StringBuilder();
		for (int k=0; k<ih.dimension(0); ++k) {
			position[0] = k;
			sb.append(k + " :: {");
			for (int h=0; h<ih.dimension(1); ++h) {
				position[1] = h;
				ra.setPosition(position);
				System.out.println(ra.get().getRealDouble());
				sb.append(h).append(':').append((long)ra.get().getRealDouble()).append("; ");
			}
			sb.append("}\n");
		}
		System.out.println(sb.toString());
	}
	
	@Test
	public void testIntegralHistogram2d() {
		// Create a 2d image with values 0-9 in every dimension, so bottom right is 81.
		Img<UnsignedByteType> img =
				new UnsignedByteType().createSuitableNativeImg(
						new ArrayImgFactory<UnsignedByteType>(),
						new long[]{10, 10});
		long[] p = new long[2];
		Cursor<UnsignedByteType> c = img.cursor();
		while (c.hasNext()) {
			c.fwd();
			c.localize(p);
			c.get().setInteger(p[0] * p[1]);
		}
		// Create integral histogram with 10 bins
		LinearHistogram<UnsignedByteType> lh = new LinearHistogram<UnsignedByteType>(10, 2, new UnsignedByteType(0), new UnsignedByteType(81));
		Img<? extends RealType<?>> ih = IntegralHistogram.create(img, lh);
		new ImageJ();
		try {
			ImgLib.wrap((Img)ih, "histogram").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
	
	public void testIntegralHistogram2dB() {
		try {
			Img<UnsignedByteType> img = new ImgOpener().openImg("/home/albert/Desktop/t2/bridge-crop.tif");
			// Integral histogram with 10 bins
			LinearHistogram<UnsignedByteType> lh = new LinearHistogram<UnsignedByteType>(10, 2, new UnsignedByteType(0), new UnsignedByteType(255));
			Img<? extends RealType<?>> ih = IntegralHistogram.create(img, lh);
			new ImageJ();
			try {
				ImgLib.wrap((Img)ih, "histogram").show();
			} catch (ImgLibException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testUnsignedAnyBitImg() {
		try {
			Img<UnsignedByteType> img1 = new ImgOpener().openImg("/home/albert/Desktop/t2/bridge-crop.tif");
			
			Img<UnsignedAnyBitType> img2 = new UnsignedAnyBitType(10).createSuitableNativeImg(new ArrayImgFactory<UnsignedAnyBitType>(), Util.intervalDimensions(img1));

			Cursor<UnsignedByteType> c1 = img1.cursor();
			Cursor<UnsignedAnyBitType> c2 = img2.cursor();
			
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c2.get().set(c1.get().getIntegerLong());
			}
			
			new ImageJ();
			ImgLib.wrap(img2, "copy").show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
