package net.imglib2.type.mask;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.type.Type;
import net.imglib2.util.Cast;

public interface Masked< T >
{
	T value();

	void setValue( T value );

	double mask();

	void setMask( double mask );

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	static < T,
			R extends Type<R>,
			VR extends Type<VR> & Volatile< ? >>

	RandomAccessibleInterval< ? extends Masked< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		T type = rai.getType();
		if ( type instanceof Type )
		{
			if ( type instanceof Volatile )
				return Cast.unchecked( withConstant( ( RandomAccessibleInterval< VR > ) rai, mask, MaskedVolatileType::new ) );
			else
				return Cast.unchecked( withConstant( ( RandomAccessibleInterval< R > ) rai, mask, MaskedType::new ) );
		}
		else
		{
			throw new UnsupportedOperationException( "NOT IMPLEMENTED. TODO." + type.getClass().getName() );
		}


		// if T is RealType<T> use MaskedRealType
		// if T is Type<T> use MaskedType
		// otherwise use MaskedObj
	}

	@FunctionalInterface
	interface MaskedSupplier<T, M> {
		M get( T type, double mask );
	}

	static < T extends Type< T >, M extends Masked< T > > RandomAccessibleInterval< M > withConstant(
			final RandomAccessibleInterval< T > rai,
			final double mask,
			final MaskedSupplier< T, M > maskedSupplier )
	{
		final T type = rai.getType();
		return rai.view().convert(
				() -> maskedSupplier.get( type.createVariable(), mask ),
				new ToMaskedConverter<>() );
	}

	static < T > RealRandomAccessible< ? extends Masked< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		throw new UnsupportedOperationException("NOT IMPLEMENTED. TODO.");
	}
}
