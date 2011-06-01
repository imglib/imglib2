/**
 * 
 */
package net.imglib2;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class TestAbstractRealRandomAccess {
	private class AbstractRealRandomAccessImpl<T> extends AbstractRealRandomAccess<T> {
		public AbstractRealRandomAccessImpl(int nDimensions) {
			super(nDimensions);
		}

		@Override
		public RealRandomAccess<T> copyRealRandomAccess() {
			return null;
		}

		@Override
		public T get() {
			return null;
		}

		@Override
		public AbstractSampler<T> copy() {
			return null;
		}
		
	}
	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#AbstractRealRandomAccess(int)}.
	 */
	@Test
	public void testAbstractRealRandomAccess() {
		new AbstractRealRandomAccessImpl<Integer>(2);
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#fwd(int)}.
	 */
	@Test
	public void testFwd() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double expected [][] = { { 1,0 }, { 1,1}};
		for (int i=0; i<2; i++) {
			x.fwd(i);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#bck(int)}.
	 */
	@Test
	public void testBck() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double expected [][] = { { -1,0 }, { -1,-1}};
		for (int i=0; i<2; i++) {
			x.bck(i);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(int, int)}.
	 */
	@Test
	public void testMoveIntInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		int move [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 5, -3}};
		for (int i=0; i<move.length; i++) {
			x.move(move[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(long, int)}.
	 */
	@Test
	public void testMoveLongInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		long move [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 5, -3}};
		for (int i=0; i<move.length; i++) {
			x.move(move[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(net.imglib2.Localizable)}.
	 */
	@Test
	public void testMoveLocalizable() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		long move [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { 2, 8} };
		for (int i=0; i<move.length; i++) {
			x.move(new Point(move[i]));
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(int[])}.
	 */
	@Test
	public void testMoveIntArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		int move [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { 2, 8} };
		for (int i=0; i<move.length; i++) {
			x.move(move[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(long[])}.
	 */
	@Test
	public void testMoveLongArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		long move [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { 2, 8} };
		for (int i=0; i<move.length; i++) {
			x.move(move[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(net.imglib2.Localizable)}.
	 */
	@Test
	public void testSetPositionLocalizable() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		long position [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { -3, 2} };
		for (int i=0; i<position.length; i++) {
			x.setPosition(new Point(position[i]));
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(int[])}.
	 */
	@Test
	public void testSetPositionIntArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		int position [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { -3, 2} };
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(long[])}.
	 */
	@Test
	public void testSetPositionLongArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		long position [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { -3, 2} };
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(int, int)}.
	 */
	@Test
	public void testSetPositionIntInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		int position [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 1, -3}};
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(long, int)}.
	 */
	@Test
	public void testSetPositionLongInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		long position [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 1, -3}};
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(float, int)}.
	 */
	@Test
	public void testMoveFloatInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		float move [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 5, -3}};
		for (int i=0; i<move.length; i++) {
			x.move(move[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(double, int)}.
	 */
	@Test
	public void testMoveDoubleInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double move [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 5, -3}};
		for (int i=0; i<move.length; i++) {
			x.move(move[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testMoveRealLocalizable() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double move [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { 2, 8} };
		for (int i=0; i<move.length; i++) {
			x.move(new RealPoint(move[i]));
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(float[])}.
	 */
	@Test
	public void testMoveFloatArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		float move [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { 2, 8} };
		for (int i=0; i<move.length; i++) {
			x.move(move[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#move(double[])}.
	 */
	@Test
	public void testMoveDoubleArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double move [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { 2, 8} };
		for (int i=0; i<move.length; i++) {
			x.move(move[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(net.imglib2.RealLocalizable)}.
	 */
	@Test
	public void testSetPositionRealLocalizable() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double position [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { -3, 2} };
		for (int i=0; i<position.length; i++) {
			x.setPosition(new RealPoint(position[i]));
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(float[])}.
	 */
	@Test
	public void testSetPositionFloatArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		float position [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { -3, 2} };
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(double[])}.
	 */
	@Test
	public void testSetPositionDoubleArray() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double position [][] = { { 5,6 }, { -3, 2 }};
		double expected [][] = { { 5, 6}, { -3, 2} };
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(float, int)}.
	 */
	@Test
	public void testSetPositionFloatInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		float position [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 1, -3}};
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealRandomAccess#setPosition(double, int)}.
	 */
	@Test
	public void testSetPositionDoubleInt() {
		AbstractRealRandomAccessImpl<Integer> x = new AbstractRealRandomAccessImpl<Integer>(2);
		double position [] = { 4, -3, 1 };
		int dim [] = { 0, 1, 0 };
		double expected[][] = { { 4, 0}, { 4, -3 }, { 1, -3}};
		for (int i=0; i<position.length; i++) {
			x.setPosition(position[i], dim[i]);
			for (int j=0; j<2; j++) {
				assertEquals(expected[i][j], x.getDoublePosition(j), 0);
			}
		}
	}

}
