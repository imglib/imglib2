/**
 * Copyright (c) 2010, Stephan Saalfeld
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
package mpicbg.imglib.sampler.shapelist;

import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.IntegerPositionable;
import mpicbg.imglib.container.AbstractContainerIterator;
import mpicbg.imglib.container.ContainerIterator;
import mpicbg.imglib.container.PositionableContainerSampler;
import mpicbg.imglib.container.shapelist.ShapeList;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.VoidPositionable;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class ShapeListPositionableRasterSampler< T extends Type< T > > extends AbstractContainerIterator< T > implements PositionableContainerSampler< T >, ContainerIterator< T >
{
	final protected ShapeList< T > container;
	
	final protected int numDimensions;
	final protected int[] position, dimensions;
	
	protected IntegerPositionable linkedRasterPositionable = VoidPositionable.getInstance();
	
	public ShapeListPositionableRasterSampler(
			final ShapeList< T > container,
			final Image< T > image ) 
	{
		super( container, image );
		this.container = container;
		numDimensions = container.numDimensions(); 
		
		position = new int[ numDimensions ];
		dimensions = container.getDimensions();
	}
	
	@Override
	public T get()
	{
		return container.getShapeType( position );
	}
	
	@Override
	public boolean isOutOfBounds()
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int x = position[ d ];
			if ( x < 0 || x >= dimensions[ d ] )
				return true;
		}
		return false;
	}
	
	@Override
	public void fwd( final int dim )
	{
		++position[ dim ];
		
		linkedRasterPositionable.fwd( dim );
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;	

		linkedRasterPositionable.move( steps, dim );
	}
	
	@Override
	public void move( final long steps, final int dim )
	{
		position[ dim ] += steps;	

		linkedRasterPositionable.move( steps, dim );
	}
	
	
	@Override
	public void bck( final int dim )
	{
		--position[ dim ];

		linkedRasterPositionable.bck( dim );
	}
		
	@Override
	public void moveTo( final int[] pos )
	{		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int dist = pos[ d ] - getIntPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}
	
	@Override
	public void moveTo( final long[] pos )
	{		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final long dist = pos[ d ] - getIntPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}

	@Override
	public void moveTo( final IntegerLocalizable localizable )
	{
		localizable.localize( position );
		
		linkedRasterPositionable.moveTo( localizable );
	}

	@Override
	public void setPosition( final IntegerLocalizable localizable )
	{
		localizable.localize( position );

		linkedRasterPositionable.setPosition( localizable );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];

		linkedRasterPositionable.setPosition( position );
	}
	
	@Override
	/* TODO change position to long accuracy */
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = ( int )position[ d ];

		linkedRasterPositionable.setPosition( position );
	}
	

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;
		
		linkedRasterPositionable.setPosition( position, dim );
	}
	
	@Override
	/* TODO change position to long accuracy */
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );

		linkedRasterPositionable.setPosition( position, dim );
	}

	@Override
	public ShapeList<T> getContainer(){ return container; }

	@Override
	public void reset()
	{
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; ++d )
			position[ d ] = 0;
	}

	/**
	 * Assumes that position is not out of bounds.
	 * 
	 * TODO Not the most efficient way to calculate this on demand.  Better: count an index while moving...
	 */
	@Override
	public boolean hasNext()
	{
		for ( int d = numDimensions - 1; d >= 0; --d )
		{
			final int sizeD = dimensions[ d ] - 1;
			if ( position[ d ] < sizeD )
				return true;
			else if ( position[ d ] > sizeD )
				return false;
		}
		return false;
	}

	@Override
	public void fwd()
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( ++position[ d ] >= dimensions[ d ] )
				position[ d ] = 0;
			else
				break;
		}
	}

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}

	
	@Override
	public float getFloatPosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public double getDoublePosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public int getIntPosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public long getLongPosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public String toString()
	{
		String pos = "(" + position[ 0 ];
		
		for ( int d = 1; d < numDimensions; d++ )
			pos += ", " + position[ d ];
		
		pos += ") = " + get();
		
		return pos;
	}
	
	@Override
	public int numDimensions(){ return numDimensions; }
}
