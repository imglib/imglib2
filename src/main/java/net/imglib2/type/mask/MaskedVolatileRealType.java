package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;
import net.imglib2.type.numeric.RealType;

public class MaskedVolatileRealType< T extends RealType< T > & Volatile< ? > >
		extends AbstractMaskedRealType< T, MaskedVolatileRealType< T > >
		implements Volatile< T >
{
	public MaskedVolatileRealType( T value )
	{
		this( value, 0 );
	}

	public MaskedVolatileRealType( final T value, final double mask )
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
		return new MaskedVolatileRealType<>( value().createVariable(), 0 );
	}

	@Override
	// TODO: move to super-class --> createVariable + set
	public MaskedVolatileRealType< T > copy()
	{
		return new MaskedVolatileRealType<>( value().copy(), mask() );
	}

	// TODO: remove?
	static < T extends RealType< T > & Volatile< ? > > RandomAccessibleInterval< MaskedVolatileRealType< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedVolatileRealType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

}
