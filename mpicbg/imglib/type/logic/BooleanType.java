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

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.BooleanTypeDisplay;
import mpicbg.imglib.type.LogicType;
import mpicbg.imglib.type.TypeImpl;

public class BooleanType extends TypeImpl<BooleanType> implements LogicType<BooleanType>
{
	final BitContainer<BooleanType> bitStorage;
	BitContainer<BooleanType> b;
	
	int outputType = 0;
	
	// this is the constructor if you want it to read from an array
	public BooleanType( final BitContainer<BooleanType> bitStorage )
	{
		this.bitStorage = bitStorage;
		this.b = bitStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public BooleanType( final boolean value )
	{
		this( new BitArray<BooleanType>( null, new int[]{1}, 1 ) );
		b.setValue( i, value );
	}

	// this is the constructor if you want it to be a variable
	public BooleanType() { this( false ); }
	
	@Override
	public BitContainer<BooleanType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createBitInstance( dim, 1 );	
	}
	
	@Override
	public BooleanTypeDisplay getDefaultDisplay( final Image<BooleanType> image )
	{
		return new BooleanTypeDisplay( image );
	}
	
	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		b = bitStorage;
		b.updateStorageArray( c );
	}

	public boolean get() { return b.getValue( i ); }
	public void set( final boolean value) { b.setValue( i, value ); }

	@Override
	public void set( final BooleanType c ) { b.setValue(i, c.get() ); }

	@Override
	public void and( final BooleanType c ) { b.setValue(i, b.getValue(i) && c.get() ); }
	
	@Override
	public void or( final BooleanType c ) { b.setValue(i, b.getValue(i) || c.get() ); }
	
	@Override
	public void xor( final BooleanType c ) { b.setValue(i, b.getValue(i) ^ c.get() ); }
	
	@Override
	public void not() { b.setValue(i, !b.getValue(i) ); }
	
	@Override
	public int compareTo( final BooleanType c ) 
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
	public BooleanType[] createArray1D(int size1){ return new BooleanType[ size1 ]; }

	@Override
	public BooleanType[][] createArray2D(int size1, int size2){ return new BooleanType[ size1 ][ size2 ]; }

	@Override
	public BooleanType[][][] createArray3D(int size1, int size2, int size3) { return new BooleanType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public BooleanType getType() { return this; }

	@Override
	public BooleanType createType( Container<BooleanType> container )
	{
		return new BooleanType( (BitContainer<BooleanType>)container );
	}
	
	@Override
	public BooleanType createVariable(){ return new BooleanType(); }

	@Override
	public BooleanType copyVariable(){ return new BooleanType( b.getValue(i) ); }

	@Override
	public String toString() 
	{
		final boolean value = b.getValue(i);
		
		if ( outputType == 0)
			return "" + value;
		else
			if ( value ) 
				return "1"; 
			else 
				return "0"; 			
	}
	
	public void setOutputType( final int outputType ) { this.outputType = outputType; }
}
