/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.type.numeric.complex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link ComplexFloatType}.
 * 
 * @author Curtis Rueden
 */
public class ComplexFloatTypeTest
{

	/**
	 * Tests {@link ComplexFloatType#equals(Object)}.
	 */
	@Test
	public void testEquals()
	{
		final ComplexFloatType b = new ComplexFloatType( 1.234f, 5.678f );

		// non-matching types and values
		final ComplexDoubleType i = new ComplexDoubleType( 8.765, 4.321 );
		assertFalse( b.equals( i ) );
		assertFalse( i.equals( b ) );

		// non-matching types
		final ComplexDoubleType i2 = new ComplexDoubleType( 1.234, 5.678 );
		assertFalse( b.equals( i2 ) );
		assertFalse( i2.equals( b ) );

		// non-matching values
		final ComplexFloatType i3 = new ComplexFloatType( 8.765f, 4.321f );
		assertFalse( b.equals( i3 ) );
		assertFalse( i3.equals( b ) );

		// matching type and value
		final ComplexFloatType i4 = new ComplexFloatType( 1.234f, 5.678f );
		assertTrue( b.equals( i4 ) );
		assertTrue( i4.equals( b ) );
	}


	/** Tests {@link ComplexFloatType#hashCode()}. */
	@Test
	public void testHashCode()
	{
		final ComplexFloatType b = new ComplexFloatType( 1.234f, 5.678f );
		assertEquals( 2133344667, b.hashCode() );
	}

}
