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
import mpicbg.imglib.container.basictypecontainer.ShortContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ShortTypeDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public class ShortType extends TypeImpl<ShortType> implements NumericType<ShortType>
{
	final ShortContainer<ShortType> shortStorage;
	short[] v;
	
	// this is the constructor if you want it to read from an array
	public ShortType( final ShortContainer<ShortType> shortStorage )
	{
		this.shortStorage = shortStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ShortType( final short value )
	{
		shortStorage = null;
		v = new short[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public ShortType() { this( (short)0 ); }
	
	@Override
	public ShortContainer<ShortType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createShortInstance( dim, 1 );	
	}

	@Override
	public ShortTypeDisplay getDefaultDisplay( Image<ShortType> image )
	{
		return new ShortTypeDisplay( image );
	}

	@Override
	public void updateDataArray( Cursor<?> c ) 
	{ 
		v = shortStorage.getCurrentStorageArray( c ); 
	}

	@Override
	public void mul( final float c ) { v[ i ] = (short)Math.round( v[ i ] * c ); }

	@Override
	public void mul( final double c ) { v[ i ] = (short)Math.round( v[ i ] * c ); }

	public short get() { return v[ i ]; }
	public void set( final short f ) { v[ i ] = f; }
	public float getReal() { return v[ i ]; }
	public void setReal( final float f ) { v[ i ] = (short)MathLib.round( f ); }

	@Override
	public void add( final ShortType c ) { v[ i ] += c.get(); }

	@Override
	public void div( final ShortType c ) { v[ i ] /= c.get(); }

	@Override
	public void mul( final ShortType c ) { v[ i ] *= c.get(); }

	@Override
	public void sub( final ShortType c ) { v[ i ] -= c.get(); }

	@Override
	public int compareTo( final ShortType c ) 
	{ 
		if ( v[ i ] > c.get() )
			return 1;
		else if ( v[ i ] < c.get() )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final ShortType c ) { v[ i ] = c.get(); }

	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }

	@Override
	public ShortType[] createArray1D(int size1){ return new ShortType[ size1 ]; }

	@Override
	public ShortType[][] createArray2D(int size1, int size2){ return new ShortType[ size1 ][ size2 ]; }

	@Override
	public ShortType[][][] createArray3D(int size1, int size2, int size3) { return new ShortType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public ShortType getType() { return this; }
	
	@Override
	public ShortType createType( Container<ShortType> container )
	{
		return new ShortType( (ShortContainer<ShortType>)container );
	}

	@Override
	public ShortType createVariable(){ return new ShortType( (short)0 ); }

	@Override
	public ShortType copyVariable(){ return new ShortType( v[ i ] ); }

	@Override
	public String toString() { return "" + v[i]; }
}
