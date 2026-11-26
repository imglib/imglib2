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

package net.imglib2.view.iteration;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.util.IntervalIndexer;

/**
 * Iterates an interval of a reshaped view (see
 * {@link net.imglib2.transform.integer.ReshapeTransform}) by forwarding to a
 * cursor that iterates the corresponding source elements in flat order. This
 * is possible if the source elements of the view interval form a single run in
 * the flat order of the source, in which case the flat order of the view
 * interval and of the source run coincide.
 * <p>
 * This cursor only keeps the flat index, and computes the position on demand.
 * {@link FlatIndexLocalizingCursor} keeps track of the position instead.
 *
 * @param <T>
 */
public class FlatIndexCursor< T > extends AbstractCursor< T >
{
	protected final Cursor< T > sourceCursor;

	protected final long[] min;

	protected final long[] dimensions;

	/**
	 * Flat index of the current element; -1 before the first {@code fwd()}.
	 */
	protected long index;

	/**
	 * @param sourceCursor
	 *            iterates the source elements of {@code interval}, in flat
	 *            order.
	 * @param interval
	 *            the interval of the view that is iterated.
	 */
	public FlatIndexCursor( final Cursor< T > sourceCursor, final Interval interval )
	{
		super( interval.numDimensions() );
		this.sourceCursor = sourceCursor;
		this.min = interval.minAsLongArray();
		this.dimensions = interval.dimensionsAsLongArray();
		this.index = -1;
	}

	protected FlatIndexCursor( final FlatIndexCursor< T > cursor )
	{
		super( cursor.numDimensions() );
		this.sourceCursor = cursor.sourceCursor.copy();
		this.min = cursor.min;
		this.dimensions = cursor.dimensions;
		this.index = cursor.index;
	}

	/**
	 * @return the cursor on the source elements that this cursor forwards to.
	 */
	public Cursor< T > getSourceCursor()
	{
		return sourceCursor;
	}

	@Override
	public T get()
	{
		return sourceCursor.get();
	}

	@Override
	public T getType()
	{
		return sourceCursor.getType();
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	@Override
	public boolean hasNext()
	{
		return sourceCursor.hasNext();
	}

	@Override
	public void fwd()
	{
		sourceCursor.fwd();
		++index;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		sourceCursor.jumpFwd( steps );
		index += steps;
	}

	@Override
	public void reset()
	{
		sourceCursor.reset();
		index = -1;
	}

	@Override
	public void localize( final long[] position )
	{
		IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, d ) + min[ d ];
	}

	@Override
	public FlatIndexCursor< T > copy()
	{
		return new FlatIndexCursor<>( this );
	}
}
