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

import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.basictypeaccess.FloatAccess;
import mpicbg.imglib.img.basictypeaccess.array.FloatArray;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.RealType;

public class FloatType extends AbstractRealType<FloatType> implements RealType<FloatType>, NativeType<FloatType>
{
	private int i = 0;

	final protected NativeImg<FloatType, ? extends FloatAccess> img;
	
	// the DataAccess that holds the information 
	protected FloatAccess dataAccess;
	
	// this is the constructor if you want it to read from an array
	public FloatType( NativeImg<FloatType, ? extends FloatAccess> floatStorage )
	{
		img = floatStorage;
	}

	// this is the constructor if you want it to be a variable
	public FloatType( final float value )
	{
		img = null;
		dataAccess = new FloatArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public FloatType() { this( 0 ); }

	@Override
	public NativeImg<FloatType, ? extends FloatAccess> createSuitableNativeImg( final NativeImgFactory<FloatType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<FloatType, ? extends FloatAccess> container = storageFactory.createFloatInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public void updateContainer( final Object c ) 
	{ 
		dataAccess = img.update( c ); 
	}
	
	@Override
	public FloatType duplicateTypeOnSameNativeImg() { return new FloatType( img ); }

	public float get(){ return dataAccess.getValue( i ); }
	public void set( final float f ){ dataAccess.setValue( i, f ); }
	
	@Override
	public float getRealFloat() { return get(); }
	@Override
	public double getRealDouble() { return get(); }
	
	@Override
	public void setReal( final float real ){ set( real ); }
	@Override
	public void setReal( final double real ){ set( (float)real ); }
	
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
	public FloatType createVariable(){ return new FloatType( 0 ); }
	
	@Override
	public FloatType copy(){ return new FloatType( get() ); }

	@Override
	public int getEntitiesPerPixel() { return 1; }
	
	@Override
	public void updateIndex( final int index ) { i = index; }
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
