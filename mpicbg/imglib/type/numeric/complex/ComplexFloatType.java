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
package mpicbg.imglib.type.numeric.complex;

import mpicbg.imglib.algorithm.Precision.PrecisionReal;
import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.type.numeric.ComplexType;

/**
 * 
 * 
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ComplexFloatType extends AbstractComplexType< ComplexFloatType > implements ComplexType< ComplexFloatType >
{
	// the DirectAccessContainer
	final DirectAccessContainer< ComplexFloatType, ? extends FloatAccess > storage;

	// the (sub)DirectAccessContainer that holds the information
	FloatAccess b;

	// this is the constructor if you want it to read from an array
	public ComplexFloatType( DirectAccessContainer< ComplexFloatType, ? extends FloatAccess > complexfloatStorage )
	{
		storage = complexfloatStorage;
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType( final float real, final float complex )
	{
		storage = null;
		b = new FloatArray( 2 );
		set( real, complex );
	}

	// this is the constructor if you want it to be a variable
	public ComplexFloatType()
	{
		this( 0, 0 );
	}

	@Override
	public DirectAccessContainer< ComplexFloatType, ? extends FloatAccess > createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final int dim[] )
	{
		// create the container
		final DirectAccessContainer< ComplexFloatType, ? extends FloatAccess > container = storageFactory.createFloatInstance( dim, 2 );

		// create a Type that is linked to the container
		final ComplexFloatType linkedType = new ComplexFloatType( container );

		// pass it to the DirectAccessContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c )
	{
		b = storage.update( c );
	}

	@Override
	public ComplexFloatType duplicateTypeOnSameDirectAccessContainer()
	{
		return new ComplexFloatType( storage );
	}

	@Override
	public float getRealFloat()
	{
		return b.getValue( realI );
	}

	@Override
	public double getRealDouble()
	{
		return b.getValue( realI );
	}

	@Override
	public float getComplexFloat()
	{
		return b.getValue( complexI );
	}

	@Override
	public double getComplexDouble()
	{
		return b.getValue( complexI );
	}

	@Override
	public void setReal( final float real )
	{
		b.setValue( realI, real );
	}

	@Override
	public void setReal( final double real )
	{
		b.setValue( realI, ( float ) real );
	}

	@Override
	public void setComplex( final float complex )
	{
		b.setValue( complexI, complex );
	}

	@Override
	public void setComplex( final double complex )
	{
		b.setValue( complexI, ( float ) complex );
	}

	@Override
	public PrecisionReal getPreferredRealPrecision()
	{
		return PrecisionReal.Float;
	}

	public void set( final float real, final float complex )
	{
		b.setValue( realI, real );
		b.setValue( complexI, complex );
	}

	@Override
	public void add( final ComplexFloatType c )
	{
		setReal( getRealFloat() + c.getRealFloat() );
		setComplex( getComplexFloat() + c.getComplexFloat() );
	}

	@Override
	public void div( final ComplexFloatType c )
	{
		final float a1 = getRealFloat();
		final float b1 = getComplexFloat();
		final float c1 = c.getRealFloat();
		final float d1 = c.getComplexFloat();

		setReal( ( a1 * c1 + b1 * d1 ) / ( c1 * c1 + d1 * d1 ) );
		setComplex( ( b1 * c1 - a1 * d1 ) / ( c1 * c1 + d1 * d1 ) );
	}

	@Override
	public void mul( final ComplexFloatType t )
	{
		// a + bi
		final float a = getRealFloat();
		final float b = getComplexFloat();

		// c + di
		final float c = t.getRealFloat();
		final float d = t.getComplexFloat();

		setReal( a * c - b * d );
		setComplex( a * d + b * c );
	}

	@Override
	public void sub( final ComplexFloatType c )
	{
		setReal( getRealFloat() - c.getRealFloat() );
		setComplex( getComplexFloat() - c.getComplexFloat() );
	}

	public void complexConjugate()
	{
		setComplex( -getComplexFloat() );
	}

	public void switchRealComplex()
	{
		final float a = getRealFloat();
		setReal( getComplexFloat() );
		setComplex( a );
	}

	@Override
	public int compareTo( final ComplexFloatType c )
	{
		final float real1 = getRealFloat();
		final float complex1 = getComplexFloat();
		final float real2 = c.getRealFloat();
		final float complex2 = c.getComplexFloat();

		if ( real1 > real2 || ( real1 == real2 && complex1 > complex2 ) )
			return 1;
		else if ( real1 < real2 || ( real1 == real2 && complex1 < complex2 ) )
			return -1;
		else
			return 0;
	}

	@Override
	public void set( final ComplexFloatType c )
	{
		setReal( c.getRealFloat() );
		setComplex( c.getComplexFloat() );
	}

	@Override
	public ComplexFloatType[] createArray1D( int size1 )
	{
		return new ComplexFloatType[ size1 ];
	}

	@Override
	public ComplexFloatType[][] createArray2D( int size1, int size2 )
	{
		return new ComplexFloatType[ size1 ][ size2 ];
	}

	@Override
	public ComplexFloatType[][][] createArray3D( int size1, int size2, int size3 )
	{
		return new ComplexFloatType[ size1 ][ size2 ][ size3 ];
	}

	@Override
	public ComplexFloatType createVariable()
	{
		return new ComplexFloatType( 0, 0 );
	}

	@Override
	public ComplexFloatType clone()
	{
		return new ComplexFloatType( getRealFloat(), getComplexFloat() );
	}
}
