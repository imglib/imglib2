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
import java.nio.FloatBuffer;

import net.imglib2.img.basictypeaccess.array.AbstractFloatArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileFloatAccess;

/**
 * Access for {@link FloatBuffer}
 *
 * @author Mark Kittisopikul
 */
public class FloatBufferAccess extends AbstractBufferAccess< FloatBufferAccess, FloatBuffer > implements VolatileFloatAccess
{

	/**
	 * Automatically generated
	 */
	private static final long serialVersionUID = -7265085228179236189L;

	private static final int NUM_BYTES_PER_ENTITY = Float.BYTES;

	public FloatBufferAccess( final FloatBuffer buffer, final boolean isValid )
	{
		super( buffer, isValid );
	}

	public FloatBufferAccess( final int numEntities, final boolean isValid )
	{
		super( FloatBuffer.allocate( numEntities ), isValid );
	}

	public FloatBufferAccess( final ByteBuffer buffer, final boolean isValid )
	{
		super( buffer.asFloatBuffer(), isValid );
	}

	// Convenience constructors

	public FloatBufferAccess( final FloatBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public FloatBufferAccess( final int numEntities )
	{
		this( numEntities, DEFAULT_IS_VALID );
	}

	public FloatBufferAccess( final ByteBuffer buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public FloatBufferAccess()
	{
		this( ( FloatBuffer ) null, false );
	}

	/*
	 * FloatAccess methods
	 */

	@Override
	public float getValue( final int index )
	{
		return buffer.get( index );
	}

	@Override
	public void setValue( final int index, final float value )
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
	public FloatBufferAccess newInstance( final ByteBuffer buffer, final boolean isValid )
	{
		return fromByteBuffer( buffer, isValid );
	}

	@Override
	public FloatBufferAccess newInstance( final FloatBuffer buffer, final boolean isValid )
	{
		return new FloatBufferAccess( buffer, isValid );
	}

	@Override
	FloatBuffer duplicateBuffer( final FloatBuffer buffer )
	{
		return buffer.duplicate();
	}

	/**
	 * Override abstract implementation to allow for longer non-direct Buffers
	 * since ByteBuffer is restricted to Integer.MAX_VALUE entities.
	 */
	@Override
	FloatBufferAccess allocate( final int numEntities, final boolean isDirect, final boolean isValid )
	{
		if ( isDirect )
			return super.allocate( numEntities, isDirect, isValid );
		else
			return new FloatBufferAccess( numEntities, isValid );
	}

	/*
	 * Static methods
	 */

	/**
	 * Create a new FloatBufferAccess from a ByteBuffer
	 *
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public static FloatBufferAccess fromByteBuffer( final ByteBuffer buffer, final boolean isValid )
	{
		return new FloatBufferAccess( buffer, isValid );
	}

	/*
	 * Bulk convenience methods
	 *
	 * These are not trivial because the buffer should be duplicated to prevent
	 * changing the current buffer state. The duplicated buffer is returned for
	 * chained operations.
	 */

	/**
	 * Copy values into a {@link AbstractFloatArray}.
	 *
	 * @param array
	 * @return
	 * @see FloatBuffer#get(float[])
	 */
	public FloatBuffer getValues( final AbstractFloatArray< ? > array )
	{
		return buffer.duplicate().get( array.getCurrentStorageArray() );
	}

	/**
	 * Copy values into a {@link AbstractFloatArray}.
	 *
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see FloatBuffer#get(float[], int, int)
	 */
	public FloatBuffer getValues( final AbstractFloatArray< ? > array, final int offset, final int length )
	{
		return buffer.duplicate().get( array.getCurrentStorageArray(), offset, length );
	}

	/**
	 * Copy values from a {@link AbstractFloatArray}.
	 *
	 * @param array
	 * @return
	 * @see FloatBuffer#put(float[])
	 */
	public FloatBuffer setValues( final AbstractFloatArray< ? > array )
	{
		return buffer.duplicate().put( array.getCurrentStorageArray() );
	}

	/**
	 * Copy values from a {@link AbstractFloatArray}.
	 *
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see FloatBuffer#put(float[], int, int)
	 */
	public FloatBuffer setValues( final AbstractFloatArray< ? > array, final int offset, final int length )
	{
		return buffer.duplicate().put( array.getCurrentStorageArray(), offset, length );
	}

	/**
	 * Copy values from another FloatBufferAccess.
	 *
	 * @param access
	 * @return
	 */
	public FloatBuffer setValues( final FloatBufferAccess access )
	{
		return buffer.duplicate().put( access.getCurrentStorageArray() );
	}

}
