/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.list;

import net.imglib2.AbstractCursorInt;
import net.imglib2.util.IntervalIndexer;

/**
 * {@link Cursor} on a {@link ListImg}.
 * 
 * @param <T>
 *            the pixel type
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
final public class ListCursor< T > extends AbstractCursorInt< T >
{
	private int i;

	final private int maxNumPixels;

	final private AbstractListImg< T > img;

	protected ListCursor( final ListCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		img = cursor.img;
		this.maxNumPixels = cursor.maxNumPixels;

		i = cursor.i;
	}

	public ListCursor( final AbstractListImg< T > img )
	{
		super( img.numDimensions() );

		this.img = img;
		this.maxNumPixels = ( int ) img.size() - 1;

		reset();
	}

	@Override
	public T get()
	{
		return img.get( i );
	}

	public void set( final T t )
	{
		img.set( i, t );
	}

	@Override
	public ListCursor< T > copy()
	{
		return new ListCursor< T >( this );
	}

	@Override
	public ListCursor< T > copyCursor()
	{
		return copy();
	}

	@Override
	public boolean hasNext()
	{
		return i < maxNumPixels;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		i += steps;
	}

	@Override
	public void fwd()
	{
		++i;
	}

	@Override
	public void reset()
	{
		i = -1;
	}

	@Override
	public void localize( final int[] position )
	{
		IntervalIndexer.indexToPosition( i, img.dim, position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return IntervalIndexer.indexToPosition( i, img.dim, img.step, d );
	}
}
