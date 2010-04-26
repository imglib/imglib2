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
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategy;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class ArrayPositionableOutOfBoundsCursor<T extends Type<T>> extends ArrayPositionableCursor<T>
{
	final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory;
	final OutOfBoundsStrategy<T> outOfBoundsStrategy;
	
	boolean isOutOfBounds = false;
	
	public ArrayPositionableOutOfBoundsCursor( final Array<T,?> container, final Image<T> image, final T type, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.outOfBoundsStrategy = outOfBoundsStrategyFactory.createStrategy( this );
		
		reset();
	}	
	
	@Override
	public void reset()
	{
		isOutOfBounds = false;
		super.reset();
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
				type.incIndex( step[ dim ] );
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
	public void bck( final int dim )
	{
		position[ dim ]--;	

		if ( isOutOfBounds )
		{
			// reenter the image?
			if ( position[ dim ] == dimensions[ dim ] - 1 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBoundsBck( dim );
		}
		else
		{			
			if ( position[ dim ] > -1 )
			{
				// moved within the image
				type.decIndex( step[ dim ] );
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
					type.updateContainer( this );
					
					// get the offset inside the image
					type.updateIndex( container.getPos( position ) );
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
				//type.i += step[ dim ] * steps;
				type.incIndex( step[ dim ] * steps );
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
			
			if ( wasOutOfBounds ) // we reenter the image with this setPosition() call
				type.updateContainer( this );
			
			// get the offset inside the image
			type.updateIndex( container.getPos( position ) );			
		}

		linkedRasterPositionable.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// we are out of image bounds or in the initial starting position
		if ( isOutOfBounds || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( this.position );
		}
		else if ( position < 0 || position >= dimensions[ dim ]) // we can just check in this dimension if it is still inside
		{
			// cursor has left the image
			isOutOfBounds = true;
			outOfBoundsStrategy.initOutOfBOunds();

			linkedRasterPositionable.setPosition( position, dim );
		}
		else
		{
			// jumped around inside the image
			type.updateIndex( container.getPos( this.position ) );

			linkedRasterPositionable.setPosition( position, dim );
		}
	}	
}
