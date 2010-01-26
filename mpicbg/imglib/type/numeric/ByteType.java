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
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ByteTypeDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public class ByteType extends TypeImpl<ByteType> implements NumericType<ByteType>
{
	final ByteContainer<ByteType> byteStorage;
	byte[] v;
	
	// this is the constructor if you want it to read from an array
	public ByteType( final ByteContainer<ByteType> byteStorage )
	{
		this.byteStorage = byteStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ByteType( final byte value )
	{
		byteStorage = null;
		v = new byte[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public ByteType() { this( (byte)0 ); }
	
	@Override
	public ByteContainer<ByteType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createByteInstance( dim, 1 );	
	}
	
	@Override
	public ByteTypeDisplay getDefaultDisplay( Image<ByteType> image )
	{
		return new ByteTypeDisplay( image );
	}
	
	@Override
	public void updateDataArray( Cursor<?> c ) 
	{ 
		v = byteStorage.getCurrentStorageArray( c ); 
	}

	@Override
	final public void mul( final float c ) { v[ i ] = (byte)Math.round( v[ i ] * c ); }

	@Override
	public void mul( final double c ) { v[ i ] = (byte)Math.round( v[ i ] * c ); }

	public byte get() { return v[ i ]; }
	public void set( final byte f ) { v[ i ] = f; }
	public float getReal() { return v[ i ]; }
	public void setReal( final float f ) { v[ i ] = (byte)MathLib.round( f ); }

	@Override
	public void add( final ByteType c ) { v[ i ] += c.get(); }

	@Override
	public void div( final ByteType c ) { v[ i ] /= c.get(); }

	@Override
	public void mul( final ByteType c ) { v[ i ] *= c.get(); }

	@Override
	public void sub( final ByteType c ) { v[ i ] -= c.get(); }

	@Override
	public void set( final ByteType c ) { v[ i ] = c.get(); }

	@Override
	public int compareTo( final ByteType c ) 
	{ 
		if ( v[ i ] > c.get() )
			return 1;
		else if ( v[ i ] < c.get() )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }

	@Override
	public ByteType[] createArray1D(int size1){ return new ByteType[ size1 ]; }

	@Override
	public ByteType[][] createArray2D(int size1, int size2){ return new ByteType[ size1 ][ size2 ]; }

	@Override
	public ByteType[][][] createArray3D(int size1, int size2, int size3) { return new ByteType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public ByteType getType() { return this; }

	@Override
	public ByteType createType( Container<ByteType> container )
	{
		return new ByteType( (ByteContainer<ByteType>)container );
	}
	
	@Override
	public ByteType createVariable(){ return new ByteType( (byte)0 ); }

	@Override
	public ByteType copyVariable(){ return new ByteType( v[ i ] ); }

	@Override
	public String toString() { return "" + v[i]; }
}
