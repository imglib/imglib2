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

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntAccess;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.RGBALegacyTypeDisplay;
import mpicbg.imglib.type.TypeImpl;

final public class RGBALegacyType extends TypeImpl<RGBALegacyType> implements NumericType<RGBALegacyType>
{
	// the DirectAccessContainer
	final DirectAccessContainer<RGBALegacyType, ? extends IntAccess> storage;
	
	// the (sub)DirectAccessContainer that holds the information 
	IntAccess b;
	
	// this is the constructor if you want it to read from an array
	public RGBALegacyType( DirectAccessContainer<RGBALegacyType, ? extends IntAccess> byteStorage )
	{
		storage = byteStorage;
	}

	// this is the constructor if you want it to be a variable
	public RGBALegacyType( final int value )
	{
		storage = null;
		b = new IntArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public RGBALegacyType() { this( 0 ); }
	
	@Override
	public DirectAccessContainer<RGBALegacyType, ? extends IntAccess> createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final int dim[] )
	{
		// create the container
		final DirectAccessContainer<RGBALegacyType, ? extends IntAccess> container = storageFactory.createIntInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final RGBALegacyType linkedType = new RGBALegacyType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public void updateContainer( final Cursor<?> c ) 
	{ 
		b = storage.update( c );
	}

	@Override
	public RGBALegacyType duplicateTypeOnSameDirectAccessContainer() { return new RGBALegacyType( storage ); }
	
	@Override
	public RGBALegacyTypeDisplay getDefaultDisplay( Image<RGBALegacyType> image )
	{
		return new RGBALegacyTypeDisplay( image );
	}

	final public static int rgba( final int r, final int g, final int b, final int a)
	{
		return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff) | ((a & 0xff) << 24);
	}
	
	final public static int rgba( final float r, final float g, final float b, final float a)
	{
		return rgba( MathLib.round(r), MathLib.round(g), MathLib.round(b), MathLib.round(a) );
	}

	final public static int rgba( final double r, final double g, final double b, final double a)
	{
		return rgba( (int)MathLib.round(r), (int)MathLib.round(g), (int)MathLib.round(b), (int)MathLib.round(a) );
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
	public void add( final RGBALegacyType c ) 
	{ 
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) + red(value2), green(value1) + green(value2), blue(value1) + blue(value2), alpha(value1) + alpha(value2) ) );		 
	}

	@Override
	public void div( final RGBALegacyType c ) 
	{ 
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) / red(value2), green(value1) / green(value2), blue(value1) / blue(value2), alpha(value1) / alpha(value2) ) );		 
	}

	@Override
	public void mul( final RGBALegacyType c ) 
	{
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) * red(value2), green(value1) * green(value2), blue(value1) * blue(value2), alpha(value1) * alpha(value2) ) );		 
	}

	@Override
	public void sub( final RGBALegacyType c ) 
	{
		final int value1 = get();		
		final int value2 = c.get();		
		
		set( rgba( red(value1) - red(value2), green(value1) - green(value2), blue(value1) - blue(value2), alpha(value1) - alpha(value2) ) );		 
	}
	
	@Override
	public int compareTo( final RGBALegacyType c ) 
	{ 
		final int value1 = get();		
		final int value2 = c.get();

		if ( red(value1) + green(value1) + blue(value1) + alpha(value1) > red(value2) + green(value2) + blue(value2) + alpha(value2) )
			return 1;
		else if ( red(value1) + green(value1) + blue(value1) + alpha(value1) < red(value2) + green(value2) + blue(value2) + alpha(value2) )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final RGBALegacyType c ) { set( c.get() ); }

	@Override
	public void setOne() { set( rgba( 1, 1, 1, 1 ) ); }

	@Override
	public void setZero() { set( 0 ); }
	
	@Override
	public RGBALegacyType[] createArray1D(int size1){ return new RGBALegacyType[ size1 ]; }

	@Override
	public RGBALegacyType[][] createArray2D(int size1, int size2){ return new RGBALegacyType[ size1 ][ size2 ]; }

	@Override
	public RGBALegacyType[][][] createArray3D(int size1, int size2, int size3) { return new RGBALegacyType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public RGBALegacyType createVariable() { return new RGBALegacyType( 0 ); }

	@Override
	public RGBALegacyType clone() { return new RGBALegacyType( get() ); }

	@Override
	public String toString() 
	{
		final int rgba = get();
		return "(r=" + red( rgba ) + ",g=" + green( rgba ) + ",b=" + blue( rgba ) + ",a=" + alpha( rgba ) + ")";
	}
}
