package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

public class MaskedRealType< T extends RealType< T > > extends AbstractMaskedRealType< T, MaskedRealType< T > >
{
	public MaskedRealType( T value )
	{
		this( value, 0 );
	}

	public MaskedRealType( T value, double mask )
	{
		super( value, mask );
	}

	@Override
	public MaskedRealType< T > createVariable()
	{
		return new MaskedRealType<>( value().createVariable() );
	}

	@Override
	public MaskedRealType< T > copy()
	{
		return new MaskedRealType<>( value().copy(), mask() );
	}

	static < T extends RealType< T > > RandomAccessibleInterval< MaskedRealType< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> new MaskedRealType<>( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}
}
