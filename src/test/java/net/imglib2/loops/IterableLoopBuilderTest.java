/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
 * #L%
 */
package net.imglib2.loops;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.parallel.Parallelization;
import net.imglib2.test.ImgLib2Assert;
import net.imglib2.type.numeric.integer.IntType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link IterableLoopBuilder}.
 */
public class IterableLoopBuilderTest {

	@Test
	public void testSingleImage() {
		// Test IterableLoopBuilder by setting all pixels in a single image to one.
		Img<IntType> image = ArrayImgs.ints(2,2);
		IterableInterval<IntType> iterableInterval = image;
		IterableLoopBuilder.setImages(iterableInterval).forEachPixel(pixel -> pixel.setOne());
		Img<IntType> expected = ArrayImgs.ints(new int[]{1, 1, 1, 1}, 2, 2);
		ImgLib2Assert.assertImageEquals(expected, image);
	}

	@Test
	public void testTwoImages() {
		// Test IterableLoopBuilder by using it to copy the content of a tiny image.
		Img<IntType> source = ArrayImgs.ints(new int[]{1, 2, 3, 4}, 2,2);
		Img<IntType> target = ArrayImgs.ints(2,2);
		IterableInterval<IntType> iterableIntervalSource = source;
		RandomAccessible<IntType> randomAccessibleTarget = target;
		IterableLoopBuilder.setImages(iterableIntervalSource, randomAccessibleTarget).forEachPixel((s, t) -> t.set(s));
		ImgLib2Assert.assertImageEquals(source, target);
	}

	@Test
	public void testThreeImages() {
		// Test IterableLoopBuilder by using it to calculate the difference between to tiny images.
		Img<IntType> target = ArrayImgs.ints(2, 2);
		IterableInterval<IntType> targetIterable = target;
		RandomAccessible<IntType> imageA = ArrayImgs.ints(new int[]{1, 1, 0, 0}, 2, 2);
		RandomAccessible<IntType> imageB = ArrayImgs.ints(new int[]{0, 2, 2, 0}, 2, 2);
		IterableLoopBuilder.setImages(targetIterable, imageA, imageB).forEachPixel((t, a, b) -> t.setInteger(a.getInteger() - b.getInteger()));
		RandomAccessibleInterval<IntType> expected = ArrayImgs.ints(new int[]{1, -1, -2, 0}, 2, 2);
		ImgLib2Assert.assertImageEquals(expected, target);
	}

	@Test
	public void testMultiThreading() {
		// Test if IterableLoopBuilder still produces a correct result when multi-threading is enabled.
		Img<IntType> image = ArrayImgs.ints(10, 10);
		Parallelization.runMultiThreaded(() -> {
			IterableLoopBuilder.setImages(image).multithreaded().forEachPixel(pixel -> pixel.setOne());
		});
		image.forEach(pixel -> assertEquals(1, pixel.getInteger()));
	}
}
