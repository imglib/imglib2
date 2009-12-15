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
package mpi.imglib.type.numeric;

import mpi.imglib.container.Container;
import mpi.imglib.container.ContainerFactory;
import mpi.imglib.container.basictypecontainer.FloatContainer;
import mpi.imglib.cursor.Cursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.display.FloatTypeDisplay;
import mpi.imglib.type.NumericType;
import mpi.imglib.type.TypeImpl;

public class FloatType extends TypeImpl<FloatType> implements NumericType<FloatType>
{
	final FloatContainer<FloatType> floatStorage;
	float[] v;
	
	// this is the constructor if you want it to read from an array
	public FloatType( FloatContainer<FloatType> floatStorage )
	{
		this.floatStorage = floatStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public FloatType( final float value )
	{
		floatStorage = null;
		v = new float[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public FloatType() { this( 0 ); }

	@Override
	public FloatContainer<FloatType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createFloatInstance( dim, 1 );	
	}

	@Override
	public FloatTypeDisplay getDefaultDisplay( Image<FloatType> image )
	{
		return new FloatTypeDisplay( image );
	}
	
	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = floatStorage.getCurrentStorageArray( c ); 
	}

	@Override
	public void mul( final float c ) { v[ i ] *= c; }

	@Override
	public void mul( final double c ) { v[ i ] *= c; }

	public float get() { return v[ i ]; }
	public void set( final float f ) { v[ i ] = f; }
	public float getReal() { return v[ i ]; }
	public void setReal( final float f ) { v[ i ] = f; }

	@Override
	public void add( final FloatType c ) { v[ i ] += c.get(); }

	@Override
	public void div( final FloatType c ) { v[ i ] /= c.get(); }

	@Override
	public void mul( final FloatType c ) { v[ i ] *= c.get(); }

	@Override
	public void sub( final FloatType c ) { v[ i ] -= c.get(); }

	@Override
	public int compareTo( final FloatType c ) 
	{ 
		if ( v[ i ] > c.get() )
			return 1;
		else if ( v[ i ] < c.get() )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final FloatType c ) { v[ i ] = c.get(); }

	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }
	
	@Override
	public FloatType[] createArray1D(int size1){ return new FloatType[ size1 ]; }

	@Override
	public FloatType[][] createArray2D(int size1, int size2){ return new FloatType[ size1 ][ size2 ]; }

	@Override
	public FloatType[][][] createArray3D(int size1, int size2, int size3) { return new FloatType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public FloatType getType() { return this; }

	@Override
	public FloatType createType( Container<FloatType> container )
	{
		return new FloatType( (FloatContainer<FloatType>)container );
	}
	
	@Override
	public FloatType createVariable(){ return new FloatType( 0 ); }
	
	@Override
	public FloatType copyVariable(){ return new FloatType( v[ i ] ); }
	
	@Override
	public String toString() { return "" + v[i]; }
}
