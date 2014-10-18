/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

/**
 * DataBuffer that stores signed bytes.
 * 
 * @author Melissa Linkert
 */
public class SignedByteDataBuffer extends DataBuffer
{

	private final byte[][] bankData;

	/** Construct a new buffer of signed bytes using the given byte array. */
	public SignedByteDataBuffer( final byte[] dataArray, final int size )
	{
		super( DataBuffer.TYPE_BYTE, size );
		bankData = new byte[ 1 ][];
		bankData[ 0 ] = dataArray;
	}

	/** Construct a new buffer of signed bytes using the given 2D byte array. */
	public SignedByteDataBuffer( final byte[][] dataArray, final int size )
	{
		super( DataBuffer.TYPE_BYTE, size );
		bankData = dataArray;
	}

	public byte[] getData()
	{
		return bankData[ 0 ];
	}

	public byte[] getData( final int bank )
	{
		return bankData[ bank ];
	}

	@Override
	public int getElem( final int i )
	{
		return getElem( 0, i );
	}

	@Override
	public int getElem( final int bank, final int i )
	{
		return bankData[ bank ][ i + getOffsets()[ bank ] ];
	}

	@Override
	public void setElem( final int i, final int val )
	{
		setElem( 0, i, val );
	}

	@Override
	public void setElem( final int bank, final int i, final int val )
	{
		bankData[ bank ][ i + getOffsets()[ bank ] ] = ( byte ) val;
	}

}
