package net.imglib2.histogram;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;


public class Histogram1dTest {

	@Test
	public void test() {

		List<UnsignedByteType> data = getData1();

		BinMapper<UnsignedByteType> binMapper =
			new Integer1dBinMapper<UnsignedByteType>(0, 256, false);

		Histogram1d<UnsignedByteType> hist =
			new Histogram1d<UnsignedByteType>(data, binMapper, new UnsignedByteType());

		assertEquals(256, hist.getBinCount());
		assertEquals(11, hist.totalValues());
		assertEquals(1, hist.frequency(new UnsignedByteType(3)));
		assertEquals(3, hist.frequency(new UnsignedByteType(5)));
		assertEquals(1, hist.frequency(new UnsignedByteType(7)));
		assertEquals(3, hist.frequency(new UnsignedByteType(9)));
		assertEquals(3, hist.frequency(new UnsignedByteType(10)));
		// assertEquals(0, hist.lowerTailCount());
		// assertEquals(0, hist.upperTailCount());

		binMapper = new Integer1dBinMapper<UnsignedByteType>(4, 8, true);

		hist =
			new Histogram1d<UnsignedByteType>(data, binMapper, new UnsignedByteType());

		assertEquals(8, hist.getBinCount());
		assertEquals(11, hist.totalValues());
		// assertEquals(0, hist.frequency(new UnsignedByteType(3)));
		assertEquals(3, hist.frequency(new UnsignedByteType(5)));
		assertEquals(1, hist.frequency(new UnsignedByteType(7)));
		assertEquals(3, hist.frequency(new UnsignedByteType(9)));
		// assertEquals(0, hist.frequency(new UnsignedByteType(10)));
		// assertEquals(1, hist.lowerTailCount());
		// assertEquals(3, hist.upperTailCount());

		binMapper = new Integer1dBinMapper<UnsignedByteType>(5, 5, false);

		hist =
			new Histogram1d<UnsignedByteType>(data, binMapper, new UnsignedByteType());

		assertEquals(5, hist.getBinCount());
		assertEquals(7, hist.totalValues());
		assertEquals(0, hist.frequency(new UnsignedByteType(3)));
		assertEquals(3, hist.frequency(new UnsignedByteType(5)));
		assertEquals(1, hist.frequency(new UnsignedByteType(7)));
		assertEquals(3, hist.frequency(new UnsignedByteType(9)));
		assertEquals(0, hist.frequency(new UnsignedByteType(10)));
		// assertEquals(0, hist.lowerTailCount());
		// assertEquals(0, hist.upperTailCount());
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
}
