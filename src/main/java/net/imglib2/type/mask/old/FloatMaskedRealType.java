package net.imglib2.type.mask.old;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class FloatMaskedRealType<  V extends RealType< V > > extends AbstractMaskedRealType<V, FloatType, FloatMaskedRealType< V > >
{
	public FloatMaskedRealType( final V value )
	{
		this( value, new FloatType() );
	}

	public FloatMaskedRealType( final V value, final float mask )
	{
		this( value, new FloatType( mask ) );
	}

	public FloatMaskedRealType( final V value, final FloatType mask )
	{
		super( value, mask );
	}

	@Override
	public FloatMaskedRealType< V > createVariable()
	{
		return new FloatMaskedRealType<>( value.createVariable(), mask.createVariable() );
	}

	@Override
	public FloatMaskedRealType< V > copy()
	{
		return new FloatMaskedRealType<>( value.copy(), mask.copy() );
	}
}
