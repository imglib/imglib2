package net.imglib2.type.mask;

import java.util.function.Supplier;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public interface Masked< V, M extends RealType< M > >
{
	V value();

	M mask();

	class VolatileMasked< V extends Volatile< ? >, M extends RealType< M > > extends Volatile< V > implements Masked< V, M >
	{
		private Masked< V, M > delegate;

		public VolatileMasked( Masked< V, M > delegate )
		{
			super( null );
			this.delegate = delegate;
		}

		public VolatileMasked()
		{
			super( null );
		}

		public void setDelegate( final Masked< V, M > delegate )
		{
			this.delegate = delegate;
		}

		// -- Masked --

		@Override
		public V value()
		{
			return delegate.value();
		}

		@Override
		public M mask()
		{
			return delegate.mask();
		}

		// -- Volatile --

		@Override
		public V get()
		{
			return value();
		}

		@Override
		public boolean isValid()
		{
			return value().isValid();
		}

		@Override
		public void setValid( final boolean valid )
		{
			value().setValid( valid );
		}
	}

	class DoubleMasked< T > implements Masked< T, DoubleType >
	{
		private T value;

		private DoubleType mask;

		public DoubleMasked( final T value, final double mask ) {
			this.value = value;
			this.mask = new DoubleType( mask );
		}

		public void setValue( final T value )
		{
			this.value = value;
		}

		@Override
		public T value()
		{
			return value;
		}

		@Override
		public DoubleType mask()
		{
			return mask;
		}
	}

	class MaskedTypeConverter< T extends Type< T > > implements Converter< T, Masked< T, ? > >
	{
		@Override
		public void convert( final T input, final Masked< T, ? > output )
		{
			output.value().set( input );
		}
	}

	class MaskedNoTypeConverter< T > implements Converter< T, DoubleMasked< T > >
	{
		@Override
		public void convert( final T input, final DoubleMasked< T > output )
		{
			output.setValue( input );
		}
	}

	static < T > RandomAccessibleInterval< DoubleMasked< T > > with1( RandomAccessibleInterval< T > rai )
	{
		if ( rai.getType() instanceof Type )
		{
			final Type< ? > t = ( Type< ? > ) rai.getType();
			final Supplier< DoubleMasked< T > > targetSupplier = () -> new DoubleMasked<>( ( T ) t.createVariable(), 1 );
			final Supplier< Converter< ? super T, ? super DoubleMasked< T > > > converterSupplier = () -> new MaskedTypeConverter();
			return rai.view().convert( targetSupplier, converterSupplier );
		}
		else
		{
			return rai.view().convert( () -> new DoubleMasked<>( null, 1 ), MaskedNoTypeConverter::new );
		}
	}

	static < T > RealRandomAccessible< DoubleMasked< T > > with1( RealRandomAccessible< T > rai )
	{
		if ( rai.getType() instanceof Type )
		{
			final Type< ? > t = ( Type< ? > ) rai.getType();
			final Supplier< DoubleMasked< T > > targetSupplier = () -> new DoubleMasked<>( ( T ) t.createVariable(), 1 );
			final Supplier< Converter< ? super T, ? super DoubleMasked< T > > > converterSupplier = () -> new MaskedTypeConverter();
			return rai.realView().convert( converterSupplier, targetSupplier );
		}
		else
		{
			return rai.realView().convert( MaskedNoTypeConverter::new, () -> new DoubleMasked<>( null, 1 ) );
		}
	}
}
