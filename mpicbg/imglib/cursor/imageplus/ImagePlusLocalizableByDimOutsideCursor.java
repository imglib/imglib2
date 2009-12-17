/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.cursor.imageplus;

import mpicbg.imglib.container.imageplus.ImagePlusContainer;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategy;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class ImagePlusLocalizableByDimOutsideCursor<T extends Type<T>> extends ImagePlusLocalizableByDimCursor<T> implements LocalizableByDimCursor<T>
{
	final OutsideStrategyFactory<T> outsideStrategyFactory;
	final OutsideStrategy<T> outsideStrategy;
	
	boolean isOutside = false;
	
	public ImagePlusLocalizableByDimOutsideCursor( final ImagePlusContainer<T> container, final Image<T> image, final T type, final OutsideStrategyFactory<T> outsideStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outsideStrategyFactory = outsideStrategyFactory;
		this.outsideStrategy = outsideStrategyFactory.createStrategy( this );
		
		reset();
	}	
	
	@Override
	public boolean hasNext()
	{
		if ( !isOutside && ( type.getIndex() < slicePixelCountMinus1 || slice < maxSliceMinus1 ) )
			return true;
		else
			return false;
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
	public void reset()
	{
		if ( outsideStrategy == null )
			return;
		
		isClosed = false;
		isOutside = false;
		type.updateIndex( -1 );
		
		position[ 0 ] = -1;
		slice = 0;
		
		for ( int d = 1; d < numDimensions; d++ )
			position[ d ] = 0;
		
		type.updateDataArray( this );
	}
	
	@Override
	public void fwd()
	{
		if ( !isOutside )
		{
			type.incIndex();
			
			if ( type.getIndex() > slicePixelCountMinus1 ) 
			{
				slice++;
				type.updateIndex( 0 );
				type.updateDataArray( this );
			}
			
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
			position[0]++;
			outsideStrategy.initOutside(  );
		}
	}

	@Override
	public void fwd( final int dim )
	{
		position[ dim ]++;

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
				if ( dim == 2 )
				{
					++slice;
					type.updateDataArray( this );
				}
				else
				{
					type.incIndex( step[ dim ] );
				}
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
					// new location is inside the image
					
					// get the offset inside the image
					type.updateIndex( container.getPos( position ) );
					slice = position[ 2 ];
					
					type.updateDataArray( this );			
				}
				else
				{
					outsideStrategy.notifyOutside( steps, dim  );
				}
			}
			else // moved outside of the image
			{
				outsideStrategy.notifyOutside( steps, dim );
			}
		}
		else
		{			
			if ( position[ dim ] >= 0 && position[ dim ] < dimensions[ dim ] )
			{
				// moved within the image
				if ( dim == 2 )
				{
					slice += steps;
					type.updateDataArray( this );
				}
				else
				{
					type.incIndex( step[ dim ] * steps );
				}
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
				if ( dim == 2 )
				{
					--slice;
					type.updateDataArray( this );
				}
				else
				{
					type.decIndex( step[ dim ] );
				}
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
						
			// get the offset inside the image
			type.updateIndex( container.getPos( position ) );
			slice = position[ 2 ];
			
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
			outsideStrategy.initOutside(  );
			return;
		}
		else
		{
			// jumped around inside the image
			
			if ( dim == 2 )
			{
				slice = position;
				type.updateDataArray( this );
			}
			else
			{
				type.updateIndex( container.getPos( this.position ) );
			}
		}		
	}
}
