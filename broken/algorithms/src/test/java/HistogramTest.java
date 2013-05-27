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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.algorithm.histogram.Histogram;
import net.imglib2.algorithm.histogram.HistogramBinFactory;
import net.imglib2.algorithm.histogram.discrete.DiscreteIntHistogramBinFactory;
import net.imglib2.container.array.ArrayContainerFactory;
import net.imglib2.cursor.Cursor;
import net.imglib2.image.Image;
import net.imglib2.image.ImageFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

/**
 * TODO
 *
 * @author Larry Lindsey
 */
public class HistogramTest {

	private void runTest(int[] vect, int[] expectKey, int[] expectCnt)
	{
		ImageFactory<UnsignedByteType> imFactory =
			new ImageFactory<UnsignedByteType>(new UnsignedByteType(),
				new ArrayContainerFactory());
		Image<UnsignedByteType> im = imFactory.createImage(
			new int[]{vect.length});
		Cursor<UnsignedByteType> cursor = im.createCursor();
		Histogram<UnsignedByteType> histogram;
		HistogramBinFactory<UnsignedByteType> binFactory = 
			new DiscreteIntHistogramBinFactory<UnsignedByteType>();
		UnsignedByteType k = new UnsignedByteType();

		for (int v: vect)
		{
			cursor.fwd();
			cursor.getType().set(v);
		}

		histogram = new Histogram<UnsignedByteType>(
			binFactory,
			im.createCursor());

		histogram.process();

		for (int i = 0; i < expectKey.length; ++i)
		{
			long cntVal;
			k.set(expectKey[i]);
			cntVal = histogram.getBin(k).getCount();
			assertEquals("Bin " + expectKey[i], expectCnt[i], cntVal);
		}
	}

	@Test
	public final void testHistogram() {
		final int n = 2;
		final List<int[]> vectorList = new ArrayList<int[]>(n);
		final List<int[]> expectKeys = new ArrayList<int[]>(n);
		final List<int[]> expectCnts = new ArrayList<int[]>(n);

		//Test 1
		vectorList.add(new int[]{1, 2, 3, 4});
		expectKeys.add(new int[]{1, 2, 3, 4, 5});
		expectCnts.add(new int[]{1, 1, 1, 1, 0});

		//Test 2
		int[] test2vect = new int[1024];
		int[] test2keys = new int[256];
		int[] test2cnts = new int[256];
		for (int i = 0; i < 1024; ++i)
		{
			test2vect[i] = i % 256;
		}
		for (int i = 0; i < 256; ++i)
		{
			test2keys[i] = i;
			test2cnts[i] = 4;
		}

		vectorList.add(test2vect);
		expectKeys.add(test2keys);
		expectCnts.add(test2cnts);

		for (int i = 0; i < vectorList.size(); ++i)
		{
			runTest(vectorList.get(i), expectKeys.get(i), expectCnts.get(i));
		}
	}

}
