/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.sampler.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.AbstractLocalizingRasterIterator;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ArrayLocalizingRasterIterator< T extends Type< T >> extends AbstractLocalizingRasterIterator< T >
{
	protected final T type;

	protected final Array< T, ? > container;

	protected final int lastIndex;

	public ArrayLocalizingRasterIterator( final Array< T, ? > container, final Image< T > image )
	{
		super( container, image );

		this.container = container;
		this.type = container.createLinkedType();
		this.lastIndex = ( int )container.numPixels() - 1;

		reset();
	}

	/**
	 * TODO Now, that Samplers and Iterators are separated, we do not need that
	 * FakeCursor any more, but should implement a pure Localizer without an
	 * empty `FakeType' attached.
	 *  
	 * @param dim
	 * @return
	 */
	public static ArrayLocalizingRasterIterator< FakeType > createLinearCursor( final int[] dim )
	{
		final Array< FakeType, FakeAccess > array = new Array< FakeType, FakeAccess >( null, new FakeArray(), dim, 1 );
		array.setLinkedType( new FakeType() );
		return new ArrayLocalizingRasterIterator< FakeType >( array, null );
	}

	@Override
	public T type(){ return type; }

	@Override
	public boolean hasNext(){ return type.getIndex() < lastIndex; }

	@Override
	public void fwd()
	{
		type.incIndex();

		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( ++position[ d ] >= dimensions[ d ] ) position[ d ] = 0;
			else break;
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		type.incIndex( ( int ) steps );
		container.indexToPosition( type.getIndex(), position );
	}

	@Override
	public void reset()
	{
		if ( dimensions != null )
		{
			type.updateIndex( -1 );

			position[ 0 ] = -1;

			for ( int d = 1; d < numDimensions; d++ )
				position[ d ] = 0;

			type.updateContainer( this );
		}
	}

	@Override
	public Array< T, ? > getContainer(){ return container; }
}
