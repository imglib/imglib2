package net.imglib2.type.mask;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.type.Type;

/**
 * A {@code Masked} {@code Type} wrapping a {@code Type<T>}.
 * <p>
 * Provides implementations of {@code Masked} and {@code Type} interfaces.
 *
 * @param <T>
 * 		the value type.
 */
public class MaskedType< T extends Type< T > >
		extends AbstractMasked< T, MaskedType< T > >
		implements Type< MaskedType< T > >
{
	private final T value;

	public MaskedType( T value )
	{
		this( value, 0 );
	}

	public MaskedType( T value, double mask )
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
	public void set( final MaskedType< T > c )
	{
		setValue( c.value() );
		setMask( c.mask() );
	}

	@Override
	public MaskedType< T > copy()
	{
		final MaskedType< T > copy = createVariable();
		copy.set( this );
		return copy;
	}

	@Override
	public MaskedType< T > createVariable()
	{
		return new MaskedType<>( value().createVariable() );
	}

	// --- ValueEquals< M > ---

	@Override
	public boolean valueEquals( final MaskedType< T > other )
	{
		return mask() == other.mask() && value().valueEquals( other.value() );
	}

	// ------------------------

	static < T extends Type< T > > RandomAccessibleInterval< MaskedType< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

	static < T extends Type< T > > RandomAccessible< MaskedType< T > > withConstant( final RandomAccessible< T > ra, final double mask )
	{
		final T type = ra.getType();
		return ra.view().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

	static < T extends Type< T > > RealRandomAccessible< MaskedType< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		final T type = rra.getType();
		return rra.realView().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}
}
