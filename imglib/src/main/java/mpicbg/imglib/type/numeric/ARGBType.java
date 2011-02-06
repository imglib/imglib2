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

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.type.AbstractNativeType;
import mpicbg.imglib.util.Util;

final public class ARGBType extends AbstractNativeType<ARGBType> implements NumericType<ARGBType>
{
	// the NativeContainer
	final NativeContainer<ARGBType, ? extends IntAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	IntAccess b;
	
	// this is the constructor if you want it to read from an array
	public ARGBType( NativeContainer<ARGBType, ? extends IntAccess> byteStorage )
	{
		storage = byteStorage;
	}

	// this is the constructor if you want it to be a variable
	public ARGBType( final int value )
	{
		storage = null;
		b = new IntArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public ARGBType() { this( 0 ); }
	
	@Override
	public NativeContainer<ARGBType, ? extends IntAccess> createSuitableNativeContainer( final NativeContainerFactory<ARGBType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeContainer<ARGBType, ? extends IntAccess> container = storageFactory.createIntInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final ARGBType linkedType = new ARGBType( container );
		
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
	public ARGBType duplicateTypeOnSameNativeContainer() { return new ARGBType( storage ); }
	
	final public static int rgba( final int r, final int g, final int b, final int a)
	{
		return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff) | ((a & 0xff) << 24);
	}
	
	final public static int rgba( final float r, final float g, final float b, final float a)
	{
		return rgba( Util.round(r), Util.round(g), Util.round(b), Util.round(a) );
	}

	final public static int rgba( final double r, final double g, final double b, final double a)
	{
		return rgba( (int)Util.round(r), (int)Util.round(g), (int)Util.round(b), (int)Util.round(a) );
	}
	
	final public static int red( final int value )
	{
		return (value >> 16) & 0xff;
	}
	
	final public static int green( final int value )
	{
		return (value >> 8) & 0xff;
	}
	
	final public static int blue( final int value )
	{
		return value & 0xff;
	}
	
	final public static int alpha( final int value )
	{
		return (value >> 24) & 0xff;
	}
	
	public int get(){ return b.getValue( i ); }
	public void set( final int f ){ b.setValue( i, f ); }
		
	@Override
	public void mul( final float c )
	{
		final int value = get();
		set( rgba( red(value) * c, green(value) * c, blue(value) * c, alpha(value) * c ) );
	}

	@Override
	public void mul( final double c ) 
	{ 
		final int value = get();		
		set( rgba( red(value) * c, green(value) * c, blue(value) * c, alpha(value) * c ) );
	}

	@Override
	public void add( final ARGBType c ) 
	{ 
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) + red(value2), green(value1) + green(value2), blue(value1) + blue(value2), alpha(value1) + alpha(value2) ) );		 
	}

	@Override
	public void div( final ARGBType c ) 
	{ 
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) / red(value2), green(value1) / green(value2), blue(value1) / blue(value2), alpha(value1) / alpha(value2) ) );		 
	}

	@Override
	public void mul( final ARGBType c ) 
	{
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) * red(value2), green(value1) * green(value2), blue(value1) * blue(value2), alpha(value1) * alpha(value2) ) );		 
	}

	@Override
	public void sub( final ARGBType c ) 
	{
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) - red(value2), green(value1) - green(value2), blue(value1) - blue(value2), alpha(value1) - alpha(value2) ) );		 
	}
	
	@Override
	public void set( final ARGBType c ) { set( c.get() ); }

	@Override
	public void setOne() { set( rgba( 1, 1, 1, 1 ) ); }

	@Override
	public void setZero() { set( 0 ); }
	
	@Override
	public ARGBType createVariable() { return new ARGBType( 0 ); }

	@Override
	public ARGBType copy() { return new ARGBType( get() ); }

	@Override
	public String toString() 
	{
		final int rgba = get();
		return "(r=" + red( rgba ) + ",g=" + green( rgba ) + ",b=" + blue( rgba ) + ",a=" + alpha( rgba ) + ")";
	}

	@Override
	public int getEntitiesPerPixel() { return 1; }
}
