/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img.basictypeaccess.nio;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import net.imglib2.img.basictypeaccess.array.AbstractLongArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileLongAccess;

/**
 * Access for {@link LongBuffer}
 *
 * @author Mark Kittisopikul
 */
public class LongBufferAccess extends AbstractBufferAccess< LongBufferAccess, LongBuffer > implements VolatileLongAccess
{

	/**
	 * Automatically generated
	 */
	private static final long serialVersionUID = -7265085228179236189L;

	private static final int NUM_BYTES_PER_ENTITY = Long.BYTES;

	public LongBufferAccess( final LongBuffer buffer, final boolean isValid )
	{
		super( buffer, isValid );
	}

	public LongBufferAccess( final int numEntities, final boolean isValid )
	{
		super( LongBuffer.allocate( numEntities ), isValid );
	}

	public LongBufferAccess( final ByteBuffer buffer, final boolean isValid )
	{
		super( buffer.asLongBuffer(), isValid );
	}

	// Convenience constructors

	public LongBufferAccess( final LongBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public LongBufferAccess( final int numEntities )
	{
		this( numEntities, DEFAULT_IS_VALID );
	}

	public LongBufferAccess( final ByteBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public LongBufferAccess()
	{
		this( ( LongBuffer ) null, false );
	}

	/*
	 * LongAccess methods
	 */

	@Override
	public long getValue( final int index )
	{
		return buffer.get( index );
	}

	@Override
	public void setValue( final int index, final long value )
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
	public LongBufferAccess newInstance( final ByteBuffer buffer, final boolean isValid )
	{
		return fromByteBuffer( buffer, isValid );
	}

	@Override
	public LongBufferAccess newInstance( final LongBuffer buffer, final boolean isValid )
	{
		return new LongBufferAccess( buffer, isValid );
	}

	@Override
	LongBuffer duplicateBuffer( final LongBuffer buffer )
	{
		return buffer.duplicate();
	}

	/**
	 * Override abstract implementation to allow for longer non-direct Buffers
	 * since ByteBuffer is restricted to Integer.MAX_VALUE entities.
	 */
	@Override
	LongBufferAccess allocate( final int numEntities, final boolean isDirect, final boolean isValid )
	{
		if ( isDirect )
			return super.allocate( numEntities, isDirect, isValid );
		else
			return new LongBufferAccess( numEntities, isValid );
	}

	/*
	 * Static methods
	 */

	/**
	 * Create a new LongBufferAccess from a ByteBuffer
	 *
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public static LongBufferAccess fromByteBuffer( final ByteBuffer buffer, final boolean isValid )
	{
		return new LongBufferAccess( buffer, isValid );
	}

	/*
	 * Bulk convenience methods
	 *
	 * These are not trivial because the buffer should be duplicated to prevent
	 * changing the current buffer state. The duplicated buffer is returned for
	 * chained operations.
	 */

	/**
	 * Copy values into a {@link AbstractLongArray}.
	 *
	 * @param array
	 * @return
	 * @see LongBuffer#get(long[])
	 */
	public LongBuffer getValues( final AbstractLongArray< ? > array )
	{
		return buffer.duplicate().get( array.getCurrentStorageArray() );
	}

	/**
	 * Copy values into a {@link AbstractLongArray}.
	 *
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see LongBuffer#get(long[], int, int)
	 */
	public LongBuffer getValues( final AbstractLongArray< ? > array, final int offset, final int length )
	{
		return buffer.duplicate().get( array.getCurrentStorageArray(), offset, length );
	}

	/**
	 * Copy values from a {@link AbstractLongArray}.
	 *
	 * @param array
	 * @return
	 * @see LongBuffer#put(long[])
	 */
	public LongBuffer setValues( final AbstractLongArray< ? > array )
	{
		return buffer.duplicate().put( array.getCurrentStorageArray() );
	}

	/**
	 * Copy values from a {@link AbstractLongArray}.
	 *
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see LongBuffer#put(long[], int, int)
	 */
	public LongBuffer setValues( final AbstractLongArray< ? > array, final int offset, final int length )
	{
		return buffer.duplicate().put( array.getCurrentStorageArray(), offset, length );
	}

	/**
	 * Copy values from another LongBufferAccess.
	 *
	 * @param access
	 * @return
	 */
	public LongBuffer setValues( final LongBufferAccess access )
	{
		return buffer.duplicate().put( access.getCurrentStorageArray() );
	}

}
