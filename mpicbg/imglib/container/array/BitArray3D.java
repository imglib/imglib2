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

import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class BitArray3D<T extends Type<T>> extends Array3D<T> implements BitContainer<T>
{
	final static int bitsPerEntity = Integer.SIZE;

	protected int data[];	

	public BitArray3D( ArrayContainerFactory factory, int width, int height, int depth, final int entitiesPerPixel )
	{
		super( factory, width, height, depth, entitiesPerPixel );
		
		final int numElements;
		
		if ( this.numEntities % bitsPerEntity == 0 )
			numElements = this.numEntities / bitsPerEntity;
		else
			numElements = this.numEntities / bitsPerEntity + 1;
			
		this.data = new int[ numElements ];
	}

	@Override
	public void close() { data = null; }

	@Override
	public boolean getValue( final int index ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;

		final int entry = data[ arrayIndex ];		
		final int value = (entry & ( 1 << arrayOffset ) );
		
		return value != 0; 
	}

	@Override
	public void setValue( final int index, final boolean value ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;
		
		if ( value )
			data[ arrayIndex ] = data[ arrayIndex ] | ( 1 << arrayOffset );
		else
			data[ arrayIndex ] = data[ arrayIndex ] & ~( 1 << arrayOffset ); 
	}

	@Override
	public void updateStorageArray( final Cursor<?> c ) { }	
}
