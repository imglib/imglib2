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
import mpi.imglib.image.display.ComplexFloatTypePowerSpectrumDisplay;
import mpi.imglib.type.NumericType;
import mpi.imglib.type.TypeImpl;

public class ComplexFloatType extends TypeImpl<ComplexFloatType> implements NumericType<ComplexFloatType>
{
	final FloatContainer<ComplexFloatType> floatStorage;
	float[] v;
	
	// the index position of the real and complex component in the array
	int realI, complexI;
	
	// this is the constructor if you want it to read from an array
	public ComplexFloatType( FloatContainer<ComplexFloatType> floatStorage )
	{
		this.floatStorage = floatStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ComplexFloatType( final float real, final float complex )
	{
		floatStorage = null;
		v = new float[ 2 ];
		v[ 0 ] = real;
		v[ 1 ] = complex;
		i = 0;
		realI = 0;
		complexI = 1;
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType() { this( 0, 0 ); }

	@Override
	public void updateIndex( final int i ) 
	{ 
		this.i = i;
		realI = i * 2;
		complexI = i * 2 + 1;
	}
	
	@Override
	public void incIndex() 
	{ 
		++i;
		++realI;
		++realI;
		++complexI;
		++complexI;
	}
	@Override
	public void incIndex( final int increment ) 
	{ 
		i += increment; 
		
		final int inc2 = 2 * increment;		
		realI += inc2;
		complexI += inc2;
	}
	@Override
	public void decIndex() 
	{ 
		--i; 
		--realI;
		--realI;
		--complexI;
		--complexI;
	}
	@Override
	public void decIndex( final int decrement ) 
	{ 
		i -= decrement; 
		final int dec2 = 2 * decrement;		
		realI -= dec2;
		complexI -= dec2;
	}

	@Override
	public FloatContainer<ComplexFloatType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createFloatInstance( dim, 2 );	
	}

	@Override
	public ComplexFloatTypePowerSpectrumDisplay getDefaultDisplay( Image<ComplexFloatType> image )
	{
		return new ComplexFloatTypePowerSpectrumDisplay( image );
	}
	
	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = floatStorage.getCurrentStorageArray( c ); 
	}
	
	@Override
	public void mul( final float c ) { v[ realI ] *= c; }

	@Override
	public void mul( final double c ) { v[ realI ] *= c; }

	public float getReal() { return v[ realI ]; }
	public float getComplex() { return v[ complexI ]; }
	
	public void setReal( final float real ) { v[ realI ] = real; }
	public void setComplex( final float complex ) { v[ complexI ] = complex; }
	public void set( final float real, final float complex ) 
	{ 
		v[ realI ] = real;
		v[ complexI ] = complex;
	}

	@Override
	public void add( final ComplexFloatType c ) 
	{ 
		v[ realI ] += c.getReal(); 
		v[ complexI ] += c.getComplex(); 
	}

	@Override
	public void div( final ComplexFloatType c ) 
	{ 
		final float a1 = v[ realI ]; 
		final float b1 = v[ complexI ];
		final float c1 = c.getReal();
		final float d1 = c.getComplex();
		
		v[ realI ] = ( a1*c1 + b1*d1 ) / ( c1*c1 + d1*d1 );
		v[ complexI ] = ( b1*c1 - a1*d1 ) / ( c1*c1 + d1*d1 );
	}
	
	public void complexConjugate()
	{
		v[ complexI ] = -v[ complexI ];
	}
	
	public void switchRealComplex()
	{
		final float a = v[ realI ];
		v[ realI ] = v[ complexI ];
		v[ complexI ] = a;
	}

	@Override
	public void mul( final ComplexFloatType t ) 
	{
		// a + bi
		final float a = v[ realI ]; 
		final float b = v[ complexI ];
		
		// c + di
		final float c = t.getReal();
		final float d = t.getComplex();
		
		v[ realI ] = a*c - b*d; 
		v[ complexI ] = a*d + b*c; 
	}

	@Override
	public void sub( final ComplexFloatType c ) 
	{ 
		v[ realI ] -= c.getReal(); 
		v[ complexI ] -= c.getComplex(); 
	}

	@Override
	public int compareTo( final ComplexFloatType c ) 
	{
		final float real1 = v[ realI ];
		final float complex1 = v[ complexI ];
		final float real2 = c.getReal();
		final float complex2 = c.getComplex();
		
		if ( real1 > real2 || ( real1 == real2 && complex1 > complex2 ) )
			return 1;
		else if ( real1 < real2 ||  ( real1 == real2 && complex1 < complex2 ))
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final ComplexFloatType c ) 
	{ 
		v[ realI ] = c.getReal();
		v[ complexI ] = c.getComplex();
	}

	@Override
	public void setOne() 
	{ 
		v[ realI ] = 1; 
		v[ complexI ] = 0; 
	}

	@Override
	public void setZero() 
	{ 
		v[ realI ] = 0; 
		v[ complexI ] = 0; 
	}

	@Override
	public void inc() { v[ realI ]++; }

	@Override
	public void dec() { v[ realI ]--; }
	
	@Override
	public ComplexFloatType[] createArray1D(int size1){ return new ComplexFloatType[ size1 ]; }

	@Override
	public ComplexFloatType[][] createArray2D(int size1, int size2){ return new ComplexFloatType[ size1 ][ size2 ]; }

	@Override
	public ComplexFloatType[][][] createArray3D(int size1, int size2, int size3) { return new ComplexFloatType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public ComplexFloatType getType() { return this; }

	@Override
	public ComplexFloatType createType( Container<ComplexFloatType> container )
	{
		return new ComplexFloatType( (FloatContainer<ComplexFloatType>)container );
	}
	
	@Override
	public ComplexFloatType createVariable(){ return new ComplexFloatType( 0, 0 ); }
	
	@Override
	public ComplexFloatType copyVariable(){ return new ComplexFloatType( v[ realI ], v[ complexI] ); }
	
	@Override
	public String toString() { return "(" + v[ realI ] + ") + (" + v[complexI] + ")i"; }
}
