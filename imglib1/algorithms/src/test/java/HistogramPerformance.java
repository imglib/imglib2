/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBinFactory;
import mpicbg.imglib.algorithm.histogram.discrete.DiscreteIntHistogramBinFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.IntegerType;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;

/**
 * TODO
 *
 * @author Curtis Rueden
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
