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
package mpicbg.imglib.sampler.imageplus;

import mpicbg.imglib.container.AbstractImgRandomAccess;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ImagePlusPositionableRasterSampler< T extends Type< T > > extends AbstractImgRandomAccess< T > implements ImagePlusStorageAccess
{
	/* the type instance accessing the pixel value the cursor points at */
	protected final T type;
	
	protected final ImagePlusContainer< T, ? > container;
	
	/* precalculated step sizes for row.column,... access in a linear array */
	final protected int[] step, sliceSteps;
	
	protected int sliceIndex;
	
	/* TODO do we need this still? */
	int numNeighborhoodCursors = 0;
	
	public ImagePlusPositionableRasterSampler( final ImagePlusContainer< T, ? > container, final Image< T > image ) 
	{
		super( container, image );
		
		this.type = container.createLinkedType();
		this.container = container;
		step = Array.createAllocationSteps( container.getDimensions() );
		sliceSteps = ImagePlusContainer.createSliceSteps( size );
		sliceIndex = 0;
	}	

	@Override
	public void fwd( final int dim )
	{
		position[ dim ]++;

		if ( dim > 1 )
		{
			sliceIndex += sliceSteps[ dim ];
			type.updateContainer( this );
		}
		else
		{
			type.incIndex( step[ dim ] );
		}
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;	

		if ( dim > 1 )
		{
			sliceIndex += sliceSteps[ dim ] * steps;
			type.updateContainer( this );
		}
		else
		{
			type.incIndex( step[ dim ] * steps );
		}
	}
	
	@Override
	public void bck( final int dim )
	{		
		position[ dim ]--;
		
		if ( dim > 1 )
		{
			sliceIndex -= sliceSteps[ dim ];
			type.updateContainer( this );
		}
		else
		{
			type.decIndex( step[ dim ] );
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		type.updateIndex( container.getIndex( position ) );
		
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
		for ( int d = 0; d < n; d++ )
			this.position[ d ] = ( int )position[ d ];
		
		type.updateIndex( container.getIndex( this.position ) );
		
		sliceIndex = 0;
		for ( int d = 2; d < n; ++d )
			sliceIndex += position[ d ] * sliceSteps[ d ];
		
		type.updateContainer( this );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		if ( dim > 1 )
		{
			sliceIndex = position * sliceSteps[ dim ];
			type.updateContainer( this );
		}
		else
		{
			type.updateIndex( container.getIndex( this.position ) );
		}
	}

	@Override
	public ImagePlusContainer< T, ? > getImg(){ return container; }

	@Override
	public T get(){ return type; }

	@Override
	public int getStorageIndex(){ return sliceIndex; }
}
