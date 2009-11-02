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
package mpi.imglib.cursor.imageplus;

import mpi.imglib.container.imageplus.ImagePlusContainer;

import mpi.imglib.cursor.Cursor;
import mpi.imglib.cursor.CursorImpl;

import mpi.imglib.image.Image;

import mpi.imglib.type.Type;

public class ImagePlusCursor<T extends Type<T>> extends CursorImpl<T> implements Cursor<T>
{
	protected final ImagePlusContainer<T> container;
	protected final int slicePixelCountMinus1, maxSliceMinus1;
	protected int slice; // TODO: support hyperstacks	

	public ImagePlusCursor( final ImagePlusContainer<T> container, final Image<T> image, final T type )
	{
		super( container, image, type );

		this.container = container;
		slicePixelCountMinus1 = container.getDimension( 0 ) * container.getDimension( 1 ) - 1; 
		maxSliceMinus1 = container.getDimension( 2 ) - 1;
		
		reset();
	}

	@Override
	public boolean hasNext()
	{
		if ( type.getIndex() < slicePixelCountMinus1 || slice < maxSliceMinus1 )
			return true;
		else
			return false;
	}

	@Override
	public void fwd() 
	{
		type.incIndex();
		
		if ( type.getIndex() > slicePixelCountMinus1 ) 
		{
			slice++;
			type.updateIndex( 0 );
			type.updateDataArray( this );
		}
	}

	@Override
	public void close()
	{
		isClosed = true;
		type.updateIndex( slicePixelCountMinus1 + 1 );
		slice = maxSliceMinus1 + 1;
	}

	@Override
	public void reset()
	{
		slice = 0;
		type.updateIndex( -1 );
		type.updateDataArray( this );
		isClosed = false;
	}

	@Override
	public ImagePlusContainer<T> getStorageContainer(){ return container; }

	@Override
	public int getStorageIndex() { return slice; }

	@Override
	public String toString() { return type.toString(); }
}
