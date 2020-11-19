/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Charair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
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
package net.imglib2.img.basictypeaccess.nio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import net.imglib2.img.basictypeaccess.array.AbstractCharArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileCharAccess;

/**
 * Access for {@link CharBuffer}
 *
 * @author Mark Kittisopikul
 */
public class CharBufferAccess extends AbstractBufferAccess< CharBufferAccess, CharBuffer > implements VolatileCharAccess
{

	/**
	 * Automatically generated
	 */
	private static final long serialVersionUID = -7265085228179236189L;

	private static final int NUM_BYTES_PER_ENTITY = Character.BYTES;

	public CharBufferAccess( final CharBuffer buffer, final boolean isValid )
	{
		super( buffer, isValid );
	}

	public CharBufferAccess( final int numEntities, final boolean isValid )
	{
		super( CharBuffer.allocate( numEntities ), isValid );
	}

	public CharBufferAccess( final ByteBuffer buffer, final boolean isValid )
	{
		super( buffer.asCharBuffer(), isValid );
	}

	// Convenience constructors

	public CharBufferAccess( final CharBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public CharBufferAccess( final int numEntities )
	{
		this( numEntities, DEFAULT_IS_VALID );
	}

	public CharBufferAccess( final ByteBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public CharBufferAccess()
	{
		this( ( CharBuffer ) null, false );
	}

	/*
	 * CharAccess methods
	 */

	@Override
	public char getValue( final int index )
	{
		return buffer.get( index );
	}

	@Override
	public void setValue( final int index, final char value )
	{
		buffer.put( index, value );
	}

	/*
	 * AbstractBufferAccess methods
	 */

	@Override
	public int getNumBytesPerEntity()
	{
		return NUM_BYTES_PER_ENTITY;
	}

	@Override
	public CharBufferAccess newInstance( final ByteBuffer buffer, final boolean isValid )
	{
		return fromByteBuffer( buffer, isValid );
	}

	@Override
	public CharBufferAccess newInstance( final CharBuffer buffer, final boolean isValid )
	{
		return new CharBufferAccess( buffer, isValid );
	}

	@Override
	protected CharBuffer duplicateBuffer( final CharBuffer buffer )
	{
		return buffer.duplicate();
	}

	/**
	 * Override abstract implementation to allow for longer non-direct Buffers
	 * since ByteBuffer is restricted to Integer.MAX_VALUE entities.
	 */
	@Override
	protected CharBufferAccess allocate( final int numEntities, final boolean isDirect, final boolean isValid )
	{
		if ( isDirect )
			return super.allocate( numEntities, isDirect, isValid );
		else
			return new CharBufferAccess( numEntities, isValid );
	}

	/*
	 * Static methods
	 */

	/**
	 * Create a new CharBufferAccess from a ByteBuffer
	 *
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public static CharBufferAccess fromByteBuffer( final ByteBuffer buffer, final boolean isValid )
	{
		return new CharBufferAccess( buffer, isValid );
	}

	/*
	 * Bulk convenience methods
	 *
	 * These are not trivial because the buffer should be duplicated to prevent
	 * changing the current buffer state. The duplicated buffer is returned for
	 * chained operations.
	 */

	/**
	 * Copy values into a {@link AbstractCharArray}.
	 *
	 * @param array
	 * @return
	 * @see CharBuffer#get(char[])
	 */
	public CharBuffer getValues( final AbstractCharArray< ? > array )
	{
		return buffer.duplicate().get( array.getCurrentStorageArray() );
	}

	/**
	 * Copy values into a {@link AbstractCharArray}.
	 *
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see CharBuffer#get(char[], int, int)
	 */
	public CharBuffer getValues( final AbstractCharArray< ? > array, final int offset, final int length )
	{
		return buffer.duplicate().get( array.getCurrentStorageArray(), offset, length );
	}

	/**
	 * Copy values from a {@link AbstractCharArray}.
	 *
	 * @param array
	 * @return
	 * @see CharBuffer#put(char[])
	 */
	public CharBuffer setValues( final AbstractCharArray< ? > array )
	{
		return buffer.duplicate().put( array.getCurrentStorageArray() );
	}

	/**
	 * Copy values from a {@link AbstractCharArray}.
	 *
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see CharBuffer#put(char[], int, int)
	 */
	public CharBuffer setValues( final AbstractCharArray< ? > array, final int offset, final int length )
	{
		return buffer.duplicate().put( array.getCurrentStorageArray(), offset, length );
	}

	/**
	 * Copy values from another CharBufferAccess.
	 *
	 * @param access
	 * @return
	 */
	public CharBuffer setValues( final CharBufferAccess access )
	{
		return buffer.duplicate().put( access.getCurrentStorageArray() );
	}

}
