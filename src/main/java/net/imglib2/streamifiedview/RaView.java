package net.imglib2.streamifiedview;

import java.util.function.Function;
import java.util.function.Supplier;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.view.Views;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RaView< T, V extends RaView< T, V > > extends RandomAccessible< T >
{
	RandomAccessible< T > delegate();

	default RaiView< T > interval( Interval interval )
	{
		return RaiView.wrap( Views.interval( delegate(), interval ) );
	}

	default RraView< T > interpolate( final InterpolatorFactory< T, ? super RandomAccessible< T > > factory )
	{
		return RraView.wrap( Views.interpolate( delegate(), factory ) );
	}

	default < U > RaView< U, ? > convert(
			final Converter< ? super T, ? super U > converter,
			final Supplier< U > targetSupplier )
	{
		return wrap( Converters.convert2( delegate(), converter, targetSupplier ) );
	}

	default < U > U apply( Function< ? super V, U > function )
	{
		return function.apply( ( V ) this );
	}

	static < T > RaView< T, ? > wrap( final RandomAccessible< T > delegate )
	{
		return new RaWrapper<>( delegate );
	}

	@Override
	default RaView< T, ? > view()
	{
		return this;
	}

	@Override
	default T getType()
	{
		return delegate().getType();
	}

	// TODO: Delegate all methods of RandomAccessible, also those that
	//       have a default implementations ...

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}

	@Override
	default RandomAccess< T > randomAccess()
	{
		return delegate().randomAccess();
	}

	@Override
	default RandomAccess< T > randomAccess( final Interval interval )
	{
		return delegate().randomAccess(interval);
	}
}
