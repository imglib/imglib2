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
 * @author Johannes Schindelin
 */
package mpicbg.imglib.container.imageplus;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.*;
import mpicbg.imglib.type.Type;

public class ImagePlusContainerFactory extends ContainerFactory
{
	@Override
	public <T extends Type<T>>BitContainer<T> createBitInstance(int[] dimensions, final int entitiesPerPixel)
	{
		throw new RuntimeException( "Unsupported type: bit" );
	}

	@Override
	public <T extends Type<T>>ByteContainer<T> createByteInstance( final int[] dimensions, final int entitiesPerPixel )
	{
		if ( entitiesPerPixel > 1 )
			throw new RuntimeException( "Unsupported entities per pixel: "+ entitiesPerPixel );
		
		if ( dimensions.length != 3 )
			throw new RuntimeException( "Unsupported dimensionality: "+ dimensions.length );
						
		return new ByteImagePlus<T>( this, dimensions, 1 );
	}

	@Override
	public <T extends Type<T>>CharContainer<T> createCharInstance(int[] dimensions, final int entitiesPerPixel)
	{
		throw new RuntimeException( "Unsupported type: char" );
	}

	@Override
	public <T extends Type<T>>DoubleContainer<T> createDoubleInstance(int[] dimensions, final int entitiesPerPixel)
	{
		throw new RuntimeException( "Unsupported type: double" );
	}

	@Override
	public <T extends Type<T>>FloatContainer<T> createFloatInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( entitiesPerPixel > 1 )
			throw new RuntimeException( "Unsupported entities per pixel: "+ entitiesPerPixel );
		
		if ( dimensions.length != 3 )
			throw new RuntimeException( "Unsupported dimensionality: "+ dimensions.length );
						
		return new FloatImagePlus<T>( this, dimensions, 1 );
	}

	@Override
	public <T extends Type<T>>IntContainer<T> createIntInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( entitiesPerPixel > 1 )
			throw new RuntimeException( "Unsupported entities per pixel: "+ entitiesPerPixel );
		
		if ( dimensions.length != 3 )
			throw new RuntimeException( "Unsupported dimensionality: "+ dimensions.length );
						
		return new IntImagePlus<T>( this, dimensions, 1 );
	}

	@Override
	public <T extends Type<T>>LongContainer<T> createLongInstance(int[] dimensions, final int entitiesPerPixel)
	{
		throw new RuntimeException( "Unsupported type: long" );
	}

	@Override
	public <T extends Type<T>>ShortContainer<T> createShortInstance(int[] dimensions, final int entitiesPerPixel)
	{
		if ( entitiesPerPixel > 1 )
			throw new RuntimeException( "Unsupported entities per pixel: "+ entitiesPerPixel );
		
		if ( dimensions.length != 3 )
			throw new RuntimeException( "Unsupported dimensionality: "+ dimensions.length );
						
		return new ShortImagePlus<T>( this, dimensions, 1 );
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
