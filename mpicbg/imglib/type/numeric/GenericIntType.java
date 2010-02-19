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
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public abstract class GenericIntType<T extends GenericIntType<T>> extends TypeImpl<T> implements NumericType<T>
{
	final IntContainer<T> intStorage;
	int[] v;
	
	// this is the constructor if you want it to read from an array
	public GenericIntType( IntContainer<T> intStorage )
	{
		this.intStorage = intStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public GenericIntType( final int value )
	{
		intStorage = null;
		v = new int[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public GenericIntType() { this( 0 ); }

	@Override
	public IntContainer<T> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createIntInstance( dim, 1 );	
	}

	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = intStorage.getCurrentStorageArray( c ); 
	}

	@Override
	public void mul( final float c ) { v[ i ] = Math.round( v[ i ] * c ); }

	@Override
	public void mul( final double c ) { v[ i ] = (int)Math.round( v[ i ] * c ); }

	protected int getValue() { return v[ i ]; }
	protected void setValue( final int f ) { v[ i ] = f; }
	public float getReal() { return v[ i ]; }
	public void setReal( final float f ) { v[ i ] = MathLib.round( f ); }

	@Override
	public void add( final T c ) { v[ i ] += c.getValue(); }

	@Override
	public void div( final T c ) { v[ i ] /= c.getValue(); }

	@Override
	public void mul( final T c ) { v[ i ] *= c.getValue(); }

	@Override
	public void sub( final T c ) { v[ i ] -= c.getValue(); }

	@Override
	public int compareTo( final T c ) 
	{ 
		if ( v[ i ] > c.getValue() )
			return 1;
		else if ( v[ i ] < c.getValue() )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final T c ) { v[ i ] = c.getValue(); }

	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }
	
	@Override
	public String toString() { return "" + v[i]; }
}
