package net.imglib2.type.mask;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;

public class MaskedObject< T > extends AbstractMasked< T, MaskedObject< T > >
{
	private T value;

	public MaskedObject( final T value )
	{
		this( value, 0 );
	}

	public MaskedObject( final T value, double mask )
	{
		super( mask );
		this.value = value;
	}

	@Override
	public T value()
	{
		return value;
	}

	@Override
	public void setValue( final T value )
	{
		this.value = value;
	}

	static < T > RandomAccessibleInterval< MaskedObject< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedObject<>( type, mask ),
				new ToMaskedConverter<>() );
	}

	static < T > RandomAccessible< MaskedObject< T > > withConstant( final RandomAccessible< T > ra, final double mask )
	{
		final T type = ra.getType();
		return ra.view().convert(
				() -> new MaskedObject<>( type, mask ),
				new ToMaskedConverter<>() );
	}

	static < T > RealRandomAccessible< MaskedObject< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		final T type = rra.getType();
		return rra.realView().convert(
				() -> new MaskedObject<>( type, mask ),
				new ToMaskedConverter<>() );
	}
}
