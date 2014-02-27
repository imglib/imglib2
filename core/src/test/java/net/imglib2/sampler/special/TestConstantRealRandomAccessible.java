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

package net.imglib2.sampler.special;

import static org.junit.Assert.assertEquals;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;

import org.junit.Test;

/**
 * 
 * @author leek
 */
public class TestConstantRealRandomAccessible
{

	/**
	 * Test method for
	 * {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#ConstantRealRandomAccessible(java.lang.Object, int)}
	 * .
	 */
	@Test
	public void testConstantRealRandomAccessible()
	{
		new ConstantRealRandomAccessible< Integer >( 5, 2 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#numDimensions()}
	 * .
	 */
	@Test
	public void testNumDimensions()
	{
		final ConstantRealRandomAccessible< Integer > x = new ConstantRealRandomAccessible< Integer >( 5, 2 );
		assertEquals( 2, x.numDimensions() );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.sampler.special.ConstantRealRandomAccessible#realRandomAccess()}
	 * .
	 */
	@Test
	public void testRealRandomAccess()
	{
		final ConstantRealRandomAccessible< Integer > x = new ConstantRealRandomAccessible< Integer >( 5, 2 );
		RealRandomAccess< Integer > y = x.realRandomAccess();
		assertEquals( 5, y.get().intValue() );
		y = y.copyRealRandomAccess();
		assertEquals( 5, y.get().intValue() );
		final Sampler< Integer > z = y.copy();
		assertEquals( 5, z.get().intValue() );
	}

}
