/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.NativeTypeFactory;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Albert Cardona
 * @author Mark Hiner
 */
public class UnsignedLongType extends GenericLongType< UnsignedLongType >
{

	private static final double MAX_VALUE_PLUS_ONE = Math.pow( 2, 64 ); // not precise, because double is not sufficient

	private static final double MAX_LONG_PLUS_ONE = Math.pow( 2, 63 ); // not precise, because double is not sufficient

	// this is the constructor if you want it to read from an array
	public UnsignedLongType( final NativeImg< ?, ? extends LongAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedLongType( final long value )
	{
		super( value );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedLongType( final BigInteger value )
	{
		super( ( NativeImg< ?, ? extends LongAccess > ) null );
		dataAccess = new LongArray( 1 );
		set( value.longValue() );
	}

	// this is the constructor if you want to specify the dataAccess
	public UnsignedLongType( final LongAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public UnsignedLongType()
	{
		this( 0 );
	}

	@Override
	public UnsignedLongType duplicateTypeOnSameNativeImg()
	{
		return new UnsignedLongType( img );
	}

	private static final NativeTypeFactory< UnsignedLongType, LongAccess > typeFactory = NativeTypeFactory.LONG( UnsignedLongType::new );

	@Override
	public NativeTypeFactory< UnsignedLongType, LongAccess > getNativeTypeFactory()
	{
		return typeFactory;
	}

	@Override
	public void mul( final float c )
	{
		set( Util.round( ( double ) ( get() * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		set( Util.round( ( get() * c ) ) );
	}

	@Override
	public void add( final UnsignedLongType c )
	{
		set( get() + c.get() );
	}

	/**
	 * @see #divide(long, long)
	 */
	@Override
	public void div( final UnsignedLongType c )
	{
		set( divide( get(), c.get() ) );
	}

	/**
	 * Unsigned division of {@code d1} by {@code d2}.
	 *
	 * See "Division by Invariant Integers using Multiplication", by Torbjorn
	 * Granlund and Peter L. Montgomery, 1994. <a href=
	 * "http://gmplib.org/~tege/divcnst-pldi94.pdf">http://gmplib.org/~tege/divcnst-pldi94.pdf</a>
	 *
	 * @throws ArithmeticException
	 *             when c equals zero.
	 */
	static public final long divide( final long d1, final long d2 )
	{
		if ( d2 < 0 )
		{
			// d2 is larger than the maximum signed long value
			if ( Long.compareUnsigned( d1, d2 ) < 0 )
			{
				// d1 is smaller than d2
				return 0;
			}
			else
			{
				// d1 is larger or equal than d2
				return 1;
			}
		}

		if ( d1 < 0 )
		{
			// Approximate division: exact or one less than the actual value
			final long quotient = ( ( d1 >>> 1 ) / d2 ) << 1;
			final long reminder = d1 - quotient * d2;
			return quotient + ( Long.compareUnsigned( d2, reminder ) < 0 ? 0 : 1 );
		}

		// Exact division, given that both d1 and d2 are smaller than
		// or equal to the maximum signed long value
		return d1 / d2;
	}

	@Override
	public void mul( final UnsignedLongType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final UnsignedLongType c )
	{
		set( get() - c.get() );
	}

	@Override
	public void setOne()
	{
		set( 1 );
	}

	@Override
	public void setZero()
	{
		set( 0 );
	}

	@Override
	public void inc()
	{
		set( get() + 1 );
	}

	@Override
	public void dec()
	{
		set( get() - 1 );
	}

	@Override
	public String toString()
	{
		return getBigInteger().toString();
	}

	/**
	 * This method returns the value of the UnsignedLongType as a signed long.
	 * To get the unsigned value, use {@link #getBigInteger()}.
	 */
	public long get()
	{
		return dataAccess.getValue( i.get() );
	}

	/**
	 * This method returns the unsigned representation of this UnsignedLongType
	 * as a {@code BigInteger}.
	 */
	@Override
	public BigInteger getBigInteger()
	{
		final BigInteger mask = new BigInteger( "FFFFFFFFFFFFFFFF", 16 );
		return BigInteger.valueOf( get() ).and( mask );
	}

	public void set( final long value )
	{
		dataAccess.setValue( i.get(), value );
	}

	@Override
	public int getInteger()
	{
		return ( int ) get();
	}

	@Override
	public long getIntegerLong()
	{
		return get();
	}

	@Override
	public void setInteger( final int f )
	{
		set( f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( f );
	}

	@Override
	public void setBigInteger( final BigInteger b )
	{
		set( b.longValue() );
	}

	@Override
	public void setReal( double real )
	{
		set( doubleToUnsignedLong( real ) );
	}

	static long doubleToUnsignedLong( double real )
	{
		double value = real < MAX_LONG_PLUS_ONE ?
				Math.max(0, real) :
				Math.min(-1, real - MAX_VALUE_PLUS_ONE);
		return Util.round( value );
	}

	@Override
	public void setReal( float real )
	{
		setReal( (double) real );
	}

	public void set( final BigInteger bi )
	{
		set( bi.longValue() );
	}

	/**
	 * The maximum value that can be stored is {@code Math.pow( 2, 64 ) - 1},
	 * which can't be represented with exact precision using a double
	 */
	@Override
	public double getMaxValue()
	{
		return MAX_VALUE_PLUS_ONE - 1;
	} // imprecise

	/**
	 * Returns the true maximum value as a BigInteger, since it cannot be
	 * precisely represented as a {@code double}.
	 */
	public BigInteger getMaxBigIntegerValue()
	{
		return new BigInteger( "+FFFFFFFFFFFFFFFF", 16 );
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	/**
	 * @deprecated Use {@link Long#compareUnsigned(long, long)} instead.
	 */
	@Deprecated
	static public final int compare( final long a, final long b )
	{
		if ( a == b )
			return 0;
		else
		{
			boolean test = ( a < b );
			if ( ( a < 0 ) != ( b < 0 ) )
			{
				test = !test;
			}
			return test ? -1 : 1;
		}
	}

	@Override
	public UnsignedLongType createVariable()
	{
		return new UnsignedLongType( 0 );
	}

	@Override
	public UnsignedLongType copy()
	{
		return new UnsignedLongType( dataAccess != null ? get() : 0 );
	}

	@Override
	public float getRealFloat()
	{
		long l = get();
		return l >= 0 ? l : ((float) MAX_VALUE_PLUS_ONE + l);
	}

	@Override
	public double getRealDouble()
	{
		return unsignedLongToDouble( get() );
	}

	static double unsignedLongToDouble( long l )
	{
		return l >= 0 ? l : (MAX_VALUE_PLUS_ONE + l);
	}

	@Override
	public int compareTo( final UnsignedLongType other )
	{
		return Long.compareUnsigned( get(), other.get() );
	}

}
