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
package mpicbg.imglib.cursor;

import java.util.Iterator;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * We use the class {@link CursorImpl} instead of implementing methods here so that other classes can
 * only implement {@link Cursor} and extend other classes instead. As each {@link CursorImpl} is also
 * a {@link Cursor} there are no disadvantages for the {@link Cursor} implementations.
 * 
 * @author Stephan
 *
 * @param <T>
 */
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
	
	@Override
	public int getNumDimensions() { return image.getNumDimensions(); }
	
	@Override
	public int[] getDimensions() { return image.getDimensions(); }
	
	@Override
	public void getDimensions( int[] position ) { image.getDimensions( position ); }
	
}
