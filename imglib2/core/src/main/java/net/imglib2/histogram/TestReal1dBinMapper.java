package net.imglib2.histogram;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class TestReal1dBinMapper {

	@Test
	public void test() {
		UnsignedByteType tmp = new UnsignedByteType();
		Real1dBinMapper<UnsignedByteType> binMapper =
			new Real1dBinMapper<UnsignedByteType>(256, 0, 255, new UnsignedByteType());
		assertArrayEquals(new long[] { 256 }, binMapper.getBinDimensions());
		for (int i = 0; i < 256; i++) {
			tmp.setReal(i);
			long[] binPos = new long[] { i };
			assertArrayEquals(binPos, binMapper.getBinPosition(tmp));
			assertEquals((i + 0.5), binMapper.getCenterValue(binPos).getRealDouble(),
				0.0);
			assertEquals((i), binMapper.getMinValue(binPos).getRealDouble(), 0.0);
			assertEquals((i + 1), binMapper.getMaxValue(binPos).getRealDouble(), 0.0);
		}
	}

}
