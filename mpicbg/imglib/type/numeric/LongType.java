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
import mpicbg.imglib.container.basictypecontainer.LongContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.LongTypeDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public class LongType extends TypeImpl<LongType> implements NumericType<LongType>
{
	final LongContainer<LongType> longStorage;
	long[] v;
	
	// this is the constructor if you want it to read from an array
	public LongType( LongContainer<LongType> longStorage )
	{
		this.longStorage = longStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public LongType( final long value )
	{
		longStorage = null;
		v = new long[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public LongType() { this( 0 ); }

	@Override
	public LongContainer<LongType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createLongInstance( dim, 1 );	
	}

	@Override
	public LongTypeDisplay getDefaultDisplay( Image<LongType> image )
	{
		return new LongTypeDisplay( image );
	}

	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = longStorage.getCurrentStorageArray( c ); 
	}

	@Override
	public void mul( final float c ) { v[ i ] = Math.round( v[ i ] * c ); }

	@Override
	public void mul( final double c ) { v[ i ] = Math.round( v[ i ] * c ); }

	public long get() { return v[ i ]; }
	public void set( final long f ) { v[ i ] = f; }
	public float getReal() { return (float)v[ i ]; }
	public void setReal( final float f ) { v[ i ] = MathLib.round( f ); }

	@Override
	public void add( final LongType c ) { v[ i ] += c.get(); }

	@Override
	public void div( final LongType c ) { v[ i ] /= c.get(); }

	@Override
	public void mul( final LongType c ) { v[ i ] *= c.get(); }

	@Override
	public void sub( final LongType c ) { v[ i ] -= c.get(); }

	@Override
	public int compareTo( final LongType c ) 
	{ 
		if ( v[ i ] > c.get() )
			return 1;
		else if ( v[ i ] < c.get() )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final LongType c ) { v[ i ] = c.get(); }

	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }
	
	@Override
	public LongType[] createArray1D(int size1){ return new LongType[ size1 ]; }

	@Override
	public LongType[][] createArray2D(int size1, int size2){ return new LongType[ size1 ][ size2 ]; }

	@Override
	public LongType[][][] createArray3D(int size1, int size2, int size3) { return new LongType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public LongType getType() { return this; }
	
	@Override
	public LongType createType( Container<LongType> container )
	{
		return new LongType( (LongContainer<LongType>)container );
	}

	@Override
	public LongType createVariable(){ return new LongType( 0 ); }

	@Override
	public LongType copyVariable(){ return new LongType( v[ i ] ); }

	@Override
	public String toString() { return "" + v[i]; }
}
