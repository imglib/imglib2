/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.type.numeric;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.AbstractNativeType;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Fraction;
import net.imglib2.view.composite.Composite;

/**
 * Double vector types.
 *
 */
public class DoubleVectorType extends AbstractNativeType< DoubleVectorType > implements NumericType< DoubleVectorType >, RealPositionable, RealLocalizable, Composite< DoubleType >
{
	private final int numElements;

	private final NativeImg< ?, ? extends DoubleAccess > img;

	// the DataAccess that holds the information
	private DoubleAccess dataAccess;

	private final DoubleType type = new DoubleType();

	private final NativeTypeFactory< DoubleVectorType, DoubleAccess > typeFactory;

	private static final NativeTypeFactory< DoubleVectorType, DoubleAccess > createTypeFactory( final int numElements )
	{
		return NativeTypeFactory.DOUBLE( ( img ) -> new DoubleVectorType( img, numElements ) );
	}

	// this is the constructor if you want it to read from an array
	public DoubleVectorType( final NativeImg< ?, ? extends DoubleAccess > doubleStorage, final int numElements )
	{
		super();
		img = doubleStorage;
		this.numElements = numElements;
		typeFactory = createTypeFactory( numElements );
	}

	// this is the constructor if you want it to be a variable
	public DoubleVectorType( final double[] value )
	{
		super();
		img = null;
		numElements = value.length;
		dataAccess = new DoubleArray( value );
		typeFactory = createTypeFactory( numElements );
	}

	// this is the constructor if you want to specify the dataAccess
	public DoubleVectorType( final DoubleAccess access, final int numElements )
	{
		super();
		img = null;
		this.numElements = numElements;
		dataAccess = access;
		typeFactory = createTypeFactory( numElements );
	}

	// this is the constructor if you want it to be a variable
	public DoubleVectorType( final int numElements )
	{
		this( new double[ numElements ] );
	}

	@Override
	public Fraction getEntitiesPerPixel()
	{
		return new Fraction( numElements, 1 );
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public NativeTypeFactory< DoubleVectorType, DoubleAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	public double getDouble( final int j )
	{
		return dataAccess.getValue( i.get() * numElements + j );
	}

	/**
	 * Fill an array with the elements of this {@link DoubleVectorType}. This is
	 * more efficient than subsequent calls of {@link #getDouble(int)} because
	 * the base offset has to be calculated only once.
	 *
	 * @param values
	 */
	public void read( final double[] values )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			values[ j ] = dataAccess.getValue( ai + j );
	}

