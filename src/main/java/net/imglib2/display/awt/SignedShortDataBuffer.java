/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.display.awt;

import java.awt.image.DataBuffer;
import java.awt.image.DataBufferShort;

/**
 * DataBuffer that stores signed shorts. SignedShortBuffer serves the same
 * purpose as java.awt.image.DataBufferShort; the only difference is that
 * SignedShortBuffer's getType() method returns DataBuffer.TYPE_USHORT. This is
 * a workaround for the fact that java.awt.image.BufferedImage does not support
 * DataBuffers with type DataBuffer.TYPE_SHORT.
 */
public class SignedShortDataBuffer extends DataBuffer
{

	private final DataBufferShort helper;

	// -- Constructors --

	public SignedShortDataBuffer( final int size )
	{
		super( DataBuffer.TYPE_USHORT, size );
		helper = new DataBufferShort( size );
	}

	public SignedShortDataBuffer( final int size, final int numbanks )
	{
		super( DataBuffer.TYPE_USHORT, size, numbanks );
		helper = new DataBufferShort( size, numbanks );
	}

	public SignedShortDataBuffer( final short[] data, final int size )
	{
		super( DataBuffer.TYPE_USHORT, size );
		helper = new DataBufferShort( data, size );
	}

	public SignedShortDataBuffer( final short[] data, final int size, final int offset )
	{
		super( DataBuffer.TYPE_USHORT, size, 1, offset );
		helper = new DataBufferShort( data, size, offset );
	}

	public SignedShortDataBuffer( final short[][] data, final int size )
	{
		super( DataBuffer.TYPE_USHORT, size, data.length );
		helper = new DataBufferShort( data, size );
	}

	public SignedShortDataBuffer( final short[][] data, final int size,
			final int[] offsets )
	{
		super( DataBuffer.TYPE_USHORT, size, data.length, offsets );
		helper = new DataBufferShort( data, size, offsets );
	}

	// -- DataBuffer API methods --

	public short[] getData()
	{
		return helper.getData();
	}

	public short[] getData( final int bank )
	{
		return helper.getData( bank );
	}

	@Override
	public int getElem( final int bank, final int i )
	{
		return helper.getElem( bank, i );
	}

	@Override
	public void setElem( final int bank, final int i, final int val )
	{
		helper.setElem( bank, i, val );
	}

}
