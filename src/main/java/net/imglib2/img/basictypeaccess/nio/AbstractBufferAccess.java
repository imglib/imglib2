/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.function.Function;

import net.imglib2.img.basictypeaccess.DataAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileAccess;

/**
 * Base abstract class for {@link DataAccess} implementations that wrap
 * {@link Buffer}.
 *
 * @author Mark Kittisopikul
 * @author Philipp Hanslovsky (initial specification)
 *
 * @param <A>
 *            Subclass used as return type for
 *            {@link #newInstance(Buffer, boolean)} and
 *            {@link #newInstance(ByteBuffer, boolean)}
 *
 * @param <B>
 *            {@link Buffer} subclass
 */
public abstract class AbstractBufferAccess< A extends AbstractBufferAccess< A, B >, B extends Buffer > implements BufferAccess< A >
{

	/**
	 * Default valid setting if not specified
	 */
	static final boolean DEFAULT_IS_VALID = true;

	/**
	 * If valid or not as per {@link VolatileAccess}.
	 */
	private final boolean isValid;

	final B buffer;

	/*
	 * Constructors
	 */

	public AbstractBufferAccess( final B buffer, final boolean isValid )
	{
		this.buffer = buffer;
		this.isValid = isValid;
	}

	public AbstractBufferAccess( final B buffer )
	{
		this( buffer, DEFAULT_IS_VALID );
	}

	public AbstractBufferAccess( final Function< Integer, B > allocate, final int numEntities )
	{
		this( allocate.apply( numEntities ) );
	}

	/*
	 * Public methods
	 */

	/**
	 * Returns a new instance of BufferAccess with duplicated Buffer for thread
	 * safety.
	 */
	@Override
	public A createView( final Object o )
	{
		return newInstance( duplicateBuffer( buffer ), isValid() );
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}

	/**
	 * Respects the directness of the buffer. If this buffer is direct, then the
	 * new backing buffer is also direct.
	 */
	@Override
	public A createArray( final int numEntities )
	{
		return allocate( numEntities );
	}

	/**
	 * Returns the raw buffer (not duplicated)
	 */
	@Override
	public B getCurrentStorageArray()
	{
		return buffer;
	}

	/**
	 * Returns the Buffer's limit or zero if buffer is null.
	 *
	 * @see Buffer#limit()
	 */
	@Override
	public int getArrayLength()
	{
		if ( buffer == null )
			return 0;
		else
			return buffer.limit();
	}

	/**
	 * Return the Buffer's directness or false is the buffer is null.
	 */
	@Override
	public boolean isDirect()
	{
		return buffer != null && buffer.isDirect();
	}

	@Override
	public boolean isReadOnly()
	{
		return buffer.isReadOnly();
	}

	/*
	 * Protected methods
	 */

	/**
	 * Allocate a new ByteBuffer with initial capacity and directness.
	 *
	 * @param numEntities
	 * @param isDirect
	 * @return
	 */
	ByteBuffer allocateByteBuffer( final int numEntities, final boolean isDirect )
	{
		if ( isDirect )
			return ByteBuffer.allocateDirect( numEntities * getNumBytesPerEntity() );
		else
			return ByteBuffer.allocate( numEntities * getNumBytesPerEntity() );
	}

	/**
	 * Allocate a new BufferAccess specifying the directness and validity.
	 *
	 * @param numEntities
	 * @param isDirect
	 * @param isValid
	 * @return
	 */
	A allocate( final int numEntities, final boolean isDirect, final boolean isValid )
	{
		return newInstance( allocateByteBuffer( numEntities, isDirect ), isValid );
	}

	/**
	 * Allocate a new BufferAccess using the current buffer's directness and
	 * validity.
	 *
	 * @param numEntities
	 * @return
	 */
	A allocate( final int numEntities )
	{
		return allocate( numEntities, isDirect(), isValid() );
	}

	/*
	 * Abstract methods
	 */

	/**
	 * Create a new instance of this class given a Buffer of the same type.
	 *
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	public abstract A newInstance( B buffer, boolean isValid );

	/**
	 * Call Buffer.duplicate()
	 *
	 * Buffer.duplicate() only exists in the interface since Java 9
	 * https://docs.oracle.com/javase/9/docs/api/java/nio/Buffer.html#duplicate--
	 *
	 * @return
	 */
	abstract B duplicateBuffer( B buffer );

}
