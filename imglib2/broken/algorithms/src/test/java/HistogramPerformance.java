/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

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
