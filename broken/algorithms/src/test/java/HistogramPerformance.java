
 * @author Curtis Rueden

import net.imglib2.algorithm.histogram.Histogram;
import net.imglib2.algorithm.histogram.HistogramBinFactory;
import net.imglib2.algorithm.histogram.discrete.DiscreteIntHistogramBinFactory;
import net.imglib2.container.array.ArrayContainerFactory;
import net.imglib2.image.Image;
import net.imglib2.image.ImageFactory;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/**
 * TODO
 *
 */
public class HistogramPerformance<T extends IntegerType<T>> {

	private static final int[] DIMS = {1024, 1024, 3, 5};

	public static void main(String[] args) {
		System.out.println("== UNSIGNED 8-BIT ==");
		new HistogramPerformance<UnsignedByteType>().run(new UnsignedByteType(), 256);
		System.out.println("== UNSIGNED 16-BIT ==");
		new HistogramPerformance<UnsignedShortType>().run(new UnsignedShortType(), 65536);
	}

	public void run(T type, int max) {
		long start, end;

		System.out.print("Creating image... ");
		start = System.currentTimeMillis();
		final Image<T> img = createImage(type, max);
		end = System.currentTimeMillis();
		long createMillis = end - start;
		System.out.println(createMillis + " ms");

		// build histogram with Histogram implementation
		System.out.print("Building histogram... ");
		start = System.currentTimeMillis();
		final HistogramBinFactory<T> binFactory = 
			new DiscreteIntHistogramBinFactory<T>();
		final Histogram<T> histogram = new Histogram<T>(binFactory, img.createCursor());
		histogram.process();
		end = System.currentTimeMillis();
		long histMillis = end - start;
		System.out.println(histMillis + " ms");

		// build histogram through manual pixel counting
		System.out.print("Counting pixel values manually... ");
		start = System.currentTimeMillis();
		final int[] bins = new int[max];
		for (T t : img) {
			double value = t.getRealDouble();
			bins[(int) value]++;
		}
		end = System.currentTimeMillis();
		long manualMillis = end - start;
		System.out.println(manualMillis + " ms");

		// check results
		final T k = img.createType();
		for (int i = 0; i < max; i++) {
			k.setReal(i);
			final int actual = (int) histogram.getBin(k).getCount();
			final int expect = bins[i];
			if (actual != expect) {
				System.out.println("Error: for bin #" + i +
					": expected=" + expect + ", actual=" + actual);
			}
		}
	}

	private Image<T> createImage(T type, int max) {
		ImageFactory<T> imFactory =
			new ImageFactory<T>(type, new ArrayContainerFactory());
		Image<T> img = imFactory.createImage(DIMS);

		// populate image with random samples
		for (T t : img) t.setReal(max * Math.random());

		return img;
	}

}
