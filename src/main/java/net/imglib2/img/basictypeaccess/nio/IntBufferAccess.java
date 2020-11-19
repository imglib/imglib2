/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Intair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
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
import java.nio.IntBuffer;

import net.imglib2.img.basictypeaccess.array.AbstractIntArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileIntAccess;

/**
 * Access for {@link IntBuffer}
 * 
 * @author Mark Kittisopikul
 */
public class IntBufferAccess extends AbstractBufferAccess< IntBufferAccess, IntBuffer > implements VolatileIntAccess
{

	/**
	 * Automatically generated 
	 */
	private static final long serialVersionUID = -7265085228179236189L;

	private static final int NUM_BYTES_PER_ENTITY = Integer.BYTES;

	public IntBufferAccess( final IntBuffer buffer, final boolean isValid )
	{
		super(buffer, isValid);
	}
	
	public IntBufferAccess( final int numEntities, final boolean isValid ) {
		super( IntBuffer.allocate(numEntities), isValid );
	}
	
	public IntBufferAccess( final ByteBuffer buffer, boolean isValid) {
		super(buffer.asIntBuffer(), isValid);
	}
	
	// Convenience constructors
	
	public IntBufferAccess( final IntBuffer buffer ) { this(buffer,       DEFAULT_IS_VALID); }
	public IntBufferAccess( final int numEntities   ) { this(numEntities, DEFAULT_IS_VALID); }
	public IntBufferAccess( final ByteBuffer buffer ) { this(buffer,      DEFAULT_IS_VALID); }
	public IntBufferAccess() { this( (IntBuffer) null, false); }

	/*
	 * IntAccess methods
	 */
	
	@Override
	public int getValue( int index ) {
		return buffer.get( index );
	}

	@Override
	public void setValue( int index, int value ) {
		buffer.put( index, value );
	}
	
	/*
	 * AbstractBufferAccess methods
	 */

	@Override
	public int getNumBytesPerEntity() {
		return NUM_BYTES_PER_ENTITY;
	}

	@Override
	public IntBufferAccess newInstance(ByteBuffer buffer, boolean isValid) {
		return fromByteBuffer(buffer, isValid);
	}
	
	@Override
	public IntBufferAccess newInstance(IntBuffer buffer, boolean isValid) {
		return new IntBufferAccess(buffer, isValid);
	}
	
	@Override
	protected IntBuffer duplicateBuffer(IntBuffer buffer) {
		return buffer.duplicate();
	}
	
	/**
	 * Override abstract implementation to allow for longer non-direct Buffers since
	 * ByteBuffer is restricted to Integer.MAX_VALUE entities.
	 */
	@Override
	protected IntBufferAccess allocate( int numEntities, boolean isDirect, boolean isValid) {
		if(isDirect)
			return super.allocate( numEntities, isDirect, isValid );
		else
			return new IntBufferAccess( numEntities, isValid );
	}
	
	/*
	 * Static methods
	 */

	/**
	 * Create a new IntBufferAccess from a ByteBuffer
	 * 
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public static IntBufferAccess fromByteBuffer(ByteBuffer buffer, boolean isValid) {
		return new IntBufferAccess(buffer, isValid);
	}

	
	/*
	 * Bulk convenience methods
	 * 
	 * These are not trivial because the buffer should be duplicated
	 * to prevent changing the current buffer state. The duplicated
	 * buffer is returned for chained operations.
	 */
	
	/**
	 * Copy values into a {@link AbstractIntArray}.
	 * 
	 * @param array
	 * @return
	 * @see IntBuffer#get(int[])
	 */
	public IntBuffer getValues(final AbstractIntArray< ? > array) {
		return buffer.duplicate().get( array.getCurrentStorageArray() );
	}
	
	/**
	 * Copy values into a {@link AbstractIntArray}.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see IntBuffer#get(int[], int, int)
	 */
	public IntBuffer getValues(final AbstractIntArray< ? > array, int offset, int length) {
		return buffer.duplicate().get( array.getCurrentStorageArray(), offset, length );
	}
	
	/**
	 * Copy values from a {@link AbstractIntArray}.
	 * 
	 * @param array
	 * @return
	 * @see IntBuffer#put(int[])
	 */
	public IntBuffer setValues(final AbstractIntArray< ? > array) {
		return buffer.duplicate().put( array.getCurrentStorageArray() );
	}
	
	/**
	 * Copy values from a {@link AbstractIntArray}.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see IntBuffer#put(int[], int, int)
	 */
	public IntBuffer setValues(final AbstractIntArray< ? > array, int offset, int length) {
		return buffer.duplicate().put( array.getCurrentStorageArray(), offset, length );
	}
	
	/**
	 * Copy values from another IntBufferAccess.
	 * 
	 * @param access
	 * @return
	 */
	public IntBuffer setValues(final IntBufferAccess access) {
		return buffer.duplicate().put( access.getCurrentStorageArray() );
	}

}
