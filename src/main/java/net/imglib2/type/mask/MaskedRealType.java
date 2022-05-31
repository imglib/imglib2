package net.imglib2.type.mask;

import net.imglib2.type.numeric.RealType;

public class MaskedRealType< V extends RealType< V >, M extends RealType< M > > extends AbstractMaskedRealType< V, M, MaskedRealType< V, M > >
{
	public MaskedRealType( final V value, final M mask )
	{
		super( value, mask );
	}

	@Override
	public MaskedRealType< V, M > createVariable()
	{
		return new MaskedRealType<>( value.createVariable(), mask.createVariable() );
	}

	@Override
	public MaskedRealType< V, M > copy()
	{
		return new MaskedRealType<>( value.copy(), mask.copy() );
	}
}
