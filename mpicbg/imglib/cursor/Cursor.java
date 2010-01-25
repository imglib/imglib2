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
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.cube.Cube;
import mpicbg.imglib.cursor.vector.Dimensionality;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * The {@link Cursor} is responsible for iterating over the image. Therefore it has to be implemented 
 * for each type of {@link Container} like {@link Array}, {@link Cube}, ... 
 * 
 * The {@link Cursor} does not know which {@link Type} of {@link Image} it is working on as there this
 * is not important for its positioning. It is typed to the {@link Type} so that the {@link Cursor} is
 * able to return the correct instance of {@link Type} when calling the getType() method.
 * 
 * The {@link Cursor} class itself is only capable of iterating over all pixels of the {@link Image}, 
 * there is no guaranteed order in which the pixels are iterated, this depends on the implementation for
 * the specific {@link Container}.
 *  
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 * @param <T> - the {@link Type} this {@link Cursor} works on
 */
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
