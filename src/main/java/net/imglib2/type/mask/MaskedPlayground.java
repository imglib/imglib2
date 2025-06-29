package net.imglib2.type.mask;

import java.util.Objects;
import java.util.function.Supplier;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

public class MaskedPlayground
{

	// TODO: It would be cool to avoid generics hell here.
	//       Can we get away with Masked<T> interface that is extended by MaskedRealType, etc?
	//       Maybe it just has a "double mask()" method and we can cast a bit ???
	//       ~~> see CastingHell experiment


	public interface Masked< T >
	{
		T value();

		double mask();
	}

	public static class MaskedObj< T > implements Masked< T >
	{
		private T value;

		private double mask;

		public MaskedObj( T value )
		{
			this( value, 0 );
		}

		public MaskedObj( T value, double mask )
		{
			this.value = value;
			this.mask = mask;
		}

		@Override
		public T value()
		{
			return value;
		}

		public void setValue( T value )
		{
			this.value = value;
		}

		@Override
		public double mask()
		{
			return mask;
		}

		public void setMask( final double mask )
		{
			this.mask = mask;
		}

		// TODO equals, hashcode
	}

	// TODO: after this all works, consider making parallel recursively typed
	//       abstract class hierarchy to reduce code duplication
	//       i.e. AbstractMaskedType< T extends Type< T >, M extends AbstractMaskedType< T, M > >
	//	          		implements Masked< T >, Type< M >
	//       and AbstractMaskedRealTypeType< T extends RealType< T >, M extends AbstractMaskedRealType< T, M > > extends AbstractMaskedType< T, M >
	//	          		implements RealType< M >
	public static class MaskedType< T extends Type< T > > implements Masked< T >, Type< MaskedType< T > >
	{
		private final T value;

		private double mask;

		public MaskedType( T value )
		{
			this( value, 0 );
		}

		public MaskedType( T value, double mask )
		{
			this.value = value;
			this.mask = mask;
		}

		@Override
		public T value()
		{
			return value;
		}

		@Override
		public double mask()
		{
			return mask;
		}

		public void setMask( final double mask )
		{
			this.mask = mask;
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

		@Override
		public void set( final MaskedType< T > c )
		{
			value().set( c.value() );
			setMask( c.mask() );
		}

		@Override
		public boolean valueEquals( MaskedType< T > other )
		{
			return mask() == other.mask() && value().valueEquals( other.value() );
		}

		// TODO equals, hashcode
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



	public static class MaskedRealType< T extends RealType< T > > implements Masked< T >, NumericType< MaskedRealType< T > >
	{
		private final T value;

		private double mask;

		public MaskedRealType( T value )
		{
			this( value, 0 );
		}

		public MaskedRealType( T value, double mask )
		{
			this.value = value;
			this.mask = mask;
		}

		@Override
		public T value()
		{
			return value;
		}

		@Override
		public double mask()
		{
			return mask;
		}

		public void setMask( final double mask )
		{
			this.mask = mask;
		}

		@Override
		public boolean equals( final Object obj )
		{
			if ( !getClass().isInstance( obj ) )
				return false;
			final MaskedRealType< ? > other = ( MaskedRealType< ? > ) obj;
			return mask() == other.mask() && Objects.equals( value, other.value );
		}

		@Override
		public int hashCode()
		{
			return Util.combineHash( Objects.hashCode( value ), Double.hashCode( mask() ) );
		}

		static < T extends RealType< T > > RandomAccessibleInterval< MaskedRealType< T > > withMask( final RandomAccessibleInterval< T > rai, final double mask )
		{
			final T type = rai.getType();
			final Supplier< MaskedRealType< T > > targetSupplier = () -> new MaskedRealType<>( type.createVariable(), mask );
			return rai.view().convert( targetSupplier, ( i, o ) -> o.value().set( i ) );
		}

		// --- Type< T > ---

		@Override
		public MaskedRealType< T > createVariable()
		{
			return new MaskedRealType<>( value.createVariable() );
		}

		@Override
		public MaskedRealType< T > copy()
		{
			return new MaskedRealType<>( value.copy(), mask );
		}

		@Override
		public void set( final MaskedRealType< T > c )
		{
			value().set( c.value() );
			setMask( c.mask() );
		}

		@Override
		public boolean valueEquals( MaskedRealType< T > other )
		{
			return mask() == other.mask() && value().valueEquals( other.value() );
		}

		// --- Add< T >, Sub< T >, SetOne, SetZero, MulFloatingPoint ---

		@Override
		public void mul( final float c )
		{
			setMask( mask() * c );
		}

		@Override
		public void mul( final double c )
		{
			setMask( mask() * c );
		}

		@Override
		public void add( final MaskedRealType< T > c )
		{
			final double a0 = mask();
			final double a1 = c.mask();
			final double alpha = a0 + a1;
			final double v0 = value().getRealDouble();
			final double v1 = c.value().getRealDouble();
			value().setReal( alpha < EPSILON ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
			setMask( alpha );
		}

		@Override
		public void sub( final MaskedRealType< T > c )
		{
			// N.B. equivalent to add(c.mul(-1))
			final double a0 = mask();
			final double a1 = -c.mask();
			final double alpha = a0 + a1;
			final double v0 = value().getRealDouble();
			final double v1 = c.value().getRealDouble();
			value().setReal( alpha < EPSILON ? 0 : ( v0 * a0 + v1 * a1 ) / alpha );
			setMask( alpha );
		}

		@Override
		public void setZero()
		{
			value().setZero();
			setMask( 0 );
		}

		@Override
		public void setOne()
		{
			value().setOne();
			setMask( 1 );
		}

		// --- unsupported NumericType methods ---

		@Override
		public void mul( final MaskedRealType< T > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void div( final MaskedRealType< T > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void pow( final MaskedRealType< T > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void pow( final double power )
		{
			throw new UnsupportedOperationException();
		}
	}

	static final double EPSILON = 1e-10;
}
