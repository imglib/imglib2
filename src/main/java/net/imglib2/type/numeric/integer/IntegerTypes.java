/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Arrays;
import java.util.List;

import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.IntegerType;

public class IntegerTypes
{
	private static List< ? extends IntegerType< ? > > signedTypes = Arrays.asList( new ByteType(), new ShortType(), new IntType(), new LongType() );

	private static List< ? extends IntegerType< ? > > unsignedTypes = Arrays.asList( new BitType(), new UnsignedByteType(), new UnsignedShortType(), new UnsignedIntType(), new UnsignedLongType() );

	private IntegerTypes()
	{
		// NB: prevent instantiation of static utility class
	}

	/**
	 * Get an {@link IntegerType} that can hold the given {@code max} value.
	 * 
	 * The smallest byte-size type matching the constraints will be selected.
	 * 
	 * @param signed
	 *            - {@code true} if a signed {@link IntegerType} is required
	 * @param max
	 *            - the expected maximum value
	 * @return an {@link IntegerType} matching the given constraints
	 */
	public static IntegerType< ? > smallestType( boolean signed, long max )
	{
		return smallestType( signed ? -1 : 0, max );
	}

	/**
	 * Get an {@link IntegerType} that can hold the given {@code min} and
	 * {@code max} values.
	 * 
	 * The smallest byte-size type matching the constraints will be selected,
	 * with a preference for unsigned types.
	 * 
	 * @param min
	 *            - the expected minimum value
	 * @param max
	 *            - the expected maximum value
	 * @return an {@link IntegerType} matching the given constraints
	 */
	public static IntegerType< ? > smallestType( long min, long max )
	{
		if ( min > max )
			throw new IllegalArgumentException( "Wrong usage: min (" + min + ") > max (" + max + ")" );
		if ( min >= 0 ) // unsigned
		{
			return unsignedTypes.stream().filter( i -> min >= i.getMinValue() && max <= i.getMaxValue() ).findFirst().get().createVariable();
		}
		return signedTypes.stream().filter( i -> min >= i.getMinValue() && max <= i.getMaxValue() ).findFirst().get().createVariable();
	}
}
