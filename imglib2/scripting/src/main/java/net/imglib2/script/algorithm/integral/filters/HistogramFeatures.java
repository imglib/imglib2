package net.imglib2.script.algorithm.integral.filters;


import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.algorithm.integral.IntegralHistogram;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.util.Util;

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
		
		try {
			ImgLib.wrap(integralHistogram, "integral histogram").show();
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
		
		final long[] dims = new long[img.numDimensions() + 1];
		for (int d=0; d<dims.length -1; ++d) dims[d] = img.dimension(d);
		dims[dims.length -1] = NUM_FEATURES;

		System.out.println("dimensions of input img:              " + Util.printCoordinates(Util.intervalDimensions(img)));
		System.out.println("dimensions of features img:           " + Util.printCoordinates(dims));
		System.out.println("dimensions of integral histogram img: " + Util.printCoordinates(Util.intervalDimensions(integralHistogram)));
		
		final Img<R> features = img.factory().create(dims, img.firstElement().createVariable());
		final RandomAccess<R> fr = features.randomAccess();
		
		final Histograms<P> h = new Histograms<P>(integralHistogram, window);
		
		while (h.hasNext()) {
			h.fwd();
			for (int d=0; d<h.numDimensions(); ++d) {
				fr.setPosition(h.getLongPosition(d), d);
			}
			// Compute features
			final Cursor<LongType> bins = h.get().cursor();
			double imgMin = 0;
			double imgMax = 0;
			double imgMean = 0;
			long nValues = 0;
			while (bins.hasNext()) {
				bins.fwd();
				final long count = bins.get().get();
				final double binVal = min + (bins.getLongPosition(0) / (double)nBins) * (max - min);
				if (0 == imgMin && count > 0) {
					imgMin = binVal;
				}
				if (count > 0 && binVal > imgMax) {
					imgMax = binVal;
				}
				imgMean += binVal * count; // TODO may overflow for large images
				nValues += count; // isn't this the same as the window dimensions? No need to recompute TODO
			}
			imgMean /= nValues;
			//System.out.println("mean: " + imgMean + " at " + new Point(fr).toString());
			// Store
			try {
			fr.setPosition(0, fr.numDimensions() -1);
			fr.get().setReal(imgMin);
			fr.setPosition(1, fr.numDimensions() -1);
			fr.get().setReal(imgMax);
			fr.setPosition(2, fr.numDimensions() -1);
			fr.get().setReal(imgMean);
			} catch (ArrayIndexOutOfBoundsException a) {
				System.out.println("FAILED at: mean: " + imgMean + " at " + new Point(fr).toString());
				a.printStackTrace();
				break;
			}
		}

		
		return features;
	}

}
