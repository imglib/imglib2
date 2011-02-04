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
 */
package mpicbg.imglib.type.numeric.integer;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;

/**
 * 
 * 
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class UnsignedShortType extends GenericShortType< UnsignedShortType >
{
	// this is the constructor if you want it to read from an array
	public UnsignedShortType( NativeContainer< UnsignedShortType, ? extends ShortAccess > shortStorage )
	{
		super( shortStorage );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedShortType( final int value )
	{
		super( getCodedSignedShortChecked( value ) );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedShortType()
	{
		this( 0 );
	}

	public static short getCodedSignedShortChecked( int unsignedShort )
	{
		if ( unsignedShort < 0 )
			unsignedShort = 0;
		else if ( unsignedShort > 65535 )
			unsignedShort = 65535;

		return getCodedSignedShort( unsignedShort );
	}

	public static short getCodedSignedShort( final int unsignedShort )
	{
		return ( short ) ( unsignedShort & 0xffff );
	}

	public static int getUnsignedShort( final short signedShort )
	{
		return signedShort & 0xffff;
	}

	@Override
	public NativeContainer< UnsignedShortType, ? extends ShortAccess > createSuitableDirectAccessContainer( final NativeContainerFactory storageFactory, final int dim[] )
	{
		// create the container
		final NativeContainer< UnsignedShortType, ? extends ShortAccess > container = storageFactory.createShortInstance( dim, 1 );

		// create a Type that is linked to the container
		final UnsignedShortType linkedType = new UnsignedShortType( container );

		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public UnsignedShortType duplicateTypeOnSameDirectAccessContainer()
	{
		return new UnsignedShortType( storage );
	}

	@Override
	public void mul( final float c )
	{
		final int a = getUnsignedShort( getValue() );
		setValue( getCodedSignedShort( MathLib.round( a * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		final int a = getUnsignedShort( getValue() );
		setValue( getCodedSignedShort( ( int ) MathLib.round( a * c ) ) );
	}

	public int get()
	{
		return getUnsignedShort( getValue() );
	}

	public void set( final int f )
	{
		setValue( getCodedSignedShort( f ) );
	}

	@Override
	public int getInteger()
	{
		return get();
	}

	@Override
	public long getIntegerLong()
	{
		return get();
	}

	@Override
	public void setInteger( final int f )
	{
		set( f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( ( int ) f );
	}

	@Override
	public double getMaxValue()
	{
		return -Short.MIN_VALUE + Short.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public void div( final UnsignedShortType c )
	{
		set( get() / c.get() );
	}

	@Override
	public int compareTo( final UnsignedShortType c )
	{
		final int a = get();
		final int b = c.get();

		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else
			return 0;
	}

	@Override
	public UnsignedShortType[] createArray1D( final int size1 )
	{
		return new UnsignedShortType[ size1 ];
	}

	@Override
	public UnsignedShortType[][] createArray2D( final int size1, final int size2 )
	{
		return new UnsignedShortType[ size1 ][ size2 ];
	}

	@Override
	public UnsignedShortType[][][] createArray3D( final int size1, final int size2, final int size3 )
	{
		return new UnsignedShortType[ size1 ][ size2 ][ size3 ];
	}

	@Override
	public UnsignedShortType createVariable()
	{
		return new UnsignedShortType( 0 );
	}

	@Override
	public UnsignedShortType copy()
	{
		return new UnsignedShortType( get() );
	}
}
