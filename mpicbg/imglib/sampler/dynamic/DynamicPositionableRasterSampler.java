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

import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.container.AbstractRandomAccessContainerSampler;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.container.dynamic.DynamicContainerAccessor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class DynamicPositionableRasterSampler< T extends Type< T > > extends AbstractRandomAccessContainerSampler< T > implements DynamicStorageAccess
{
	/* the type instance accessing the pixel value the cursor points at */
	protected final T type;
	
	/* a stronger typed pointer to Container< T > */
	protected final DynamicContainer< T, ? extends DataAccess > container;
	
	/* access proxy */
	protected final DynamicContainerAccessor accessor;

	protected int internalIndex;
	
	final protected int[] step;
	
	protected int numNeighborhoodCursors = 0;
	
	public DynamicPositionableRasterSampler(
			final DynamicContainer< T, ? > container,
			final Image< T > image ) 
	{
		super( container, image );
		
		this.type = container.createLinkedType();
		this.container = container;
		accessor = container.createAccessor();
		
		step = container.getSteps();
		
		type.updateIndex( 0 );
		type.updateContainer( this );
		accessor.updateIndex( 0 );
	}	
	
	@Override
	public void fwd( final int dim )
	{
		internalIndex += step[ dim ];
		accessor.updateIndex( internalIndex );

		++position[ dim ];
	}

	@Override
	public void move( final int steps, final int dim )
	{
		internalIndex += step[ dim ] * steps;
		accessor.updateIndex( internalIndex );

		position[ dim ] += steps;
	}
	
	@Override
	/* TODO change position to long accuracy */
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );
	}

	@Override
	public void bck( final int dim )
	{
		internalIndex -= step[ dim ];
		accessor.updateIndex( internalIndex );
 
		--position[ dim ];
	}
		
	@Override
	public void moveTo( final int[] position )
	{		
		for ( int d = 0; d < n; ++d )
		{
			final int dist = position[ d ] - getIntPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}

	@Override
	public void moveTo( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = position[ d ] - getIntPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}

	@Override
	public void moveTo( final IntegerLocalizable localizable )
	{
		localizable.localize( tmp );
		moveTo( tmp );
	}
	
	@Override
	public void setPosition( final IntegerLocalizable localizable )
	{
		localizable.localize( tmp );
		setPosition( tmp );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		internalIndex = container.getPos( position );
		accessor.updateIndex( internalIndex );
		
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = ( int )position[ d ];
		
		internalIndex = container.getPos( this.position );
		accessor.updateIndex( internalIndex );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		internalIndex = container.getPos( this.position );
		accessor.updateIndex( internalIndex );
	}

	@Override
	/* TODO change position to long accuracy */
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}
	
	@Override
	public DynamicContainerAccessor getAccessor() { return accessor; }

	@Override
	public int getInternalIndex() { return internalIndex; }

	@Override
	public DynamicContainer< T, ? > getContainer(){ return container; }
	
	@Override
	public T get(){ return type; }
}
