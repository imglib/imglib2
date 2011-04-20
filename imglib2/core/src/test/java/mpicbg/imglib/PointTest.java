package mpicbg.imglib;

import static org.junit.Assert.*;

import org.junit.Test;

public class PointTest {

	@Test
	public void testPointInt() {
		Point p = new Point(3);
		assertEquals(p.numDimensions(), 3);
	}

	@Test
	public void testPointLongArray() {
		Point p = new Point(new long[] { 5, 3});
		assertEquals(p.numDimensions(), 2);
		assertEquals(p.getLongPosition(0), 5);
		assertEquals(p.getLongPosition(1), 3);
	}

	@Test
	public void testPointIntArray() {
		Point p = new Point(new int[] { 5, 3});
		assertEquals(p.numDimensions(), 2);
		assertEquals(p.getLongPosition(0), 5);
		assertEquals(p.getLongPosition(1), 3);
	}
	@Test
	public void testPointLocalizable() {
		Point p = new Point(new Point(new int[] {15,2,1}));
		assertEquals(p.numDimensions(), 3);
		assertEquals(p.getLongPosition(0), 15);
		assertEquals(p.getLongPosition(1), 2);
		assertEquals(p.getLongPosition(2), 1);
	}

	@Test
	public void testLocalizeFloatArray() {
		long [] initial = new long[] { 532,632, 987421};
		float [] result = new float[3];
		Point p = new Point(initial);
		p.localize(result);
		for (int i=0; i<initial.length; i++) {
			assertEquals(initial[i], result[i], 0);
		}
	}

	@Test
	public void testLocalizeDoubleArray() {
		long [] initial = new long[] { 532,632, 987421};
		double [] result = new double[3];
		Point p = new Point(initial);
		p.localize(result);
		for (int i=0; i<initial.length; i++) {
			assertEquals(initial[i], result[i], 0);
		}
	}

