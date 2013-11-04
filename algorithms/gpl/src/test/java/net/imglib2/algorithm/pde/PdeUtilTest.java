package net.imglib2.algorithm.pde;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class PdeUtilTest {

	@Test
	public void test() {
		double[] results;
		results = PdeUtil.realSymetricMatrix2x2(2, Float.MIN_VALUE, 0.1);
		assertEquals(1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, Float.MIN_VALUE, -0.1);
		assertEquals(-1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, -Float.MIN_VALUE, 0.1);
		assertEquals(1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, Float.MIN_VALUE, 0.1);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, -Float.MIN_VALUE, -0.1);
		assertEquals(-1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, Float.MIN_VALUE, -0.1);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, -Float.MIN_VALUE, 0.1);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, -Float.MIN_VALUE, -0.1);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, Float.MIN_VALUE, 0.9);
		assertEquals(1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, Float.MIN_VALUE, -0.9);
		assertEquals(-1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, -Float.MIN_VALUE, 0.9);
		assertEquals(1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, Float.MIN_VALUE, 0.9);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(2, -Float.MIN_VALUE, -0.9);
		assertEquals(-1, results[2], 0);
		assertEquals(0, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, Float.MIN_VALUE, -0.9);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, -Float.MIN_VALUE, 0.9);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
		results = PdeUtil.realSymetricMatrix2x2(-2, -Float.MIN_VALUE, -0.9);
		assertEquals(0, results[2], 0);
		assertEquals(1, results[3], 0);
	}
}
