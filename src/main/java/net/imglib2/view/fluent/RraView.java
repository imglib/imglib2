package net.imglib2.view.fluent;

import java.util.function.Function;

import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.view.Views;

/**
 * Gateway for creating light-weight views on a {@code RealRandomAccessible}.
 * <p>
 * A view is itself a {@code RealRandomAccessible} or {@code RandomAccessible}
 * whose accessors transform coordinates and/or values on-the-fly without
 * copying the underlying data. Consecutive transformations are concatenated and
 * simplified to provide optimally efficient accessors.
 * <p>
 * The {@code RraView} gateway implements {@code RealRandomAccessible},
 * forwarding all methods to its {@link #delegate}. Additionally, it provides
 * methods analogous to the {@code static} {@link Views} methods that operate on
 * its {@link #delegate} and return {@code RaiView}, {@code RaView}, or {@code
 * RraView} wrappers.
 * <p>
 * This provides a fluent API for conveniently chaining {@code Views} methods.
 * For example
 * <pre>
 * {@code RandomAccessibleInterval< IntType > view =
 *                img.view()
 *                   .permute( 0, 1 )
 *                   .extendBorder()
 *                   .interval( interval );
 * }
 * </pre>
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RraView< T > extends RealRandomAccessible< T >
{
	RealRandomAccessible< T > delegate();

	static < T > RraView< T > wrap( final RealRandomAccessible< T > delegate )
	{
		return new RraWrapper<>( delegate );
	}

	// -- Views methods -------------------------------------------------------

	/**
	 * Create a rasterized {@code RandomAccessible} view of this {@code
	 * RealRandomAccessible} by providing {@code RandomAccess} at integer
	 * coordinates.
	 *
	 * @return a rasterized view
	 */
	default RaView< T, ? > raster()
	{
		return RaView.wrap( Views.raster( delegate() ) );
	}

	// done until here
	//////////////////








	// TODO: rename? transform()? apply()? map()?
	default < U > U apply( Function< ? super RraView< T >, U > function )
	{
		return function.apply( this );
	}






	// -- RandomAccessible ----------------------------------------------------

	@Override
	default RraView< T > realView()
	{
		return this;
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
