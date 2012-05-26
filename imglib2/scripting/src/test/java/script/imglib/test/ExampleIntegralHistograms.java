package script.imglib.test;

import ij.ImageJ;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogramCursor;
import net.imglib2.script.algorithm.integral.histogram.IntegralHistogram;
import net.imglib2.script.algorithm.integral.histogram.LinearHistogram;
import net.imglib2.script.algorithm.integral.histogram.features.IHMax;
import net.imglib2.script.algorithm.integral.histogram.features.IHMean;
import net.imglib2.script.algorithm.integral.histogram.features.IHMedian;
import net.imglib2.script.algorithm.integral.histogram.features.IHMin;
import net.imglib2.script.algorithm.integral.histogram.features.IHStdDev;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

public class ExampleIntegralHistograms
{
	public ExampleIntegralHistograms() throws Exception {
		// Open an image
		String src = "/home/albert/lab/TEM/abd/microvolumes/Seg/180-220-int/180-220-int-00.tif";
		Img<UnsignedByteType> img = new ImgOpener().openImg(src);

		// Create an histogram
		int nBins = 128; // overkill for most purposes
		double min = 0;
		double max = 255;
		Histogram histogram = new LinearHistogram(nBins, img.numDimensions(), min, max);
		
		// Create an integral histogram with a storage type sufficient for the dimensions of img
		Img<IntType> integralHistogram = IntegralHistogram.create(img, histogram, new IntType());
		
		// Create cursors over the integral histogram, with different windows:
		long[] radius1 = new long[]{5, 5};
		IntegralHistogramCursor<IntType> hs1 = new IntegralHistogramCursor<IntType>(integralHistogram, histogram, radius1);
		
		long[] radius2 = new long[]{10, 10};
		IntegralHistogramCursor<IntType> hs2 = new IntegralHistogramCursor<IntType>(integralHistogram, histogram, radius2);
		
		// Define a set of features to use
		IHMin<DoubleType> ihMin = new IHMin<DoubleType>();
		IHMax<DoubleType> ihMax = new IHMax<DoubleType>();
		IHMean<DoubleType> ihMean = new IHMean<DoubleType>();
		IHMedian<DoubleType> ihMedian = new IHMedian<DoubleType>();
		IHStdDev<DoubleType> ihStdDev = new IHStdDev<DoubleType>();
		
		// Number of features to use: 2 * 5 + 2 + 1 (the difference of means and of medians, and the original image)
		int numFeatures = 2 * 5 + 2 + 1;
		
		// Create an output image to show us the features
		long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length-1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = numFeatures;
		Img<UnsignedByteType> featureStack =
				new UnsignedByteType().createSuitableNativeImg(new ArrayImgFactory<UnsignedByteType>(), dims);
		
		final RandomAccess<UnsignedByteType> ra = featureStack.randomAccess();
		final Cursor<UnsignedByteType> c = img.cursor();
		final int histDim = dims.length -1;
		
		while (c.hasNext()) {
			c.fwd();
			hs1.setPosition(c);
			hs2.setPosition(c);
			ra.setPosition(c);
			
			final Histogram h1 = hs1.get();
			final Histogram h2 = hs2.get();

			// The original image
			ra.setPosition(0, dims.length -1);
			ra.get().setReal(c.get().get());
			
			// Features for h1
			ra.move(1, histDim);
			ra.get().setReal(ihMin.get(h1));
			ra.move(1, histDim);
			ra.get().setReal(ihMax.get(h1));
			ra.move(1, histDim);
			final double mean1 = ihMean.get(h1);
			ra.get().setReal(mean1);
			ra.move(1, histDim);
			final double median1 = ihMedian.get(h1);
			ra.get().setReal(median1);
			ra.move(1, histDim);
			ra.get().setReal(ihStdDev.get(h1, mean1));
			
			// Features for h2
			ra.move(1, histDim);
			ra.get().setReal(ihMin.get(h2));
			ra.move(1, histDim);
			ra.get().setReal(ihMax.get(h2));
			ra.move(1, histDim);
			final double mean2 = ihMean.get(h2);
			ra.get().setReal(mean2);
			ra.move(1, histDim);
			final double median2 = ihMedian.get(h2);
			ra.get().setReal(median2);
			ra.move(1, histDim);
			ra.get().setReal(ihStdDev.get(h2, mean2));

			// Features combining both
			ra.move(1, histDim);
			ra.get().setReal(Math.abs(mean1 - mean2));
			ra.move(1, histDim);
			ra.get().setReal(Math.abs(median1 - median2));
		}
		
		new ImageJ();
		ImgLib.show(featureStack, "Feature Stack");
	}
	
	static public final void main(String[] arg) {
		try {
			new ExampleIntegralHistograms();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
