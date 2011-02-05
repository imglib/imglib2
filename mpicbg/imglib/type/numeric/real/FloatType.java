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
package mpicbg.imglib.type.numeric.real;

import mpicbg.imglib.algorithm.Precision.PrecisionReal;
import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.RealType;

public class FloatType extends RealTypeImpl<FloatType> implements RealType<FloatType>, NativeType<FloatType>
{
	private int i = 0;

	// the NativeContainer
	final NativeContainer<FloatType, ? extends FloatAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	FloatAccess b;
	
	// this is the constructor if you want it to read from an array
	public FloatType( NativeContainer<FloatType, ? extends FloatAccess> floatStorage )
	{
		storage = floatStorage;
	}

	// this is the constructor if you want it to be a variable
	public FloatType( final float value )
	{
		storage = null;
		b = new FloatArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public FloatType() { this( 0 ); }

	@Override
	public NativeContainer<FloatType, ? extends FloatAccess> createSuitableNativeContainer( final NativeContainerFactory<FloatType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeContainer<FloatType, ? extends FloatAccess> container = storageFactory.createFloatInstance( new FloatType(), dim, 1 );
		
		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public void updateContainer( final Object c ) 
	{ 
		b = storage.update( c ); 
	}
	
	@Override
	public FloatType duplicateTypeOnSameNativeContainer() { return new FloatType( storage ); }

	public float get(){ return b.getValue( i ); }
	public void set( final float f ){ b.setValue( i, f ); }
	
	@Override
	public float getRealFloat() { return get(); }
	@Override
	public double getRealDouble() { return get(); }
	
	@Override
	public void setReal( final float real ){ set( real ); }
	@Override
	public void setReal( final double real ){ set( (float)real ); }
	
	@Override
	public PrecisionReal getPreferredRealPrecision() { return PrecisionReal.Float; }

	@Override
	public double getMaxValue() { return Float.MAX_VALUE; }
	@Override
	public double getMinValue()  { return -Float.MAX_VALUE; }
	@Override
	public double getMinIncrement()  { return Float.MIN_VALUE; }

	@Override
	public void mul( final float c )
	{
		set( get() * c );
	}

	@Override
	public void mul( final double c )
	{
		set( ( float )( get() * c ) );
	}
	
	@Override
	public void add( final FloatType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final FloatType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final FloatType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final FloatType c )
	{
		set( get() - c.get() );
	}

	@Override
	public int compareTo( final FloatType c ) 
	{ 
		final float a = get();
		final float b = c.get();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public void set( final FloatType c ){ set( c.get() ); }

	@Override
	public void setOne() { set( 1 ); }

	@Override
	public void setZero() { set( 0 ); }

	@Override
	public void inc()
	{
		float a = get();
		set( ++a );
	}

	@Override
	public void dec()
	{
		float a = get();
		set( --a );
	}
	
	@Override
	public int getEntitiesPerPixel() { return 1; }
	
	@Override
	public FloatType createVariable(){ return new FloatType( 0 ); }
	
	@Override
	public FloatType copy(){ return new FloatType( get() ); }

	@Override
	public void updateIndex( final int i ) { this.i = i; }
	@Override
	public int getIndex() { return i; }
	
	@Override
	public void incIndex() { ++i; }
	@Override
	public void incIndex( final int increment ) { i += increment; }
	@Override
	public void decIndex() { --i; }
	@Override
	public void decIndex( final int decrement ) { i -= decrement; }	
}
