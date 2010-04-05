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

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.LinkedType;
import mpicbg.imglib.type.numeric.RealType;

public class FloatType extends RealTypeImpl<FloatType> implements RealType<FloatType>
{
	// the DirectAccessContainer
	final DirectAccessContainer<FloatType, ? extends FloatAccess> storage;
	
	// for linking a Type instance to a certain DirectAccessContainer
	LinkedType<FloatType, ? extends FloatAccess> lt = null;
	
	// the (sub)DirectAccessContainer that holds the information 
	FloatAccess b;
	
	// this is the constructor if you want it to read from an array
	public FloatType( DirectAccessContainer<FloatType, ? extends FloatAccess> floatStorage )
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
	public DirectAccessContainer<FloatType, ? extends FloatAccess> createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final int dim[] )
	{
		DirectAccessContainer<FloatType, ? extends FloatAccess> container = storageFactory.createFloatInstance( this, dim, 1 );
		
		// link this Type instance with the DirectAccessContainer
		lt = container.getLinkedType();		
		
		return container;
	}
	
	// this is only done by the DirectAccessContainer when creating a Type for a Cursor
	@Override
	public FloatType createDirectAccessType()
	{
		return new FloatType( lt.directAccessContainer );
	}

	@Override
	public void updateContainer( final Cursor<?> c ) 
	{ 
		b = storage.update( c ); 
	}
	
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
	public FloatType[] createArray1D(int size1){ return new FloatType[ size1 ]; }

	@Override
	public FloatType[][] createArray2D(int size1, int size2){ return new FloatType[ size1 ][ size2 ]; }

	@Override
	public FloatType[][][] createArray3D(int size1, int size2, int size3) { return new FloatType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public FloatType createVariable(){ return new FloatType( 0 ); }
	
	@Override
	public FloatType clone(){ return new FloatType( get() ); }
}
