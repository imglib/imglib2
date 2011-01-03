/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Curtis Rueden
 *
 */

import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBinMapper;
import mpicbg.imglib.algorithm.histogram.IntBinMapper;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.IntegerType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;

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

		final T zeroType = type.createVariable();
		final T maxType = type.createVariable();
		zeroType.setZero();
		maxType.setInteger(0);
		
		System.out.print("Creating image... ");
		start = System.currentTimeMillis();
		final Image<T> img = createImage(type, max);
		end = System.currentTimeMillis();
		long createMillis = end - start;
		System.out.println(createMillis + " ms");

		// build histogram with Histogram implementation
		System.out.print("Building histogram... ");
		start = System.currentTimeMillis();
		final HistogramBinMapper<T> binMapper = new IntBinMapper<T>(zeroType, maxType);
		final Histogram<T> histogram = new Histogram<T>(binMapper, img.createCursor());
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
			final int actual = (int) histogram.getHistogram()[i];
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
