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
public class TestAbstractRealLocalizableSampler {

	static private class AbstractRealLocalizableSamplerImpl<T> extends AbstractRealLocalizableSampler<T> {
		public AbstractRealLocalizableSamplerImpl(int nDimensions) {
			super(nDimensions);
		}

		@Override
		public T get() {
			return null;
		}

		@Override
		public AbstractSampler<T> copy() {
			return null;
		}
		
		double getPosition(int d) {
			return position[d];
		}
		
	}
	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizableSampler#AbstractRealLocalizableSampler(int)}.
	 */
	@Test
	public void testAbstractRealLocalizableSampler() {
		AbstractRealLocalizableSamplerImpl<Integer> x = new AbstractRealLocalizableSamplerImpl<Integer>(2);
		assertEquals(x.numDimensions(), 2);
		for (int i=0; i<2; i++) {
			assertEquals(x.getPosition(i), 0, 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizableSampler#localize(float[])}.
	 */
	@Test
	public void testLocalizeFloatArray() {
		AbstractRealLocalizableSamplerImpl<Integer> x = new AbstractRealLocalizableSamplerImpl<Integer>(2);
		float [] p = { 1.1F, 2.2F };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals((float)(x.getPosition(i)), p[i], 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizableSampler#localize(double[])}.
	 */
	@Test
	public void testLocalizeDoubleArray() {
		AbstractRealLocalizableSamplerImpl<Integer> x = new AbstractRealLocalizableSamplerImpl<Integer>(2);
		double [] p = { 1.1, 2.2 };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals(x.getPosition(i), p[i], 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizableSampler#getFloatPosition(int)}.
	 */
	@Test
	public void testGetFloatPosition() {
		AbstractRealLocalizableSamplerImpl<Integer> x = new AbstractRealLocalizableSamplerImpl<Integer>(2);
		float [] p = { 1.1F, 2.2F };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals(x.getFloatPosition(i), p[i], 0);
		}
	}

	/**
	 * Test method for {@link net.imglib2.AbstractRealLocalizableSampler#getDoublePosition(int)}.
	 */
	@Test
	public void testGetDoublePosition() {
		AbstractRealLocalizableSamplerImpl<Integer> x = new AbstractRealLocalizableSamplerImpl<Integer>(2);
		double [] p = { 1.1, 2.2 };
		x.localize(p);
		for (int i=0; i<2; i++) {
			assertEquals(x.getDoublePosition(i), p[i], 0);
		}
	}

}
