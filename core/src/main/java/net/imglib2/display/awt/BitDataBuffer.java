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

import net.imglib2.img.basictypeaccess.array.BitArray;

/**
 * DataBuffer for BitType which reflects behaviour of {@link BitArray} which is
 * backed by an int[].
 * 
 * @author Christian Dietz
 * 
 */
public class BitDataBuffer extends DataBuffer
{

	final static protected int bitsPerEntity = Integer.SIZE;

	private final int[] data;

	private final int bankdata[][];

	/***
	 * Create a data buffer with
	 * 
	 * @param numEntities
	 */
	public BitDataBuffer( final int[] source, final int numEntities )
	{
		super( TYPE_INT, numEntities );

		this.data = source;
		this.bankdata = new int[ 1 ][];
		this.bankdata[ 0 ] = source;
	}

	/***
	 * Create a empty data buffer with size = numEntitites
	 * 
	 * @param numEntities
	 */
	protected BitDataBuffer( final int numEntities )
	{
		super( TYPE_INT, numEntities );

		final int bufferSize;

		if ( numEntities % bitsPerEntity == 0 )
			bufferSize = numEntities / bitsPerEntity;
		else
			bufferSize = numEntities / bitsPerEntity + 1;

		this.data = new int[ bufferSize ];

		this.bankdata = new int[ 1 ][];
		this.bankdata[ 0 ] = data;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getElem( final int bank, final int i )
	{
		// only one bank supported since now
		assert ( bank == 0 );

		final int arrayIndex = i / bitsPerEntity;
		final int arrayOffset = i % bitsPerEntity;

		final int entry = data[ arrayIndex ];
		final int value = ( entry & ( 1 << arrayOffset ) );

		return value == 0 ? value : 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setElem( final int bank, final int i, final int val )
	{
		final int arrayIndex = i / bitsPerEntity;
		final int arrayOffset = i % bitsPerEntity;

		synchronized ( data )
		{
			if ( val == 1 )
				data[ arrayIndex ] = data[ arrayIndex ] | ( 1 << arrayOffset );
			else
				data[ arrayIndex ] = data[ arrayIndex ] & ~( 1 << arrayOffset );
		}
	}

	/**
	 * Returns the requested data array element from the first (default) bank.
	 * 
	 * @param i
	 *            The data array element you want to get.
	 * @return The requested data array element as an integer.
	 * @see #setElem(int, int)
	 * @see #setElem(int, int, int)
	 */
	@Override
	public int getElem( final int i )
	{
		return data[ i + offset ];
	}

	/**
	 * Returns the data array for the specified bank.
	 * <p>
	 * Note that calling this method may cause this {@code DataBuffer} object to
	 * be incompatible with <a href="#optimizations">performance
	 * optimizations</a> used by some implementations (such as caching an
	 * associated image in video memory).
	 * 
	 * @param bank
	 *            The bank whose data array you want to get.
	 * @return The data array for the specified bank.
	 */
	public int[] getData()
	{
		return data;
	}
}
