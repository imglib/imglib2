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
package mpicbg.imglib.img.planar;

import mpicbg.imglib.AbstractRandomAccessInt;
import mpicbg.imglib.type.NativeType;

/**
 * RandomAccess for a {@link PlanarImg PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class PlanarRandomAccess< T extends NativeType< T > > extends AbstractRandomAccessInt< T > implements PlanarImg.PlanarContainerSampler
{
	final protected int[] sliceSteps;
	final protected int width;
	
	final protected T type;
	protected int sliceIndex;
	
	public PlanarRandomAccess( final PlanarImg< T, ? > container )
	{
		super( container.numDimensions() );
		
		sliceSteps = container.sliceSteps;
		width = ( int ) container.dimension( 0 );
		
		type = container.createLinkedType();
		type.updateIndex( 0 );
		type.updateContainer( this );		
	}

	@Override
	public int getCurrentSliceIndex() { return sliceIndex; }

	@Override
	public T get() { return type; }
	
	@Override
	public void fwd( final int d )
	{
		++position[ d ];

		if ( d == 0 )
			type.incIndex();
		else if ( d == 1 )
			type.incIndex( width );
		else
		{
			sliceIndex += sliceSteps[ d ];
			type.updateContainer( this );
		}
	}

	@Override
	public void bck( final int d )
	{		
		--position[ d ];
		
		if ( d == 0 )
			type.decIndex();
		else if ( d == 1 )
			type.decIndex( width );
		else
		{
			sliceIndex -= sliceSteps[ d ];
			type.updateContainer( this );
		}
	}

	@Override
	public void move( final int distance, final int dim )
	{
		position[ dim ] += distance;	

		if ( dim == 0 )
		{
			type.incIndex( distance );
		}
		else if ( dim == 1 )
		{
			type.incIndex( distance * width );
		}
		else
		{
			sliceIndex += sliceSteps[ dim ] * distance;
			type.updateContainer( this );
		}
	}

	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int ) distance, dim );
	}

	@Override
	public void setPosition( final int pos, final int dim )
	{
		if ( dim == 0 )
		{
			type.incIndex( pos - position[ 0 ] );
		}
		else if ( dim == 1 )
		{
			type.incIndex( ( pos - position[ 1 ] ) * width );			
		}
		else
		{
			sliceIndex += ( pos - position[ dim ] ) * sliceSteps[ dim ];
			type.updateContainer( this );
		}
		
		position[ dim ] = pos;
	}

	@Override
	public void setPosition( final long pos, final int dim )
	{
		setPosition( ( int ) pos, dim );
	}
	
	@Override
	public void setPosition( final int[] pos )
	{
		type.updateIndex( pos[ 0 ] + pos[ 1 ] * width );
		position[ 0 ] = pos[ 0 ];
		position[ 1 ] = pos[ 1 ];

		for ( int d = 2; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
				position[ d ] = pos[ d ];

				for ( ++d; d < n; ++d )
				{
					if ( pos[ d ] != position[ d ] )
					{
						sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
						position[ d ] = pos[ d ];
					}
				}
				type.updateContainer( this );
			}
		}		
	}

	@Override
	public void setPosition( final long[] pos )
	{	
		type.updateIndex( ( int ) pos[ 0 ] + ( int ) pos[ 1 ] * width );
		position[ 0 ] = ( int ) pos[ 0 ];
		position[ 1 ] = ( int ) pos[ 1 ];

		for ( int d = 2; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
				position[ d ] = ( int ) pos[ d ];

				for ( ++d; d < n; ++d )
				{
					if ( pos[ d ] != position[ d ] )
					{
						sliceIndex += ( pos[ d ] - position[ d ] ) * sliceSteps[ d ];
						position[ d ] = ( int ) pos[ d ];
					}
				}
				type.updateContainer( this );
			}
		}		
	}
}
