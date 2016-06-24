/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.iterationorder;

import static org.junit.Assert.assertTrue;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;

import org.junit.Test;

/**
 * Tests to check code of iteration orders
 * 
 * @author Christian Dietz (dietzc85@googlemail.com)
 * 
 */
public class IterationOrderTests
{

	// Object to test
	FlatIterationOrder flatA = new FlatIterationOrder( new FinalInterval(
			new long[] { 10, 1, 20, 1, 1, 1, 30 } ) );

	// Exactly the same
	FlatIterationOrder flatB = new FlatIterationOrder( new FinalInterval(
			new long[] { 10, 1, 20, 1, 1, 1, 30 } ) );

	// Same if you remove dims of size one
	FlatIterationOrder flatC = new FlatIterationOrder( new FinalInterval(
			new long[] { 10, 20, 30 } ) );

	// not the same
	FlatIterationOrder flatD = new FlatIterationOrder( new FinalInterval(
			new long[] { 10, 1, 10, 1, 1, 1, 10 } ) );

	// not the same
	FlatIterationOrder flatE = new FlatIterationOrder( new FinalInterval(
			new long[] { 10, 10, 10 } ) );

	@Test
	public void testFlatIterationOrder()
	{

		// the same
		assertTrue( flatA.equals( flatB ) );
		assertTrue( flatA.equals( flatC ) );

		// not the same
		assertTrue( !flatA.equals( flatD ) );
		assertTrue( !flatA.equals( flatE ) );
	}
}
