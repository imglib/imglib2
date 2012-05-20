package net.imglib2.script.algorithm.integral.filters;


import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgFactory;
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
			final long[] window) {
		super(create(img, min, max, nBins, window));
	}

	private static final <R extends RealType<R> & NativeType<R>, P extends IntegerType<P> & NativeType<P>>
	Img<R> create(
			final Img<R> img,
			final double min,
			final double max,
			final int nBins,
			final long[] window)
	{
		final Img<P> integralHistogram = IntegralHistogram.create(img, min, max, nBins);
		final long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length -1; ++d) dims[0] = img.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;
		
		final Img<R> features = img.factory().create(dims, img.firstElement().createVariable());
				//new PlanarImgFactory<R>().createByteInstance(dims, 1);
		final RandomAccess<R> fr = features.randomAccess();
		
		final Histograms<P> h = new Histograms<P>(integralHistogram, window);
		while (h.hasNext()) {
			for (int d=0; d<h.numDimensions(); ++d) {
				fr.setPosition(h.getLongPosition(d), d);
			}
			// Compute features
			final Cursor<LongType> bins = h.get().cursor();
			double imgMin = 0;
			double imgMax = 0;
			double imgMean = 0;
			long nValues = 0;
			int ibin = 0;
			while (bins.hasNext()) {
				bins.fwd();
				final long count = bins.get().get();
				final double binVal = min + (ibin / (double)nBins) * (max - min);
				if (0 == imgMin && count > 0) {
					imgMin = binVal;
				}
				if (count > 0 && binVal > imgMax) {
					imgMax = binVal;
				}
				imgMean += binVal * count; // TODO may overflow for large images
				nValues += count; // isn't this the same as the window dimensions? No need to recompute TODO
				++ibin;
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
