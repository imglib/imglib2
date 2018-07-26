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

/**
 * Utility and helper methods for accesses ({@link ByteAccess} etc).
 *
 * @author Philipp Hanslovsky
 */
public class Accesses
{

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
	 */
	public static void copy(
			final ByteAccess src,
			final int srcPos,
			final ByteAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

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
	 */
	public static void copy(
			final CharAccess src,
			final int srcPos,
			final CharAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

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
	 */
	public static void copy(
			final DoubleAccess src,
			final int srcPos,
			final DoubleAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

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
	 */
	public static void copy(
			final FloatAccess src,
			final int srcPos,
			final FloatAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

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
	 */
	public static void copy(
			final IntAccess src,
			final int srcPos,
			final IntAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

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
	 */
	public static void copy(
			final LongAccess src,
			final int srcPos,
			final LongAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

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
	 */
	public static void copy(
			final ShortAccess src,
			final int srcPos,
			final ShortAccess dest,
			final int destPos,
			final int length )
	{

		if ( src == dest )
		{
			if ( srcPos == destPos ) { return; }
			if ( srcPos < destPos )
			{
				for ( int index = length - 1; index >= 0; --index )
				{
					dest.setValue( index + destPos, src.getValue( index + srcPos ) );
				}
				return;
			}
		}

		for ( int index = 0; index < length; ++index )
		{
			dest.setValue( index + destPos, src.getValue( index + srcPos ) );
		}
	}

}
