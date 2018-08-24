/*-
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

import static net.imglib2.img.basictypeaccess.AccessFlags.DIRTY;
import static net.imglib2.img.basictypeaccess.AccessFlags.VOLATILE;

import java.util.Set;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.BooleanArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DirtyBooleanArray;
import net.imglib2.img.basictypeaccess.array.DirtyByteArray;
import net.imglib2.img.basictypeaccess.array.DirtyCharArray;
import net.imglib2.img.basictypeaccess.array.DirtyDoubleArray;
import net.imglib2.img.basictypeaccess.array.DirtyFloatArray;
import net.imglib2.img.basictypeaccess.array.DirtyIntArray;
import net.imglib2.img.basictypeaccess.array.DirtyLongArray;
import net.imglib2.img.basictypeaccess.array.DirtyShortArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileBooleanArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileByteArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileCharArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileDoubleArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileFloatArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileIntArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileLongArray;
import net.imglib2.img.basictypeaccess.volatiles.array.DirtyVolatileShortArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileBooleanArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileByteArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileCharArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileDoubleArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileFloatArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileIntArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileLongArray;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.PrimitiveType;
import net.imglib2.type.NativeTypeFactory;

/**
 * Given a {@link PrimitiveType} and {@link AccessFlags} creates a specific
 * {@link ArrayDataAccess}. For example, {@code BYTE} with flags {@code DIRTY}
 * and {@code VOLATILE} specifies {@link DirtyVolatileByteArray}.
 *
 * @author Tobias Pietzsch
 */
public class ArrayDataAccessFactory
{
	public static < T extends NativeType< T >, A > A get(
			final T type )
	{
		return get( type, AccessFlags.setOf() );
	}

	public static < T extends NativeType< T >, A > A get(
			final T type,
			final Set< AccessFlags > flags )
	{
		return get( type.getNativeTypeFactory().getPrimitiveType(), flags );
	}

	public static < A extends ArrayDataAccess< A > > A get(
			final NativeTypeFactory< ?, ? super A > typeFactory )
	{
		return get( typeFactory.getPrimitiveType(), AccessFlags.setOf() );
	}

	public static < A extends ArrayDataAccess< A > > A get(
			final NativeTypeFactory< ?, ? super A > typeFactory,
			final Set< AccessFlags > flags )
	{
		return get( typeFactory.getPrimitiveType(), flags );
	}

	@SuppressWarnings( "unchecked" )
	public static < A extends ArrayDataAccess< A > > A get(
			final PrimitiveType primitiveType,
			final Set< AccessFlags > flags )
	{
		final boolean dirty = flags.contains( DIRTY );
		final boolean volatil = flags.contains( VOLATILE );
		switch ( primitiveType )
		{
		case BOOLEAN:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileBooleanArray( 0, true )
							: ( A ) new DirtyBooleanArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileBooleanArray( 0, true )
							: ( A ) new BooleanArray( 0 ) );
		case BYTE:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileByteArray( 0, true )
							: ( A ) new DirtyByteArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileByteArray( 0, true )
							: ( A ) new ByteArray( 0 ) );
		case CHAR:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileCharArray( 0, true )
							: ( A ) new DirtyCharArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileCharArray( 0, true )
							: ( A ) new CharArray( 0 ) );
		case DOUBLE:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileDoubleArray( 0, true )
							: ( A ) new DirtyDoubleArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileDoubleArray( 0, true )
							: ( A ) new DoubleArray( 0 ) );
		case FLOAT:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileFloatArray( 0, true )
							: ( A ) new DirtyFloatArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileFloatArray( 0, true )
							: ( A ) new FloatArray( 0 ) );
		case INT:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileIntArray( 0, true )
							: ( A ) new DirtyIntArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileIntArray( 0, true )
							: ( A ) new IntArray( 0 ) );
		case LONG:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileLongArray( 0, true )
							: ( A ) new DirtyLongArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileLongArray( 0, true )
							: ( A ) new LongArray( 0 ) );
		case SHORT:
			return dirty
					? ( volatil
							? ( A ) new DirtyVolatileShortArray( 0, true )
							: ( A ) new DirtyShortArray( 0 ) )
					: ( volatil
							? ( A ) new VolatileShortArray( 0, true )
							: ( A ) new ShortArray( 0 ) );
		default:
			throw new IllegalArgumentException();
		}
	}
}
