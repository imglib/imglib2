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
package mpicbg.imglib.container.planar;

import mpicbg.imglib.container.AbstractImgRandomAccess;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.NativeType;

/**
 * Positionable for a {@link PlanarContainer PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class PlanarRandomAccess< T extends NativeType< T > > extends AbstractImgRandomAccess< T >
{
	final protected int[] tmp, sliceSteps, dim;
	final int width, n;
	
	final T type;
	int sliceIndex;
	
	final PlanarContainer< T, ? > container;

	public PlanarRandomAccess( final PlanarContainer< T, ? > container )
	{
		super( container );
		
		this.container = container;
		this.width = container.dim[ 0 ];
		this.n = container.numDimensions();
		this.type = container.createLinkedType();
		this.dim = container.dim;
		
		tmp = new int[ n ];

		if ( n > 2 )
		{
			sliceSteps = new int[ n ];
			sliceSteps[ 2 ] = 1;
			for ( int i = 3; i < n; ++i )
			{
				final int j = i - 1;
				sliceSteps[ i ] = dim[ j ] * sliceSteps[ j ];
			}
		}
		else
		{
			sliceSteps = null;
		}
	}
	
	@Override
	public void fwd( final int dim )
	{
		++position[ dim ];

		if ( dim == 0 )
			type.incIndex();
		else if ( dim == 1 )
			type.incIndex( width );
		else
		{
			sliceIndex += sliceSteps[ dim ];
			type.updateContainer( this );
		}
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;	

		if ( dim == 0 )
			type.incIndex( steps );
		else if ( dim == 1 )
			type.incIndex( steps * width );
		else
		{
			sliceIndex += sliceSteps[ dim ] * steps;
			type.updateContainer( this );
		}
	}
	
	@Override
	public void bck( final int dim )
	{		
		--position[ dim ];
		
		if ( dim == 0 )
			type.decIndex();
		else if ( dim == 1 )
			type.decIndex( width );
		else
		{
			sliceIndex -= sliceSteps[ dim ];
			type.updateContainer( this );
		}
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		if ( dim == 0 )
		{
			type.updateIndex( type.getIndex() - (int)this.position[ 0 ] + position );
		}
		else if ( dim == 1 )
		{
			type.updateIndex( type.getIndex() - (int)this.position[ 0 ]*width + position*width );			
		}
		else
		{
			sliceIndex -= this.position[ dim ] * sliceSteps[ dim ]; 
			sliceIndex += position * sliceSteps[ dim ];
			type.updateContainer( this );
		}
		
		this.position[ dim ] = position;
	}

	@Override
	public Img<T> getImg() { return container; }

	@Override
	public T get() { return type; }

	@Override
	public void move( final long distance, final int dim ) { move( (int)distance, dim ); }

	@Override
	public void setPosition( final int[] position )
	{
		type.updateIndex( position[ 0 ] + position[ 1 ] * width );
		
		for ( int d = 0; d < n; d++ )
			this.position[ d ] = position[ d ];
		
		sliceIndex = 0;
		for ( int d = 2; d < n; ++d )
			sliceIndex += position[ d ] * sliceSteps[ d ];
		
		type.updateContainer( this );		
	}

	@Override
	public void setPosition( final long[] position )
	{
		type.updateIndex( (int)position[ 0 ] + (int)position[ 1 ] * width );
		
		for ( int d = 0; d < n; d++ )
			this.position[ d ] = position[ d ];
		
		sliceIndex = 0;
		for ( int d = 2; d < n; ++d )
			sliceIndex += position[ d ] * sliceSteps[ d ];
		
		type.updateContainer( this );		
	}

	@Override
	public void setPosition( final long position, final int dim ) { setPosition( (int)position, dim ); }
}
