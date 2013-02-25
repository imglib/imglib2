package net.imglib2.histogram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

public class Real1dBinMapperTest {

	@Test
	public void testIntNoTail() {
		IntType tmp = new IntType();
		Real1dBinMapper<IntType> binMapper =
			new Real1dBinMapper<IntType>(0.0, 100.0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValue(binPos, tmp);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValue(binPos, tmp);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			// Note - one would hope this would calc easily but due to roudning errors
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
		Real1dBinMapper<IntType> binMapper =
			new Real1dBinMapper<IntType>(0.0, 100.0, 102, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 102 }, tmpArr);
		// test lower tail
		tmp.setReal(-1);
		binMapper.getBinPosition(tmp, tmpArr);
		assertEquals(0, tmpArr[0]);
		// test upper tail
		tmp.setReal(100);
		binMapper.getBinPosition(tmp, tmpArr);
		assertEquals(101, tmpArr[0]);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i + 1, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValue(binPos, tmp);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValue(binPos, tmp);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			// Note - one would hope this would calc easily but due to roudning errors
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
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(0.0, 100.0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValue(binPos, tmp);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValue(binPos, tmp);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			binMapper.getCenterValue(binPos, tmp);
			assertEquals((i + 0.5), tmp.getRealDouble(), 0.0);
		}
	}

	@Test
	public void testFloatTail() {
		FloatType tmp = new FloatType();
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(0.0, 100.0, 102, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 102 }, tmpArr);
		// test lower tail
		tmp.setReal(-0.1);
		binMapper.getBinPosition(tmp, tmpArr);
		assertEquals(0, tmpArr[0]);
		// test upper tail
		tmp.setReal(100.1);
		binMapper.getBinPosition(tmp, tmpArr);
		assertEquals(101, tmpArr[0]);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i + 1, tmpArr[0]);
			long[] binPos = tmpArr;
			binMapper.getMinValue(binPos, tmp);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValue(binPos, tmp);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
			binMapper.getCenterValue(binPos, tmp);
			assertEquals((i + 0.5), tmp.getRealDouble(), 0.0);
		}
	}
}
