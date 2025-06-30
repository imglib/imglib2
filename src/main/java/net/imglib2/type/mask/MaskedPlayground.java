package net.imglib2.type.mask;

import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;

public class MaskedPlayground
{

	// TODO: It would be cool to avoid generics hell here.
	//       Can we get away with Masked<T> interface that is extended by MaskedRealType, etc?
	//       Maybe it just has a "double mask()" method and we can cast a bit ???
	//       ~~> see CastingHell experiment

	public static class MaskedType< T extends Type< T > > extends AbstractMaskedType< T, MaskedType< T > >
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

		@Override
		public MaskedType< T > copy()
		{
			return new MaskedType<>( value().copy(), mask() );
		}
	}

	public static class MaskedRealType< T extends RealType< T > > extends AbstractMaskedRealType< T, MaskedRealType< T > >
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
	}

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

	public static class VolatileMaskedType< T extends Type< T > & Masked< ? > > extends Volatile< T > implements Type< VolatileMaskedType< T > >
	{
		public VolatileMaskedType( final T t, final boolean valid )
		{
			super( t, valid );
		}

		public VolatileMaskedType( final T t )
		{
			this( t, true );
		}

		@Override
		public void set( final VolatileMaskedType< T > c )
		{
			get().set( c.get() );
			setValid( c.isValid() );
		}

		@Override
		public VolatileMaskedType< T > createVariable()
		{
			return new VolatileMaskedType<>( get().createVariable(), true );
		}

		@Override
		public VolatileMaskedType< T > copy()
		{
			return new VolatileMaskedType<>( get().copy(), isValid() );
		}

		@Override
		public boolean valueEquals( final VolatileMaskedType< T > other )
		{
			return isValid() == other.isValid() && get().valueEquals( other.get() );
		}
	}

	static final double EPSILON = 1e-10;
}
