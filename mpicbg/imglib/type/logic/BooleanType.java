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

package mpicbg.imglib.type.logic;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.BooleanTypeDisplay;
import mpicbg.imglib.type.LogicType;
import mpicbg.imglib.type.TypeImpl;

public class BooleanType extends TypeImpl<BooleanType> implements LogicType<BooleanType>
{
	final BitContainer<BooleanType> bitStorage;
	BitContainer<BooleanType> b;
	
	int outputType = 0;
	
	// this is the constructor if you want it to read from an array
	public BooleanType( final BitContainer<BooleanType> bitStorage )
	{
		this.bitStorage = bitStorage;
		this.b = bitStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public BooleanType( final boolean value )
	{
		this( new BitArray<BooleanType>( null, new int[]{1}, 1 ) );
		b.setValue( i, value );
	}

	// this is the constructor if you want it to be a variable
	public BooleanType() { this( false ); }
	
	@Override
	public BitContainer<BooleanType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createBitInstance( dim, 1 );	
	}
	
	@Override
	public BooleanTypeDisplay getDefaultDisplay( final Image<BooleanType> image )
	{
		return new BooleanTypeDisplay( image );
	}
	
	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		b = bitStorage;
		b.updateStorageArray( c );
	}

	public boolean get() { return b.getValue( i ); }
	public void set( final boolean value) { b.setValue( i, value ); }

	@Override
	public void set( final BooleanType c ) { b.setValue(i, c.get() ); }

	@Override
	public void and( final BooleanType c ) { b.setValue(i, b.getValue(i) && c.get() ); }
	
	@Override
	public void or( final BooleanType c ) { b.setValue(i, b.getValue(i) || c.get() ); }
	
	@Override
	public void xor( final BooleanType c ) { b.setValue(i, b.getValue(i) ^ c.get() ); }
	
	@Override
	public void not() { b.setValue(i, !b.getValue(i) ); }
	
	@Override
	public int compareTo( final BooleanType c ) 
	{
		final boolean b1 = b.getValue(i);
		final boolean b2 = c.get();
		
		if ( b1 && !b2 )
			return 1;
		else if ( !b1 && b2 )
			return -1;
		else 
			return 0;
	}

	@Override
	public BooleanType[] createArray1D(int size1){ return new BooleanType[ size1 ]; }

	@Override
	public BooleanType[][] createArray2D(int size1, int size2){ return new BooleanType[ size1 ][ size2 ]; }

	@Override
	public BooleanType[][][] createArray3D(int size1, int size2, int size3) { return new BooleanType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public BooleanType getType() { return this; }

	@Override
	public BooleanType createType( Container<BooleanType> container )
	{
		return new BooleanType( (BitContainer<BooleanType>)container );
	}
	
	@Override
	public BooleanType createVariable(){ return new BooleanType(); }

	@Override
	public BooleanType copyVariable(){ return new BooleanType( b.getValue(i) ); }

	@Override
	public String toString() 
	{
		final boolean value = b.getValue(i);
		
		if ( outputType == 0)
			return "" + value;
		else
			if ( value ) 
				return "1"; 
			else 
				return "0"; 			
	}
	
	public void setOutputType( final int outputType ) { this.outputType = outputType; }
}