	@Test
	public void testGetFloatPosition() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		for (int i=0; i<initial.length; i++) {
			assertEquals(initial[i], p.getFloatPosition(i), 0);
		}
	}

	@Test
	public void testGetDoublePosition() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		for (int i=0; i<initial.length; i++) {
			assertEquals(initial[i], p.getDoublePosition(i), 0);
		}
	}

	@Test
	public void testNumDimensions() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		assertEquals(p.numDimensions(), 3);
	}

	@Test
	public void testFwd() {
		long [] initial = new long[] { 532,632, 987421};
		for (int j=0; j<initial.length; j++) {
			Point p = new Point(initial);
			p.fwd(j);
			for (int i=0; i<initial.length; i++) {
				if (i == j) {
					assertEquals(p.getLongPosition(i), initial[i]+1);
				} else {
					assertEquals(initial[i], p.getLongPosition(i), 0);
				}
			}
		}
	}

	@Test
	public void testBck() {
		long [] initial = new long[] { 532,632, 987421};
		for (int j=0; j<initial.length; j++) {
			Point p = new Point(initial);
			p.bck(j);
			for (int i=0; i<initial.length; i++) {
				if (i == j) {
					assertEquals(p.getLongPosition(i), initial[i]-1);
				} else {
					assertEquals(initial[i], p.getLongPosition(i), 0);
				}
			}
		}
	}

	@Test
	public void testMoveIntInt() {
		long [] initial = new long[] { 532,632, 987421};
		int [] displacement = new int [] { 85, 8643, -973 };
		for (int j=0; j<initial.length; j++) {
			Point p = new Point(initial);
			p.move(displacement[j], j);
			for (int i=0; i<initial.length; i++) {
				if (i == j) {
					assertEquals(p.getLongPosition(i), initial[i] + displacement[i]);
				} else {
					assertEquals(initial[i], p.getLongPosition(i), 0);
				}
			}
		}
	}

	@Test
	public void testMoveLongInt() {
		long [] initial = new long[] { 532,632, 987421};
		long [] displacement = new long [] { 85, 8643, -973 };
		for (int j=0; j<initial.length; j++) {
			Point p = new Point(initial);
			p.move(displacement[j], j);
			for (int i=0; i<initial.length; i++) {
				if (i == j) {
					assertEquals(p.getLongPosition(i), initial[i] + displacement[i]);
				} else {
					assertEquals(initial[i], p.getLongPosition(i), 0);
				}
			}
		}
	}

	@Test
	public void testMoveLocalizable() {
		long [] initial = new long[] { 532,632, 987421};
		long [] displacement = new long [] { 85, 8643, -973 };
		Point p = new Point(initial);
		Point d = new Point(displacement);
		p.move(d);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), initial[i] + displacement[i]);
		}
	}

	@Test
	public void testMoveIntArray() {
		long [] initial = new long[] { 532,632, 987421};
		int [] displacement = new int [] { 85, 8643, -973 };
		Point p = new Point(initial);
		p.move(displacement);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), initial[i] + displacement[i]);
		}
	}

	@Test
	public void testMoveLongArray() {
		long [] initial = new long[] { 532,632, 987421};
		long [] displacement = new long [] { 85, 8643, -973 };
		Point p = new Point(initial);
		p.move(displacement);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), initial[i] + displacement[i]);
		}
	}

	@Test
	public void testSetPositionLocalizable() {
		long [] initial = new long[] { 532,632, 987421};
		long [] displacement = new long [] { 85, 8643, -973 };
		Point p = new Point(initial);
		Point d = new Point(displacement);
		p.setPosition(d);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), displacement[i]);
		}
	}

	@Test
	public void testSetPositionIntArray() {
		long [] initial = new long[] { 532,632, 987421};
		int [] displacement = new int [] { 85, 8643, -973 };
		Point p = new Point(initial);
		p.setPosition(displacement);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), displacement[i]);
		}
	}

	@Test
	public void testSetPositionLongArray() {
		long [] initial = new long[] { 532,632, 987421};
		long [] displacement = new long [] { 85, 8643, -973 };
		Point p = new Point(initial);
		p.setPosition(displacement);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), displacement[i]);
		}
	}

	@Test
	public void testSetPositionIntInt() {
		long [] initial = new long[] { 532,632, 987421};
		int [] displacement = new int [] { 85, 8643, -973 };
		for (int j=0; j<initial.length; j++) {
			Point p = new Point(initial);
			p.setPosition(displacement[j], j);
			for (int i=0; i<initial.length; i++) {
				if (i == j) {
					assertEquals(p.getLongPosition(i), displacement[i]);
				} else {
					assertEquals(initial[i], p.getLongPosition(i));
				}
			}
		}
	}

	@Test
	public void testSetPositionLongInt() {
		long [] initial = new long[] { 532,632, 987421};
		long [] displacement = new long [] { 85, 8643, -973 };
		for (int j=0; j<initial.length; j++) {
			Point p = new Point(initial);
			p.setPosition(displacement[j], j);
			for (int i=0; i<initial.length; i++) {
				if (i == j) {
					assertEquals(p.getLongPosition(i), displacement[i]);
				} else {
					assertEquals(initial[i], p.getLongPosition(i));
				}
			}
		}
	}

	@Test
	public void testLocalizeIntArray() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		int [] result = new int[3];
		p.localize(result);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), initial[i]);
		}
	}

	@Test
	public void testLocalizeLongArray() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		long [] result = new long[3];
		p.localize(result);
		for (int i=0; i<initial.length; i++) {
			assertEquals(p.getLongPosition(i), initial[i]);
		}
	}

	@Test
	public void testGetIntPosition() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		assertEquals(p.getIntPosition(1), initial[1]);
	}

	@Test
	public void testGetLongPosition() {
		long [] initial = new long[] { 532,632, 987421};
		Point p = new Point(initial);
		assertEquals(p.getLongPosition(1), initial[1]);
	}

}
