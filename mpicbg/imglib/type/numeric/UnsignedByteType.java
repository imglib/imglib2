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
import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.UnsignedByteTypeDisplay;

public class UnsignedByteType extends GenericByteType<UnsignedByteType>
{
	// this is the constructor if you want it to read from an array
	public UnsignedByteType( final ByteContainer<UnsignedByteType> byteStorage ) { super( byteStorage );	}

	// this is the constructor if you want it to be a variable
	public UnsignedByteType( final int value ) { super( getCodedSignedByteChecked(value) ); }

	// this is the constructor if you want it to be a variable
	public UnsignedByteType() { this( 0 ); }
	
	public static byte getCodedSignedByteChecked( int unsignedByte )
	{
		if ( unsignedByte < 0 )
			unsignedByte = 0;
		else if ( unsignedByte > 255 )
			unsignedByte = 255;
		
		return getCodedSignedByte( unsignedByte );
	}
	public static byte getCodedSignedByte( final int unsignedByte ) { return (byte)( unsignedByte & 0xff );	}
	public static int getUnsignedByte( final byte signedByte ) { return signedByte & 0xff; }
		
	@Override
	public UnsignedByteTypeDisplay getDefaultDisplay( Image<UnsignedByteType> image ) { return new UnsignedByteTypeDisplay( image ); }

	@Override
	public void mul( final float c )
	{
		final int a = getUnsignedByte( getValue() );
		setValue( getCodedSignedByte( MathLib.round( a * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		final int a = getUnsignedByte( getValue() );
		setValue( getCodedSignedByte( ( int )MathLib.round( a * c ) ) );
	}

	public int get(){ return getUnsignedByte( getValue() ); }
	public void set( final int f ){ setValue( getCodedSignedByte( f ) ); }
	
	@Override
	public float getReal(){ return get(); }
	
	@Override
	public void setReal( final float f ){ set( MathLib.round( f ) ); }

	@Override
	public void div( final UnsignedByteType c )
	{
		set( get() / c.get() );
	}

	@Override
	public int compareTo( final UnsignedByteType c ) 
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
	public UnsignedByteType[] createArray1D( final int size1 ){ return new UnsignedByteType[ size1 ]; }

	@Override
	public UnsignedByteType[][] createArray2D( final int size1, final int size2 ){ return new UnsignedByteType[ size1 ][ size2 ]; }

	@Override
	public UnsignedByteType[][][] createArray3D( final int size1, final int size2, final int size3 ) { return new UnsignedByteType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public UnsignedByteType createType( final Container<UnsignedByteType> container ) { return new UnsignedByteType( (ByteContainer<UnsignedByteType>)container ); }
	
	@Override
	public UnsignedByteType createVariable(){ return new UnsignedByteType( 0 ); }

	@Override
	public UnsignedByteType clone(){ return new UnsignedByteType( get() ); }

	@Override
	public String toString() { return "" + get(); }
}
