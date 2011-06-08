/**
 * 
 */
package net.imglib2.sampler.special;

import static org.junit.Assert.*;

import net.imglib2.RandomAccess;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class TestConstantRandomAccessible {

	/**
	 * Test method for {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#ConstantRealRandomAccessible(java.lang.Object, int)}.
	 */
	@Test
	public void testConstantRandomAccessible() {
		new ConstantRandomAccessible<Integer>(5, 2);
	}

	/**
	 * Test method for {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#numDimensions()}.
	 */
	@Test
	public void testNumDimensions() {
		ConstantRandomAccessible<Integer> x = new ConstantRandomAccessible<Integer>(5, 2);
		assertEquals(2, x.numDimensions());
	}

	/**
	 * Test method for {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#realRandomAccess()}.
	 */
	@Test
	public void testRandomAccess() {
		ConstantRandomAccessible<Integer> x = new ConstantRandomAccessible<Integer>(5, 2);
		RandomAccess<Integer> y = x.randomAccess();
		assertEquals(5, y.get().intValue());
		y = y.copyRandomAccess();
		assertEquals(5, y.get().intValue());
		Sampler<Integer> z = y.copy();
		assertEquals(5, z.get().intValue());
	}

}
