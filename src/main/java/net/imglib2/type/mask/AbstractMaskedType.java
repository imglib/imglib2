package net.imglib2.type.mask;

import net.imglib2.type.Type;

public abstract class AbstractMaskedType< T extends Type< T >, M extends AbstractMaskedType< T, M > >
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

	// --- ValueEquals< M > ---

	@Override
	public boolean valueEquals( final M other )
	{
		return mask() == other.mask() && value().valueEquals( other.value() );
	}
}
