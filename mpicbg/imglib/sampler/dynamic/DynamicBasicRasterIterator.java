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
package mpicbg.imglib.sampler.dynamic;

import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.container.dynamic.DynamicContainerAccessor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.sampler.AbstractBasicRasterIterator;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class DynamicBasicRasterIterator< T extends Type< T > > extends AbstractBasicRasterIterator< T > implements DynamicStorageAccess
{
	/* the type instance accessing the pixel value the cursor points at */
	protected final T type;
	
	/* a stronger typed pointer to Container< T > */
	protected final DynamicContainer< T, ? extends DataAccess > container;
	
	/* access proxy */
	protected final DynamicContainerAccessor accessor;

	protected int internalIndex;
	
	public DynamicBasicRasterIterator(
			final DynamicContainer< T, ? extends DynamicContainerAccessor > container,
			final Image< T > image )
	{
		super( container, image );
		
		this.type = container.createLinkedType();
		this.container = container;
	
		accessor = container.createAccessor();
		
		reset();
	}
	
	@Override
	public DynamicContainerAccessor getAccessor() { return accessor; }

	@Override
	public T type() { return type; }

	@Override
	public boolean hasNext() { return internalIndex < container.numPixels() - 1; }

	@Override
	public void jumpFwd( final long steps ) 
	{ 
		internalIndex += steps;
		accessor.updateIndex( internalIndex );
		
		linkedIterator.jumpFwd( steps );
	}

	@Override
	public void fwd() 
	{ 
		++internalIndex; 
		accessor.updateIndex( internalIndex );
		
		linkedIterator.fwd();
	}

	@Override
	public void close() 
	{ 
		internalIndex = Integer.MAX_VALUE;
		super.close();
	}

	@Override
	public void reset()
	{		
		type.updateIndex( 0 );
		internalIndex = 0;
		type.updateContainer( this );
		accessor.updateIndex( internalIndex );
		internalIndex = -1;
		
		linkedIterator.reset();
	}
	
	@Override
	public int getInternalIndex() { return internalIndex; }

	@Override
	public DynamicContainer<T,?> getContainer(){ return container; }
	
	@Override
	public String toString() { return type.toString(); }
	
	@Override
	public long getLongPosition( final int dim )
	{
		return container.indexToPosition( type.getIndex(), dim );
	}

	@Override
	public void localize( final long[] position )
	{
		container.indexToPosition( type.getIndex(), position );
	}
}
