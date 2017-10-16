/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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


package net.imglib2.type.numeric.integer;

import java.math.BigDecimal;
import java.math.BigInteger;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Fraction;

/**
 * A {@link Type} with a bit depth of 128.
 * Each value is stored in two adjacent long in an array,
 * with the lower long first, then the upper long.
 * Currently the math methods defined in the superinterface {@link NumericType} are implemented using {@link BigInteger} and {@link BigDecimal}.
 * This class is not {@link Thread}-safe; do a {@link #copy()} first to operate on a different {@link Thread}.
 *
 * @author Albert Cardona
 */
public class Unsigned128BitType extends AbstractIntegerType< Unsigned128BitType > implements NativeType< Unsigned128BitType >
{
	private int i = 0;

	final protected NativeImg< ?, ? extends LongAccess > img;

	// 17, so the first byte is 0 to mean positive integer
	final protected byte[] bytes = new byte[17];

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public Unsigned128BitType( final NativeImg< ?, ? extends LongAccess > bitStorage )
	{
		img = bitStorage;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned128BitType( final long lower, final long upper )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = new LongArray( 2 );
		set( lower, upper );
	}

	// this is the constructor if you want it to be a variable
	public Unsigned128BitType( final BigInteger value )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = new LongArray( 2 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned128BitType( final LongAccess access )
	{
		this( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned128BitType( ) { this( 0, 0 ); }

	@Override
	public NativeImg< Unsigned128BitType, ? extends LongAccess > createSuitableNativeImg( final NativeImgFactory< Unsigned128BitType > storageFactory, final long dim[] )
	{
		// create the container:
		final NativeImg< Unsigned128BitType, ? extends LongAccess > container = storageFactory.createLongInstance( dim, new Fraction( 2, 1 ) );

		// create a Type that is linked to the container
		final Unsigned128BitType linkedType = new Unsigned128BitType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c ) { dataAccess = img.update( c ); }

	@Override
	public Unsigned128BitType duplicateTypeOnSameNativeImg() { return new Unsigned128BitType( img ); }

	private final void intoBytes( final long lower, final long upper ) {
		bytes[ 0] = 0; // so the number is non-negative
		bytes[ 1] = (byte)((upper >>> 56) & 0xffL);
		bytes[ 2] = (byte)((upper >>> 48) & 0xffL);
		bytes[ 3] = (byte)((upper >>> 40) & 0xffL);
		bytes[ 4] = (byte)((upper >>> 32) & 0xffL);
		bytes[ 5] = (byte)((upper >>> 24) & 0xffL);
		bytes[ 6] = (byte)((upper >>> 16) & 0xffL);
		bytes[ 7] = (byte)((upper >>>  8) & 0xffL);
		bytes[ 8] = (byte) (upper         & 0xffL);
		bytes[ 9] = (byte)((lower >>> 56) & 0xffL);
		bytes[10] = (byte)((lower >>> 48) & 0xffL);
		bytes[11] = (byte)((lower >>> 40) & 0xffL);
		bytes[12] = (byte)((lower >>> 32) & 0xffL);
		bytes[13] = (byte)((lower >>> 24) & 0xffL);
		bytes[14] = (byte)((lower >>> 16) & 0xffL);
		bytes[15] = (byte)((lower >>>  8) & 0xffL);
		bytes[16] = (byte) (lower         & 0xffL);
	}

	/** The first byte is the most significant byte, like in {@link BigInteger#toByteArray()}.
	 * Only the last 16 bytes are read, if there are more. */
	public void set( final byte[] bytes ) {
		final int k = i * 2;
		int b = bytes.length - 1;
		for ( int offset = 0; offset < 2; ++offset ) {
			final int cut = Math.max( -1, b - 8 );
			long u = 0;
			for ( int p = 0; b > cut; --b, p += 8 ) {
				u |= ( bytes[ b ] & 0xffL ) << p;
			}
			dataAccess.setValue( k + offset, u );
		}
	}

	public BigInteger get() {
		final int k = i * 2;
		intoBytes( dataAccess.getValue( k ), dataAccess.getValue( k + 1 ) );
		return new BigInteger( bytes );
	}

	public void set( final BigInteger value ) {
		set( value.toByteArray() );
	}

	public void set( final long lower, final long upper ) {
		final int k = i * 2;
		dataAccess.setValue( k, lower );
		dataAccess.setValue( k + 1, upper );
	}

	/** Return the lowest 32 bits, like {@link BigInteger#intValue()}. */
	@Override
	public int getInteger() {
		return (int)(dataAccess.getValue( i * 2 ) & 0xffffffffL);
	}

	/** Return the lowest 64 bits, like {@link BigInteger#intValue()}. */
	@Override
	public long getIntegerLong() {
		return dataAccess.getValue( i * 2 );
	}

	@Override
	public BigInteger getBigInteger() {
		return get();
	}

	@Override
	public void setInteger( final int value ) {
		final int k = i * 2;
		dataAccess.setValue( k, value );
		dataAccess.setValue( k + 1, 0 );
	}

	@Override
	public void setInteger( final long value ) {
		final int k = i * 2;
		dataAccess.setValue( k, value );
		dataAccess.setValue( k + 1, 0 );
	}

	@Override
	public void setBigInteger(final BigInteger b) {
		set( b );
	}

	/** The maximum value that can be stored is {@code Math.pow(2, 128) -1},
	 * which cannot be represented with precision using a double */
	@Override
	public double getMaxValue() { return Math.pow( 2, 128 ) - 1; } // imprecise

	/** The true maximum value, unlike {@link #getMaxValue()} which cannot represent
	 * it in a {@code double}. */
	public BigInteger getMaxBigIntegerValue() {
		bytes[0] = 0;
		for (int b=1; b<bytes.length; ++b) {
			bytes[b] = -1; // 0xff
		}
		return new BigInteger(bytes);
	}

	@Override
	public double getMinValue()  { return 0; }

	@Override
	public int getIndex() { return i; }

	@Override
	public void updateIndex( final int index )
	{
		i = index;
	}

	@Override
	public void incIndex()
	{
		++i;
	}
	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}
	@Override
	public void decIndex()
	{
		--i;
	}
	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public Unsigned128BitType createVariable(){ return new Unsigned128BitType(); }

	@Override
	public Unsigned128BitType copy(){
		final Unsigned128BitType copy = new Unsigned128BitType();
		final int k = i * 2;
		copy.set( dataAccess.getValue( k ), dataAccess.getValue( k + 1 ));
		return copy;
	}

	@Override
	public Fraction getEntitiesPerPixel() { return new Fraction( 2, 1 ); }

	@Override
	public int getBitsPerPixel() { return 128; }

	@Override
	public void inc() {
		final int k = i * 2;
		final long lower = dataAccess.getValue( k );
		if ( 0xffffffffffffffffL == lower ) {
			dataAccess.setValue( k, 0 );
			final long upper = dataAccess.getValue( k + 1 );
			if ( 0xffffffffffffffffL == upper ) {
				dataAccess.setValue( k + 1, 0 );
			} else {
				dataAccess.setValue( k + 1, upper + 1 );
			}
		} else {
			dataAccess.setValue( k, lower + 1 );
		}
	}

	@Override
	public void dec() {
		final int k = i * 2;
		final long lower = dataAccess.getValue( k );
		if ( 0 == lower ) {
			dataAccess.setValue( k, 0xffffffffffffffffL );
			final long upper = dataAccess.getValue( k + 1 );
			if ( 0 == upper ) {
				dataAccess.setValue( k + 1, 0xffffffffffffffffL );
			} else {
				dataAccess.setValue( k + 1, upper - 1 );
			}
		} else {
			dataAccess.setValue( k, lower - 1 );
		}
	}

	@Override
	public void setZero() {
		set( 0, 0 );
	}

	@Override
	public void setOne() {
		set( 1, 0 );
	}

	/** See {@link #mul(double)}. */
	@Override
	public void mul( final float c ) {
		mul( (double)c );
	}

	/** Implemented using {@link BigDecimal#multiply(BigDecimal)} and {@link BigDecimal#toBigInteger()}. */
	@Override
	public void mul( final double c ) {
		set( new BigDecimal( get() ).multiply( new BigDecimal( c ) ).toBigInteger() );
	}

	/** Relies on {@link BigInteger#add(BigInteger)}. */
	@Override
	public void add(final Unsigned128BitType t) {
		set( get().add( t.get() ).toByteArray() );
	}

	/** Relies on {@link BigInteger#subtract(BigInteger)}. */
	@Override
	public void sub(final Unsigned128BitType t) {
		set( get().subtract( t.get() ).toByteArray() );
	}

	/** Relies on {@link BigInteger#multiply(BigInteger)}. */
	@Override
	public void mul(final Unsigned128BitType t) {
		set( get().multiply( t.get() ).toByteArray() );
	}

	/** Relies on {@link BigInteger#divide(BigInteger)}. */
	@Override
	public void div(final Unsigned128BitType t) {
		set( get().divide( t.get() ).toByteArray() );
	}

	@Override
	public int compareTo( final Unsigned128BitType t ) {
		final long upper1 = dataAccess.getValue( i * 2 + 1 ),
		            upper2 = t.dataAccess.getValue( t.i * 2 + 1 );
		if ( -1 == UnsignedLongType.compare( upper1, upper2 ) ) {
			return -1;
		} else if ( upper1 == upper2 ) {
			final long lower1 = dataAccess.getValue( i * 2 ),
			            lower2 = t.dataAccess.getValue( t.i * 2 );
			return UnsignedLongType.compare( lower1, lower2 );
		}
		return 1;
	}

	@Override
	public boolean valueEquals( final Unsigned128BitType t )
	{
		final int k = i * 2;
		final int kt = t.i * 2;

		return
				( dataAccess.getValue( k ) == t.dataAccess.getValue( kt ) ) &&
				( dataAccess.getValue( k + 1 ) == t.dataAccess.getValue( kt + 1 ) );
	}
}
