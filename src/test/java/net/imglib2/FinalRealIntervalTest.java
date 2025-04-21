/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 *	Tests {@link FinalRealInterval}.
 *
 *  @author Matthias Arzt
 */
public class FinalRealIntervalTest
{

	@Test
	public void testEquals()
	{
		FinalRealInterval interval = FinalRealInterval.createMinMax( 1.0, 2.5, 3.0, 4.0 );
		FinalRealInterval same = FinalRealInterval.createMinMax( 1.0, 2.5, 3.0, 4.0 );
		FinalRealInterval different = FinalRealInterval.createMinMax( 1.0, 2.5001, 3.0, 4.0 );
		assertTrue( interval.equals( same ) );
		assertFalse( interval.equals( different ) );
	}

	@Test
	public void testHashCode()
	{
		FinalRealInterval interval = FinalRealInterval.createMinMax( 1.0, 2.5, 3.0, 4.0 );
		FinalRealInterval same = FinalRealInterval.createMinMax( 1.0, 2.5, 3.0, 4.0 );
		assertEquals( interval.hashCode(), same.hashCode() );
	}

	@Test
	public void testToString()
	{
		final FinalRealInterval interval = FinalRealInterval.createMinMax( 1.0, 2.0, 3.0, 4.0 );
		assertEquals( "FinalRealInterval [(1.0, 2.0) -- (3.0, 4.0)]", interval.toString() );
	}
}
