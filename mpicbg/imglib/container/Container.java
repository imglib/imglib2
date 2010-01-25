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
package mpicbg.imglib.container;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public interface Container<T extends Type<T>>
{
	public Cursor<T> createCursor( T type, Image<T> image );
	public LocalizableCursor<T> createLocalizableCursor( T type, Image<T> image );
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( T type, Image<T> image );
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( T type, Image<T> image );
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( T type, Image<T> image, OutsideStrategyFactory<T> outsideFactory );
	
	public void close();

	public ContainerFactory getFactory();
	
	public int getNumDimensions();
	public int[] getDimensions();
	public int getId();
	
	public void getDimensions( int[] dimensions );
	public int getDimension( int dim );
	public int getNumPixels();
	public int getNumEntities();
	public int getNumEntitiesPerPixel();
		
	public boolean compareStorageContainerDimensions( final Container<?> img );
	public boolean compareStorageContainerCompatibility( final Container<?> img );

}
