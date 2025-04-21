/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileAccess;

/**
 * BufferAccess wraps java.nio.Buffer subclasses and implements
 * {@link ArrayDataAccess} and {@link VolatileAccess}.
 *
 * Subclasses should be able to be constructed from either a specific buffer
 * (e.g. LongBuffer) or {@link ByteBuffer}.
 *
 * @author Mark Kittisopikul
 * @author Philipp Hanslovsky (initial specification)
 */
public interface BufferAccess< A > extends VolatileAccess, ArrayDataAccess< A >
{

	/**
	 * Determine if the underlying Buffer is allocated direct (outside of the
	 * JVM).
	 *
	 * @return true if the Buffer is direct.
	 * @see Buffer#isDirect()
	 */
	boolean isDirect();

	/**
	 * Determine if data can be read only and not written
	 *
	 * @return
	 * @see Buffer#isReadOnly()
	 */
	boolean isReadOnly();

	/**
	 * Get number of bytes for one entity in this {@code BufferAccess}.
	 * This usually retrieves a static field.
	 *
	 * @return number of bytes
	 */
	int getNumBytesPerEntity();

	/**
	 * Create a new instance from a ByteBuffer
	 *
	 * @param buffer
	 * @param isValid
	 * @return
	 */
	A newInstance( ByteBuffer buffer, boolean isValid );

}
