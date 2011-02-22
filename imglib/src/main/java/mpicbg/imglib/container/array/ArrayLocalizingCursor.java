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
package mpicbg.imglib.container.array;

//import mpicbg.imglib.container.AbstractImgLocalizingCursor;
import mpicbg.imglib.container.AbstractImgLocalizingCursorInt;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.util.IntervalIndexer;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ArrayLocalizingCursor< T extends NativeType< T > > extends AbstractImgLocalizingCursorInt< T >
{
	protected final T type;

	protected final Array< T, ? > container;

	protected final int lastIndex;

	public ArrayLocalizingCursor( final Array< T, ? > container )
	{
		super( container );

		this.container = container;
		this.type = container.createLinkedType();
		this.lastIndex = ( int )container.size() - 1;

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
			if ( ++position[ d ] >= size[ d ] ) position[ d ] = 0;
			else break;
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		type.incIndex( ( int ) steps );
		IntervalIndexer.indexToPosition( type.getIndex(), size, position );
	}

	@Override
	public void reset()
	{
		if ( size != null )
		{
			type.updateIndex( -1 );

			position[ 0 ] = -1;

			for ( int d = 1; d < n; d++ )
				position[ d ] = 0;

			type.updateContainer( this );
		}
	}

	@Override
	public Array< T, ? > getImg(){ return container; }
}
