package net.imglib2.view.fluent;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * Gateway for creating light-weight views on a {@code RandomAccessibleInterval}.
 * <p>
 * A view is itself a {@code RandomAccessible} or {@code
 * RandomAccessibleInterval} whose accessors transform coordinates and/or
 * values on-the-fly without copying the underlying data. Consecutive
 * transformations are concatenated and simplified to provide optimally
 * efficient accessors.
 * <p>
 * The {@code RaiView} gateway implements {@code RandomAccessibleInterval},
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
public interface RaiView< T > extends RaView< T, RaiView< T > >, RandomAccessibleInterval< T >
{
	@Override
	RandomAccessibleInterval< T > delegate();

	static < T > RaiView< T > wrap( final RandomAccessibleInterval< T > delegate )
	{
		return new RaiWrapper<>( delegate );
	}

	// -- Views methods -------------------------------------------------------

	/**
	 * Enforce {@link FlatIterationOrder} order for this {@code
	 * RandomAccessibleInterval}.
	 * <p>
	 * If this passed {@code RandomAccessibleInterval} already has flat
	 * iteration order then it is returned directly. If not, then it is wrapped
	 * in a {@link IterableRandomAccessibleInterval}.
	 *
	 * @return a view with flat iteration order
	 */
	default RaiView< T > flatIterable()
	{
		return wrap( Views.flatIterable( delegate() ) );
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
	@Override
	default RaiView< T > slice( int d, long pos )
	{
		return wrap( Views.hyperSlice( delegate(), d, pos ) );
	}

	/**
	 * Create a <em>(n+1)</em>-dimensional view of this <em>n</em>-dimensional
	 * {@code RandomAccessibleInterval}, by replicating values along the added
	 * axis.
	 * <p>
	 * The additional dimension is the last dimension. For example, an XYZ view
	 * is created for an XY source. When accessing an XYZ sample in the view,
	 * the final coordinate is discarded and the source XY sample is accessed.
	 *
	 * @param minOfNewDim
	 * 		interval min in the added dimension.
	 * @param maxOfNewDim
	 * 		interval max in the added dimension.
	 *
	 * @return a view with an additional dimension
	 */
	default RaiView< T > addDimension( long minOfNewDim, long maxOfNewDim )
	{
		return wrap( Views.addDimension( delegate(), minOfNewDim, maxOfNewDim ) );
	}



	// done until here
	//////////////////



	default RaiView< T > expandValue( final T value, long... border )
	{
		return wrap( Views.expandValue( delegate(), value, border ) );
	}

	default RaiView< T > permute( final int from, final int to )
	{
		return wrap( Views.permute( delegate(), from, to ) );
	}

	default RaiView< T > translate( long... translation )
	{
		return wrap( Views.translate( delegate(), translation ) );
	}

	default RaiView< T >  zeroMin()
	{
		return wrap( Views.zeroMin( delegate() ) );
	}

	default RaiView< T > rotate( int fromAxis, int toAxis )
	{
		return wrap( Views.rotate( delegate(), fromAxis, toAxis ) );
	}

	default RaView< T, ? > extendBorder()
	{
		return RaView.wrap( Views.extendBorder( delegate() ) );
	}







	// -- RandomAccessible ----------------------------------------------------

	@Override
	default RaiView< T > view()
	{
		return this;
	}

	@Override
	default T getType()
	{
		return delegate().getType();
	}

	// TODO: Delegate all methods of RandomAccessibleInterval, also those that
	//       have a default implementations ...

	@Override
	default int numDimensions()
	{
		return delegate().numDimensions();
	}

	@Override
	default long min( final int d )
	{
		return delegate().min( d );
	}

	@Override
	default long max( final int d )
	{
		return delegate().max( d );
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

	@Override
	default Cursor< T > cursor()
	{
		return delegate().cursor();
	}

	@Override
	default Cursor< T > localizingCursor()
	{
		return delegate().localizingCursor();
	}

	@Override
	default long size()
	{
		return delegate().size();
	}

	@Override
	default Object iterationOrder()
	{
		return delegate().iterationOrder();
	}
}
