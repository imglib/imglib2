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

package net.imglib2.img.array;

import net.imglib2.AbstractCursorInt;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;

/**
 * {@link Cursor} on an {@link ArrayImg}.
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Christian Dietz
 * @author Tobias Pietzsch
 */
//TODO Review Javadoc
public abstract class AbstractArrayCursor< T extends NativeType< T > > extends AbstractCursorInt< T >
{

	/**
	 * Offset of this cursor
	 */
	protected final int offset;

	/**
	 * Size of this cursor
	 */
	protected final int size;

	/**
	 * An instance of T
	 */
	protected final T type;

	/**
	 * Source image
	 */
	protected final ArrayImg< T, ? > img;

	/**
	 * Last index
	 */
	protected final int lastIndex;

	/**
	 * TODO Javadoc
	 * 
	 * @param cursor
	 */
	protected AbstractArrayCursor( final AbstractArrayCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		this.img = cursor.img;
		this.type = img.createLinkedType();
		this.offset = cursor.offset;
		this.size = cursor.size;
		this.lastIndex = cursor.lastIndex;

		type.updateIndex( cursor.type.getIndex() );
		type.updateContainer( this );

		reset();
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param img
	 * @param offset
	 * @param size
	 */
	public AbstractArrayCursor( final ArrayImg< T, ? > img, final int offset, final int size )
	{
		super( img.numDimensions() );

		this.type = img.createLinkedType();
		this.img = img;
		this.lastIndex = offset + size - 1;
		this.offset = offset;
		this.size = size;

		reset();
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public boolean hasNext()
	{
		return type.getIndex() < lastIndex;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		type.incIndex( ( int ) steps );
	}

	@Override
	public void fwd()
	{
		type.incIndex();
	}

	@Override
	public void reset()
	{
		type.updateIndex( offset - 1 );
		type.updateContainer( this );
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public int getIntPosition( final int dim )
	{
		return IntervalIndexer.indexToPosition( type.getIndex(), img.dim, dim );
	}

	@Override
	public void localize( final int[] position )
	{
		IntervalIndexer.indexToPosition( type.getIndex(), img.dim, position );
	}
}
