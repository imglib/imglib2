/**
 * 
 */
package net.imglib2.sampler.special;

import static org.junit.Assert.*;

import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class TestConstantRealRandomAccessible {

	/**
	 * Test method for {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#ConstantRealRandomAccessible(java.lang.Object, int)}.
	 */
	@Test
	public void testConstantRealRandomAccessible() {
		new ConstantRealRandomAccessible<Integer>(5, 2);
	}

	/**
	 * Test method for {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#numDimensions()}.
	 */
	@Test
	public void testNumDimensions() {
		ConstantRealRandomAccessible<Integer> x = new ConstantRealRandomAccessible<Integer>(5, 2);
		assertEquals(2, x.numDimensions());
	}

	/**
	 * Test method for {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#realRandomAccess()}.
	 */
	@Test
	public void testRealRandomAccess() {
		ConstantRealRandomAccessible<Integer> x = new ConstantRealRandomAccessible<Integer>(5, 2);
		RealRandomAccess<Integer> y = x.realRandomAccess();
		assertEquals(5, y.get().intValue());
		y = y.copyRealRandomAccess();
		assertEquals(5, y.get().intValue());
		Sampler<Integer> z = y.copy();
		assertEquals(5, z.get().intValue());
	}

}
