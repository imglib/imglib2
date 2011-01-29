package script.imglib.analysis;

import java.util.ArrayList;
import java.util.TreeMap;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.numeric.RealType;
import script.imglib.algorithm.fn.AlgorithmUtil;

/** An histogram of the image (or an image computed from an IFunction)
 * between its minimum and maximum values,
 * with as many bins as desired (defaults to 256 bins).
 * 
 * The number of bins is then the {@link Histogram#size()},
 * and the value of each bin (each element in this {@link ArrayList})
 * is an integer, which is the count of voxels whose value falls within
 * the bin.
 */
public class Histogram<T extends RealType<T>> extends TreeMap<Double,Integer>
{
	private static final long serialVersionUID = 1L;

	private final Image<T> img;
	private final double min, max, increment;

	public Histogram(final Object fn) throws Exception {
		this(fn, 256);
	}

	public Histogram(final Object fn, final Number nBins) throws Exception {
		this(fn, nBins.intValue());
	}

	@SuppressWarnings("unchecked")
	public Histogram(final Object fn, final int nBins) throws Exception {
		this.img = AlgorithmUtil.wrap(fn);
		Display<T> display = img.getDisplay();
		display.setMinMax();
		this.min = display.getMin();
		this.max = display.getMax();
		this.increment = process(img, nBins, min, max);
	}

	@SuppressWarnings("unchecked")
	public Histogram(final Object fn, final int nBins, final double min, final double max) throws Exception {
		this.img = AlgorithmUtil.wrap(fn);
		this.min = min;
		this.max = max;
		this.increment = process(img, nBins, min, max);
	}

	private final double process(final Image<T> img, final int nBins, final double min, final double max) throws Exception {
		final double range = max - min;
		final double increment = range / nBins;
		final int[] bins = new int[nBins];
		//
		if (0.0 == range) {
			bins[0] = img.size();
		} else {
			final Cursor<T> c = img.createCursor();
			// zero-based:
			final int N = nBins -1;
			// Analyze the image
			while (c.hasNext()) {
				c.fwd();
				int v = (int)(((c.getType().getRealDouble() - min) / range) * N);
				if (v < 0) v = 0;
				else if (v > N) v = N;
				bins[v] += 1;
			}
			c.close();
		}
		// Put the contents of the bins into this ArrayList:
		for (int i=0; i<bins.length; i++) {
			this.put( min + i * increment, bins[i] );
		}
		
		return increment;
	}
	
	public double getMin() { return min; }
	public double getMax() { return max; }
	public double getIncrement() { return increment; }
	public Image<T> getImage() { return img; }
}