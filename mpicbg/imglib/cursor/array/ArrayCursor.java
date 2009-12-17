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

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.CursorImpl;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public class ArrayCursor<T extends Type<T>> extends CursorImpl<T> implements Cursor<T>
{
	protected final Array<T> container;
	protected final int sizeMinus1;
	//protected final int size;
	
	public ArrayCursor( final Array<T> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );

		this.container = container;
		//this.size = container.getNumPixels();
		this.sizeMinus1 = container.getNumPixels() - 1;
		
		reset();
	}
	
	@Override
	public boolean hasNext()
	{
		return type.getIndex() < sizeMinus1;
		
		/*if ( type.getIndex() < sizeMinus1 )
			return true;
		else
			return false;*/
	}

	@Override
	public void fwd( final long steps ) { type.incIndex( (int)steps ); }

	@Override
	public void fwd() { type.incIndex(); }

	@Override
	public void close() 
	{ 
		isClosed = true;
		type.updateIndex( sizeMinus1 + 1 );
	}

	@Override
	public void reset()
	{ 
		type.updateIndex( -1 ); 
		type.updateDataArray( this );
		isClosed = false;
	}

	@Override
	public Array<T> getStorageContainer(){ return container; }

	@Override
	public int getStorageIndex() { return 0; }
	
	@Override
	public String toString() { return type.toString(); }		
}
