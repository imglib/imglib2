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

package net.imglib2.histogram.rev2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;


/**
 * @author Barry DeZonia
 */
public class HistogramNdTest {

	@Test
	public void testUnconstrainedNoTails() {

		List<UnsignedByteType> data1 = getData1();
		List<UnsignedByteType> data2 = getData2();
		List<Iterable<UnsignedByteType>> data =
			new ArrayList<Iterable<UnsignedByteType>>();
		data.add(data1);
		data.add(data2);

		long[] minVals = new long[] { 0, 0 };
		long[] numBins = new long[] { 256, 256 };
		boolean[] tailBins = new boolean[] { false, false };

		IntegerNdBinMapper<UnsignedByteType> binMapper =
			new IntegerNdBinMapper<UnsignedByteType>(minVals, numBins, tailBins);

		HistogramNd<UnsignedByteType> hist =
			new HistogramNd<UnsignedByteType>(data, binMapper.definitions());

		assertEquals(256 * 256, hist.getBinCount());
		assertEquals(11, hist.totalCount());
		assertEquals(0, hist.lowerTailCount());
		assertEquals(0, hist.lowerTailCount(0));
		assertEquals(0, hist.lowerTailCount(1));
		assertEquals(0, hist.upperTailCount());
		assertEquals(0, hist.upperTailCount(0));
		assertEquals(0, hist.upperTailCount(1));
		assertEquals(0, hist.ignoredCount());

		List<UnsignedByteType> list = new ArrayList<UnsignedByteType>();
		list.add(null);
		list.add(null);

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(4));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(3));
		list.set(1, new UnsignedByteType(4));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(7));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(9));
		list.set(1, new UnsignedByteType(7));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(10));
		list.set(1, new UnsignedByteType(1));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(7));
		list.set(1, new UnsignedByteType(1));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(10));
		list.set(1, new UnsignedByteType(9));
		assertEquals(2, hist.frequency(list));

		list.set(0, new UnsignedByteType(9));
		list.set(1, new UnsignedByteType(12));
		assertEquals(2, hist.frequency(list));

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(0));
		assertEquals(1, hist.frequency(list));
	}

	@Test
	public void testConstrainedWithTails() {
		List<UnsignedByteType> data1 = getData1();
		List<UnsignedByteType> data2 = getData2();
		List<Iterable<UnsignedByteType>> data =
			new ArrayList<Iterable<UnsignedByteType>>();
		data.add(data1);
		data.add(data2);

		List<UnsignedByteType> list = new ArrayList<UnsignedByteType>();
		list.add(null);
		list.add(null);

		long[] minVals = new long[] { 4, 4 };
		long[] numBins = new long[] { 8, 8 };
		boolean[] tailBins = new boolean[] { true, true };

		IntegerNdBinMapper<UnsignedByteType> binMapper =
			new IntegerNdBinMapper<UnsignedByteType>(minVals, numBins, tailBins);

		HistogramNd<UnsignedByteType> hist =
			new HistogramNd<UnsignedByteType>(data, binMapper.definitions());

		assertEquals(8 * 8, hist.getBinCount());
		assertEquals(11, hist.distributionCount());
		assertEquals(4, hist.lowerTailCount());
		assertEquals(1, hist.lowerTailCount(0));
		assertEquals(3, hist.lowerTailCount(1));
		assertEquals(5, hist.upperTailCount());
		assertEquals(3, hist.upperTailCount(0));
		assertEquals(2, hist.upperTailCount(1));
		assertEquals(0, hist.ignoredCount());

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(4));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(3));
		list.set(1, new UnsignedByteType(4));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(7));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(9));
		list.set(1, new UnsignedByteType(7));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(10));
		list.set(1, new UnsignedByteType(1));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(7));
		list.set(1, new UnsignedByteType(1));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(10));
		list.set(1, new UnsignedByteType(9));
		assertEquals(2, hist.frequency(list));

		list.set(0, new UnsignedByteType(9));
		list.set(1, new UnsignedByteType(12));
		assertEquals(2, hist.frequency(list));

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(0));
		assertEquals(1, hist.frequency(list));
	}

	@Test
	public void testConstrainedWithNoTails() {

		List<UnsignedByteType> data1 = getData1();
		List<UnsignedByteType> data2 = getData2();
		List<Iterable<UnsignedByteType>> data =
			new ArrayList<Iterable<UnsignedByteType>>();
		data.add(data1);
		data.add(data2);

		List<UnsignedByteType> list = new ArrayList<UnsignedByteType>();
		list.add(null);
		list.add(null);

		long[] minVals = new long[] { 5, 5 };
		long[] numBins = new long[] { 5, 5 };
		boolean[] tailBins = new boolean[] { false, false };

		IntegerNdBinMapper<UnsignedByteType> binMapper =
			new IntegerNdBinMapper<UnsignedByteType>(minVals, numBins, tailBins);

		HistogramNd<UnsignedByteType> hist =
			new HistogramNd<UnsignedByteType>(data, binMapper.definitions());

		assertEquals(5 * 5, hist.getBinCount());
		assertEquals(2, hist.distributionCount());
		assertEquals(0, hist.lowerTailCount());
		assertEquals(0, hist.lowerTailCount(0));
		assertEquals(0, hist.lowerTailCount(1));
		assertEquals(0, hist.upperTailCount());
		assertEquals(0, hist.upperTailCount(0));
		assertEquals(0, hist.upperTailCount(1));
		assertEquals(9, hist.ignoredCount());
		
		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(4));
		assertEquals(0, hist.frequency(list));

		list.set(0, new UnsignedByteType(3));
		list.set(1, new UnsignedByteType(4));
		assertEquals(0, hist.frequency(list));

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(7));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(9));
		list.set(1, new UnsignedByteType(7));
		assertEquals(1, hist.frequency(list));

		list.set(0, new UnsignedByteType(10));
		list.set(1, new UnsignedByteType(1));
		assertEquals(0, hist.frequency(list));

		list.set(0, new UnsignedByteType(7));
		list.set(1, new UnsignedByteType(1));
		assertEquals(0, hist.frequency(list));

		list.set(0, new UnsignedByteType(10));
		list.set(1, new UnsignedByteType(9));
		assertEquals(0, hist.frequency(list));

		list.set(0, new UnsignedByteType(9));
		list.set(1, new UnsignedByteType(12));
		assertEquals(0, hist.frequency(list));

		list.set(0, new UnsignedByteType(5));
		list.set(1, new UnsignedByteType(0));
		assertEquals(0, hist.frequency(list));
	}

	private List<UnsignedByteType> getData1() {
		List<UnsignedByteType> data = new ArrayList<UnsignedByteType>();
		data.add(new UnsignedByteType(5));
		data.add(new UnsignedByteType(3));
		data.add(new UnsignedByteType(5));
		data.add(new UnsignedByteType(9));
		data.add(new UnsignedByteType(10));
		data.add(new UnsignedByteType(7));
		data.add(new UnsignedByteType(10));
		data.add(new UnsignedByteType(10));
		data.add(new UnsignedByteType(9));
		data.add(new UnsignedByteType(9));
		data.add(new UnsignedByteType(5));
		return data;
	}

	private List<UnsignedByteType> getData2() {
		List<UnsignedByteType> data = new ArrayList<UnsignedByteType>();
		data.add(new UnsignedByteType(4));
		data.add(new UnsignedByteType(4));
		data.add(new UnsignedByteType(7));
		data.add(new UnsignedByteType(7));
		data.add(new UnsignedByteType(1));
		data.add(new UnsignedByteType(1));
		data.add(new UnsignedByteType(9));
		data.add(new UnsignedByteType(9));
		data.add(new UnsignedByteType(12));
		data.add(new UnsignedByteType(12));
		data.add(new UnsignedByteType(0));
		return data;
	}
}
