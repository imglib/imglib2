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
package mpicbg.imglib.type.numeric.integer;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.basictypecontainer.IntAccess;

public class IntType extends GenericIntType<IntType>
{
	// this is the constructor if you want it to read from an array
	public IntType( Container<IntType, IntAccess> intStorage ) { super( intStorage ); }
	
	// this is the constructor if you want it to be a variable
	public IntType( final int value ) { super( value ); }

	// this is the constructor if you want it to be a variable
	public IntType() { super( 0 ); }
		
	public int get() { return getValue(); }
	public void set( final int b ) { setValue( b ); }

	@Override
	public int getInteger(){ return get(); }
	@Override
	public long getIntegerLong() { return get(); }
	@Override
	public void setInteger( final int f ){ set( f ); }
	@Override
	public void setInteger( final long f ){ set( (int)f ); }

	@Override
	public IntType[] createArray1D(int size1){ return new IntType[ size1 ]; }

	@Override
	public IntType[][] createArray2D(int size1, int size2){ return new IntType[ size1 ][ size2 ]; }

	@Override
	public IntType[][][] createArray3D(int size1, int size2, int size3) { return new IntType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public IntType createType( Container<IntType,?> container )
	{
		return new IntType( (Container<IntType, IntAccess>)container );
	}

	@Override
	public IntType createVariable(){ return new IntType( 0 ); }

	@Override
	public IntType clone(){ return new IntType( getValue() ); }
}
