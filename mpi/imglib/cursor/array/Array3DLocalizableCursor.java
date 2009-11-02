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
package mpi.imglib.cursor.array;

import mpi.imglib.container.array.Array3D;
import mpi.imglib.cursor.LocalizableCursor3D;
import mpi.imglib.image.Image;
import mpi.imglib.type.Type;

public class Array3DLocalizableCursor<T extends Type<T>> extends ArrayLocalizableCursor<T> implements LocalizableCursor3D<T>
{
	protected int x = -1, y = 0, z = 0;
	final int widthMinus1, heightMinus1, depthMinus1, width, height, depth;
	final Array3D<T> container;
	
	public Array3DLocalizableCursor( final Array3D<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );

		this.container = container;
		
		this.width = container.getWidth();
		this.height = container.getHeight();
		this.depth = container.getDepth();

		this.widthMinus1 = width - 1;
		this.heightMinus1 = height - 1;
		this.depthMinus1 = depth - 1;
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
	public void getPosition( final int[] position )
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

		System.err.println("Array3DLocalizableCursor.getPosition( int dim ): There is no dimension " + dim );
		return -1;
	}
	
	@Override
	public String getPositionAsString()
	{
		return "(" + x + ", " + y + ", " + z + ")";
	}	
}
