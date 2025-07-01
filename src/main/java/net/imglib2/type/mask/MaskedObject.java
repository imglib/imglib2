package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;

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
}
