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

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.ExponentialMathType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.util.Util;

public class DoubleType extends AbstractRealType<DoubleType> implements RealType<DoubleType>, ExponentialMathType<DoubleType>, NativeType<DoubleType>
{
	private int i = 0;

	// the NativeContainer
	final NativeContainer<DoubleType, ? extends DoubleAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	DoubleAccess b;
	
	// this is the constructor if you want it to read from an array
	public DoubleType( NativeContainer<DoubleType, ? extends DoubleAccess> doubleStorage )
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
	public NativeContainer<DoubleType, ? extends DoubleAccess> createSuitableNativeContainer( final NativeContainerFactory<DoubleType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeContainer<DoubleType, ? extends DoubleAccess> container = storageFactory.createDoubleInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final DoubleType linkedType = new DoubleType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Object c )  { b = storage.update( c ); }

	@Override
	public DoubleType duplicateTypeOnSameNativeContainer() { return new DoubleType( storage ); }
	
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
	public DoubleType createVariable(){ return new DoubleType( 0 ); }
	
	@Override
	public DoubleType copy(){ return new DoubleType( get() ); }

	@Override
	public void exp() { set( Math.exp( get() ) ); }

	@Override
	public void round() { set( Util.round( get() ) ); }
	
	@Override
	public int getEntitiesPerPixel() { return 1; }
	
	@Override
	public void updateIndex( final int i ) { this.i = i; }
	@Override
	public int getIndex() { return i; }
	
	@Override
	public void incIndex() { ++i; }
	@Override
	public void incIndex( final int increment ) { i += increment; }
	@Override
	public void decIndex() { --i; }
	@Override
	public void decIndex( final int decrement ) { i -= decrement; }		
}
