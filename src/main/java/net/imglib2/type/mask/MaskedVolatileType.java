package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;
import net.imglib2.type.Type;

public class MaskedVolatileType< T extends Type< T > & Volatile< ? > >
		extends AbstractMaskedType< T, MaskedVolatileType< T > >
		implements Volatile< T >
{
	public MaskedVolatileType( final T value )
	{
		this( value, 0 );
	}

	public MaskedVolatileType( final T value, final double mask )
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

	// --- Type ---

	@Override
	public MaskedVolatileType< T > createVariable()
	{
		return new MaskedVolatileType<>( value().createVariable(), 0 );
	}

	static < T extends Type< T > & Volatile< ? > > RandomAccessibleInterval< MaskedVolatileType< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedVolatileType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}
}
