/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

import net.imglib2.AbstractLocalizingCursorInt;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;

/**
 * Localizing {@link Cursor} on an {@link ArrayImg}.
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ArrayLocalizingCursor< T extends NativeType< T > > extends AbstractLocalizingCursorInt< T >
{
	protected final T type;

	protected final ArrayImg< T, ? > container;

	protected final int lastIndex;

	/**
	 * Maximum of the {@link ArrayImg} in every dimension.
	 * This is used to check isOutOfBounds().
	 */
	protected final int[] max;

	protected ArrayLocalizingCursor( final ArrayLocalizingCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		this.container = cursor.container;
		this.type = container.createLinkedType();
		this.lastIndex = ( int )container.size() - 1;

		max = new int[ n ];
		for( int d = 0; d < n; ++d )
		{
			position[ d ] = cursor.position[ d ];
			max[ d ] = cursor.max[ d ];
		}

		type.updateIndex( cursor.type.getIndex() );
		type.updateContainer( this );
	}

	public ArrayLocalizingCursor( final ArrayImg< T, ? > container )
	{
		super( container.numDimensions() );

		this.container = container;
		this.type = container.createLinkedType();
		this.lastIndex = ( int )container.size() - 1;

		max = new int[ n ];
		for( int d = 0; d < n; ++d )
			max[ d ] = ( int ) container.max( d );

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
	public void fwd()
	{
		type.incIndex();

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] > max[ d ] ) position[ d ] = 0;
			else break;
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		type.incIndex( ( int ) steps );
		IntervalIndexer.indexToPosition( type.getIndex(), container.dim, position );
	}

	@Override
	public void reset()
	{
		type.updateIndex( -1 );

		position[ 0 ] = -1;

		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;

		type.updateContainer( this );
	}

	@Override
	public ArrayLocalizingCursor< T > copy()
	{
		return new ArrayLocalizingCursor< T >( this );
	}

	@Override
	public ArrayLocalizingCursor< T > copyCursor()
	{
		return copy();
	}
}
