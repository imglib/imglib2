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
import mpicbg.imglib.container.basictypecontainer.ShortContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.UnsignedShortTypeDisplay;

public class UnsignedShortType extends GenericShortType<UnsignedShortType>
{
	// this is the constructor if you want it to read from an array
	public UnsignedShortType( final ShortContainer<UnsignedShortType> shortStorage ) { super( shortStorage );	}

	// this is the constructor if you want it to be a variable
	public UnsignedShortType( final int value ) { super( getCodedSignedShortChecked(value) ); }

	// this is the constructor if you want it to be a variable
	public UnsignedShortType() { this( 0 ); }
	
	public static short getCodedSignedShortChecked( int unsignedShort )
	{
		if ( unsignedShort < 0 )
			unsignedShort = 0;
		else if ( unsignedShort > 65535 )
			unsignedShort = 65535;
		
		return getCodedSignedShort( unsignedShort );
	}
	public static short getCodedSignedShort( final int unsignedShort ) { return (short)( unsignedShort & 0xffff );	}
	public static int getUnsignedShort( final short signedShort ) { return signedShort & 0xffff; }
		
	@Override
	public UnsignedShortTypeDisplay getDefaultDisplay( Image<UnsignedShortType> image ) { return new UnsignedShortTypeDisplay( image ); }

	@Override
	public void mul( final float c ) { v[ i ] = getCodedSignedShort( MathLib.round( getUnsignedShort( v[ i ] ) * c ) ); }

	@Override
	public void mul( final double c ) { v[ i ] = getCodedSignedShort( (int)MathLib.round( getUnsignedShort( v[ i ] ) * c ) ); }

	public int get() { return getUnsignedShort( v[ i ] ); }
	public void set( final int f ) { v[ i ] = getCodedSignedShort( f ); }
	public float getReal() { return getUnsignedShort( v[ i ] ); }
	public void setReal( final float f ) { v[ i ] = getCodedSignedShort( MathLib.round( f ) ); }

	@Override
	public void div( final UnsignedShortType c ) { v[ i ] = getCodedSignedShort( get() / c.get() ); }

	@Override
	public int compareTo( final UnsignedShortType c ) 
	{
		final int value1 = get();
		final int value2 = c.get();
		
		if ( value1 > value2 )
			return 1;
		else if ( value1 < value2 )
			return -1;
		else 
			return 0;
	}

	@Override
	public UnsignedShortType[] createArray1D( final int size1 ){ return new UnsignedShortType[ size1 ]; }

	@Override
	public UnsignedShortType[][] createArray2D( final int size1, final int size2 ){ return new UnsignedShortType[ size1 ][ size2 ]; }

	@Override
	public UnsignedShortType[][][] createArray3D( final int size1, final int size2, final int size3 ) { return new UnsignedShortType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public UnsignedShortType createType( final Container<UnsignedShortType> container ) { return new UnsignedShortType( (ShortContainer<UnsignedShortType>)container ); }
	
	@Override
	public UnsignedShortType createVariable(){ return new UnsignedShortType( 0 ); }

	@Override
	public UnsignedShortType clone(){ return new UnsignedShortType( v[ i ] ); }

	@Override
	public String toString() { return "" + getUnsignedShort( v[i] ); }
}
