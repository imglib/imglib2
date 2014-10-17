package script.imglib.test;

import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogram;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogramCursor;
import net.imglib2.script.algorithm.integral.histogram.LinearHistogram;
import net.imglib2.script.algorithm.integral.histogram.features.IHMax;
import net.imglib2.script.algorithm.integral.histogram.features.IHMean;
import net.imglib2.script.algorithm.integral.histogram.features.IHMedian;
import net.imglib2.script.algorithm.integral.histogram.features.IHMin;
import net.imglib2.script.algorithm.integral.histogram.features.IHStdDev;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;

public class ExampleIntegralHistograms<T extends RealType<T> & NativeType<T>>
{
	public ExampleIntegralHistograms(final Img<T> img, final T min, final T max, final long[] radius1, final long[] radius2) throws Exception {
		// Create an histogram
		int nBins = 8; // over 128 is overkill for most purposes

		Histogram<T> histogram = new LinearHistogram<T>(nBins, img.numDimensions(), min, max);
		
		// Create an integral histogram with a storage type sufficient for the dimensions of img
		Img<IntType> integralHistogram = IntegralHistogram.create(img, histogram, new IntType());
		
		// Create cursors over the integral histogram, with different windows:
		
		final IntegralHistogramCursor<IntType, T> hs1 = new IntegralHistogramCursor<IntType, T>(integralHistogram, histogram, radius1);
		final IntegralHistogramCursor<IntType, T> hs2 = new IntegralHistogramCursor<IntType, T>(integralHistogram, histogram, radius2);
		
		// Define a set of features to use
		final IHMin<T> ihMin = new IHMin<T>();
		final IHMax<T> ihMax = new IHMax<T>();
		final IHMean<T, DoubleType> ihMean = new IHMean<T, DoubleType>(new DoubleType());
		final IHMedian<T> ihMedian = new IHMedian<T>();
		final IHStdDev<T, DoubleType> ihStdDev = new IHStdDev<T, DoubleType>(new DoubleType());
		
		// Number of features to use: 2 * 5 + 2 + 1 (the difference of means and of medians, and the original image)
		int numFeatures = 2 * 5 + 2 + 1;
		
		// Create an output image to show us the features
		long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length-1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = numFeatures;
		Img<T> featureStack = img.factory().create(dims, min.createVariable());
		
		System.out.println("Created stack of features with dimensions: " + Util.printCoordinates(dims));
		
		final RandomAccess<T> ra = featureStack.randomAccess();
		final Cursor<T> c = img.cursor();
		final int histDim = dims.length -1;
		
		final T mean1 = min.createVariable(),
				mean2 = min.createVariable(),
				median1 = min.createVariable(),
				median2 = min.createVariable();
		
		while (c.hasNext()) {
			c.fwd();
			hs1.setPosition(c);
			hs2.setPosition(c);
			ra.setPosition(c);
			
			final Histogram<T> h1 = hs1.get();
			final Histogram<T> h2 = hs2.get();
			
			if (101 == c.getLongPosition(0) && 65 == c.getLongPosition(1) && 26 == c.getLongPosition(2)) {
				System.out.println(Util.printCoordinates(h1.bins));
			}

			// The original image
			ra.setPosition(0, dims.length -1);
			ra.get().set(c.get());
			
			// Features for h1
			ra.move(1, histDim);
			ihMin.compute(h1, ra.get());
			ra.move(1, histDim);
			ihMax.compute(h1, ra.get());
			ra.move(1, histDim);
			ihMean.compute(h1, ra.get());
			mean1.set(ra.get());
			ra.move(1, histDim);
			ihMedian.compute(h1, ra.get());
			median1.set(ra.get());
			ra.move(1, histDim);
			ihStdDev.compute(h1, mean1, ra.get());
			
			// Features for h2
			ra.move(1, histDim);
			ihMin.compute(h2, ra.get());
			ra.move(1, histDim);
			ihMax.compute(h2, ra.get());
			ra.move(1, histDim);
			ihMean.compute(h2, ra.get());
			mean2.set(ra.get());
			ra.move(1, histDim);
			ihMedian.compute(h2, ra.get());
			median2.set(ra.get());
			ra.move(1, histDim);
			ihStdDev.compute(h2, mean2, ra.get());

			// Features combining both
			ra.move(1, histDim);
			ra.get().setReal(Math.abs(mean1.getRealDouble() - mean2.getRealDouble()));
			ra.move(1, histDim);
			ra.get().setReal(Math.abs(median1.getRealDouble() - median2.getRealDouble()));
		}
		
		new ImageJ();
		if (dims.length > 3) {
			ImagePlusImg<T, ?> copy = new ImagePlusImgFactory<T>().create(featureStack, featureStack.firstElement().createVariable());
			Cursor<T> c1 = copy.cursor();
			Cursor<T> c2 = featureStack.cursor();
			while (c1.hasNext()) {
				c1.fwd();
				c2.fwd();
				c1.get().set(c2.get());
			}
			ImagePlus imp = copy.getImagePlus();
			//imp.setOpenAsHyperStack(true);
			System.out.println("creating composite image " + imp.getNSlices() + " slices, " + imp.getNFrames() + " frames");
			imp.show();
		} else {
			ImgLib.wrap(featureStack, "Feature Stack").show();
		}
		
	}
	
	@SuppressWarnings("unused")
	static public final void main(String[] arg) {
		try {
			// Open an image
			String src;
			long[] radius1, radius2;
			switch (1) {
			case 1:
				src = "/home/albert/lab/TEM/abd/microvolumes/Seg/180-220-int/180-220-int-00.tif"; // 2d
				radius1 = new long[]{5, 5};
				radius2 = new long[]{10, 10};
				break;
			case 2:
				src = "/home/albert/Desktop/t2/bridge.tif"; // 2d
				radius1 = new long[]{5, 5};
				radius2 = new long[]{10, 10};
				break;
			case 3:
				src = "/home/albert/Desktop/t2/bat-cochlea-volume.tif"; // 3d
				radius1 = new long[]{5, 5, 5};
				radius2 = new long[]{10, 10, 10};
				break;
			case 4:
			default:
				src = "/home/albert/Desktop/t2/bat-cochlea-volume-s26.tif"; // 2d binary, 0 or 255
				radius1 = new long[]{5, 5};
				radius2 = new long[]{10, 10};
				break;
			}
			Img<UnsignedByteType> img = new ImgOpener().openImg(src);
			UnsignedByteType min = new UnsignedByteType(0);
			UnsignedByteType max = new UnsignedByteType(255);
			new ExampleIntegralHistograms<UnsignedByteType>(img, min, max, radius1, radius2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
