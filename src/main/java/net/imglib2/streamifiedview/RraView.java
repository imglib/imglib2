package net.imglib2.streamifiedview;

import java.util.function.Function;

import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.view.Views;

/**
 * First attempt at an interface which basically copies java's stream syntax to imglib2-views.
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RraView< T > extends RealRandomAccessible< T >
{
	RealRandomAccessible< T > delegate();

	default RaView< T, ? > raster()
	{
		return RaView.wrap( Views.raster( delegate() ) );
	}

	// TODO: rename? transform()? apply()? map()?
	default < U > U apply( Function< ? super RraView< T >, U > function )
	{
		return function.apply( this );
	}

	static < T > RraView< T > wrap( final RealRandomAccessible< T > delegate )
	{
		return new RraWrapper<>( delegate );
	}

	@Override
	default T getType()
	{
		return delegate().getType();
	}

	// TODO: Delegate all methods of RealRandomAccessible, also those that
	//       have a default implementations ...

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}

	@Override
	default RealRandomAccess< T > realRandomAccess()
	{
		return delegate().realRandomAccess();
	}

	@Override
	default RealRandomAccess< T > realRandomAccess( final RealInterval interval )
	{
		return delegate().realRandomAccess( interval );
	}
}
