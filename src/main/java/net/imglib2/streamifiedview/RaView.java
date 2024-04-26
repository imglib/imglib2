package net.imglib2.streamifiedview;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.view.Views;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RaView< T > extends RandomAccessible< T >
{
	RandomAccessible< T > delegate();

	default RaiView< T > interval( Interval interval )
	{
		return RaiView.wrap( Views.interval(delegate(), interval) );
	}

	default RraView< T > interpolate( final InterpolatorFactory< T, ? super RandomAccessible< T > > factory )
	{
		return RraView.wrap( Views.interpolate( delegate(), factory ) );
	}

	static < T > RaView< T > wrap( final RandomAccessible< T > delegate )
	{
		return new RaWrapper<>( delegate );
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
