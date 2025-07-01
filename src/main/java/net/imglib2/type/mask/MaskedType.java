package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;

public class MaskedType< T extends Type< T > > extends AbstractMaskedType< T, MaskedType< T > >
{
	public MaskedType( T value )
	{
		this( value, 0 );
	}

	public MaskedType( T value, double mask )
	{
		super( value, mask );
	}

	@Override
	public MaskedType< T > createVariable()
	{
		return new MaskedType<>( value().createVariable() );
	}

	static < T extends Type< T > > RandomAccessibleInterval< MaskedType< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}
}
