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
package mpicbg.imglib.container.cube;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class BitCube<T extends Type<T>> extends Cube<BitCubeElement<T>, BitCube<T>, T> implements BitContainer<T>
{
	int[] cache = null;
	
	public BitCube(ContainerFactory factory, int[] dim, int[] cubeSize, int entitiesPerPixel)
	{
		super(factory, dim, cubeSize, entitiesPerPixel);
	}
	
	@Override
	public BitCubeElement<T> createCubeElementInstance( final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		return new BitCubeElement<T>( this, cubeId, dim, offset, entitiesPerPixel );
	}

	@Override
	public boolean getValue( final int index ) 
	{
		final int arrayIndex = index / BitCubeElement.bitsPerEntity;
		final int arrayOffset = index % BitCubeElement.bitsPerEntity;

		final int entry = cache[ arrayIndex ];		
		final int value = (entry & ( 1 << arrayOffset ) );
		
		return value != 0; 
	}

	@Override
	public void setValue( final int index, final boolean value ) 
	{
		final int arrayIndex = index / BitCubeElement.bitsPerEntity;
		final int arrayOffset = index % BitCubeElement.bitsPerEntity;
		
		if ( value )
			cache[ arrayIndex ] = cache[ arrayIndex ] | ( 1 << arrayOffset );
		else
			cache[ arrayIndex ] = cache[ arrayIndex ] & ~( 1 << arrayOffset ); 
	}

	@Override
	public void updateStorageArray( final Cursor<?> c )
	{
		cache = data.get( c.getStorageIndex() ).data;
	}	
}
