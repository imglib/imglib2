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
 * Tests the Real1dBinMapper class.
 * 
 * @author Barry DeZonia
 */
public class Real1dBinMapperTest {

	// DONE

	@Test
	public void testIntNoTails() {
		IntType tmp = new IntType();
		List<IntType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<IntType> binMapper =
			new Real1dBinMapper<IntType>(0.0, 100.0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (double i = 0; i <= 100; i += 0.125) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			double expectedBin = Math.round(i);
			if (i >= 99.5) expectedBin--;
			assertEquals(expectedBin, tmpArr[0], 0);
			long[] binPos = tmpArr;
			binMapper.getLowerBounds(binPos, tmpList);
			assertEquals(expectedBin, tmp.getRealDouble(), 0.0);
			binMapper.getUpperBounds(binPos, tmpList);
			assertEquals(expectedBin + 1, tmp.getRealDouble(), 0.0);
			// Note - one would hope this would calc easily but due to rounding errors
			// one cannot always easily tell what the bin center is when using an
			// integral type with a Real1dBinMapper. One should really use an
			// Integer1dBinMapper in these cases. Disabling test.
			// binMapper.getCenterValues(binPos, tmpList);
			// assertEquals(expectedBin + 1, tmp.getRealDouble(), 0.0);
		}
		tmp.setReal(-1);
		assertFalse(binMapper.getBinPosition(tmpList, tmpArr));
		tmp.setReal(101);
		assertFalse(binMapper.getBinPosition(tmpList, tmpArr));
	}

	// DONE

	@Test
	public void testIntTails() {
		IntType tmp = new IntType();
		List<IntType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<IntType> binMapper =
			new Real1dBinMapper<IntType>(0.0, 100.0, 102, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 102 }, tmpArr);
		for (double i = 0; i <= 100; i += 0.125) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			double expectedBin = Math.round(i) + 1;
			if (i >= 99.5) expectedBin--;
			assertEquals(expectedBin, tmpArr[0], 0);
			long[] binPos = tmpArr;
			binMapper.getLowerBounds(binPos, tmpList);
			assertEquals(expectedBin - 1, tmp.getRealDouble(), 0.0);
			binMapper.getUpperBounds(binPos, tmpList);
			assertEquals(expectedBin, tmp.getRealDouble(), 0.0);
			// Note - one would hope this would calc easily but due to rounding errors
			// one cannot always easily tell what the bin center is when using an
			// integral type with a Real1dBinMapper. One should really use an
			// Integer1dBinMapper in these cases. Disabling test.
			// binMapper.getCenterValues(binPos, tmpList);
			// assertEquals(expectedBin + 1, tmp.getRealDouble(), 0.0);
		}
		tmp.setReal(-1);
		assertTrue(binMapper.getBinPosition(tmpList, tmpArr));
		assertEquals(0, tmpArr[0]);
		tmp.setReal(101);
		assertTrue(binMapper.getBinPosition(tmpList, tmpArr));
		assertEquals(101, tmpArr[0]);
	}

	// DONE

	@Test
	public void testFloatNoTails() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(0.0, 100.0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (double i = 0; i <= 100; i += 0.125) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			double expectedBin = Math.floor(i);
			if (i == 100.0) expectedBin--;
			assertEquals(expectedBin, tmpArr[0], 0);
			long[] binPos = tmpArr;
			binMapper.getLowerBounds(binPos, tmpList);
			assertEquals(expectedBin, tmp.getRealDouble(), 0.0);
			binMapper.getUpperBounds(binPos, tmpList);
			assertEquals(expectedBin + 1, tmp.getRealDouble(), 0.0);
			binMapper.getCenterValues(binPos, tmpList);
			assertEquals(expectedBin + 0.5, tmp.getRealDouble(), 0.0);
		}
		tmp.setReal(-0.0001);
		assertFalse(binMapper.getBinPosition(tmpList, tmpArr));
		tmp.setReal(100.0001);
		assertFalse(binMapper.getBinPosition(tmpList, tmpArr));
	}

	// DONE

	@Test
	public void testFloatTails() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(0.0, 100.0, 102, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 102 }, tmpArr);
		for (double i = 0; i <= 100; i += 0.125) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmpList, tmpArr);
			double expectedBin = Math.floor(i) + 1;
			if (i == 100.0) expectedBin--;
			assertEquals(expectedBin, tmpArr[0], 0);
			long[] binPos = tmpArr;
			binMapper.getLowerBounds(binPos, tmpList);
			assertEquals(expectedBin - 1, tmp.getRealDouble(), 0.0);
			binMapper.getUpperBounds(binPos, tmpList);
			assertEquals(expectedBin, tmp.getRealDouble(), 0.0);
			binMapper.getCenterValues(binPos, tmpList);
			assertEquals(expectedBin - 0.5, tmp.getRealDouble(), 0.0);
		}
		tmp.setReal(-0.0001);
		assertTrue(binMapper.getBinPosition(tmpList, tmpArr));
		assertEquals(0, tmpArr[0]);
		tmp.setReal(100.0001);
		assertTrue(binMapper.getBinPosition(tmpList, tmpArr));
		assertEquals(101, tmpArr[0]);
	}

	// DONE

	@Test
	public void testBinBoundariesTails() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		long[] pos = new long[1];
		Real1dBinMapper<FloatType> binMapper;

		binMapper = new Real1dBinMapper<FloatType>(0.0, 4.0, 4, true);
		pos[0] = 0;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(Double.NEGATIVE_INFINITY, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(0, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesUpperBounds(pos));

		pos[0] = 1;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(0, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesUpperBounds(pos));

		pos[0] = 2;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(4, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesUpperBounds(pos));

		pos[0] = 3;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(4, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(Double.POSITIVE_INFINITY, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesUpperBounds(pos));

		tmp.setReal(-0.001);
		binMapper.getBinPosition(tmpList, pos);
		assertEquals(0, pos[0]);

		tmp.setReal(4.001);
		binMapper.getBinPosition(tmpList, pos);
		assertEquals(3, pos[0]);
	}

	// DONE

	@Test
	public void testBinBoundariesNoTails() {
		FloatType tmp = new FloatType();
		List<FloatType> tmpList = Arrays.asList(tmp);
		long[] pos = new long[1];
		Real1dBinMapper<FloatType> binMapper;

		binMapper = new Real1dBinMapper<FloatType>(0.0, 4.0, 4, false);
		pos[0] = 0;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(0, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(1, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesUpperBounds(pos));

		pos[0] = 1;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(1, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesUpperBounds(pos));

		pos[0] = 2;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(2, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(3, tmp.getRealDouble(), 0);
		assertFalse(binMapper.includesUpperBounds(pos));

		pos[0] = 3;
		binMapper.getLowerBounds(pos, tmpList);
		assertEquals(3, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesLowerBounds(pos));
		binMapper.getUpperBounds(pos, tmpList);
		assertEquals(4, tmp.getRealDouble(), 0);
		assertTrue(binMapper.includesUpperBounds(pos));

		tmp.setReal(-0.001);
		assertFalse(binMapper.getBinPosition(tmpList, pos));

		tmp.setReal(4.001);
		assertFalse(binMapper.getBinPosition(tmpList, pos));
	}
}
