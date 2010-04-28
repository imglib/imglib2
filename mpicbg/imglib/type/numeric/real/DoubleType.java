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
package mpicbg.imglib.type.numeric.real;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.numeric.RealType;

public class DoubleType extends AbstractRealType<DoubleType> implements RealType<DoubleType>
{
	// the DirectAccessContainer
	final DirectAccessContainer<DoubleType, ? extends DoubleAccess> storage;
	
	// the (sub)DirectAccessContainer that holds the information 
	DoubleAccess b;
	
	// this is the constructor if you want it to read from an array
	public DoubleType( DirectAccessContainer<DoubleType, ? extends DoubleAccess> doubleStorage )
	{
		storage = doubleStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public DoubleType( final double value )
	{
		storage = null;
		b = new DoubleArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public DoubleType() { this( 0 ); }

	@Override
	public DirectAccessContainer<DoubleType, ? extends DoubleAccess> createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final long dim[] )
	{
		// create the container
		final DirectAccessContainer<DoubleType, ? extends DoubleAccess> container = storageFactory.createDoubleInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final DoubleType linkedType = new DoubleType( container );
		
		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Cursor<?> c ) 
	{ 
		b = storage.update( c ); 
	}

	@Override
	public DoubleType duplicateTypeOnSameDirectAccessContainer() { return new DoubleType( storage ); }
	
	public double get(){ return b.getValue( i ); }
	public void set( final double f ){ b.setValue( i, f ); }
	
	@Override
	public float getRealFloat() { return (float)get(); }
	@Override
	public double getRealDouble() { return get(); }
	
	@Override
	public void setReal( final float real ){ set( real ); }
	@Override
	public void setReal( final double real ){ set( real ); }
	
	@Override
	public double getMaxValue() { return Double.MAX_VALUE; }
	@Override
	public double getMinValue()  { return -Double.MAX_VALUE; }
	@Override
	public double getMinIncrement()  { return Double.MIN_VALUE; }
	
	@Override
	public DoubleType[] createArray1D(int size1){ return new DoubleType[ size1 ]; }

	@Override
	public DoubleType[][] createArray2D(int size1, int size2){ return new DoubleType[ size1 ][ size2 ]; }

	@Override
	public DoubleType[][][] createArray3D(int size1, int size2, int size3) { return new DoubleType[ size1 ][ size2 ][ size3 ]; }
	
	@Override
	public DoubleType createVariable(){ return new DoubleType( 0 ); }
	
	@Override
	public DoubleType clone(){ return new DoubleType( get() ); }	
}
