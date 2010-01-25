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
package mpicbg.imglib.type.numeric;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ByteTypeDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public class ByteType extends TypeImpl<ByteType> implements NumericType<ByteType>
{
	final ByteContainer<ByteType> byteStorage;
	byte[] v;
	
	// this is the constructor if you want it to read from an array
	public ByteType( final ByteContainer<ByteType> byteStorage )
	{
		this.byteStorage = byteStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ByteType( final byte value )
	{
		byteStorage = null;
		v = new byte[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public ByteType() { this( (byte)0 ); }
	
	@Override
	public ByteContainer<ByteType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createByteInstance( dim, 1 );	
	}
	
	@Override
	public ByteTypeDisplay getDefaultDisplay( Image<ByteType> image )
	{
		return new ByteTypeDisplay( image );
	}
	
	@Override
	public void updateDataArray( Cursor<?> c ) 
	{ 
		v = byteStorage.getCurrentStorageArray( c ); 
	}

	@Override
	final public void mul( final float c ) { v[ i ] = (byte)Math.round( v[ i ] * c ); }

	@Override
	public void mul( final double c ) { v[ i ] = (byte)Math.round( v[ i ] * c ); }

	public byte get() { return v[ i ]; }
	public void set( final byte f ) { v[ i ] = f; }
	public float getReal() { return v[ i ]; }
	public void setReal( final float f ) { v[ i ] = (byte)MathLib.round( f ); }

	@Override
	public void add( final ByteType c ) { v[ i ] += c.get(); }

	@Override
	public void div( final ByteType c ) { v[ i ] /= c.get(); }

	@Override
	public void mul( final ByteType c ) { v[ i ] *= c.get(); }

	@Override
	public void sub( final ByteType c ) { v[ i ] -= c.get(); }

	@Override
	public void set( final ByteType c ) { v[ i ] = c.get(); }

	@Override
	public int compareTo( final ByteType c ) 
	{ 
		if ( v[ i ] > c.get() )
			return 1;
		else if ( v[ i ] < c.get() )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }

	@Override
	public ByteType[] createArray1D(int size1){ return new ByteType[ size1 ]; }

	@Override
	public ByteType[][] createArray2D(int size1, int size2){ return new ByteType[ size1 ][ size2 ]; }

	@Override
	public ByteType[][][] createArray3D(int size1, int size2, int size3) { return new ByteType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public ByteType getType() { return this; }

	@Override
	public ByteType createType( Container<ByteType> container )
	{
		return new ByteType( (ByteContainer<ByteType>)container );
	}
	
	@Override
	public ByteType createVariable(){ return new ByteType( (byte)0 ); }

	@Override
	public ByteType copyVariable(){ return new ByteType( v[ i ] ); }

	@Override
	public String toString() { return "" + v[i]; }
}
