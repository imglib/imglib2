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
package mpicbg.imglib.type.numeric.complex;

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.NativeContainerFactory;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.numeric.ComplexType;

public class ComplexDoubleType extends ComplexTypeImpl<ComplexDoubleType> implements ComplexType<ComplexDoubleType>, NativeType<ComplexDoubleType>
{
	private int i = 0;
	
	// the indices for real and complex number
	private int realI = 0, complexI = 1;

	// the NativeContainer
	final NativeContainer<ComplexDoubleType, ? extends DoubleAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	DoubleAccess b;
	
	// this is the constructor if you want it to read from an array
	public ComplexDoubleType( NativeContainer<ComplexDoubleType, ? extends DoubleAccess> complexfloatStorage )
	{
		storage = complexfloatStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ComplexDoubleType( final float real, final float complex )
	{
		storage = null;
		b = new DoubleArray( 2 );
		set( real, complex );
	}

	// this is the constructor if you want it to be a variable
	public ComplexDoubleType() { this( 0, 0 ); }

	@Override
	public NativeContainer<ComplexDoubleType, ? extends DoubleAccess> createSuitableNativeContainer( final NativeContainerFactory<ComplexDoubleType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeContainer<ComplexDoubleType, ? extends DoubleAccess> container = storageFactory.createDoubleInstance( dim, 2 );
		
		// create a Type that is linked to the container
		final ComplexDoubleType linkedType = new ComplexDoubleType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public void updateContainer( final Object c ) { b = storage.update( c ); }
	
	@Override
	public ComplexDoubleType duplicateTypeOnSameNativeContainer() { return new ComplexDoubleType( storage ); }

	@Override
	public float getRealFloat() { return (float)b.getValue( realI ); }
	@Override
	public double getRealDouble() { return b.getValue( realI ); }
	@Override
	public float getImaginaryFloat() { return (float)b.getValue( complexI ); }
	@Override
	public double getImaginaryDouble() { return b.getValue( complexI ); }
	
	@Override
	public void setReal( final float real ){ b.setValue( realI, real ); }
	@Override
	public void setReal( final double real ){ b.setValue( realI, real ); }
	@Override
	public void setImaginary( final float complex ){ b.setValue( complexI, complex ); }
	@Override
	public void setImaginary( final double complex ){ b.setValue( complexI, complex ); }
	
	public void set( final float real, final float complex ) 
	{ 
		b.setValue( realI, real );
		b.setValue( complexI, complex );
	}

	@Override
	public void set( final ComplexDoubleType c ) 
	{ 
		setReal( c.getRealDouble() );
		setImaginary( c.getImaginaryDouble() );
	}

	@Override
	public ComplexDoubleType createVariable(){ return new ComplexDoubleType( 0, 0 ); }
	
	@Override
	public ComplexDoubleType copy(){ return new ComplexDoubleType( getRealFloat(), getImaginaryFloat() ); }	

	@Override
	public int getEntitiesPerPixel() { return 2; }	
	
	@Override
	public void updateIndex( final int i )
	{
		this.i = i;
		realI = i * 2;
		complexI = i * 2 + 1;
	}

	@Override
	public void incIndex()
	{
		++i;
		realI += 2;
		complexI += 2;
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc2 = 2 * increment;
		realI += inc2;
		complexI += inc2;
	}
	@Override
	public void decIndex()
	{
		--i;
		realI -= 2;
		complexI -= 2;
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
		final int dec2 = 2 * decrement;
		realI -= dec2;
		complexI -= dec2;
	}	
	
	@Override
	public int getIndex() { return i; }		
}
