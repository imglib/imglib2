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

import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.basictypeaccess.DoubleAccess;
import mpicbg.imglib.img.basictypeaccess.array.DoubleArray;
import mpicbg.imglib.type.NativeType;

public class ComplexDoubleType extends AbstractComplexType<ComplexDoubleType> implements NativeType<ComplexDoubleType>
{
	private int i = 0;
	
	// the indices for real and imaginary value
	private int realI = 0, imaginaryI = 1;

	final protected NativeImg<ComplexDoubleType, ? extends DoubleAccess> img;
	
	// the DataAccess that holds the information 
	protected DoubleAccess dataAccess;
	
	// this is the constructor if you want it to read from an array
	public ComplexDoubleType( NativeImg<ComplexDoubleType, ? extends DoubleAccess> complexfloatStorage )
	{
		img = complexfloatStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public ComplexDoubleType( final float r, final float i )
	{
		img = null;
		dataAccess = new DoubleArray( 2 );
		set( r, i );
	}

	// this is the constructor if you want it to be a variable
	public ComplexDoubleType() { this( 0, 0 ); }

	@Override
	public NativeImg<ComplexDoubleType, ? extends DoubleAccess> createSuitableNativeImg( final NativeImgFactory<ComplexDoubleType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<ComplexDoubleType, ? extends DoubleAccess> container = storageFactory.createDoubleInstance( dim, 2 );
		
		// create a Type that is linked to the container
		final ComplexDoubleType linkedType = new ComplexDoubleType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }
	
	@Override
	public ComplexDoubleType duplicateTypeOnSameNativeImg() { return new ComplexDoubleType( img ); }

	@Override
	public float getRealFloat() { return (float)dataAccess.getValue( realI ); }
	@Override
	public double getRealDouble() { return dataAccess.getValue( realI ); }
	@Override
	public float getImaginaryFloat() { return (float)dataAccess.getValue( imaginaryI ); }
	@Override
	public double getImaginaryDouble() { return dataAccess.getValue( imaginaryI ); }
	
	@Override
	public void setReal( final float r ){ dataAccess.setValue( realI, r ); }
	@Override
	public void setReal( final double r ){ dataAccess.setValue( realI, r ); }
	@Override
	public void setImaginary( final float i ){ dataAccess.setValue( imaginaryI, i ); }
	@Override
	public void setImaginary( final double i ){ dataAccess.setValue( imaginaryI, i ); }
	
	public void set( final float r, final float i ) 
	{ 
		dataAccess.setValue( realI, r );
		dataAccess.setValue( imaginaryI, i );
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
	public void updateIndex( final int index )
	{
		this.i = index;
		realI = index * 2;
		imaginaryI = index * 2 + 1;
	}

	@Override
	public void incIndex()
	{
		++i;
		realI += 2;
		imaginaryI += 2;
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;

		final int inc2 = 2 * increment;
		realI += inc2;
		imaginaryI += inc2;
	}
	@Override
	public void decIndex()
	{
		--i;
		realI -= 2;
		imaginaryI -= 2;
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
		final int dec2 = 2 * decrement;
		realI -= dec2;
		imaginaryI -= dec2;
	}	
	
	@Override
	public int getIndex() { return i; }		
}
