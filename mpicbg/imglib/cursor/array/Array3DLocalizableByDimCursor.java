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
package mpicbg.imglib.cursor.array;

import mpicbg.imglib.container.array.Array3D;
import mpicbg.imglib.cursor.LocalizableByDimCursor3D;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class Array3DLocalizableByDimCursor<T extends Type<T>> extends ArrayLocalizableByDimCursor<T> implements LocalizableByDimCursor3D<T>
{
	protected int x = -1, y = 0, z = 0;
	final int widthMinus1, heightMinus1, depthMinus1, width, height, depth;
	final int stepY, stepZ;
	final Array3D<T> container;

	public Array3DLocalizableByDimCursor( final Array3D<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
		
		this.container = container;
		
		this.width = container.getWidth();
		this.height = container.getHeight();
		this.depth = container.getDepth();

		this.widthMinus1 = width - 1;
		this.heightMinus1 = height - 1;
		this.depthMinus1 = depth - 1;
		
		this.stepY = container.getWidth();
		this.stepZ = container.getWidth() * container.getHeight();
		
		reset();
	}
	
	@Override
	public void fwd()
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
		else
		{
			x = 0;
			y = 0;
			++z;
		}
	}

	@Override
	public void fwd( final long steps )
	{
		if ( steps <= 0 )
			return;
		
		type.incIndex( (int)steps );
		
		int i = type.getIndex();
		
		z = i / (width*height);		
		i -= z * width * height;
		
		y = i / width;
		i -= y * width;
		
		x = i;
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
		
		System.err.println("Array3DLocalizableByDimCursor.getPosition( int dim ): There is no dimension " + dim );
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
			System.err.println("Array3DLocalizableByDimCursor.fwd( int dim ): There is no dimension " + dim );
	}

	@Override
	public void fwdX()
	{
		type.incIndex();
		++x;		
	}

	@Override
	public void fwdY()
	{
		type.incIndex( stepY );
		++y;
	}
	
	@Override
	public void fwdZ()
	{
		type.incIndex( stepZ );
		++z;		
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
			System.err.println("Array3DLocalizableByDimCursor.move( int dim ): There is no dimension " + dim );		
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
		type.incIndex( steps );
		x += steps;		
	}

	@Override
	public void moveY( final int steps )
	{
		type.incIndex( steps * stepY );
		y += steps;		
	}
	
	@Override
	public void moveZ( final int steps )
	{
		type.incIndex( steps * stepZ );
		z += steps;		
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
		type.decIndex();
		--x;		
	}

	@Override
	public void bckY()
	{
		type.decIndex( stepY );
		--y;
	}
	
	@Override
	public void bckZ()
	{
		type.decIndex( stepZ );
		--z;		
	}
	
	@Override
	public void setPosition( final int[] position ) { setPosition( position[0], position[1], position[2] );	}

	@Override
	public void setPosition( final int posX, final int posY, final int posZ )
	{
		this.x = posX;
		this.y = posY;
		this.z = posZ;
		
		type.updateIndex( container.getPos( x, y, z ) );				
	}
	
	@Override
	public void setPositionX( final int pos )
	{
		type.incIndex( pos - x );
		x = pos;
	}

	@Override
	public void setPositionY( final int pos )
	{
		type.incIndex( (pos - y)*stepY );
		y = pos;		
	}

	@Override
	public void setPositionZ( final int pos )
	{
		type.incIndex( (pos - z)*stepZ );
		z = pos;		
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
			System.err.println("Array3DLocalizableByDimCursor.setPosition( int dim ): There is no dimension " + dim );
	}
	
	@Override
	public String getPositionAsString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}	
}
