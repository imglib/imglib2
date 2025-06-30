package net.imglib2.type.mask;

import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;

public abstract class AbstractMaskedRealType< T extends RealType< T >, M extends AbstractMaskedRealType< T, M > >
		extends AbstractMaskedType< T, M >
		implements NumericType< M >
{

	protected AbstractMaskedRealType( final T value, final double mask )
	{
		super( value, mask );
	}

	// --- Add<M>, Sub<M>, SetOne, SetZero, MulFloatingPoint ---

	@Override
	public void mul( final float c )
	{
		setMask( mask() * c );
	}

	@Override
	public void mul( final double c )
	{
		setMask( mask() * c );
	}

	@Override
	public void add( final M c )
	{
		final double a0 = mask();
		final double a1 = c.mask();
		final double alpha = a0 + a1;
		final double v0 = value().getRealDouble();
		final double v1 = c.value().getRealDouble();
		value().setReal( Math.abs( alpha ) < MaskedPlayground.EPSILON ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
		setMask( alpha );
	}

	@Override
	public void sub( final M c )
	{
		// N.B. equivalent to add(c.mul(-1))
		final double a0 = mask();
		final double a1 = -c.mask();
		final double alpha = a0 + a1;
		final double v0 = value().getRealDouble();
		final double v1 = c.value().getRealDouble();
		value().setReal( Math.abs( alpha ) < MaskedPlayground.EPSILON ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
		setMask( alpha );
	}

	@Override
	public void setZero()
	{
		value().setZero();
		setMask( 0 );
	}

	@Override
	public void setOne()
	{
		value().setOne();
		setMask( 1 );
	}

	// --- unsupported: Mul<T>, Div<T>, Pow<T>, PowFloatingPoint ---

	@Override
	public void mul( final M c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void div( final M c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void pow( final M c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void pow( final double power )
	{
		throw new UnsupportedOperationException();
	}
}
