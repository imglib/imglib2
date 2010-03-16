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
import mpicbg.imglib.container.array.ShortArray;
import mpicbg.imglib.container.basictypecontainer.ShortContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public abstract class GenericShortType<T extends GenericShortType<T>> extends TypeImpl<T> implements NumericType<T>
{
	final ShortContainer< T > b;
	
	// this is the constructor if you want it to read from an array
	public GenericShortType( final ShortContainer<T> shortStorage )
	{
		this.b = shortStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public GenericShortType( final short value )
	{
		this( new ShortArray< T >( null, new int[]{ 1 }, 1 ) );
		setValue( value );
	}

	// this is the constructor if you want it to be a variable
	public GenericShortType(){ this( ( short )0 ); }
	
	@Override
	public ShortContainer<T> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createShortInstance( dim, 1 );	
	}

	@Override
	public void updateContainer( Cursor< ? > c ) 
	{ 
		b.update( c ); 
	}
	
	protected short getValue(){ return b.getValue( i ); }
	protected void setValue( final short f ){ b.setValue( i, f ); }
	
	@Override
	public float getReal() { return getValue(); }
	
	@Override
	public void setReal( final float f ){ setValue( ( short )MathLib.round( f ) ); }

	@Override
	public void mul( final float c )
	{
		final short a = getValue();
		setValue( ( short )MathLib.round( a * c ) );
	}

	@Override
	public void mul( final double c )
	{
		final short a = getValue();
		setValue( ( short )MathLib.round( a * c ) );
	}

	@Override
	public void add( final T c )
	{
		final short a = getValue( );
		setValue( ( short )( a + c.getValue() ) );
	}

	@Override
	public void div( final T c )
	{
		final short a = getValue();
		setValue( ( short )( a / c.getValue() ) );
	}

	@Override
	public void mul( final T c )
	{
		final short a = getValue( );
		setValue( ( short )( a * c.getValue() ) );
	}

	@Override
	public void sub( final T c )
	{
		final short a = getValue( );
		setValue( ( byte )( a - c.getValue() ) );
	}

	@Override
	public int compareTo( final T c ) 
	{ 
		final short a = getValue();
		final short b = c.getValue();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final T c )
	{
		setValue( c.getValue() );
	}

	@Override
	public void setOne() { setValue( ( short )1 ); }

	@Override
	public void setZero() { setValue( ( short )0 ); }

	@Override
	public void inc()
	{
		short a = getValue();
		setValue( ++a );
	}

	@Override
	public void dec()
	{
		short a = getValue();
		setValue( --a );
	}
	
	@Override
	public String toString() { return "" + getValue(); }
}
