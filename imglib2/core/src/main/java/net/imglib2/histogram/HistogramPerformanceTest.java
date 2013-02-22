package net.imglib2.histogram;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

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

/**
 * TODO
 * 
 * @author Curtis Rueden
 */
public class HistogramPerformanceTest<T extends IntegerType<T> & NativeType<T>>
{

	private static final int[] DIMS = { 1024, 1024, 3, 5 };

	public static void main(String[] args) {
		System.out.println("== UNSIGNED 8-BIT ==");
		new HistogramPerformanceTest<UnsignedByteType>().run(
			new UnsignedByteType(),
			256);
		System.out.println("== UNSIGNED 16-BIT ==");
		new HistogramPerformanceTest<UnsignedShortType>().run(
			new UnsignedShortType(),
			65536);
	}

	public void run(T type, int max) {
		long start, end;

		System.out.print("Creating image... ");
		start = System.currentTimeMillis();
		final Img<T> img = createImage(type, max);
		end = System.currentTimeMillis();
		long createMillis = end - start;
		System.out.println(createMillis + " ms");

		// build histogram with Histogram implementation
		System.out.print("Building histogram... ");
		start = System.currentTimeMillis();
		BinnedDistribution distrib =
			new BinnedDistribution(1, new double[] { 0 }, new double[] { max },
				new long[] { max });
		double[] value = new double[1];
		Cursor<T> cursor = img.cursor();
		while (cursor.hasNext()) {
			value[0] = cursor.next().getRealDouble();
			distrib.countValue(value);
		}
		end = System.currentTimeMillis();
		long histMillis = end - start;
		System.out.println(histMillis + " ms");

		// build histogram through manual pixel counting
		System.out.print("Counting pixel values manually... ");
		start = System.currentTimeMillis();
		final int[] bins = new int[max];
		for (T t : img) {
			double v = t.getRealDouble();
			bins[(int) v]++;
		}
		end = System.currentTimeMillis();
		long manualMillis = end - start;
		System.out.println(manualMillis + " ms");

		// check results
		// final T k = img.firstElement();
		double[] val = new double[1];
		for (int i = 0; i < max; i++) {
			// k.setReal(i);
			val[0] = i;
			final long actual = distrib.numValues(val);
			final int expect = bins[i];
			if (actual != expect) {
				System.out.println("Error: for bin #" + i + ": expected=" + expect +
					", actual=" + actual);
			}
		}
	}

	private Img<T> createImage(T type, int max) {
		ImgFactory<T> imFactory = new ArrayImgFactory<T>();
		Img<T> img = imFactory.create(DIMS, type);

		// populate image with random samples
		for (T t : img)
			t.setReal(max * Math.random());

		return img;
	}

}
