package net.imglib2.type.mask;

import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;

public interface UNUSED_MaskedRealType< V extends RealType< V >, T extends UNUSED_MaskedRealType< V, T > > extends MaskedPlayground.Masked< V >, NumericType< T >
{
	void setMask( double mask );

	// --- Type< T >, Add< T >, Sub< T >, SetOne, SetZero, MulFloatingPoint ---

	@Override
	default void set( final T c )
	{
		value().set( c.value() );
		setMask( c.mask() );
	}

	@Override
	default void mul( final float c )
	{
		setMask( mask() * c );
	}

	@Override
	default void mul( final double c )
	{
		setMask( mask() * c );
	}

	@Override
	default void add( final T c )
	{
		final double a0 = mask();
		final double a1 = c.mask();
		final double alpha = a0 + a1;
		final double v0 = value().getRealDouble();
		final double v1 = c.value().getRealDouble();
		value().setReal( alpha < MaskedPlayground.EPSILON ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
		setMask( alpha );
	}

	@Override
	default void sub( final T c )
	{
		// N.B. equivalent to add(c.mul(-1))
		final double a0 = mask();
		final double a1 = -c.mask();
		final double alpha = a0 + a1;
		final double v0 = value().getRealDouble();
		final double v1 = c.value().getRealDouble();
		value().setReal( alpha < MaskedPlayground.EPSILON ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
		setMask( alpha );
	}

	@Override
	default void setZero()
	{
		value().setZero();
		setMask( 0 );
	}

	@Override
	default void setOne()
	{
		value().setOne();
		setMask( 1 );
	}

	@Override
	default boolean valueEquals( T other )
	{
		return mask() == other.mask() && value().valueEquals( other.value() );
	}

	// --- NumericType ---

	@Override
	default void mul( final T c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	default void div( final T c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	default void pow( final T c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	default void pow( final double power )
	{
		throw new UnsupportedOperationException();
	}
}
