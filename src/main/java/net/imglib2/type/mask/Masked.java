package net.imglib2.type.mask;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Cast;

public interface Masked< T >
{
	T value();

	double mask();

	void setMask( double mask );

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	static < T,
			R extends RealType<R>,
			VR extends RealType<VR> & Volatile< ? >>

	RandomAccessibleInterval< ? extends Masked< T > > withConstant( final RandomAccessibleInterval< T > rai, final double mask )
	{
		T type = rai.getType();
		if ( type instanceof RealType )
		{
			if ( type instanceof Volatile )
				return Cast.unchecked( withConstant( ( RandomAccessibleInterval< VR > ) rai, mask, MaskedVolatileRealType::new ) );
			else
				return Cast.unchecked( withConstant( ( RandomAccessibleInterval< R > ) rai, mask, MaskedRealType::new ) );
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
				new TypeToMaskedTypeConverter<>() );
	}

	static < T > RealRandomAccessible< ? extends Masked< T > > withConstant( final RealRandomAccessible< T > rra, final double mask )
	{
		throw new UnsupportedOperationException("NOT IMPLEMENTED. TODO.");
	}
}
