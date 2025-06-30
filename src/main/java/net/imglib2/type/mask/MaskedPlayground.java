package net.imglib2.type.mask;

import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.volatiles.AbstractVolatileNumericType;
import net.imglib2.type.volatiles.AbstractVolatileType;

public class MaskedPlayground
{

	// TODO: It would be cool to avoid generics hell here.
	//       Can we get away with Masked<T> interface that is extended by MaskedRealType, etc?
	//       Maybe it just has a "double mask()" method and we can cast a bit ???
	//       ~~> see CastingHell experiment

	public class VolatileToVolatileMaskedConverter<
			T extends Type< T >,
			A extends Volatile< T >,
			B extends Volatile< ? extends MaskedType< T > > >
		implements Converter< A, B >
	{
		@Override
		public void convert( final A input, final B output )
		{
			output.setValid( input.isValid() );
			output.get().value().set( input.get() );
		}
	}

	static final double EPSILON = 1e-10;
}
