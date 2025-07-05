package net.imglib2.type.mask;

import net.imglib2.type.Type;
import net.imglib2.util.Cast;

/**
 * Abstract base class for {@code Masked} implementations wrapping a {@code Type<T>}.
 * The resulting {@code Masked} class is itself a {@code Type}.
 * <p>
 * Provides implementations of {@code Masked} and {@code Type} interfaces.
 *
 * @param <T>
 * 		the value type.
 * @param <M>
 * 		recursive type of derived concrete class.
 */
abstract class AbstractMaskedType< T extends Type< T >, M extends AbstractMaskedType< T, M > >
		extends AbstractMasked< T, M >
		implements Type< M >
{
	private final T value;

	protected AbstractMaskedType( final T value, final double mask )
	{
		super( mask );
		this.value = value;
	}

	// --- Masked< T > ---

	@Override
	public T value()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value.set( value );
	}

	// --- Type< M > ---

	@Override
	public void set( final M c )
	{
		setValue( c.value() );
		setMask( c.mask() );
	}

	@Override
	public M copy()
	{
		final M copy = createVariable();
		copy.set( Cast.unchecked( this ) );
		return copy;
	}

	// --- ValueEquals< M > ---

	@Override
	public boolean valueEquals( final M other )
	{
		return mask() == other.mask() && value().valueEquals( other.value() );
	}
}
