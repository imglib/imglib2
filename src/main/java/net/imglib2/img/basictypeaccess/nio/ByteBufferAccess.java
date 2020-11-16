/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Byteair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
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

import net.imglib2.img.basictypeaccess.array.AbstractByteArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileByteAccess;

/**
 * Access for {@link ByteBuffer}
 * 
 * @author Mark Kittisopikul
 */
public class ByteBufferAccess extends AbstractBufferAccess< ByteBufferAccess, ByteBuffer > implements VolatileByteAccess
{

	/**
	 * Automatically generated 
	 */
	private static final long serialVersionUID = -7265085228179236189L;

	private static final int NUM_BYTES = Byte.BYTES;

	public ByteBufferAccess( final ByteBuffer buffer, final boolean isValid )
	{
		super(buffer, isValid);
	}
	
	public ByteBufferAccess( final int numEntities, final boolean isValid ) {
		super( ByteBuffer.allocate(numEntities), isValid );
	}
	
	// Convenience constructors
	
	public ByteBufferAccess( final int numEntities   ) { this(numEntities, DEFAULT_IS_VALID); }
	public ByteBufferAccess() { this( (ByteBuffer) null, false); }

	/*
	 * ByteAccess methods
	 */
	
	@Override
	public byte getValue( int index ) {
		return buffer.get( index );
	}

	@Override
	public void setValue( int index, byte value ) {
		buffer.put( index, value );
	}
	
	/*
	 * AbstractBufferAccess methods
	 */

	@Override
	public int getNumBytes() {
		return NUM_BYTES;
	}

	@Override
	public ByteBufferAccess newInstance(ByteBuffer buffer, boolean isValid) {
		return fromByteBuffer(buffer, isValid);
	}
	
	@Override
	protected ByteBuffer duplicateBuffer(ByteBuffer buffer) {
		return buffer.duplicate();
	}
	
	/*
	 * Static methods
	 */

	/**
	 * Create a new ByteBufferAccess from a ByteBuffer
	 * 
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public static ByteBufferAccess fromByteBuffer(ByteBuffer buffer, boolean isValid) {
		return new ByteBufferAccess(buffer, isValid);
	}

	
	/*
	 * Bulk convenience methods
	 * 
	 * These are not trivial because the buffer should be duplicated
	 * to prevent changing the current buffer state. The duplicated
	 * buffer is returned for chained operations.
	 */
	
	/**
	 * Copy values into a {@link AbstractByteArray}.
	 * 
	 * @param array
	 * @return
	 * @see ByteBuffer#get(byte[])
	 */
	public ByteBuffer getValues(final AbstractByteArray< ? > array) {
		return buffer.duplicate().get( array.getCurrentStorageArray() );
	}
	
	/**
	 * Copy values into a {@link AbstractByteArray}.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see ByteBuffer#get(byte[], int, int)
	 */
	public ByteBuffer getValues(final AbstractByteArray< ? > array, int offset, int length) {
		return buffer.duplicate().get( array.getCurrentStorageArray(), offset, length );
	}
	
	/**
	 * Copy values from a {@link AbstractByteArray}.
	 * 
	 * @param array
	 * @return
	 * @see ByteBuffer#put(byte[])
	 */
	public ByteBuffer setValues(final AbstractByteArray< ? > array) {
		return buffer.duplicate().put( array.getCurrentStorageArray() );
	}
	
	/**
	 * Copy values from a {@link AbstractByteArray}.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see ByteBuffer#put(byte[], int, int)
	 */
	public ByteBuffer setValues(final AbstractByteArray< ? > array, int offset, int length) {
		return buffer.duplicate().put( array.getCurrentStorageArray(), offset, length );
	}
	
	/**
	 * Copy values from another ByteBufferAccess.
	 * 
	 * @param access
	 * @return
	 */
	public ByteBuffer setValues(final ByteBufferAccess access) {
		return buffer.duplicate().put( access.getCurrentStorageArray() );
	}

}
