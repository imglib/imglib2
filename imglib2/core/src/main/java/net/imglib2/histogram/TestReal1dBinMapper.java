package net.imglib2.histogram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

public class TestReal1dBinMapper {

	@Test
	public void testInt() {
		UnsignedByteType tmp = new UnsignedByteType();
		Real1dBinMapper<UnsignedByteType> binMapper =
			new Real1dBinMapper<UnsignedByteType>(100, 0, 100);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			long[] binPos = new long[] { i };
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i, tmpArr[0]);
			binMapper.getMinValue(binPos, tmp);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getCenterValue(binPos, tmp);
			assertEquals((i + 0.5), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValue(binPos, tmp);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
		}
	}

	@Test
	public void testFloat() {
		FloatType tmp = new FloatType();
		Real1dBinMapper<FloatType> binMapper =
			new Real1dBinMapper<FloatType>(100, 0, 100);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setReal(i);
			long[] binPos = new long[] { i };
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i, tmpArr[0]);
			binMapper.getMinValue(binPos, tmp);
			assertEquals((i + 0), tmp.getRealDouble(), 0.0);
			binMapper.getCenterValue(binPos, tmp);
			assertEquals((i + 0.5), tmp.getRealDouble(), 0.0);
			binMapper.getMaxValue(binPos, tmp);
			assertEquals((i + 1), tmp.getRealDouble(), 0.0);
		}
	}
}
