package net.imglib2.type.mask;

import java.util.Objects;

import net.imglib2.type.Type;
import net.imglib2.util.Cast;
import net.imglib2.util.Util;

public abstract class AbstractMaskedType< T extends Type< T >, M extends AbstractMaskedType< T, M > >
		extends AbstractMasked< T, M >
		implements Type< M >
{
	private final T value;

	protected AbstractMaskedType( T value, double mask )
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

	// --- Type< M > ---

	@Override
	public void set( final M c )
	{
		value().set( c.value() );
		setMask( c.mask() );
	}

	// --- ValueEquals< M > ---

	@Override
	public boolean valueEquals( final M other )
	{
		return mask() == other.mask() && value().valueEquals( other.value() );
	}
}
