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

package net.imglib2.type.logic;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.BitAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.type.BooleanType;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.AbstractIntegerType;

public class BitType extends AbstractIntegerType<BitType> implements BooleanType<BitType>, NativeType<BitType>
{
	private int i = 0;
	
	final protected NativeImg<BitType, ? extends BitAccess> img;
	
	// the DataAccess that holds the information 
	protected BitAccess dataAccess;
	
	// this is the constructor if you want it to read from an array
	public BitType( NativeImg<BitType, ? extends BitAccess> bitStorage )
	{
		img = bitStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public BitType( final boolean value )
	{
		img = null;
		dataAccess = new BitArray( 1 );
		dataAccess.setValue( i, value );
	}

	// this is the constructor if you want it to be a variable
	public BitType() { this( false ); }
	
	@Override
	public NativeImg<BitType, ? extends BitAccess> createSuitableNativeImg( final NativeImgFactory<BitType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<BitType, ? extends BitAccess> container = storageFactory.createBitInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final BitType linkedType = new BitType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
		
	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }
	
	@Override
	public BitType duplicateTypeOnSameNativeImg() { return new BitType( img ); }

	@Override
	public boolean get() { return dataAccess.getValue( i ); }
	@Override
	public void set( final boolean value ) { dataAccess.setValue( i, value ); }

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
	public void set( final BitType c ) { dataAccess.setValue(i, c.get() ); }

	@Override
	public void and( final BitType c ) { dataAccess.setValue(i, dataAccess.getValue(i) && c.get() ); }
	
	@Override
	public void or( final BitType c ) { dataAccess.setValue(i, dataAccess.getValue(i) || c.get() ); }
	
	@Override
	public void xor( final BitType c ) { dataAccess.setValue(i, dataAccess.getValue(i) ^ c.get() ); }
	
	@Override
	public void not() { dataAccess.setValue(i, !dataAccess.getValue(i) ); }
	
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
			dataAccess.setValue(i, dataAccess.getValue(i) && true );
		else
			dataAccess.setValue(i, dataAccess.getValue(i) && false );
	}

	@Override
	public void mul( final double c ) 
	{ 
		if ( c >= 0.5f )
			dataAccess.setValue(i, dataAccess.getValue(i) && true );
		else
			dataAccess.setValue(i, dataAccess.getValue(i) && false );
	}
		
	@Override
	public void setOne() { dataAccess.setValue( i, true ); }

	@Override
	public void setZero() { dataAccess.setValue( i, false ); }
	
	@Override
	public void inc() { dataAccess.setValue( i, !dataAccess.getValue( i) ); }

	@Override
	public void dec() { inc(); }

	@Override
	public int compareTo( final BitType c ) 
	{
		final boolean b1 = dataAccess.getValue(i);
		final boolean b2 = c.get();
		
		if ( b1 && !b2 )
			return 1;
		else if ( !b1 && b2 )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public BitType createVariable(){ return new BitType(); }

	@Override
	public BitType copy(){ return new BitType( dataAccess.getValue(i) ); }

	@Override
	public String toString() 
	{
		final boolean value = dataAccess.getValue(i);
		
		if ( value ) 
			return "1"; 
		else 
			return "0"; 			
	}
	
	@Override
	public int getEntitiesPerPixel() { return 1; }
	
	@Override
	public void updateIndex( final int index ) { this.i = index; }
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

	@Override
	public int getBitsPerPixel() { return 1; }
}
