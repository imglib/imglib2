package net.imglib2.type.mask;

import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

/**
 * A {@link RealType} value with an associated alpha mask.
 *
 * Add and MulFloatingPoint are implemented for pre-multiplied alpha,
 * i.e. {@code (v, a)*s := (v, a*s)} and {@code (v, a) + (w, b) := ((v*a+w*b)/(a+b), a+b)}
 * This makes default n-linear interpolation work.
 *
 * @param <V> the value type.
 * @param <M> the alpha mask type. Note that values are <em>not</em>> clamped to [0,1].
 * @param <T>
 */
public abstract class AbstractMaskedRealType< V extends RealType< V >, M extends RealType< M >, T extends AbstractMaskedRealType< V, M, T > >
		implements NumericType< T >
//		implements Type< T >, Add< T >, Sub< T >, SetOne, SetZero, MulFloatingPoint
{
	protected final V value;

	protected final M mask;

	public AbstractMaskedRealType( final V value, final M mask )
	{
		this.value = value;
		this.mask = mask;
	}



	// --- get components ---

	public V value()
	{
		return value;
	}

	public M mask()
	{
		return mask;
	}



	// --- Type< T >, Add< T >, Sub< T >, SetOne, SetZero, MulFloatingPoint ---

	@Override
	public void set( final T c )
	{
		value.set( c.value );
		mask.set( c.mask );
	}

	@Override
	public void mul( final float c )
	{
		mask.mul( c );
	}

	@Override
	public void mul( final double c )
	{
		mask.mul( c );
	}

	@Override
	public void add( final T c )
	{
		final double v0 = value.getRealDouble();
		final double a0 = mask.getRealDouble();
		final double v1 = c.value.getRealDouble();
		final double a1 = c.mask.getRealDouble();
		final double alpha = a0 + a1;
		value.setReal( alpha == 0 ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
		mask.setReal( alpha );
	}

	@Override
	public void sub( final T c )
	{
		// N.B. equivalent to add(c.mul(-1))
		final double v0 = value.getRealDouble();
		final double a0 = mask.getRealDouble();
		final double v1 = c.value.getRealDouble();
		final double a1 = -c.mask.getRealDouble();
		final double alpha = a0 + a1;
		value.setReal( alpha == 0 ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
		mask.setReal( alpha );
	}

	@Override
	public void setZero()
	{
		value.setZero();
		mask.setZero();
	}

	@Override
	public void setOne()
	{
		value.setOne();
		mask.setOne();
	}

	@Override
	public boolean valueEquals( T other )
	{
		return value.valueEquals( other.value ) && mask.valueEquals( other.mask );
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		@SuppressWarnings( "unchecked" )
		T t = ( T ) obj;
		return AbstractMaskedRealType.this.valueEquals( t );
	}

	@Override
	public int hashCode()
	{
		return Util.combineHash( value.hashCode(), mask.hashCode() );
	}



	// --- NumericType ---

	@Override
	public void mul( final T c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void div( final T c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void pow( final T c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void pow( final double power )
	{
		throw new UnsupportedOperationException();
	}
}
