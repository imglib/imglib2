package net.imglib2.type.mask;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.type.Type;

/**
 * A {@code Masked} {@code Volatile} {@code Type} wrapping a {@code Volatile}
 * {@code Type<T>}.
 * <p>
 * The {@code MaskedVolatileType} is valid if the wrapped value is valid.
 * Setting validity of the {@code MaskedVolatileType} is also forwarded to the
 * wrapped value.
 * <p>
 * Not that implementing {@code Volatile} in this way is mostly for convenience
 * Existing code (e.g., in BigDataViewer) that has branches for volatile and
 * non-volatile types doesn't have to add another branch for masked volatile
 * types (because {@code instanceof Volatile} checks and casting to {@code
 * Volatile} work as expected).
 *
 * @param <T>
 * 		the value type.
 */
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

	static < T extends Type< T > & Volatile< ? > > RandomAccessible< MaskedVolatileType< T > > withConstant( final RandomAccessible< T > ra, final double mask )
	{
		final T type = ra.getType();
		return ra.view().convert(
				() -> new MaskedVolatileType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

	static < T extends Type< T > & Volatile< ? > > RealRandomAccessible< MaskedVolatileType< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		final T type = rra.getType();
		return rra.realView().convert(
				() -> new MaskedVolatileType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}
}
