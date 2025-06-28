package net.imglib2.type.mask;

import java.util.function.Supplier;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public class DoubleMaskedRealType<  V extends RealType< V > > extends AbstractMaskedRealType<V, DoubleType, DoubleMaskedRealType< V > >
{
	public DoubleMaskedRealType( final V value )
	{
		this( value, new DoubleType() );
	}

	public DoubleMaskedRealType( final V value, final double mask )
	{
		this( value, new DoubleType(mask) );
	}

	public DoubleMaskedRealType( final V value, final DoubleType mask )
	{
		super( value, mask );
	}

	@Override
	public DoubleMaskedRealType< V > createVariable()
	{
		return new DoubleMaskedRealType<>( value.createVariable(), mask.createVariable() );
	}

	@Override
	public DoubleMaskedRealType< V > copy()
	{
		return new DoubleMaskedRealType<>( value.copy(), mask.copy() );
	}

	public static < V extends RealType< V > > Supplier< DoubleMaskedRealType< V > > supplier( Supplier< V > valueSupplier, final double mask )
	{
		return () -> new DoubleMaskedRealType<>( valueSupplier.get(), mask );
	}
}
