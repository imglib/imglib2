package net.imglib2.type.mask;

import java.util.Objects;

import net.imglib2.util.Cast;
import net.imglib2.util.Util;

/**
 * Abstract base class for {@code Masked} implementations. Holds a {@code double
 * mask} and implements {@code equals()} and {@code hashCode()} based on {@code
 * mask()} and {@code value()}.
 *
 * @param <T>
 * 		the value type.
 * @param <M>
 * 		recursive type of derived concrete class.
 */
abstract class AbstractMasked< T, M extends AbstractMasked< T, M > >
		implements Masked< T >
{
	private double mask;

	protected AbstractMasked( double mask )
	{
		this.mask = mask;
	}

	// --- Masked< T > ---

	@Override
	public double mask()
	{
		return mask;
	}

	public void setMask( final double mask )
	{
		this.mask = mask;
	}

	// --- Object ---

	@Override
	public int hashCode()
	{
		return Util.combineHash( Objects.hashCode( value() ), Double.hashCode( mask() ) );
	}

	@Override
	public boolean equals( final Object obj )
	{
		if ( !getClass().isInstance( obj ) )
			return false;
		final M other = Cast.unchecked( obj );
		return other.mask() == mask() && Objects.equals( other.value(), value() );
	}
}
