/**
 * Copyright (c) 2009--2010, Stephan Preibisch
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
 * @author Stephan Preibisch
 */
package mpicbg.imglib.cursor.dynamic;

import mpicbg.imglib.container.dynamic.DynamicContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategy;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class DynamicPositionableOutOfBoundsCursor<T extends Type<T>> extends DynamicPositionableCursor<T>
{
	final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory;
	final OutOfBoundsStrategy<T> outOfBoundsStrategy;
	
	boolean isOutOfBounds = false;
	
	public DynamicPositionableOutOfBoundsCursor( final DynamicContainer<T,?> container, final Image<T> image, final T type, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.outOfBoundsStrategy = outOfBoundsStrategyFactory.createStrategy( this );
		
		reset();
	}	
	
	@Override
	public boolean hasNext()
	{
		if ( !isOutOfBounds && internalIndex < container.getNumPixels() - 1 )
			return true;
		else
			return false;
	}

	@Override
	public void reset()
	{
		if ( outOfBoundsStrategy != null )
		{
			isOutOfBounds = false;		
			
			// in this way he returns the value of index 0 even if not moved at all
			type.updateIndex( 0 );
			internalIndex = 0;
			type.updateContainer( this );
			accessor.updateIndex( internalIndex );
			internalIndex = -1;
			isClosed = false;
			
			position[ 0 ] = -1;
			
			for ( int d = 1; d < numDimensions; d++ )
				position[ d ] = 0;		
		}
		
		linkedIterator.reset();
	}
	
	@Override
	public T type() 
	{ 
		if ( isOutOfBounds )
			return outOfBoundsStrategy.getType();
		else
			return type; 
	}
		
	@Override
	public void fwd()
	{
		if ( !isOutOfBounds )
		{
			++internalIndex;
			accessor.updateIndex( internalIndex );
			
			for ( int d = 0; d < numDimensions; d++ )
			{
				if ( position[ d ] < dimensions[ d ] - 1 )
				{
					position[ d ]++;
					
					for ( int e = 0; e < d; e++ )
						position[ e ] = 0;
					
					linkedIterator.fwd();

					return;
				}
			}
			
			// if it did not return we moved out of image bounds
			isOutOfBounds = true;
			++position[0];
			outOfBoundsStrategy.initOutOfBOunds(  );
		}
		
		linkedIterator.fwd();
	}

	@Override
	public void fwd( final int dim )
	{
		++position[ dim ];

		if ( isOutOfBounds )
		{
			// reenter the image?
			if ( position[ dim ] == 0 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOundsFwd( dim );
		}
		else
		{			
			if ( position[ dim ] < dimensions[ dim ] )
			{
				// moved within the image
				internalIndex += step[ dim ];
				accessor.updateIndex( internalIndex );
			}
			else
			{
				// left the image
				isOutOfBounds = true;
				outOfBoundsStrategy.initOutOfBOunds(  );
			}
		}

		linkedRasterPositionable.fwd( dim );
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;

		if ( isOutOfBounds )
		{
			// reenter the image?
			if ( position[ dim ] >= 0 && position[ dim ] < dimensions[ dim ] )
			{
				isOutOfBounds = false;
				
				for ( int d = 0; d < numDimensions && !isOutOfBounds; d++ )
					if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
						isOutOfBounds = true;
				
				if ( !isOutOfBounds )
				{
					// we re-entered the image
					// new location is inside the image					
					// get the offset inside the image
					internalIndex = container.getPos( position );
					accessor.updateIndex( internalIndex );
				}
				else
				{
					outOfBoundsStrategy.notifyOutOfBOunds( steps, dim  );
				}
			}
			else // moved out of image bounds
			{
				outOfBoundsStrategy.notifyOutOfBOunds( steps, dim  );
			}
		}
		else
		{			
			if ( position[ dim ] >= 0 && position[ dim ] < dimensions[ dim ] )
			{
				// moved within the image
				internalIndex += step[ dim ] * steps;
				accessor.updateIndex( internalIndex );
			}
			else
			{
				// left the image
				isOutOfBounds = true;
				outOfBoundsStrategy.initOutOfBOunds(  );
			}
		}

		linkedRasterPositionable.move( steps, dim );
	}
	
	@Override
	public void bck( final int dim )
	{
		--position[ dim ];	

		if ( isOutOfBounds )
		{
			// reenter the image?
			if ( position[ dim ] == dimensions[ dim ] - 1 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOundsBck( dim );
		}
		else
		{			
			if ( position[ dim ] > -1 )
			{
				// moved within the image
				internalIndex -= step[ dim ];
				accessor.updateIndex( internalIndex );
			}
			else
			{
				// left the image
				isOutOfBounds = true;
				outOfBoundsStrategy.initOutOfBOunds(  );
			}
		}

		linkedRasterPositionable.bck( dim );
	}

	@Override
	public void setPosition( final int[] position )
	{
		// save current state
		final boolean wasOutOfBounds = isOutOfBounds;
		isOutOfBounds = false;
		
		// update positions and check if we are inside the image
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[ d ];
			
			if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
			{
				// we are out of image bounds
				isOutOfBounds = true;
			}
		}
		
		if ( isOutOfBounds )
		{
			// new location is out of image bounds
		
			if ( wasOutOfBounds ) // just moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOunds(  );
			else // we left the image with this setPosition() call
				outOfBoundsStrategy.initOutOfBOunds(  );
		}
		else
		{
			// new location is inside the image			
			// we reenter the image with this setPosition() call
			// get the offset inside the image
			internalIndex = container.getPos( position );			
			accessor.updateIndex( internalIndex );
		}
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// we are out of image bounds or in the initial starting position
		if ( isOutOfBounds || internalIndex == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( this.position );
		}
		else
		{
			if ( position < 0 || position >= dimensions[ dim ]) // we can just check in this dimension if it is still inside
			{
				// cursor has left the image
				isOutOfBounds = true;
				outOfBoundsStrategy.initOutOfBOunds();
			}
			else
			{
				// jumped around inside the image
				internalIndex = container.getPos( this.position );
				accessor.updateIndex( internalIndex );
			}

			linkedRasterPositionable.setPosition( position, dim );
		}
	}
}