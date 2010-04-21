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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.cursor.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.cursor.AbstractCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ArrayCursor<T extends Type<T>> extends AbstractCursor<T>
{
	protected final T type;
	protected final Array<T,?> container;
	protected final int sizeMinus1;
	
	public ArrayCursor( final Array<T,?> container, final Image<T> image, final T type ) 
	{
		super( container, image );

		this.type = type;
		this.container = container;
		this.sizeMinus1 = container.getNumPixels() - 1;
		
		reset();
	}
	
	@Override
	public T type() { return type; }
	
	@Override
	public boolean hasNext(){ return type.getIndex() < sizeMinus1; }

	@Override
	public void jumpFwd( final long steps )
	{
		type.incIndex( (int)steps );
		
		linkedIterator.jumpFwd( steps );
	}

	@Override
	public void fwd()
	{
		type.incIndex();
		
		linkedIterator.fwd();
	}

	@Override
	public void close() 
	{ 
		isClosed = true;
		type.updateIndex( sizeMinus1 + 1 );
	}

	@Override
	public void reset()
	{ 
		type.updateIndex( -1 ); 
		type.updateContainer( this );
		isClosed = false;
		
		linkedIterator.reset();
	}

	@Override
	public Array<T,?> getStorageContainer(){ return container; }

	@Override
	public int getStorageIndex() { return 0; }
	
	@Override
	public String toString() { return type.toString(); }		
}
