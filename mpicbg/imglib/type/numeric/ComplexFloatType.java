/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.type.numeric;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.FloatContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ComplexFloatTypePowerSpectrumDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

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
	
	public void complexConjugate() { v[ complexI ] = -v[ complexI ]; }

	public void normalizeLength( final float threshold )
	{
		final float real = v[ realI ];
		final float complex = v[ complexI ];
		
		final float length = (float)Math.sqrt( real*real + complex*complex );
		
		if ( length < threshold )
		{
			v[ realI ] = v[ complexI ] = 0;
		}
		else
		{
			v[ realI ] /= length;
			v[ complexI ] /= length;
		}
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

	//@Override
	//public ComplexFloatType getType() { return this; }

	@Override
	public ComplexFloatType createType( Container<ComplexFloatType> container )
	{
		return new ComplexFloatType( (FloatContainer<ComplexFloatType>)container );
	}
	
	@Override
	public ComplexFloatType createVariable(){ return new ComplexFloatType( 0, 0 ); }
	
	@Override
	public ComplexFloatType clone(){ return new ComplexFloatType( v[ realI ], v[ complexI] ); }
	
	@Override
	public String toString() { return "(" + v[ realI ] + ") + (" + v[complexI] + ")i"; }
}
