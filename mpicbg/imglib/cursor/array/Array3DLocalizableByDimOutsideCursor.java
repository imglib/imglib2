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

import mpicbg.imglib.container.array.Array3D;
import mpicbg.imglib.cursor.LocalizableByDimCursor3D;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class Array3DLocalizableByDimOutsideCursor<T extends Type<T>> extends ArrayLocalizableByDimOutsideCursor<T> implements LocalizableByDimCursor3D<T>
{
	protected int x = -1, y = 0, z = 0;
	final int widthMinus1, heightMinus1, depthMinus1;
	final int width, height, depth;
	final int stepY, stepZ;
	final Array3D<T> container;

	public Array3DLocalizableByDimOutsideCursor( final Array3D<T> container, final Image<T> image, final T type, final OutsideStrategyFactory<T> outsideStrategyFactory ) 
	{
		super( container, image, type, outsideStrategyFactory );
		
		this.container = container;
		
		this.widthMinus1 = container.getWidth() - 1;
		this.heightMinus1 = container.getHeight() - 1;
		this.depthMinus1 = container.getDepth() - 1;

		this.width = container.getWidth();
		this.height = container.getHeight();
		this.depth = container.getDepth();
		
		this.stepY = container.getWidth();
		this.stepZ = container.getWidth() * container.getHeight();
		
		reset();
	}
	
	@Override
	public void fwd()
	{ 
		if ( !isOutside )
		{
			//++type.i;
			type.incIndex();
			
			if ( x < widthMinus1 )
			{
				++x;
			}
			else if ( y < heightMinus1 )
			{
				x = 0;
				++y;
			}
			else if ( z < depthMinus1 )
			{
				x = 0;
				y = 0;
				++z;
			}
			else
			{
				// if it did not return we moved outside the image
				isOutside = true;
				++x;
				outsideStrategy.initOutside(  );				
			}
		}
	}
	
	@Override
	public int getX() { return x; }
	@Override
	public int getY() { return y; }
	@Override
	public int getZ() { return z; }

	@Override
	public void reset()
	{ 
		if ( outsideStrategy == null )
			return;
		
		isOutside = false;
		isClosed = false;
		x = -1;
		y = z = 0;
		type.updateIndex( -1 );
		type.updateDataArray( this );
	}

	@Override
	public void getPosition( int[] position )
	{
		position[ 0 ] = x;
		position[ 1 ] = y;
		position[ 2 ] = z;
	}

	@Override
	public Array3D<T> getStorageContainer(){ return container; }

	@Override
	public int[] getPosition(){ return new int[]{x, y, z}; }
	
	@Override
	public int getPosition( final int dim )
	{
		if ( dim == 0 )
			return x;
		else if ( dim == 1 )
			return y;
		else if ( dim == 2 )
			return z;
		
		System.err.println("Array3DLocalizableByDimOutsideCursor.getPosition( int dim ): There is no dimension " + dim );
		return -1;
	}

	@Override
	public void fwd( final int dim )
	{
		if ( dim == 0 )
			fwdX();
		else if ( dim == 1 )
			fwdY();
		else if ( dim == 2 )
			fwdZ();
		else
			System.err.println("Array3DLocalizableByDimOutsideCursor.fwd( int dim ): There is no dimension " + dim );
		
		/*
		position[ dim ]++;

		if ( isOutside )
		{
			// reenter the image?
			if ( position[ dim ] == 0 )
				setPosition( position );
			else // moved outside of the image
				outsideStrategy.notifyOutside( type );
		}
		else
		{			
			if ( position[ dim ] < dimensions[ dim ] )
			{
				// moved within the image
				type.i += step[ dim ];
			}
			else
			{
				// left the image
				isOutside = true;
				outsideStrategy.initOutside( type );
			}
		}
		 */
	}

	@Override
	public void fwdX()
	{
		if ( isOutside )
		{
			if ( x == -1 )
			{
				// possibly moved back into the image, depending on the other dimensions
				setPositionX( 0 );
			}
			else // moved outside of the image
			{
				++x;
				outsideStrategy.notifyOutsideFwd( 0 );
			}
		}
		else
		{
			if ( x < widthMinus1 )
			{
				// moved within the image
				type.incIndex();
				++x;
			}
			else
			{
				// left the image
				++x;
				isOutside = true;
				outsideStrategy.initOutside(  );				
			}
		}
	}

	@Override
	public void fwdY()
	{
		if ( isOutside )
		{
			if ( y == -1 )
			{
				// possibly moved back into the image, depending on the other dimensions
				setPositionY( 0 );
			}
			else // moved outside of the image
			{
				++y;
				outsideStrategy.notifyOutsideFwd( 1 );
			}
		}
		else
		{
			if ( y < heightMinus1 )
			{
				// moved within the image
				type.incIndex( stepY );
				++y;
			}
			else
			{
				// left the image
				++y;
				isOutside = true;
				outsideStrategy.initOutside(  );				
			}
		}
	}
	
	@Override
	public void fwdZ()
	{
		if ( isOutside )
		{
			if ( z == -1 )
			{
				// possibly moved back into the image, depending on the other dimensions
				setPositionZ( 0 );
			}
			else // moved outside of the image
			{
				++z;
				outsideStrategy.notifyOutsideFwd( 2 );
			}
		}
		else
		{
			if ( z < depthMinus1 )
			{
				// moved within the image
				type.incIndex( stepZ );
				++z;
			}
			else
			{
				// left the image
				++z;
				isOutside = true;
				outsideStrategy.initOutside(  );				
			}
		}
	}

	@Override
	public void move( final int steps, final int dim )
	{
		if ( dim == 0 )
			moveX( steps );
		else if ( dim == 1 )
			moveY( steps );
		else if ( dim == 2 )
			moveZ( steps );
		else
			System.err.println("Array3DLocalizableByDimOutsideCursor.move( int dim ): There is no dimension " + dim );
		
	}
	
	@Override
	public void moveRel( final int x, final int y, final int z )
	{
		moveX( x );
		moveY( y );
		moveZ( z );
	}

	@Override
	public void moveTo( final int x, final int y, final int z )
	{		
		moveX( x - this.x );
		moveY( y - this.y );
		moveZ( z - this.z );
	}
	
	@Override
	public void moveX( final int steps )
	{
		x += steps;
		
		if ( isOutside )
		{
			if ( x > -1 && x < width )
			{
				// possibly moved back into the image, depending on the other dimensions
				if ( y < 0 || y >= height || z < 0 || z >= depth )
				{
					outsideStrategy.notifyOutside( steps, 0 );
				}
				else
				{
					type.updateIndex( container.getPos( x, y, z ) );
					
					// new location is inside the image			
					type.updateDataArray( this );
					
					isOutside = false;					
				}
			}
			else // moved outside of the image
			{
				outsideStrategy.notifyOutside( steps, 0 );
			}
		}
		else
		{
			if ( x > -1 && x < width )
			{
				// moved within the image
				type.incIndex( steps );
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
	public void moveY( final int steps )
	{
		y += steps;
		
		if ( isOutside )
		{
			if ( y > -1 && y < height)
			{
				// possibly moved back into the image, depending on the other dimensions
				if ( x < 0 || x >= width || z < 0 || z >= depth )
				{
					outsideStrategy.notifyOutside( steps, 1 );
				}
				else
				{
					type.updateIndex( container.getPos( x, y, z ) );
					
					// new location is inside the image			
					type.updateDataArray( this );
					
					isOutside = false;					
				}
			}
			else
			{
				outsideStrategy.notifyOutside( steps, 1 );
			}
		}
		else
		{
			if (  y > -1 && y < height )
			{
				// moved within the image
				type.incIndex( steps * stepY );
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
	public void moveZ( final int steps )
	{
		z += steps;
		
		if ( isOutside )
		{
			if ( z > -1 && z < depth )
			{
				// possibly moved back into the image, depending on the other dimensions
				if ( y < 0 || y >= height || x < 0 || x >= width )
				{
					outsideStrategy.notifyOutside( steps, 2 );
				}
				else
				{
					type.updateIndex( container.getPos( x, y, z ) );
					
					// new location is inside the image			
					type.updateDataArray( this );
					
					isOutside = false;					
				}
			}
			else
			{
				outsideStrategy.notifyOutside( steps, 2 );
			}
		}
		else
		{
			if (  z > -1 && z < depth )
			{
				// moved within the image
				type.incIndex( steps * stepZ );
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
		if ( dim == 0 )
			bckX();
		else if ( dim == 1 )
			bckY();
		else if ( dim == 2 )
			bckZ();
		else
			System.err.println("Array3DLocalizableByDimCursor.bck( int dim ): There is no dimension " + dim );
	}

	@Override
	public void bckX()
	{
		if ( isOutside )
		{
			if ( x == width )
			{
				// possibly moved back into the image, depending on the other dimensions
				setPositionX( widthMinus1 );
			}
			else // moved outside of the image
			{
				--x;
				outsideStrategy.notifyOutsideBck( 0 );
			}
		}
		else
		{
			--x;
			
			if ( x > -1 )
			{
				// moved within the image
				type.decIndex();
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
	public void bckY()
	{
		if ( isOutside )
		{
			if ( y == height )
			{
				// possibly moved back into the image, depending on the other dimensions
				setPositionY( heightMinus1 );
			}
			else // moved outside of the image
			{
				--y;
				outsideStrategy.notifyOutsideBck( 1 );
			}
		}
		else
		{
			--y;
			
			if ( y > -1 )
			{
				// moved within the image
				type.decIndex( stepY );
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
	public void bckZ()
	{
		if ( isOutside )
		{
			if ( z == depth )
			{
				// possibly moved back into the image, depending on the other dimensions
				setPositionZ( depthMinus1 );
			}
			else // moved outside of the image
			{
				--z;
				outsideStrategy.notifyOutsideBck( 2 );
			}
		}
		else
		{
			--z;
			
			if ( z > -1 )
			{
				// moved within the image
				type.decIndex( stepZ );
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
	public void setPosition( final int[] position ) { setPosition( position[0], position[1], position[2] );	}

	@Override
	public void setPosition( final int posX, final int posY, final int posZ )
	{
		this.x = posX;
		this.y = posY;
		this.z = posZ;

		if ( posX > -1 && posX < width &&
			 posY > -1 && posY < height &&
			 posZ > -1 && posZ < depth)
		{			
			type.updateIndex( container.getPos( x, y, z ) );
			
			// new location is inside the image			
			if ( isOutside ) // we reenter the image with this setPosition() call
				type.updateDataArray( this );
			
			isOutside = false;
		}
		else
		{
			// new location is outside the image			
			if ( isOutside ) // just moved outside the image
			{
				outsideStrategy.notifyOutside(  );
			}
			else // we left the image with this setPosition() call
			{
				isOutside = true;
				outsideStrategy.initOutside(  );
			}
		}
	}
	
	@Override
	public void setPositionX( final int pos )
	{		
		// we are outside the image or in the initial starting position
		if ( isOutside || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( pos, y, z );
		}
		else if ( pos > -1 && pos < width )
		{
			type.incIndex( pos - x );
			x = pos;
		}
		else
		{
			x = pos;

			// moved outside the image
			isOutside = true;
			outsideStrategy.initOutside(  );
		}
	}

	@Override
	public void setPositionY( final int pos )
	{
		// we are outside the image or in the initial starting position
		if ( isOutside || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( x, pos, z );
		}
		else if ( pos > -1 && pos < height )
		{
			type.incIndex( (pos - y)*stepY );
			y = pos;
		}
		else
		{
			y = pos;

			// moved outside the image
			isOutside = true;
			outsideStrategy.initOutside(  );
		}
	}

	@Override
	public void setPositionZ( final int pos )
	{
		// we are outside the image or in the initial starting position
		if ( isOutside || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( x, y, pos );
		}
		else if ( pos > -1 && pos < depth )
		{
			type.incIndex( (pos - z)*stepZ );
			z = pos;
		}
		else
		{
			z = pos;

			// moved outside the image
			isOutside = true;
			outsideStrategy.initOutside(  );
		}
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		if ( dim == 0 )
			setPositionX( position );
		else if ( dim == 1 )
			setPositionY( position );
		else if ( dim == 2 )
			setPositionZ( position );
		else
			System.err.println("Array3DLocalizableByDimOutsideCursor.setPosition( int dim ): There is no dimension " + dim );
	}
	
	@Override
	public String getPositionAsString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
}
