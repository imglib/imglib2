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

import java.math.BigInteger;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.type.AbstractBit64Type;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.util.Util;

/**
 * A {@link Type} with arbitrary bit depth up to maximum 64 bits.
 * The behavior beyond 64 bits is undefined.
 *
 * The performance of this type is traded off for the gain in memory storage.
 * The {@link #set(long)} operation takes have the time as the {@link #get} operation.
 * The performance may degrade very slightly with increasing bit depth, but the decrease is barely noticeable.
 *
 * @author Albert Cardona
 * @author Stephan Preibisch
 */
public class UnsignedVariableBitLengthType extends AbstractBit64Type< UnsignedVariableBitLengthType > implements IntegerType< UnsignedVariableBitLengthType >
{
	// this is the constructor if you want it to read from an array
	public UnsignedVariableBitLengthType( final NativeImg< ?, ? extends LongAccess > bitStorage, final int nBits )
	{
		super( bitStorage, nBits );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedVariableBitLengthType( final long value, final int nBits ) { super( value, nBits ); }

	// this is the constructor if you want to specify the dataAccess
	public UnsignedVariableBitLengthType( final LongAccess access, final int nBits ) { super( access, nBits ); }

	// this is the constructor if you want it to be a variable
	public UnsignedVariableBitLengthType( final int nBits ) { super(nBits ); }

	@Override
	public NativeImg< UnsignedVariableBitLengthType, ? extends LongAccess > createSuitableNativeImg( final NativeImgFactory< UnsignedVariableBitLengthType > storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg< UnsignedVariableBitLengthType, ? extends LongAccess > container = storageFactory.createLongInstance( dim, getEntitiesPerPixel() );

		// create a Type that is linked to the container
		final UnsignedVariableBitLengthType linkedType = new UnsignedVariableBitLengthType( container, nBits );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	public void set( final long value ) { setBits( value ); }

	public long get() { return getBits(); }

	@Override
	public void set( final UnsignedVariableBitLengthType c ) { setBits( c.getBits() ); }

	@Override
	public UnsignedVariableBitLengthType duplicateTypeOnSameNativeImg() { return new UnsignedVariableBitLengthType( img, nBits ); }

	@Override
	public UnsignedVariableBitLengthType createVariable(){ return new UnsignedVariableBitLengthType( nBits ); }

	@Override
	public UnsignedVariableBitLengthType copy(){ return new UnsignedVariableBitLengthType( getBits(), nBits ); }

	/** @see UnsignedLongType#divide(long, long) */
	@Override
	public void div(final UnsignedVariableBitLengthType t) { setBits( UnsignedLongType.divide( getBits(), t.getBits() ) ); }

	/** @see UnsignedLongType#compare(long, long) */
	@Override
	public int compareTo( final UnsignedVariableBitLengthType t ) { return UnsignedLongType.compare( getBits(), t.getBits() ); }

	@Override
	public void mul( final float c ) { setReal( getRealDouble() * c ); }
	@Override
	public void mul( final double c ) { setReal( getRealDouble() * c ); }

	@Override
	public float getRealFloat() { return getIntegerLong(); }
	@Override
	public double getRealDouble() { return getIntegerLong(); }

	@Override
	public void setReal( final float real ){ setInteger( Util.round( real ) ); }
	@Override
	public void setReal( final double real ){ setInteger( Util.round( real ) ); }

	@Override
	public void setZero() { setInteger( 0 ); }
	@Override
	public void setOne() { setInteger( 1 ); }

	@Override
	public int getBitsPerPixel() { return nBits; }
	@Override
	public double getMinIncrement() { return 1; }

	@Override
	public String toString() { return "" + getIntegerLong(); }

	@Override
	public int getInteger() { return (int)get(); }

	@Override
	public long getIntegerLong() { return get(); }

	@Override
	public BigInteger getBigInteger() {
		if( get() < 0 )
			return BigInteger.valueOf(get()).add(new BigInteger("2", 10).pow(nBits));
		return BigInteger.valueOf( get() ); }

	@Override
	public void setInteger( final int f ) { setBits( f ); }

	@Override
	public void setInteger( final long f ) { setBits( f ); }

	@Override
	public void setBigInteger(final BigInteger b) { setBits( b.longValue() ); }

	/** The maximum value that can be stored is {@code Math.pow(2, nBits) -1}. */
	@Override
	public double getMaxValue() { return Math.pow( 2, getBitsPerPixel() ) -1; }
	@Override
	public double getMinValue()  { return 0; }

	@Override
	public void inc() {	setBits(get() + 1); }

	@Override
	public void dec() {	set(get() - 1); }

	@Override
	public void add(final UnsignedVariableBitLengthType t) { set(get() + t.get()); }

	@Override
	public void sub(final UnsignedVariableBitLengthType t) { set(get() - t.get()); }

	@Override
	public void mul(final UnsignedVariableBitLengthType t) { set(get() * t.get()); }

	@Override
	public float getImaginaryFloat() { return 0; }
	@Override
	public double getImaginaryDouble() { return 0; }

	@Override
	public void setImaginary( final float complex ){}
	@Override
	public void setImaginary( final double complex ){}

	@Override
	public float getPhaseFloat() { return 0; }
	@Override
	public double getPhaseDouble() { return 0; }

	@Override
	public float getPowerFloat() { return getRealFloat(); }
	@Override
	public double getPowerDouble() { return getRealDouble(); }

	@Override
	public void setComplexNumber( final float r, final float i ) { setReal( r ); }
	@Override
	public void setComplexNumber( final double r, final double i ) { setReal( r ); }

	@Override
	public void complexConjugate(){}

	/**
	 * Default test at long precision.  Please override for types longer than 64bit.
	 *
	 * @param t
	 * @return
	 */
	@Override
	public boolean valueEquals( final UnsignedVariableBitLengthType t )
	{
		return getBits() == t.getBits();
	}
}
