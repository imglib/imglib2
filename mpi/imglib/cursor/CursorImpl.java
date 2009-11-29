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
package mpi.imglib.cursor;

import java.util.Iterator;

import mpi.imglib.container.Container;
import mpi.imglib.image.Image;
import mpi.imglib.type.Type;

public abstract class CursorImpl<T extends Type<T>> implements Cursor<T>
{
	final protected T type;
	final protected Image<T> image;
	final protected Container<T> container;
	protected boolean isClosed = false, debug = false;
	
	public CursorImpl( final Container<T> container, final Image<T> image, final T type )
	{
		this.type = type;	
		this.image = image;
		this.container = container;
	}

	@Override
	public Iterator<T> iterator() 
	{
		reset();
		return this;
	}
	
	@Override
	public int getArrayIndex() { return type.getIndex(); }
	@Override
	public Image<T> getImage() { return image; }
	@Override
	public T getType() { return type; }
	@Override
	public Container<T> getStorageContainer() { return container; }
	@Override
	public boolean isActive() { return !isClosed; }
	@Override
	public void setDebug( final boolean debug ) { this.debug = debug; }
	
	@Override
	public void remove() {}
	
	@Override
	public T next(){ fwd(); return type; }

	@Override
	public void fwd( final long steps )
	{ 
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public int[] createPositionArray() { return new int[ image.getNumDimensions() ]; }	
}
