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

import mpicbg.imglib.container.ContainerImpl;
import mpicbg.imglib.cursor.array.ArrayCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableByDimOutsideCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public abstract class Array<T extends Type<T>> extends ContainerImpl<T>
{
	final protected int[] step;
	final ArrayContainerFactory factory;

	public Array( ArrayContainerFactory factory, int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );
		step = Array.createAllocationSteps( dim );
		this.factory = factory;
	}
	
	@Override
	public ArrayContainerFactory getFactory() { return factory; }
	
	@Override
	public ArrayCursor<T> createCursor( final T type, final Image<T> image ) 
	{ 
		ArrayCursor<T> c = new ArrayCursor<T>( this, image, type );
		return c;
	}

	@Override
	public ArrayLocalizableCursor<T> createLocalizableCursor( final T type, final Image<T> image ) 
	{ 
		ArrayLocalizableCursor<T> c = new ArrayLocalizableCursor<T>( this, image, type );
		return c;
	}

	@Override
	public ArrayLocalizablePlaneCursor<T> createLocalizablePlaneCursor( final T type, final Image<T> image ) 
	{ 
		ArrayLocalizablePlaneCursor<T> c = new ArrayLocalizablePlaneCursor<T>( this, image, type );
		return c;
	}
	
	@Override
	public ArrayLocalizableByDimCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image ) 
	{ 
		ArrayLocalizableByDimCursor<T> c = new ArrayLocalizableByDimCursor<T>( this, image, type );
		return c;
	}
	
	@Override
	public ArrayLocalizableByDimOutsideCursor<T> createLocalizableByDimCursor( final T type, final Image<T> image, final OutsideStrategyFactory<T> outsideFactory ) 
	{ 
		ArrayLocalizableByDimOutsideCursor<T> c = new ArrayLocalizableByDimOutsideCursor<T>( this, image, type, outsideFactory );
		return c;
	}
	
	public static int[] createAllocationSteps( final int[] dim )
	{
		int[] steps = new int[ dim.length ];
		createAllocationSteps( dim, steps );
		return steps;		
	}

	public static void createAllocationSteps( final int[] dim, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dim.length; ++d )
			  steps[ d ] = steps[ d - 1 ] * dim[ d - 1 ];
	}
	
	public final int getPos( final int[] l ) 
	{ 
		int i = l[ 0 ];
		for ( int d = 1; d < numDimensions; ++d )
			i += l[ d ] * step[ d ];
		
		return i;
	}	
}
