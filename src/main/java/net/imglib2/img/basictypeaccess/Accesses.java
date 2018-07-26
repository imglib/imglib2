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

package net.imglib2.img.basictypeaccess;

import java.util.function.Function;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Utility and helper methods for accesses ({@link ByteAccess} etc).
 *
 * @author Philipp Hanslovsky
 */
public class Accesses
{

	private static final Function< ByteAccess, ByteType> BYTE_FACTORY = ByteType::new;

	private static final Function< ShortAccess, ShortType> SHORT_FACTORY =  ShortType::new;

	private static final Function< IntAccess, IntType> INT_FACTORY =  IntType::new;

	private static final Function< LongAccess, LongType> LONG_FACTORY =  LongType::new;

	private static final Function< DoubleAccess, DoubleType> DOUBLE_FACTORY =  DoubleType::new;

	private static final Function< FloatAccess, FloatType> FLOAT_FACTORY =  FloatType::new;

	private Accesses() {}

	/**
	 *
	 * Following {@link System#arraycopy}, copies {@code length} elements from
	 * the specified source access, beginning at the specified position
	 * {@code srcPos}, to the specified position of the destination array
	 * {@code destPos}. A subsequence of access components are copied from the
	 * source access referenced by {@code src} to the destination access
	 * referenced by {@code dest}. The number of components copied is equal to
	 * the {@code length} argument. The components at positions {@code srcPos}
	 * through {@code srcPos+length-1} in the source array are copied into
	 * positions {@code destPos} through {@code destPos+length-1}, respectively,
	 * of the destination array.
	 *
	 * If the {@code src} and {@code dest} arguments refer to the same array
	 * object, then the copying is performed as if the components at positions
	 * {@code srcPos} through {@code srcPos+length-1} were first copied to a
	 * temporary array with {@code length} components and then the contents of
	 * the temporary array were copied into positions destPos through
	 * {@code destPos+length-1} of the destination array.
	 *
	 * @param src
	 *            the source access.
	 * @param srcPos
	 *            starting position in the source access.
	 * @param dest
	 *            the destination access.
	 * @param destPos
	 *            starting position in the destination access.
	 * @param length
	 *            the number of access elements to be copied
	 * @param <A>
	 *            Common ancestor type for {@code src} and {@code dest}
	 */
	public static < A > void copy(
			final A src,
			final int srcPos,
			final A dest,
			final int destPos,
			final int length )
	{
		Function typeFactory = getTypeFactory( src );

		if ( !typeFactory.equals( getTypeFactory( dest ) ) )
			throw new IllegalArgumentException( "src and dest types are not compatible: "
					+ src.getClass().getName() + " "
					+ dest.getClass().getName() );

		copy( src, srcPos, dest, destPos, length, typeFactory );
	}

	private static < A, T extends NativeType< T >> void copy(
			A src,
			int srcPos,
			A dest,
			int destPos,
			int length,
			Function<A, T> typeFactory)
	{
		T srcType = typeFactory.apply( src );
		T destType = typeFactory.apply( dest );

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = 0, dp = length + destPos - 1, sp = length + srcPos - 1; index < length; ++index, --dp, --sp )
				{
					destType.updateIndex( dp );
					srcType.updateIndex( sp );
					destType.set( srcType );
				}
				return;
			}
		}

		for ( int index = 0, dp = destPos, sp = srcPos; index < length; ++index, ++dp, ++sp )
		{
			destType.updateIndex( dp );
			srcType.updateIndex( sp );
			destType.set( srcType );
		}
	}

	private static < A > Function<A, ? extends NativeType< ? > > getTypeFactory( A a ) {
		if ( a instanceof ByteAccess )
			return ( Function< A, ByteType> ) BYTE_FACTORY;
		if ( a instanceof ShortAccess )
			return ( Function< A, ShortType> ) SHORT_FACTORY;
		if ( a instanceof IntAccess )
			return ( Function< A, IntType> ) INT_FACTORY;
		if ( a instanceof LongAccess )
			return ( Function< A, LongType> ) LONG_FACTORY;
		if ( a instanceof DoubleAccess )
			return ( Function< A, DoubleType> ) DOUBLE_FACTORY;
		if ( a instanceof FloatAccess )
			return( Function< A, FloatType> ) FLOAT_FACTORY;

		throw new IllegalArgumentException( "Access is not supported: " + a.getClass().getName() );
	}

}
