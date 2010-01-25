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

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.*;
import mpicbg.imglib.type.Type;

public class ArrayContainerFactory extends ContainerFactory
{
	@Override
	public <T extends Type<T>>BitContainer<T> createBitInstance( int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new BitArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new BitArray<T>( this, dimensions, entitiesPerPixel );
	}
	
	@Override
	public <T extends Type<T>>ByteContainer<T> createByteInstance( int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new ByteArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new ByteArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>CharContainer<T> createCharInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new CharArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new CharArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>DoubleContainer<T> createDoubleInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new DoubleArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new DoubleArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>FloatContainer<T> createFloatInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new FloatArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new FloatArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>IntContainer<T> createIntInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new IntArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new IntArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>LongContainer<T> createLongInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new LongArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new LongArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>ShortContainer<T> createShortInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new ShortArray3D<T>( this, dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new ShortArray<T>( this, dimensions, entitiesPerPixel );
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printProperties()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParameters(String configuration)
	{
		// TODO Auto-generated method stub
		
	}

}
