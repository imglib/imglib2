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
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.Container3D;
import mpicbg.imglib.cursor.array.Array3DLocalizableByDimCursor;
import mpicbg.imglib.cursor.array.Array3DLocalizableByDimOutsideCursor;
import mpicbg.imglib.cursor.array.Array3DLocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public abstract class Array3D<T extends Type<T>> extends Array<T> implements Container3D<T>
{
	final int width, height, depth;
	
	public Array3D( ArrayContainerFactory factory, int width, int height, int depth, final int entitiesPerPixel )
	{
		super( factory, new int[]{ width, height, depth }, entitiesPerPixel );

		this.width = dim[ 0 ];
		this.height = dim[ 1 ];
		this.depth = dim[ 2 ];
	}

	@Override
	public Array3DLocalizableCursor<T> createLocalizableCursor( final T type, final Image<T> image ) 
	{ 
		Array3DLocalizableCursor<T> c = new Array3DLocalizableCursor<T>( this, image, type );
		return c;
	}

	@Override
	public Array3DLocalizableByDimCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image ) 
	{ 
		Array3DLocalizableByDimCursor<T> c = new Array3DLocalizableByDimCursor<T>( this, image, type );
		return c;
	}

	@Override
	public Array3DLocalizableByDimOutsideCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image, final OutsideStrategyFactory<T> outsideFactory ) 
	{ 
		Array3DLocalizableByDimOutsideCursor<T> c = new Array3DLocalizableByDimOutsideCursor<T>( this, image, type, outsideFactory );
		return c;
	}
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getDepth() { return depth; }

	public int getPos( final int x, final int y, final int z )
	{
		return (x + width * (y + z * height));
	}
}
