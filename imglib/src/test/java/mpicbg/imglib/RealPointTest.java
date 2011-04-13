/**
 * 
 */
package mpicbg.imglib;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class RealPointTest {

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#RealPoint(int)}.
	 */
	@Test
	public void testRealPointInt() {
		RealPoint p = new RealPoint(3);
		assertEquals(p.numDimensions(), 3);
		for (int i=0; i<3; i++) {
			assertEquals(p.getDoublePosition(i), 0, 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#RealPoint(double[])}.
	 */
	@Test
	public void testRealPointDoubleArray() {
		double [] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
		RealPoint p = new RealPoint(expected);
		assertEquals(p.numDimensions(), 4);
		for (int i=0; i<4; i++) {
			assertEquals(p.getDoublePosition(i), expected[i], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#RealPoint(float[])}.
	 */
	@Test
	public void testRealPointFloatArray() {
		float [] expected = new float[] { 1.5f, 2.5f, 4.5f, 6.5f };
		RealPoint p = new RealPoint(expected);
		assertEquals(p.numDimensions(), 4);
		for (int i=0; i<4; i++) {
			assertEquals(p.getFloatPosition(i), expected[i], 0);
		}
	}
	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#fwd(int)}.
	 */
	@Test
	public void testFwd() {
		for (int i=0; i<3; i++) {
			double [] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			RealPoint p = new RealPoint(expected);
			p.fwd(i);
			expected[i] += 1;
			for (int j=0; j<4; j++) {
				assertEquals(p.getDoublePosition(j), expected[j], 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#bck(int)}.
	 */
	@Test
	public void testBck() {
		for (int i=0; i<3; i++) {
			double [] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			RealPoint p = new RealPoint(expected);
			p.bck(i);
			expected[i] -= 1;
			for (int j=0; j<4; j++) {
				assertEquals(p.getDoublePosition(j), expected[j], 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(int, int)}.
	 */
	@Test
	public void testMoveIntInt() {
		int [] move = { 1, 5, -3, 16 };
		for (int i=0; i<move.length; i++) {
			double [] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			RealPoint p = new RealPoint(expected);
			p.move(move[i], i);
			expected[i] += move[i];
			for (int j=0; j<4; j++) {
				assertEquals(p.getDoublePosition(j), expected[j], 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(long, int)}.
	 */
	@Test
	public void testMoveLongInt() {
		long [] move = { 1, 5, -3, 16 };
		for (int i=0; i<move.length; i++) {
			double [] expected = new double[] { 1.5, 2.5, 4.5, 6.5 };
			RealPoint p = new RealPoint(expected);
			p.move(move[i], i);
			expected[i] += move[i];
			for (int j=0; j<4; j++) {
				assertEquals(p.getDoublePosition(j), expected[j], 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(mpicbg.imglib.Localizable)}.
	 */
	@Test
	public void testMoveLocalizable() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] displacement = { 98.2, -16, 44.2, 0 };
		RealPoint p1 = new RealPoint(initial);
		RealPoint p2 = new RealPoint(displacement);
		p1.move(p2);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j] + displacement[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(int[])}.
	 */
	@Test
	public void testMoveIntArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		int [] displacement = { 98, -16, 44, 0 };
		RealPoint p1 = new RealPoint(initial);
		p1.move(displacement);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j] + displacement[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(long[])}.
	 */
	@Test
	public void testMoveLongArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		long [] displacement = { 98, -16, 44, 0 };
		RealPoint p1 = new RealPoint(initial);
		p1.move(displacement);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j] + displacement[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(mpicbg.imglib.Localizable)}.
	 */
	@Test
	public void testSetPositionLocalizable() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		long [] fynal = { 98, -16, 44, 0 };
		RealPoint p1 = new RealPoint(initial);
		Point p2 = new Point(fynal);
		p1.setPosition(p2);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), fynal[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(int[])}.
	 */
	@Test
	public void testSetPositionIntArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		int [] fynal = { 98, -16, 44, 0 };
		RealPoint p1 = new RealPoint(initial);
		p1.setPosition(fynal);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), fynal[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(long[])}.
	 */
	@Test
	public void testSetPositionLongArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		long [] fynal = { 98, -16, 44, 0 };
		RealPoint p1 = new RealPoint(initial);
		p1.setPosition(fynal);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), fynal[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(int, int)}.
	 */
	@Test
	public void testSetPositionIntInt() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		int [] fynal = { 98, -16, 44, 0 };
		for (int i=0; i<initial.length; i++) {
			RealPoint p1 = new RealPoint(initial);
			p1.setPosition(fynal[i], i);
			assertEquals(p1.getDoublePosition(i), fynal[i], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(long, int)}.
	 */
	@Test
	public void testSetPositionLongInt() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		long [] fynal = { 98, -16, 44, 0 };
		for (int i=0; i<initial.length; i++) {
			RealPoint p1 = new RealPoint(initial);
			p1.setPosition(fynal[i], i);
			assertEquals(p1.getDoublePosition(i), fynal[i], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#numDimensions()}.
	 */
	@Test
	public void testNumDimensions() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		RealPoint p1 = new RealPoint(initial);
		assertEquals(4, p1.numDimensions());
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(float, int)}.
	 */
	@Test
	public void testMoveFloatInt() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		float [] displacement = { 4.2f, 77.1f, -2f, 51.4f };
		for (int i=0; i<initial.length; i++) {
			RealPoint p1 = new RealPoint(initial);
			p1.move(displacement[i], i);
			for (int j=0; j<4; j++) {
				double expected = initial[j];
				if (i == j) expected += displacement[j];
				assertEquals(p1.getDoublePosition(j), expected, 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(double, int)}.
	 */
	@Test
	public void testMoveDoubleInt() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] displacement = { 4.2, 77.1, -2, 51.4 };
		for (int i=0; i<initial.length; i++) {
			RealPoint p1 = new RealPoint(initial);
			p1.move(displacement[i], i);
			for (int j=0; j<4; j++) {
				double expected = initial[j];
				if (i == j) expected += displacement[j];
				assertEquals(p1.getDoublePosition(j), expected, 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(mpicbg.imglib.RealLocalizable)}.
	 */
	@Test
	public void testMoveRealLocalizable() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] displacement = { 4.2, 77.1, -2, 51.4 };
		RealPoint p1 = new RealPoint(initial);
		RealPoint p2 = new RealPoint(displacement);
		p1.move(p2);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j] + displacement[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(float[])}.
	 */
	@Test
	public void testMoveFloatArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		float [] displacement = { 4.2f, 77.1f, -2f, 51.4f };
		RealPoint p1 = new RealPoint(initial);
		p1.move(displacement);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j] + displacement[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#move(double[])}.
	 */
	@Test
	public void testMoveDoubleArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] displacement = { 4.2, 77.1, -2, 51.4 };
		RealPoint p1 = new RealPoint(initial);
		p1.move(displacement);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j] + displacement[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(mpicbg.imglib.RealLocalizable)}.
	 */
	@Test
	public void testSetPositionRealLocalizable() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] fynal = { 98.2, -16.1, 44.7, 0 };
		RealPoint p1 = new RealPoint(initial);
		RealPoint p2 = new RealPoint(fynal);
		p1.setPosition(p2);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), fynal[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(float[])}.
	 */
	@Test
	public void testSetPositionFloatArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		float [] fynal = { 98.2f, -16.1f, 44.7f, 0f };
		RealPoint p1 = new RealPoint(initial);
		p1.setPosition(fynal);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), fynal[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(double[])}.
	 */
	@Test
	public void testSetPositionDoubleArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] fynal = { 98.2, -16.1, 44.7, 0 };
		RealPoint p1 = new RealPoint(initial);
		p1.setPosition(fynal);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), fynal[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(float, int)}.
	 */
	@Test
	public void testSetPositionFloatInt() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		float [] fynal = { 98.2f, -16.1f, 44.7f, 0f };
		for (int i=0; i<initial.length; i++) {
			RealPoint p1 = new RealPoint(initial);
			p1.setPosition(fynal[i], i);
			for (int j=0; j<4; j++) {
				if (i == j)
					assertEquals(p1.getDoublePosition(j), fynal[j], 0);
				else
					assertEquals(p1.getDoublePosition(j), initial[j], 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#setPosition(double, int)}.
	 */
	@Test
	public void testSetPositionDoubleInt() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] fynal = { 98.2, -16.1, 44.7, 0 };
		for (int i=0; i<initial.length; i++) {
			RealPoint p1 = new RealPoint(initial);
			p1.setPosition(fynal[i], i);
			for (int j=0; j<4; j++) {
				if (i == j)
					assertEquals(p1.getDoublePosition(j), fynal[j], 0);
				else
					assertEquals(p1.getDoublePosition(j), initial[j], 0);
			}
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#localize(float[])}.
	 */
	@Test
	public void testLocalizeFloatArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		float [] result = new float[initial.length];
		RealPoint p1 = new RealPoint(initial);
		p1.localize(result);
		for (int j=0; j<4; j++) {
			assertEquals(result[j], (float)(initial[j]), 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#localize(double[])}.
	 */
	@Test
	public void testLocalizeDoubleArray() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		double [] result = new double[initial.length];
		RealPoint p1 = new RealPoint(initial);
		p1.localize(result);
		for (int j=0; j<4; j++) {
			assertEquals(result[j], initial[j], 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#getFloatPosition(int)}.
	 */
	@Test
	public void testGetFloatPosition() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		RealPoint p1 = new RealPoint(initial);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getFloatPosition(j), (float)(initial[j]), 0);
		}
	}

	/**
	 * Test method for {@link mpicbg.imglib.RealPoint#getDoublePosition(int)}.
	 */
	@Test
	public void testGetDoublePosition() {
		double [] initial = { 5.3, 2.6, 3.1, -852.1 };
		RealPoint p1 = new RealPoint(initial);
		for (int j=0; j<4; j++) {
			assertEquals(p1.getDoublePosition(j), initial[j], 0);
		}
	}

}
