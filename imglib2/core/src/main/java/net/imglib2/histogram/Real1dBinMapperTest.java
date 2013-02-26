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

package net.imglib2.histogram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

/**
 * @author Barry DeZonia
 */
public class Real1dBinMapperTest {

	@Test
	public void testIntNoTail() {
		IntType tmp = new IntType();
		List<IntType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<IntType> binMapper =
			new Real1dBinMapper<IntType>(0.0, 100.0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			assertEquals(i, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValues(binPos, tmpList);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValues(binPos, tmpList);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			// Note - one would hope this would calc easily but due to rounding errors
			// one cannot always easily tell what the bin center is when using an
			// integral type with a Real1dBinMapper. One should really use an
			// Integer1dBinMapper in these cases. Disabling test.
			// binMapper.getCenterValue(binPos, tmp);
			// assertEquals((i + 1), tmp.getRealDouble(), 0.0);
		}
	}

	@Test
	public void testIntTail() {
		IntType tmp = new IntType();
		List<IntType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<IntType> binMapper =
			new Real1dBinMapper<IntType>(0.0, 100.0, 102, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 102 }, tmpArr);
		// test lower tail
		tmp.setReal(-1);
		binMapper.getBinPosition(tmpList, tmpArr);
		assertEquals(0, tmpArr[0]);
		// test upper tail
		tmp.setReal(100);
		binMapper.getBinPosition(tmpList, tmpArr);
		assertEquals(101, tmpArr[0]);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			assertEquals(i + 1, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValues(binPos, tmpList);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValues(binPos, tmpList);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			// Note - one would hope this would calc easily but due to rounding errors
			// one cannot always easily tell what the bin center is when using an
			// integral type with a Real1dBinMapper. One should really use an
			// Integer1dBinMapper in these cases. Disabling test.
			// binMapper.getCenterValue(binPos, tmp);
			// assertEquals((i + 1), tmp.getRealDouble(), 0.0);
		}
	}

	@Test
	public void testFloatNoTail() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(0.0, 100.0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			assertEquals(i, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValues(binPos, tmpList);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValues(binPos, tmpList);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			binMapper.getCenterValues(binPos, tmpList);
			assertEquals((i + 0.5), tmp.getRealDouble(), 0.0);
		}
	}

	@Test
	public void testFloatTail() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(0.0, 100.0, 102, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 102 }, tmpArr);
		// test lower tail
		tmp.setReal(-0.1);
		binMapper.getBinPosition(tmpList, tmpArr);
		assertEquals(0, tmpArr[0]);
		// test upper tail
		tmp.setReal(100.1);
		binMapper.getBinPosition(tmpList, tmpArr);
		assertEquals(101, tmpArr[0]);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			assertEquals(i + 1, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValues(binPos, tmpList);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValues(binPos, tmpList);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			binMapper.getCenterValues(binPos, tmpList);
			assertEquals((i + 0.5), tmp.getRealDouble(), 0.0);
		}
	}

	@Test
	public void testBinBoundariesTails() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		long[] pos = new long[1];
		Real1dBinMapper<FloatType> binMapper;

		binMapper = new Real1dBinMapper<FloatType>(0.0, 4.0, 4, true);
		pos[0] = 0;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(Double.NEGATIVE_INFINITY, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(0, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesMaxValues(pos));

		pos[0] = 1;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(0, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesMaxValues(pos));

		pos[0] = 2;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(4, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMaxValues(pos));

		pos[0] = 3;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(4, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(Double.POSITIVE_INFINITY, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMaxValues(pos));

		tmp.setReal(-0.001);
		binMapper.getBinPosition(tmpList, pos);
		assertEquals(0, pos[0]);

		tmp.setReal(4.001);
		binMapper.getBinPosition(tmpList, pos);
		assertEquals(3, pos[0]);
	}

	@Test
	public void testBinBoundaries() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		long[] pos = new long[1];
		Real1dBinMapper<FloatType> binMapper;

		binMapper = new Real1dBinMapper<FloatType>(0.0, 4.0, 4, false);
		pos[0] = 0;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(0, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(1, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesMaxValues(pos));

		pos[0] = 1;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(1, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesMaxValues(pos));

		pos[0] = 2;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(3, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesMaxValues(pos));

		pos[0] = 3;
		binMapper.getMinValues(pos, tmpList);
		assertEquals(3, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMinValues(pos));
		binMapper.getMaxValues(pos, tmpList);
		assertEquals(4, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesMaxValues(pos));

		tmp.setReal(-0.001);
		binMapper.getBinPosition(tmpList, pos);
		assertEquals(0, pos[0]);

		tmp.setReal(4.001);
		binMapper.getBinPosition(tmpList, pos);
		assertEquals(3, pos[0]);
	}
}
