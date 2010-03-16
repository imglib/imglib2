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
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategy;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class DynamicLocalizableByDimOutsideCursor<T extends Type<T>> extends DynamicLocalizableByDimCursor<T> implements LocalizableByDimCursor<T>
{
	final OutsideStrategyFactory<T> outsideStrategyFactory;
	final OutsideStrategy<T> outsideStrategy;
	
	boolean isOutside = false;
	
	public DynamicLocalizableByDimOutsideCursor( final DynamicContainer<T> container, final Image<T> image, final T type, final OutsideStrategyFactory<T> outsideStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outsideStrategyFactory = outsideStrategyFactory;
		this.outsideStrategy = outsideStrategyFactory.createStrategy( this );
		
		reset();
	}	
	
	@Override
	public boolean hasNext()
	{
		if ( !isOutside && internalIndex < container.getNumPixels() - 1 )
			return true;
		else
			return false;
	}

	@Override
	public void reset()
	{
		if ( outsideStrategy == null )
			return;

		isOutside = false;		
		type.updateIndex( 0 );
		internalIndex = 0;
		type.updateDataArray( this );
		internalIndex = -1;
		isClosed = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;		
	}
	
	@Override
	public T getType() 
	{ 
		if ( isOutside )
			return outsideStrategy.getType();
		else
			return type; 
	}
		
	@Override
	public void fwd()
	{
		if ( !isOutside )
		{
			++internalIndex;
			type.updateDataArray( this );
			
			for ( int d = 0; d < numDimensions; d++ )
			{
				if ( position[ d ] < dimensions[ d ] - 1 )
				{
					position[ d ]++;
					
					for ( int e = 0; e < d; e++ )
						position[ e ] = 0;

					return;
				}
			}
			
			// if it did not return we moved outside the image
			isOutside = true;
			++position[0];
			outsideStrategy.initOutside(  );
			//link.fwd();
		}
	}

	@Override
	public void fwd( final int dim )
	{
		++position[ dim ];

		if ( isOutside )
		{
			// reenter the image?
			if ( position[ dim ] == 0 )
				setPosition( position );
			else // moved outside of the image
				outsideStrategy.notifyOutsideFwd( dim );
		}
		else
		{			
			if ( position[ dim ] < dimensions[ dim ] )
			{
				// moved within the image
				internalIndex += step[ dim ];
				type.updateDataArray( this );
			}
			else
			{
				// left the image
				isOutside = true;
				outsideStrategy.initOutside(  );
			}
		}
		//link.fwd( dim );
	}

	@Override
	public void move( final int steps, final int dim )
	{
		position[ dim ] += steps;

		if ( isOutside )
		{
			// reenter the image?
			if ( position[ dim ] >= 0 && position[ dim ] < dimensions[ dim ] )
			{
				isOutside = false;
				
				for ( int d = 0; d < numDimensions && !isOutside; d++ )
					if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
						isOutside = true;
				
				if ( !isOutside )
				{
					// we re-entered the image
					// new location is inside the image					
					// get the offset inside the image
					internalIndex = container.getPos( position );
					type.updateDataArray( this );					
				}
				else
				{
					outsideStrategy.notifyOutside( steps, dim  );
				}
			}
			else // moved outside of the image
			{
				outsideStrategy.notifyOutside( steps, dim  );
			}
		}
		else
		{			
			if ( position[ dim ] >= 0 && position[ dim ] < dimensions[ dim ] )
			{
				// moved within the image
				internalIndex += step[ dim ] * steps;
				type.updateDataArray( this );									
			}
			else
			{
				// left the image
				isOutside = true;
				outsideStrategy.initOutside(  );
			}
		}
	}
	
	@Override
	public void bck( final int dim )
	{
		position[ dim ]--;	

		if ( isOutside )
		{
			// reenter the image?
			if ( position[ dim ] == dimensions[ dim ] - 1 )
				setPosition( position );
			else // moved outside of the image
				outsideStrategy.notifyOutsideBck( dim );
		}
		else
		{			
			if ( position[ dim ] > -1 )
			{
				// moved within the image
				internalIndex -= step[ dim ];
				type.updateDataArray( this );					
			}
			else
			{
				// left the image
				isOutside = true;
				outsideStrategy.initOutside(  );
			}
		}
		//link.bck( dim );
	}

	@Override
	public void setPosition( final int[] position )
	{
		// save current state
		final boolean wasOutside = isOutside;
		isOutside = false;
		
		// update positions and check if we are inside the image
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[ d ];
			
			if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
			{
				// we are outside of the image
				isOutside = true;
			}
		}
		
		if ( isOutside )
		{
			// new location is outside the image
		
			if ( wasOutside ) // just moved outside of the image
				outsideStrategy.notifyOutside(  );
			else // we left the image with this setPosition() call
				outsideStrategy.initOutside(  );
		}
		else
		{
			// new location is inside the image			
			// we reenter the image with this setPosition() call
			// get the offset inside the image
			internalIndex = container.getPos( position );
			type.updateDataArray( this );
		}
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// we are outside the image or in the initial starting position
		if ( isOutside || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( this.position );
		}
		else if ( position < 0 || position >= dimensions[ dim ]) // we can just check in this dimension if it is still inside
		{
			// cursor has left the image
			isOutside = true;
			outsideStrategy.initOutside();
			return;
		}
		else
		{
			// jumped around inside the image
			internalIndex = container.getPos( this.position );
			type.updateDataArray( this );
		}		
	}
}