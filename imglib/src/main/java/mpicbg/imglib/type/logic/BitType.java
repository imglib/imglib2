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

package mpicbg.imglib.type.logic;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitAccess;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.BitTypeDisplay;
import mpicbg.imglib.type.LogicType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.IntegerTypeImpl;

public class BitType extends IntegerTypeImpl<BitType> implements LogicType<BitType>, RealType<BitType>
{
	// the DirectAccessContainer
	final DirectAccessContainer<BitType, ? extends BitAccess> storage;
	
	// the (sub)DirectAccessContainer that holds the information 
	BitAccess b;
	
	// this is the constructor if you want it to read from an array
	public BitType( DirectAccessContainer<BitType, ? extends BitAccess> bitStorage )
	{
		storage = bitStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public BitType( final boolean value )
	{
		storage = null;
		b = new BitArray( 1 );
		b.setValue( i, value );
	}

	// this is the constructor if you want it to be a variable
	public BitType() { this( false ); }
	
	@Override
	public DirectAccessContainer<BitType, ? extends BitAccess> createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final int dim[] )
	{
		// create the container
		final DirectAccessContainer<BitType, ? extends BitAccess> container = storageFactory.createBitInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final BitType linkedType = new BitType( container );
		
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
	public BitType duplicateTypeOnSameDirectAccessContainer() { return new BitType( storage ); }

	@Override
	public BitTypeDisplay getDefaultDisplay( final Image<BitType> image )
	{
		return new BitTypeDisplay( image );
	}

	public boolean get() { return b.getValue( i ); }
	public void set( final boolean value ) { b.setValue( i, value ); }

	@Override
	public int getInteger(){ return get() ? 1 : 0; }
	@Override
	public long getIntegerLong() { return get() ? 1 : 0; }
	@Override
	public void setInteger( final int f )
	{
		if ( f >= 1 ) 
			set( true );
		else
			set( false );
	}
	@Override
	public void setInteger( final long f )
	{
		if ( f >= 1 ) 
			set( true );
		else
			set( false );
	}

	@Override
	public double getMaxValue() { return 1; }
	@Override
	public double getMinValue()  { return 0; }
	
	@Override
	public void set( final BitType c ) { b.setValue(i, c.get() ); }

	@Override
	public void and( final BitType c ) { b.setValue(i, b.getValue(i) && c.get() ); }
	
	@Override
	public void or( final BitType c ) { b.setValue(i, b.getValue(i) || c.get() ); }
	
	@Override
	public void xor( final BitType c ) { b.setValue(i, b.getValue(i) ^ c.get() ); }
	
	@Override
	public void not() { b.setValue(i, !b.getValue(i) ); }
	
	@Override
	public void add( final BitType c ) { xor( c ); }

	@Override
	public void div( final BitType c ) { and( c ); }

	@Override
	public void mul( final BitType c ) { and( c ); }

	@Override
	public void sub( final BitType c ) { xor( c ); }
	
	@Override
	public void mul( final float c ) 
	{ 
		if ( c >= 0.5f )
			b.setValue(i, b.getValue(i) && true );
		else
			b.setValue(i, b.getValue(i) && false );
	}

	@Override
	public void mul( final double c ) 
	{ 
		if ( c >= 0.5f )
			b.setValue(i, b.getValue(i) && true );
		else
			b.setValue(i, b.getValue(i) && false );
	}
		
	@Override
	public void setOne() { b.setValue( i, true ); }

	@Override
	public void setZero() { b.setValue( i, false ); }
	
	@Override
	public void inc() { b.setValue( i, !b.getValue( i) ); }

	@Override
	public void dec() { inc(); }

	@Override
	public int compareTo( final BitType c ) 
	{
		final boolean b1 = b.getValue(i);
		final boolean b2 = c.get();
		
		if ( b1 && !b2 )
			return 1;
		else if ( !b1 && b2 )
			return -1;
		else 
			return 0;
	}

	@Override
	public BitType[] createArray1D(int size1){ return new BitType[ size1 ]; }

	@Override
	public BitType[][] createArray2D(int size1, int size2){ return new BitType[ size1 ][ size2 ]; }

	@Override
	public BitType[][][] createArray3D(int size1, int size2, int size3) { return new BitType[ size1 ][ size2 ][ size3 ]; }
	
	@Override
	public BitType createVariable(){ return new BitType(); }

	@Override
	public BitType copy(){ return new BitType( b.getValue(i) ); }

	@Override
	public String toString() 
	{
		final boolean value = b.getValue(i);
		
		if ( value ) 
			return "1"; 
		else 
			return "0"; 			
	}
}
