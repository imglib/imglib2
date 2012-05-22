package net.imglib2.script.algorithm.integral.filters;


import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.algorithm.integral.IntegralHistogram;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

public class HistogramFeatures<T extends RealType<T> & NativeType<T>> extends ImgProxy<T>
{
	static public final int NUM_FEATURES = 3;
	
	public HistogramFeatures(
			final Img<T> img,
			final double min,
			final double max,
			final int nBins,
			final long[] radius) {
		super(create(img, min, max, nBins, radius));
	}

	private static final <R extends RealType<R> & NativeType<R>, P extends IntegerType<P> & NativeType<P>>
	Img<R> create(
			final Img<R> img,
			final double min,
			final double max,
			final int nBins,
			final long[] radius)
	{
		final Img<P> integralHistogram = IntegralHistogram.create(img, min, max, nBins);
		
		final long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length -1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;
		
		final Img<R> features = img.factory().create(dims, img.firstElement().createVariable());
		final RandomAccess<R> fr = features.randomAccess();
		
		// One histogram per pixel position, representing the histogram of the window centered at that pixel
		final Histograms<P> h = new Histograms<P>(integralHistogram, radius);
		
		final double K = nBins - 1;
		final double range = max - min;
		
		while (h.hasNext()) {
			h.fwd();
			for (int d=0; d<h.numDimensions(); ++d) {
				fr.setPosition(h.getLongPosition(d), d);
			}
			// Compute features
			final long[] bins = h.get();
				
			double imgMin = 0;
			double imgMax = 0;
			double imgMean = 0;
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
				imgMean += binValue * binCount; // TODO may overflow for large images
				nValues += binCount; // isn't this the same as the window dimensions? No need to recompute TODO
			}
			imgMean /= nValues;
			
			// Store
			fr.setPosition(0, fr.numDimensions() -1);
			fr.get().setReal(imgMin);
			fr.setPosition(1, fr.numDimensions() -1);
			fr.get().setReal(imgMax);
			fr.setPosition(2, fr.numDimensions() -1);
			fr.get().setReal(imgMean);
		}

		return features;
	}
}
