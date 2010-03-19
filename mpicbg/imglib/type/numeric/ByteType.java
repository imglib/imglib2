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

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.ByteTypeDisplay;

public class ByteType extends GenericByteType<ByteType>
{
	// this is the constructor if you want it to read from an array
	public ByteType( final Container<ByteType, ByteAccess> byteStorage ) { super( byteStorage ); }
	
	// this is the constructor if you want it to be a variable
	public ByteType( final byte value ) { super( value ); }

	// this is the constructor if you want it to be a variable
	public ByteType() { super( (byte)0 ); }
		
	public byte get() { return getValue(); }
	public void set( final byte b ) { setValue( b ); }
	
	@Override
	public ByteTypeDisplay getDefaultDisplay( Image<ByteType> image ) { return new ByteTypeDisplay( image ); }
	
	@Override
	public ByteType[] createArray1D( final int size1 ){ return new ByteType[ size1 ]; }

	@Override
	public ByteType[][] createArray2D( final int size1, final int size2 ){ return new ByteType[ size1 ][ size2 ]; }

	@Override
	public ByteType[][][] createArray3D( final int size1, final int size2, final int size3 ) { return new ByteType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public ByteType createType( final Container<ByteType, ?> container ) 
	{ 
		return new ByteType( (Container<ByteType, ByteAccess>)container ); 
	}
	
	@Override
	public ByteType createVariable(){ return new ByteType( (byte)0 ); }

	@Override
	public ByteType clone(){ return new ByteType( getValue() ); }

	@Override
	public String toString() { return "" + get(); }
}
