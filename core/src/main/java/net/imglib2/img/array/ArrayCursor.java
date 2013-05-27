/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.img.array;

import net.imglib2.AbstractCursorInt;
import net.imglib2.Interval;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

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
public class ArrayCursor< T extends NativeType< T > > extends AbstractCursorInt< T >
{

	protected final int offset;

	protected final int size;

	protected final T type;

	protected final ArrayImg< T, ? > img;

	protected final int lastIndex;

	protected ArrayCursor( final ArrayCursor< T > cursor )
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

	public ArrayCursor( final ArrayImg< T, ? > img, Interval interval )
	{
		super( img.numDimensions() );

		this.type = img.createLinkedType();
		this.img = img;
		this.offset = ( int ) offset(interval);
		this.size = (int) Intervals.numElements(interval);
		this.lastIndex = offset + size - 1;


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

	@Override
	public ArrayCursor< T > copyCursor()
	{
		return ( ArrayCursor< T > ) copy();
	}
	
	private long offset( final Interval interval)
	{
		final int maxDim = numDimensions() - 1;
		long i = interval.min( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * img.dimension( d ) + interval.min( d );

		return i;
	}

	@Override
	public AbstractCursorInt<T> copy() {
		return new ArrayCursor<T>( this );
	}
}
