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

package net.imglib2.io.img.virtual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;

import org.junit.Test;

/**
 * TODO
 * 
 * @author Barry DeZonia
 */
public class VirtualImgTest {

	@Test
	public void testVariousThings() {

		// open image

		VirtualImg<?> image = null;
		try {
			final URL fileURL = getClass().getResource("TestImage.tif");
			image = VirtualImg.create(fileURL.getFile(), false);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		assertNotNull(image);

		// test out factory

		final ImgFactory<? extends RealType<?>> factory = image.factory();
		try {
			factory.create(new long[] { 1, 2, 3 }, null);
		}
		catch (final UnsupportedOperationException e) {
			assertTrue(true);
		}

		// test out cursor

		long numElements = 0;
		final VirtualCursor<? extends RealType<?>> cursor = image.cursor();
		while (cursor.hasNext()) {
			cursor.next();
			numElements++;
		}
		assertEquals(20 * 30 * 10, numElements);
		assertNotNull(cursor.getCurrentPlane());

		// test out random access

		VirtualRandomAccess<? extends RealType<?>> accessor = image.randomAccess();
		final long[] pos = new long[3];
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 30; y++) {
					pos[0] = x;
					pos[1] = y;
					pos[2] = z;
					accessor.setPosition(pos);
					assertEquals(x + 2 * y + 3 * z, accessor.get().getRealDouble(), 0);
				}
			}
		}
		assertNotNull(accessor.getCurrentPlane());

		final long xDim = image.dimension(0);

		// test byte only code
		try {
			final URL fileURL = getClass().getResource("TestImage.tif");
			image = VirtualImg.create(fileURL.getFile(), true);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		assertNotNull(image);
		assertEquals(xDim * 2, image.dimension(0));

		accessor = image.randomAccess();
		for (int x = 0; x < 20; x++) {
			for (int z = 0; z < 10; z++) {
				for (int y = 0; y < 30; y++) {
					// NOTE - the following code might fail on a Power PC architecture.
					// Might need to test for platform & order hi/lo tests appropriately
					final short expected = (short) (x + 2 * y + 3 * z);
					final int hi = (expected & 0xff00) >> 8;
					final int lo = expected & 0xff;
					pos[1] = y;
					pos[2] = z;
					pos[0] = 2 * x;
					accessor.setPosition(pos);
					assertEquals(hi, accessor.get().getRealDouble(), 0);
					pos[0] = 2 * x + 1;
					accessor.setPosition(pos);
					assertEquals(lo, accessor.get().getRealDouble(), 0);
				}
			}
		}
		assertNotNull(accessor.getCurrentPlane());
	}

}
