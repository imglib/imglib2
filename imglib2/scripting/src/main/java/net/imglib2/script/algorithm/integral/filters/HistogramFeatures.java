package net.imglib2.script.algorithm.integral.filters;


import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.algorithm.integral.IntegralHistogram;
import net.imglib2.script.algorithm.integral.features.IHMax;
import net.imglib2.script.algorithm.integral.features.IHMean;
import net.imglib2.script.algorithm.integral.features.IHMedian;
import net.imglib2.script.algorithm.integral.features.IHMin;
import net.imglib2.script.algorithm.integral.features.IHStdDev;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;

public class HistogramFeatures<T extends RealType<T> & NativeType<T>, P extends IntegerType<P> & NativeType<P>> extends ImgProxy<T>
{
	static public final int NUM_FEATURES = 5;
	
	public HistogramFeatures(
			final Img<T> img,
			final Img<P> integralHistogram,
			final double min,
			final double max,
			final int nBins,
			final long[] radius) {
		super(create(img, integralHistogram, min, max, nBins, radius));
	}

	private static final <R extends RealType<R> & NativeType<R>, P extends IntegerType<P> & NativeType<P>>
	Img<R> create(
			final Img<R> img,
			final Img<P> integralHistogram,
			final double min,
			final double max,
			final int nBins,
			final long[] radius)
	{	
		final long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length -1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;
		
		final Img<R> features = img.factory().create(dims, img.firstElement().createVariable());
		final RandomAccess<R> fr = features.randomAccess();
		
		// One histogram per pixel position, representing the histogram of the window centered at that pixel
		final Histograms<P> h = new Histograms<P>(integralHistogram, radius);
		
		final double K = nBins - 1;
		final double range = max - min;
		
		final int lastDimension = fr.numDimensions() -1;
		
		final IHMin<DoubleType> ihMin = new IHMin<DoubleType>();
		final IHMax<DoubleType> ihMax = new IHMax<DoubleType>();
		final IHMean<DoubleType> ihMean = new IHMean<DoubleType>();
		final IHMedian<DoubleType> ihMedian = new IHMedian<DoubleType>();
		final IHStdDev<DoubleType> ihStdDev = new IHStdDev<DoubleType>();
		final double[] binValues = new double[nBins];
		for (int i=0; i<nBins; ++i) {
			binValues[i] = min + (i / K) * range;
		}
		
		while (h.hasNext()) {
			h.fwd();
			for (int d=0; d<h.numDimensions(); ++d) {
				fr.setPosition(h.getLongPosition(d), d);
			}
			// Compute features
			final long[] bins = h.get();

			/*
			double imgMin = 0;
			double imgMax = 0;
			double imgMean = 0;
			double imgMedian = 0;
			long nValues = 0;
			
			for (int i=0; i<bins.length; ++i) {
				// binValue is the value that the bin has in the range from min to max
				final double binValue = min + (i / K) * range;
				// binCount is the number stored in each bin
				final long binCount = bins[i];
				// Find the minimum value of region defined by radius by finding the minimum value of the histogram
				if (0 == imgMin && binCount > 0) {
					// this condition happens only once
					imgMin = binValue;
				}
				// Find the maximum value
				if (binCount > 0 && binValue > imgMax) {
					imgMax = binValue;
				}
				// Estimate mean, first by summing up all values
				imgMean += binValue * binCount; // TODO may overflow for large radius
				// Cumulative pixel count
				nValues += binCount;
			}
			// .. then by dividing by the total
			imgMean /= nValues;

			// Can't compute from radius because it would fail at the edges of the image
			final long halfNValues = nValues / 2;
			
			nValues = 0;
			for (int i=0; i<bins.length; ++i) {
				// Find the approximate median value
				nValues += bins[i];
				if (nValues > halfNValues) {
					imgMedian = (min + (i / K) * range) + (range / (nBins -1)) / 2;
					break;
				}
			}
			*/

			long nPixels = 0;
			for (int i=0; i<nBins; ++i) {
				nPixels += bins[i];
			}
			
			double imgMin = ihMin.get(min, max, bins, binValues, nPixels);
			double imgMax = ihMax.get(min, max, bins, binValues, nPixels);
			double imgMean = ihMean.get(min, max, bins, binValues, nPixels);
			double imgMedian = ihMedian.get(min, max, bins, binValues, nPixels);
			double imgStdDev = ihStdDev.get(min, max, bins, binValues, nPixels, imgMedian);

			// Store
			fr.setPosition(0, lastDimension);
			fr.get().setReal(imgMin);
			fr.setPosition(1, lastDimension);
			fr.get().setReal(imgMax);
			fr.setPosition(2, lastDimension);
			fr.get().setReal(imgMean);
			fr.setPosition(3, lastDimension);
			fr.get().setReal(imgMedian);
			fr.setPosition(4, lastDimension);
			fr.get().setReal(imgStdDev);
		}

		return features;
	}
}
