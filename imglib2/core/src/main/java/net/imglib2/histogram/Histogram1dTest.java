package net.imglib2.histogram;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;


public class Histogram1dTest {

	@Test
	public void test() {
		List<UnsignedByteType> data = getData();
		BinMapper<UnsignedByteType> binMapper =
			new Integer1dBinMapper<UnsignedByteType>(0, 256, false);
		Histogram1d<UnsignedByteType> hist =
			new Histogram1d<UnsignedByteType>(data, binMapper, new UnsignedByteType());
		assertEquals(1, hist.frequency(3));
		assertEquals(3, hist.frequency(5));
		assertEquals(1, hist.frequency(7));
		assertEquals(3, hist.frequency(9));
		assertEquals(3, hist.frequency(10));
	}

	private List<UnsignedByteType> getData() {
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
