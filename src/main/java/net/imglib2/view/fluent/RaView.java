package net.imglib2.view.fluent;

import java.util.function.Function;
import java.util.function.Supplier;

import net.imglib2.EuclideanSpace;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

/**
 * Gateway for creating light-weight views on a {@code RandomAccessible}.
 * <p>
 * A view is itself a {@code RandomAccessible} or {@code
 * RandomAccessibleInterval} whose accessors transform coordinates and/or values
 * on-the-fly without copying the underlying data. Consecutive transformations
 * are concatenated and simplified to provide optimally efficient accessors.
 * <p>
 * The {@code RaView} gateway implements {@code RandomAccessible}, forwarding
 * all methods to its {@link #delegate}. Additionally, it provides methods
 * analogous to the {@code static} {@link Views} methods that operate on its
 * {@link #delegate} and return {@code RaiView}, {@code RaView}, or {@code
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
public interface RaView< T, V extends RaView< T, V > > extends RandomAccessible< T >
{
	RandomAccessible< T > delegate();

	static < T > RaView< T, ? > wrap( final RandomAccessible< T > delegate )
	{
		return new RaWrapper<>( delegate );
	}

	// -- Views methods -------------------------------------------------------

	/**
	 * Define an interval on this {@code RandomAccessible}.
	 * <p>
	 * It is the callers responsibility to ensure that the source {@code
	 * RandomAccessible} is defined in the specified {@code interval}.
	 * <p>
	 * For constructing an {@code Interval} from min and max coordinates, min
	 * coordinates and dimensions, by modifying other intervals, etc, see {@link
	 * FinalInterval} and {@link Intervals}.
	 *
	 * @param interval
	 * 		interval boundaries.
	 *
	 * @return a view on the given interval
	 */
	default RaiView< T > interval( Interval interval )
	{
		return RaiView.wrap( Views.interval( delegate(), interval ) );
	}

	/**
	 * Take a <em>(n-1)</em>-dimensional slice of this <em>n</em>-dimensional
	 * {@code RandomAccessible}, by fixing the {@code d} dimension of
	 * coordinates to {@code pos}.
	 *
	 * @param d
	 * 		coordinate dimension to fix
	 * @param pos
	 * 		coordinate value to fix {@code d}th dimension to
	 *
	 * @return a view on the given slice
	 */
	default RaView< T, ? > slice( int d, long pos )
	{
		return wrap( Views.hyperSlice( delegate(), d, pos ) );
	}

	/**
	 * Create a <em>(n+1)</em>-dimensional view of this <em>n</em>-dimensional
	 * {@code RandomAccessible}, by replicating values along the added axis.
	 * <p>
	 * The additional dimension is the last dimension. For example, an XYZ view
	 * is created for an XY source. When accessing an XYZ sample in the view,
	 * the final coordinate is discarded and the source XY sample is accessed.
	 *
	 * @return a view with an additional dimension
	 */
	default RaView< T, ? > addDimension()
	{
		return wrap( Views.addDimension( delegate() ) );
	}

	/**
	 * Returns a {@link RealRandomAccessible} view of this {@code RandomAccessibleInterval}
	 * using interpolation with the given method.
	 *
	 * @param factory
	 *            the {@link InterpolatorFactory} to provide interpolators
	 *
	 * @return an interpolated view
	 */
	default RraView< T > interpolate( final InterpolatorFactory< T, ? super RandomAccessible< T > > factory )
	{
		return RraView.wrap( Views.interpolate( delegate(), factory ) );
	}

	// done until here
	//////////////////






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






	// -- RandomAccessible ----------------------------------------------------

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
