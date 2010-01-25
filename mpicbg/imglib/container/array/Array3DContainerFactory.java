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

import mpicbg.imglib.container.basictypecontainer.*;
import mpicbg.imglib.type.Type;


public class Array3DContainerFactory extends ArrayContainerFactory
{
	@Override
	public <T extends Type<T>>BitContainer<T> createBitInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new BitArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>ByteContainer<T> createByteInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new ByteArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	
	@Override
	public <T extends Type<T>>CharContainer<T> createCharInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new CharArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	
	@Override
	public <T extends Type<T>>DoubleContainer<T> createDoubleInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new DoubleArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	
	@Override
	public <T extends Type<T>>FloatContainer<T> createFloatInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new FloatArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	
	@Override
	public <T extends Type<T>>IntContainer<T> createIntInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new IntArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	
	@Override
	public <T extends Type<T>>LongContainer<T> createLongInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new LongArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
	}

	
	@Override
	public <T extends Type<T>>ShortContainer<T> createShortInstance( final int[] dim, final int entitiesPerPixel)
	{
		return new ShortArray3D<T>( this, dim[0], dim[1], dim[2], entitiesPerPixel );
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