	/**
	 * Fill this {@link DoubleVectorType} with an array. This is more efficient
	 * than subsequent calls of {@link #setDouble(double, int)} because the base
	 * offset has to be calculated only once.
	 *
	 * @param values
	 */
	public void set( final double[] values )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, values[ j ] );
	}

	/**
	 * Set the values to those of another {@link DoubleVectorType} starting from
	 * an offset in the other type. The array's length must be &ge; offset +
	 * this type's length.
	 */
	public void setOffset( final double[] t, final int offset )
	{
		final int ai = i.get() * numElements;

		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, t[ j + offset ] );
	}

	/**
	 * Set the values to those of another {@link DoubleVectorType}. The other
	 * type's length must be &ge; this type's length.
	 */
	@Override
	public void set( final DoubleVectorType t )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, t.dataAccess.getValue( ti + j ) );
	}

	/**
	 * Set the values to those of another {@link DoubleVectorType} starting from
	 * an offset in the other type. The other type's length must be &ge; offset
	 * + this type's length.
	 */
	public void setOffset( final DoubleVectorType t, final int offset )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, t.dataAccess.getValue( ti + j + offset ) );
	}

	/**
	 * Modify the value at a specified index.
	 *
	 * @param value
	 * @param index
	 */
	public void setDouble( final double value, final int index )
	{
		dataAccess.setValue( i.get() * numElements + index, value );
	}

	@Override
	public boolean valueEquals( final DoubleVectorType t )
	{

		if ( numElements != t.numElements )
			return false;

		final int ai = i.get() * numElements;
		final int ti = t.i.get() * numElements;

		for ( int j = 0; j < numElements; ++j )
			if ( dataAccess.getValue( ai + j ) != t.dataAccess.getValue( ti + j ) )
				return false;
		return true;
	}

	@Override
	public String toString()
	{

		final int ai = i.get() * numElements;
		final StringBuilder str = new StringBuilder( "[" );
		for ( int j = 0; j < numElements; ++j )
		{
			if ( j > 0 )
				str.append( "," );
			str.append( dataAccess.getValue( ai + j ) );
		}
		str.append( "]" );
		return str.toString();
	}

	@Override
	public void mul( final float c )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) * c );
		}
	}

	@Override
	public void mul( final double c )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) * c );
		}
	}

	/**
	 * Add the values of another {@link DoubleVectorType} element wise. Behavior
	 * is undefined if the other {@link DoubleVectorType} has a different number
	 * of elements, this is not checked.
	 */
	@Override
	public void add( final DoubleVectorType t )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + t.dataAccess.getValue( ti + j ) );
		}
	}

	/**
	 * Divide this vector by another {@link DoubleVectorType} element wise.
	 * Behavior is undefined if the other {@link DoubleVectorType} has a
	 * different number of elements, this is not checked.
	 */
	@Override
	public void div( final DoubleVectorType t )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) / t.dataAccess.getValue( ti + j ) );
		}
	}

	/**
	 * Multiply this vector by another {@link DoubleVectorType} element wise.
	 * Behavior is undefined if the other {@link DoubleVectorType} has a
	 * different number of elements, this is not checked.
	 */
	@Override
	public void mul( final DoubleVectorType t )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) * t.dataAccess.getValue( ti + j ) );
		}
	}

	/**
	 * Subtract the values of another {@link DoubleVectorType} element wise.
	 * Behavior is undefined if the other {@link DoubleVectorType} has a
	 * different number of elements, this is not checked.
	 */
	@Override
	public void sub( final DoubleVectorType t )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) - t.dataAccess.getValue( ti + j ) );
		}
	}

	/**
	 * Powers with values of another {@link DoubleVectorType} element wise.
	 * Behavior is undefined if the other {@link DoubleVectorType} has a
	 * different number of elements, this is not checked.
	 */
	@Override
	public void pow( final DoubleVectorType t )
	{
		final int ai = i.get() * numElements;
		final int ti = t.i.get() * t.numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, Math.pow( dataAccess.getValue( k ), t.dataAccess.getValue( ti + j ) ) );
		}
	}

	@Override
	public void pow( final double d )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, Math.pow( dataAccess.getValue( k ), d ) );
		}
	}

	@Override
	public void setOne()
	{
		final int ai = i.get() * numElements;

		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, 1 );
	}

	@Override
	public void setZero()
	{
		final int ai = i.get() * numElements;

		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, 0 );
	}

	public void inc()
	{
		final int ai = i.get() * numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + 1 );
		}
	}

	public void dec()
	{
		final int ai = i.get() * numElements;

		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) - 1 );
		}
	}

	public int getBitsPerPixel()
	{
		return 64 * numElements;
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		final DoubleVectorType t = ( DoubleVectorType ) obj;
		return DoubleVectorType.this.valueEquals( t );
	}

	@Override
	public void fwd( final int d )
	{
		final int ai = i.get() * numElements + d;
		dataAccess.setValue( ai, dataAccess.getValue( ai ) + 1 );
	}

	@Override
	public void bck( final int d )
	{
		final int ai = i.get() * numElements + d;
		dataAccess.setValue( ai, dataAccess.getValue( ai ) - 1 );
	}

	@Override
	public void move( final int distance, final int d )
	{
		final int ai = i.get() * numElements + d;
		dataAccess.setValue( ai, dataAccess.getValue( ai ) + distance );
	}

	@Override
	public void move( final long distance, final int d )
	{
		final int ai = i.get() * numElements + d;
		dataAccess.setValue( ai, dataAccess.getValue( ai ) + distance );
	}

	@Override
	public void move( final Localizable distance )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + distance.getLongPosition( j ) );
		}
	}

	@Override
	public void move( final int[] distance )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + distance[ j ] );
		}
	}

	@Override
	public void move( final long[] distance )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + distance[ j ] );
		}
	}

	@Override
	public void setPosition( final Localizable position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, position.getLongPosition( j ) );
	}

	@Override
	public void setPosition( final int[] position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, position[ j ] );
	}

	@Override
	public void setPosition( final long[] position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, position[ j ] );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		setDouble( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		setDouble( position, d );
	}

	@Override
	public int numDimensions()
	{
		return numElements;
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getDouble( d );
	}

	@Override
	public void localize( final float[] position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			position[ j ] = ( float ) dataAccess.getValue( ai + j );
	}

	@Override
	public void localize( final double[] position )
	{
		read( position );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			position.setPosition( dataAccess.getValue( ai + j ), j );
	}

	@Override
	public DoubleVectorType createVariable()
	{
		return new DoubleVectorType( numElements );
	}

	@Override
	public DoubleVectorType copy()
	{
		final DoubleVectorType copy = new DoubleVectorType( numElements );
		copy.set( this );
		return copy;
	}

	@Override
	public DoubleVectorType duplicateTypeOnSameNativeImg()
	{
		return new DoubleVectorType( img, numElements );
	}

	@Override
	public void move( final float distance, final int d )
	{
		final int ai = i.get() * numElements + d;
		dataAccess.setValue( ai, dataAccess.getValue( ai ) + distance );
	}

	@Override
	public void move( final double distance, final int d )
	{
		final int ai = i.get() * numElements + d;
		dataAccess.setValue( ai, dataAccess.getValue( ai ) + distance );
	}

	@Override
	public void move( final RealLocalizable distance )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + distance.getDoublePosition( j ) );
		}
	}

	@Override
	public void move( final float[] distance )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + distance[ j ] );
		}
	}

	@Override
	public void move( final double[] distance )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
		{
			final int k = ai + j;
			dataAccess.setValue( k, dataAccess.getValue( k ) + distance[ j ] );
		}
	}

	@Override
	public void setPosition( final RealLocalizable position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, position.getDoublePosition( j ) );
	}

	@Override
	public void setPosition( final float[] position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, position[ j ] );
	}

	@Override
	public void setPosition( final double[] position )
	{
		final int ai = i.get() * numElements;
		for ( int j = 0; j < numElements; ++j )
			dataAccess.setValue( ai + j, position[ j ] );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		setDouble( position, d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		setDouble( position, d );
	}

	@Override
	public DoubleType get( @SuppressWarnings( "hiding" ) final long i )
	{
		type.set( getDouble( ( int ) i ) );
		return type;
	}
}
