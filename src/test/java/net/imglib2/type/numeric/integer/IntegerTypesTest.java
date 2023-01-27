/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import net.imglib2.type.logic.BitType;

public class IntegerTypesTest
{

	@Test
	public void testIntegerTypes()
	{
		/* smallestType(min, max) */
		assertEquals( BitType.class, IntegerTypes.smallestType( 0, 1 ).getClass() );
		assertEquals( UnsignedByteType.class, IntegerTypes.smallestType( 0, 128 ).getClass() );
		assertEquals( UnsignedByteType.class, IntegerTypes.smallestType( 0, 255 ).getClass() );
		assertEquals( UnsignedShortType.class, IntegerTypes.smallestType( 0, 256 ).getClass() );
		assertEquals( UnsignedShortType.class, IntegerTypes.smallestType( 0, 65535 ).getClass() );
		assertEquals( UnsignedIntType.class, IntegerTypes.smallestType( 0, 65536 ).getClass() );
		assertEquals( UnsignedIntType.class, IntegerTypes.smallestType( 0, ( long ) Integer.MAX_VALUE - Integer.MIN_VALUE ).getClass() );
		assertEquals( UnsignedLongType.class,
				IntegerTypes.smallestType( 0, ( long ) Integer.MAX_VALUE - Integer.MIN_VALUE + 1 ).getClass() );

		assertEquals( ByteType.class, IntegerTypes.smallestType( -128, 0 ).getClass() );
		assertEquals( ShortType.class, IntegerTypes.smallestType( -129, 0 ).getClass() );
		assertEquals( ShortType.class, IntegerTypes.smallestType( -32768, 0 ).getClass() );
		assertEquals( IntType.class, IntegerTypes.smallestType( -32769, 0 ).getClass() );
		assertEquals( IntType.class, IntegerTypes.smallestType( Integer.MIN_VALUE, 0 ).getClass() );
		assertEquals( LongType.class, IntegerTypes.smallestType( ( long ) Integer.MIN_VALUE - 1, 0 ).getClass() );

		/* smallestType(signed, max) */
		assertEquals( UnsignedByteType.class, IntegerTypes.smallestType( false, 128 ).getClass() );
		assertEquals( ShortType.class, IntegerTypes.smallestType( true, 128 ).getClass() );

		assertEquals( UnsignedShortType.class, IntegerTypes.smallestType( false, 32768 ).getClass() );
		assertEquals( IntType.class, IntegerTypes.smallestType( true, 32768 ).getClass() );

		assertEquals( UnsignedIntType.class, IntegerTypes.smallestType( false, ( long ) Integer.MAX_VALUE + 1 ).getClass() );
		assertEquals( LongType.class, IntegerTypes.smallestType( true, ( long ) Integer.MAX_VALUE + 1 ).getClass() );

		/* wrong usage */
		assertThrows( IllegalArgumentException.class, () -> IntegerTypes.smallestType( 0, -1 ) );
	}

}
