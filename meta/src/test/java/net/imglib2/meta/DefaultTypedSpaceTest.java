/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Barry DeZonia
 */
public class DefaultTypedSpaceTest {

	private DefaultTypedSpace space;

	@Test
	public void test1() {
		space = new DefaultTypedSpace(3);
		assertTrue(space.axis(0).type() instanceof Axes.CustomType);
		assertTrue(space.axis(1).type() instanceof Axes.CustomType);
		assertTrue(space.axis(2).type() instanceof Axes.CustomType);
		space.axis(0).setType(Axes.CHANNEL);
		space.axis(1).setType(Axes.Z);
		space.axis(2).setType(Axes.TIME);
		assertEquals(Axes.CHANNEL, space.axis(0).type());
		assertEquals(Axes.Z, space.axis(1).type());
		assertEquals(Axes.TIME, space.axis(2).type());
	}

	@Test
	public void test2() {
		TypedAxis axis0 = new DefaultTypedAxis(Axes.CHANNEL);
		TypedAxis axis1 = new DefaultTypedAxis(Axes.FREQUENCY);
		space = new DefaultTypedSpace(axis0, axis1);
		assertEquals(Axes.CHANNEL, space.axis(0).type());
		assertEquals(Axes.FREQUENCY, space.axis(1).type());
		space.axis(0).setType(Axes.CHANNEL);
		space.axis(1).setType(Axes.Z);
		assertEquals(Axes.CHANNEL, space.axis(0).type());
		assertEquals(Axes.Z, space.axis(1).type());
	}

}
