package net.imglib2.type.mask;

import net.imglib2.Volatile;
import net.imglib2.type.numeric.RealType;

public class MaskedVolatileRealType< T extends RealType< T > & Volatile< ? > >
		extends AbstractMaskedRealType< T, MaskedVolatileRealType< T > >
		implements Volatile< T >
{
	protected MaskedVolatileRealType( final T value, final double mask )
	{
		super( value, mask );
	}

	// --- Volatile ---

	@Override
	public T get()
	{
		return value();
	}

	@Override
	public boolean isValid()
	{
		return value().isValid();
	}

	@Override
	public void setValid( final boolean valid )
	{
		value().setValid( valid );
	}

	// --- NumericType ---

	@Override
	public void add( final MaskedVolatileRealType< T > c )
	{
		super.add( c );
		setValid( isValid() & c.isValid() );
	}

	@Override
	public void sub( final MaskedVolatileRealType< T > c )
	{
		super.sub( c );
		setValid( isValid() & c.isValid() );
	}

	// --- Type ---

	@Override
	public MaskedVolatileRealType< T > createVariable()
	{
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public MaskedVolatileRealType< T > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}

}
