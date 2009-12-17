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
import mpicbg.imglib.cursor.vector.Dimensionality;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

public interface Cursor<T extends Type<T>> extends Iterator<T>, java.lang.Iterable<T>, Iterable, Dimensionality
{	
	public void reset();			
	public boolean isActive();	

	public Image<T> getImage();
	public T getType();
	public int getArrayIndex();
	public int getStorageIndex();
	public Container<T> getStorageContainer();
	public void setDebug( final boolean debug );
	public int[] createPositionArray();
	
	public void close();
}
