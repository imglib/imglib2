/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.meta.units;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link DefaultUnitService}.
 * 
 * @author Barry DeZonia
 */
public class DefaultUnitServiceTest {

	/** Tests {@link DefaultUnitService#value(double, String, String)}. */
	@Test
	public void testConversion() {
		final DefaultUnitService c = new DefaultUnitService();
		// a peeb is 5 meters
		c.defineUnit("peeb", "m", 5);
		assertEquals(5.0, c.value(1, "peeb", "m"), 0);
		assertEquals(1 / 5.0, c.value(1, "m", "peeb"), 0);
		// a wuzpang is 2 peebs
		c.defineUnit("wuzpang", "peeb", 2);
		assertEquals(2.0, c.value(1, "wuzpang", "peeb"), 0);
		assertEquals(1 / 2.0, c.value(1, "peeb", "wuzpang"), 0);
		assertEquals(2.0 * 5, c.value(1, "wuzpang", "m"), 0);
		assertEquals(1.0 / (2.0 * 5), c.value(1, "m", "wuzpang"), 0);
		// a plook is 7 wuzpangs
		c.defineUnit("plook", "wuzpang", 7);
		assertEquals(1 / 7.0, c.value(1, "wuzpang", "plook"), 0);
		assertEquals(7.0, c.value(1, "plook", "wuzpang"), 0);
		assertEquals(7.0 * 2 * 5, c.value(1, "plook", "m"), 0);
		assertEquals(1 / (7.0 * 2 * 5), c.value(1, "m", "plook"), 0);
		// a korch is 4 m/s^2
		c.defineUnit("korch", "m/s^2", 4);
		assertEquals(1 / 4.0, c.value(1, "m/s^2", "korch"), 0);
		assertEquals(1000 * 1 / 4.0, c.value(1, "km/s^2", "korch"), 0);
		// define a scale/offset unit
		c.defineUnit("MyCel", "K", 1, 273.15);
		assertEquals(c.value(1, "Cel", "K"), c.value(1, "MyCel", "K"), 0);
	}

	/**
	 * Tests {@link DefaultUnitService#value(double, String, String)} with invalid
	 * arguments.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBadConversion() {
		final DefaultUnitService c = new DefaultUnitService();
		c.value(1, "kelvin", "meter");
	}

}
