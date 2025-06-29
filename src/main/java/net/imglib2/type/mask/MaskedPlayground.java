package net.imglib2.type.mask;

import java.util.Objects;
import java.util.function.Supplier;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

public class MaskedPlayground
{

	// TODO: It would be cool to avoid generics hell here.
	//       Can we get away with Masked<T> interface that is extended by MaskedRealType, etc?
	//       Maybe it just has a "double mask()" method and we can cast a bit ???
	//       ~~> see CastingHell experiment



	static final double EPSILON = 1e-10;

	public interface Masked< V >
	{
		V value();

		double mask();
	}


	public static class DefaultMaskedRealType< V extends RealType< V > > implements Masked< V >, NumericType< DefaultMaskedRealType< V > >
	{
		private final V value;

		private double mask;

		public DefaultMaskedRealType( V value )
		{
			this( value, 0 );
		}

		public DefaultMaskedRealType( V value, double mask )
		{
			this.value = value;
			this.mask = mask;
		}

		@Override
		public V value()
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
			final DefaultMaskedRealType< ? > other = ( DefaultMaskedRealType< ? > ) obj;
			return mask() == other.mask() && Objects.equals( value, other.value );
		}

		@Override
		public int hashCode()
		{
			return Util.combineHash( Objects.hashCode( value ), Double.hashCode( mask() ) );
		}

		static < T extends RealType< T > > RandomAccessibleInterval< DefaultMaskedRealType< T > > withMask( final RandomAccessibleInterval< T > rai, final double mask )
		{
			final T type = rai.getType();
			final Supplier< DefaultMaskedRealType< T > > targetSupplier = () -> new DefaultMaskedRealType<>( type.createVariable(), mask );
			return rai.view().convert( targetSupplier, ( i, o ) -> o.value().set( i ) );
		}

		// --- Type< T >, Add< T >, Sub< T >, SetOne, SetZero, MulFloatingPoint ---

		@Override
		public DefaultMaskedRealType< V > createVariable()
		{
			return new DefaultMaskedRealType<>( value.createVariable() );
		}

		@Override
		public DefaultMaskedRealType< V > copy()
		{
			return new DefaultMaskedRealType<>( value.copy(), mask );
		}

		@Override
		public void set( final DefaultMaskedRealType< V > c )
		{
			value().set( c.value() );
			setMask( c.mask() );
		}

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
		public void add( final DefaultMaskedRealType< V > c )
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
		public void sub( final DefaultMaskedRealType< V > c )
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

		@Override
		public boolean valueEquals( DefaultMaskedRealType< V > other )
		{
			return mask() == other.mask() && value().valueEquals( other.value() );
		}

		// --- unsupported NumericType methods ---

		@Override
		public void mul( final DefaultMaskedRealType< V > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void div( final DefaultMaskedRealType< V > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void pow( final DefaultMaskedRealType< V > c )
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void pow( final double power )
		{
			throw new UnsupportedOperationException();
		}
	}
}
