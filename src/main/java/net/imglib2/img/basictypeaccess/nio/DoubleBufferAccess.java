/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Doubleair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
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
import java.nio.DoubleBuffer;

import net.imglib2.img.basictypeaccess.array.AbstractDoubleArray;
import net.imglib2.img.basictypeaccess.volatiles.VolatileDoubleAccess;

/**
 * Access for {@link DoubleBuffer}
 * 
 * @author Mark Kittisopikul
 */
public class DoubleBufferAccess extends AbstractBufferAccess< DoubleBufferAccess, DoubleBuffer > implements VolatileDoubleAccess
{

	/**
	 * Automatically generated 
	 */
	private static final long serialVersionUID = -7265085228179236189L;

	private static final int NUM_BYTES = Double.BYTES;

	public DoubleBufferAccess( final DoubleBuffer buffer, final boolean isValid )
	{
		super(buffer, isValid);
	}
	
	public DoubleBufferAccess( final int numEntities, final boolean isValid ) {
		super( DoubleBuffer.allocate(numEntities), isValid );
	}
	
	public DoubleBufferAccess( final ByteBuffer buffer, boolean isValid) {
		super(buffer.asDoubleBuffer(), isValid);
	}
	
	// Convenience constructors
	
	public DoubleBufferAccess( final DoubleBuffer buffer ) { this(buffer,    DEFAULT_IS_VALID); }
	public DoubleBufferAccess( final int numEntities   ) { this(numEntities, DEFAULT_IS_VALID); }
	public DoubleBufferAccess( final ByteBuffer buffer ) { this(buffer,      DEFAULT_IS_VALID); }
	public DoubleBufferAccess() { this( (DoubleBuffer) null, false); }

	/*
	 * DoubleAccess methods
	 */
	
	@Override
	public double getValue( int index ) {
		return buffer.get( index );
	}

	@Override
	public void setValue( int index, double value ) {
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
	public DoubleBufferAccess newInstance(ByteBuffer buffer, boolean isValid) {
		return fromByteBuffer(buffer, isValid);
	}
	
	@Override
	public DoubleBufferAccess newInstance(DoubleBuffer buffer, boolean isValid) {
		return new DoubleBufferAccess(buffer, isValid);
	}
	
	@Override
	protected DoubleBuffer duplicateBuffer(DoubleBuffer buffer) {
		return buffer.duplicate();
	}
	
	/*
	 * Static methods
	 */

	/**
	 * Create a new DoubleBufferAccess from a ByteBuffer
	 * 
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public static DoubleBufferAccess fromByteBuffer(ByteBuffer buffer, boolean isValid) {
		return new DoubleBufferAccess(buffer, isValid);
	}

	
	/*
	 * Bulk convenience methods
	 * 
	 * These are not trivial because the buffer should be duplicated
	 * to prevent changing the current buffer state. The duplicated
	 * buffer is returned for chained operations.
	 */
	
	/**
	 * Copy values into a {@link AbstractDoubleArray}.
	 * 
	 * @param array
	 * @return
	 * @see DoubleBuffer#get(double[])
	 */
	public DoubleBuffer getValues(final AbstractDoubleArray< ? > array) {
		return buffer.duplicate().get( array.getCurrentStorageArray() );
	}
	
	/**
	 * Copy values into a {@link AbstractDoubleArray}.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see DoubleBuffer#get(double[], int, int)
	 */
	public DoubleBuffer getValues(final AbstractDoubleArray< ? > array, int offset, int length) {
		return buffer.duplicate().get( array.getCurrentStorageArray(), offset, length );
	}
	
	/**
	 * Copy values from a {@link AbstractDoubleArray}.
	 * 
	 * @param array
	 * @return
	 * @see DoubleBuffer#put(double[])
	 */
	public DoubleBuffer setValues(final AbstractDoubleArray< ? > array) {
		return buffer.duplicate().put( array.getCurrentStorageArray() );
	}
	
	/**
	 * Copy values from a {@link AbstractDoubleArray}.
	 * 
	 * @param array
	 * @param offset
	 * @param length
	 * @return
	 * @see DoubleBuffer#put(double[], int, int)
	 */
	public DoubleBuffer setValues(final AbstractDoubleArray< ? > array, int offset, int length) {
		return buffer.duplicate().put( array.getCurrentStorageArray(), offset, length );
	}
	
	/**
	 * Copy values from another DoubleBufferAccess.
	 * 
	 * @param access
	 * @return
	 */
	public DoubleBuffer setValues(final DoubleBufferAccess access) {
		return buffer.duplicate().put( access.getCurrentStorageArray() );
	}

}
