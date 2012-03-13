/**
 *
 */
package net.imglib2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class TestAbstractRealLocalizable {

	static private class AbstractRealLocalizableImpl extends AbstractRealLocalizable {
		public AbstractRealLocalizableImpl(final int nDimensions) {
			super(nDimensions);
		}

		double getPosition(final int d) {
			return position[d];
		}
	}
	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizable#AbstractRealLocalizableSampler(int)}.
	 */
	@Test
	public void testAbstractRealLocalizableSampler() {
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl(2);
		assertEquals(x.numDimensions(), 2);
		for (int i=0; i<2; i++) {
			assertEquals(x.getPosition(i), 0, 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizable#localize(float[])}.
	 */
	@Test
	public void testLocalizeFloatArray() {
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl(2);
		final float [] p = { 1.1F, 2.2F };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals((float)(x.getPosition(i)), p[i], 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizable#localize(double[])}.
	 */
	@Test
	public void testLocalizeDoubleArray() {
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl(2);
		final double [] p = { 1.1, 2.2 };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals(x.getPosition(i), p[i], 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizable#getFloatPosition(int)}.
	 */
	@Test
	public void testGetFloatPosition() {
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl(2);
		final float [] p = { 1.1F, 2.2F };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals(x.getFloatPosition(i), p[i], 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizable#getDoublePosition(int)}.
	 */
	@Test
	public void testGetDoublePosition() {
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl(2);
		final double [] p = { 1.1, 2.2 };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals(x.getDoublePosition(i), p[i], 0);
		}
	}

}
