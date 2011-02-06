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
package mpicbg.imglib.type.numeric.complex;

import mpicbg.imglib.algorithm.Precision.PrecisionReal;
import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.ComplexType;

public class ComplexFloatType extends ComplexTypeImpl<ComplexFloatType> implements ComplexType<ComplexFloatType>, NativeType<ComplexFloatType>
{
	private int i = 0;
	
	// the indices for real and complex number
	private int realI = 0, complexI = 1;
	
	// the NativeContainer
	final NativeContainer<ComplexFloatType, ? extends FloatAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	FloatAccess b;
	
	// this is the constructor if you want it to read from an array
	public ComplexFloatType( NativeContainer<ComplexFloatType, ? extends FloatAccess> complexfloatStorage )
	{
		storage = complexfloatStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ComplexFloatType( final float real, final float complex )
	{
		storage = null;
		b = new FloatArray( 2 );
		set( real, complex );
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType() { this( 0, 0 ); }

	@Override
	public NativeContainer<ComplexFloatType, ? extends FloatAccess> createSuitableNativeContainer( final NativeContainerFactory<ComplexFloatType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeContainer<ComplexFloatType, ? extends FloatAccess> container = storageFactory.createFloatInstance( dim, 2 );
		
		// create a Type that is linked to the container
		final ComplexFloatType linkedType = new ComplexFloatType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Object c ) { b = storage.update( c ); }
		
	@Override
	public ComplexFloatType duplicateTypeOnSameNativeContainer() { return new ComplexFloatType( storage ); }
	
	@Override
	public float getRealFloat() { return b.getValue( realI ); }
	@Override
	public double getRealDouble() { return b.getValue( realI ); }
	@Override
	public float getImaginaryFloat() { return b.getValue( complexI ); }
	@Override
	public double getImaginaryDouble() { return b.getValue( complexI ); }
	
	@Override
	public void setReal( final float real ){ b.setValue( realI, real ); }
	@Override
	public void setReal( final double real ){ b.setValue( realI, (float)real ); }
	@Override
	public void setImaginary( final float complex ){ b.setValue( complexI, complex ); }
	@Override
	public void setImaginary( final double complex ){ b.setValue( complexI, (float)complex ); }
	
	@Override
	public PrecisionReal getPreferredRealPrecision() { return PrecisionReal.Float; }

	public void set( final float real, final float complex ) 
	{ 
		b.setValue( realI, real );
		b.setValue( complexI, complex );
	}

	@Override
	public void add( final ComplexFloatType c ) 
	{
		setReal( getRealFloat() + c.getRealFloat() );
		setImaginary( getImaginaryFloat() + c.getImaginaryFloat() );
	}

	@Override
	public void div( final ComplexFloatType c ) 
	{ 
		final float a1 = getRealFloat(); 
		final float b1 = getImaginaryFloat();
		final float c1 = c.getRealFloat();
		final float d1 = c.getImaginaryFloat();
		
		setReal( ( a1*c1 + b1*d1 ) / ( c1*c1 + d1*d1 ) );
		setImaginary( ( b1*c1 - a1*d1 ) / ( c1*c1 + d1*d1 ) );
	}

	@Override
	public void mul( final ComplexFloatType t ) 
	{
		// a + bi
		final float a = getRealFloat(); 
		final float b = getImaginaryFloat();
		
		// c + di
		final float c = t.getRealFloat();
		final float d = t.getImaginaryFloat();
		
		setReal( a*c - b*d ); 
		setImaginary( a*d + b*c ); 
	}

	@Override
	public void sub( final ComplexFloatType c ) 
	{ 
		setReal( getRealFloat() - c.getRealFloat() ); 
		setImaginary( getImaginaryFloat() - c.getImaginaryFloat() ); 
	}
	
	public void complexConjugate(){ setImaginary( -getImaginaryFloat() ); }
	
	public void switchRealComplex()
	{
		final float a = getRealFloat();
		setReal( getImaginaryFloat() );
		setImaginary( a );
	}


	@Override
	public void set( final ComplexFloatType c ) 
	{ 
		setReal( c.getRealFloat() );
		setImaginary( c.getImaginaryFloat() );
	}
	
	@Override
	public ComplexFloatType createVariable(){ return new ComplexFloatType( 0, 0 ); }
	
	@Override
	public ComplexFloatType copy(){ return new ComplexFloatType( getRealFloat(), getImaginaryFloat() ); }
	
	@Override
	public int getEntitiesPerPixel() { return 2; }	
	
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
		realI += 2;
		complexI += 2;
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
		realI -= 2;
		complexI -= 2;
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
	public int getIndex() { return i; }	
}
