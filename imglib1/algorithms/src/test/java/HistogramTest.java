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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBinFactory;
import mpicbg.imglib.algorithm.histogram.discrete.DiscreteIntHistogramBinFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

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
