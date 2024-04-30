package net.imglib2.view.fluent;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.outofbounds.OutOfBoundsZeroFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.operators.SetZero;
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

	/**
	 * Create a view that is translated by the given {@code translation} vector.
	 * <p>
	 * The pixel at coordinates <em>x</em> in this {@code RandomAccessible} has
	 * coordinates <em>(x + translation)</em> in the resulting view.
	 *
	 * @param translation
	 * 		translation vector
	 *
	 * @return a translated view
	 */
	@Override
	default RaiView< T > translate( long... translation )
	{
		return wrap( Views.translate( delegate(), translation ) );
	}

	/**
	 * Create a view that is translated by the inverse of the given {@code
	 * translation} vector.
	 * <p>
	 * The pixel at coordinates <em>x</em> in this {@code RandomAccessible} has
	 * coordinates <em>(x - translation)</em> in the resulting view.
	 *
	 * @param translation
	 * 		translation vector
	 *
	 * @return an inverse-translated view
	 */
	@Override
	default RaiView< T > translateInverse( long... translation )
	{
		return wrap( Views.translateInverse( delegate(), translation ) );
	}

	/**
	 * Create a translated view such that the min (upper left) corner is at the
	 * origin.
	 *
	 * @return a view that is translated to the origin
	 */
	default RaiView< T > zeroMin()
	{
		return wrap( Views.zeroMin( delegate() ) );
	}

	/**
	 * Sample only every <em>step<sub>d</sub></em><sup>th</sup> value of a
	 * source {@link RandomAccessible}. This is effectively an integer scaling
	 * transformation.
	 * <p>
	 * The provided {@code steps} vector is expanded or truncated to the
	 * dimensionality of this {@code RandomAccessible}. When expanding ({@code
	 * steps.length < this.numDimensions()}), the last element is repeated.
	 *
	 * @param steps
	 *            the subsampling step sizes
	 *
	 * @return a subsampled view
	 */
	@Override
	default RaiView< T > subsample( final long... steps )
	{
		return wrap( Views.subsample( delegate(), ViewUtils.getSubsampleSteps( steps, numDimensions() ) ) );
	}

	/**
	 * Create a view rotated 90 degrees, mapping {@code fromAxis} to {@code
	 * toAxis}.
	 * <p>
	 * For example, {@code fromAxis=0, toAxis=1} means that the {@code X} axis
	 * of this {@code RandomAccessibleInterval} is mapped to the {@code Y} axis
	 * of the rotated view. Correspondingly, the {@code Y} axis is mapped to
	 * {@code -X}. All other axes remain unchanged. This corresponds to a 90
	 * degree clock-wise rotation of this {@code RandomAccessibleInterval} in
	 * the {@code XY} plane.
	 * <p>
	 * Note that if this {@code RandomAccessibleInterval} has its min coordinate
	 * at the origin, the min coordinate of the rotated view will not be at the
	 * origin. To align the min coordinate of the rotated view with the origin,
	 * use {@link #zeroMin()}.
	 *
	 * @param fromAxis
	 * 		axis index
	 * @param toAxis
	 * 		axis index that {@code fromAxis} should be rotated to
	 *
	 * @return a view rotated 90 degrees
	 */
	@Override
	default RaiView< T > rotate( int fromAxis, int toAxis )
	{
		return wrap( Views.rotate( delegate(), fromAxis, toAxis ) );
	}

	/**
	 * Create a view with permuted axes where the specified {@code fromAxis} to
	 * {@code toAxis} are swapped (while all other axes remain unchanged).
	 *
	 * @param fromAxis
	 * 		axis index
	 * @param toAxis
	 * 		axis index that {@code fromAxis} should be swapped with
	 *
	 * @return a view with permuted axes
	 */
	@Override
	default RaiView< T > permute( int fromAxis, int toAxis )
	{
		return wrap( Views.permute( delegate(), fromAxis, toAxis ) );
	}

	/**
	 * Create view with permuted axes where the specified {@code fromAxis} is
	 * moved to index {@code toAxis} while the order of other axes is preserved.
	 * <p>
	 * For example, if {@code fromAxis=2, toAxis=4} and the axis order of this
	 * {@code RandomAccessibleInterval} is {@code XYCZT}, the resulting view
	 * will have the axis order {@code XYZTC}.
	 *
	 * @param fromAxis
	 * 		axis index
	 * @param toAxis
	 * 		axis index that {@code fromAxis} should be moved to
	 *
	 * @return a view with permuted axes
	 */
	@Override
	default RaiView< T > moveAxis( int fromAxis, int toAxis )
	{
		return wrap( Views.moveAxis( delegate(), fromAxis, toAxis ) );
	}

	/**
	 * Invert the {@code axis} with the given index.
	 * <p>
	 * For example, if {@code axis=1}, then coordinate {@code (x,y)} in the
	 * resulting view corresponds to coordinate {@code (x,-y)} in this {@code
	 * RandomAccessibleInterval}.
	 * <p>
	 * Note that the interval boundaries of the view are modified accordingly.
	 * If this {@code RandomAccessibleInterval} is a {@code 10x10} image with
	 * interval {@code (0,0)..(9,9)}, the interval of the view is {@code
	 * (0,-9)..(9,0)}
	 *
	 * @param axis
	 * 		the axis to invert
	 *
	 * @return a view with {@code axis} inverted
	 */
	@Override
	default RaiView< T > invertAxis( int axis )
	{
		return wrap( Views.invertAxis( delegate(), axis ) );
	}

	/**
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 */
	class Extension< T >
	{
		final OutOfBoundsFactory< T, RaiView< T > > factory;

		private Extension( OutOfBoundsFactory< T, RaiView< T > > factory )
		{
			this.factory = factory;
		}

		public static < T > Extension< T > border()
		{
			return new Extension<>(new OutOfBoundsBorderFactory<>() );
		}

		public static < T extends Type< T > & SetZero > Extension< T > zero()
		{
			return new Extension<>( new OutOfBoundsZeroFactory< T, RaiView< T > >() );
		}

		public static < T > Extension< T > value( T value )
		{
			return new Extension<>(new OutOfBoundsConstantValueFactory<>( value ) );
		}

		public static < T > Extension< T > mirrorSingle()
		{
			return new Extension<>(new OutOfBoundsMirrorFactory<>( OutOfBoundsMirrorFactory.Boundary.SINGLE ) );
		}

		public static < T > Extension< T > mirrorDouble()
		{
			return new Extension<>(new OutOfBoundsMirrorFactory<>( OutOfBoundsMirrorFactory.Boundary.DOUBLE ) );
		}

		public static < T > Extension< T > periodic()
		{
			return new Extension<>(new OutOfBoundsPeriodicFactory<>() );
		}
	}

	/**
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 */
	default RaView< T, ? > extend( Extension< T > extension )
	{
		return RaView.wrap( Views.extend( this, extension.factory ) );
	}

	/**
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 * TODO
	 */
	default RaView< T, ? > expand( Extension< T > extension, long... border )
	{
		return RaView.wrap( Views.expand( this, extension.factory, border ) );
	}


	// done until here
	//////////////////








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
