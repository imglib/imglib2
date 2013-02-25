package net.imglib2.histogram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class Integer1dBinMapperTest {

	@Test
	public void testNoTail() {
		UnsignedByteType tmp = new UnsignedByteType();
		Integer1dBinMapper<UnsignedByteType> binMapper =
			new Integer1dBinMapper<UnsignedByteType>(0, 100, false);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		for (int i = 0; i < 100; i++) {
			tmp.setInteger(i);
			long[] binPos = new long[] { i };
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i, tmpArr[0]);
			binMapper.getMinValue(binPos, tmp);
			assertEquals(i, tmp.getIntegerLong());
			binMapper.getCenterValue(binPos, tmp);
			assertEquals(i, tmp.getIntegerLong());
			binMapper.getMaxValue(binPos, tmp);
			assertEquals(i, tmp.getIntegerLong());
		}
	}

	@Test
	public void testTail() {
		ByteType tmp = new ByteType();
		Integer1dBinMapper<ByteType> binMapper =
			new Integer1dBinMapper<ByteType>(0, 100, true);
		long[] tmpArr = new long[1];
		binMapper.getBinDimensions(tmpArr);
		assertArrayEquals(new long[] { 100 }, tmpArr);
		// test the interior areas
		for (int i = 0; i < 98; i++) {
			tmp.setInteger(i);
			binMapper.getBinPosition(tmp, tmpArr);
			assertEquals(i + 1, tmpArr[0]);
			long[] binPos = tmpArr.clone();
			binMapper.getMinValue(binPos, tmp);
			assertEquals(i, tmp.getIntegerLong());
			binMapper.getCenterValue(binPos, tmp);
			assertEquals(i, tmp.getIntegerLong());
			binMapper.getMaxValue(binPos, tmp);
			assertEquals(i, tmp.getIntegerLong());
		}
		// test the lower tail
		tmp.setInteger(-1);
		binMapper.getBinPosition(tmp, tmpArr);
		assertEquals(0, tmpArr[0]);

		// test the upper tail
		tmp.setInteger(98);
		binMapper.getBinPosition(tmp, tmpArr);
		assertEquals(99, tmpArr[0]);
	}
}
