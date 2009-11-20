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
package mpi.imglib.type;

import mpi.imglib.container.Container;
import mpi.imglib.container.ContainerFactory;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.display.Display;

public interface Type<T extends Type<T>>
{		
	public Container<T> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] );
	public Display<T> getDefaultDisplay( Image<T> image );
	
	public void updateIndex( final int i );
	public int getIndex();	
	public void incIndex();
	public void incIndex( final int increment );
	public void decIndex();
	public void decIndex( final int decrement );
	
	public void updateDataArray( Cursor<?> c );	
	//public void updateDataArray( T type );	
	//public boolean hasSameDataArray( T type );		
	
	public T getType();
	public T createType( Container<T> container );
	public T createVariable();
	public T copyVariable();
	
	public void set( T c );	
	
	public T[] createArray1D( int size1 );
	public T[][] createArray2D( int size1, int size2 );
	public T[][][] createArray3D( int size1, int size2, int size3 );	
}
