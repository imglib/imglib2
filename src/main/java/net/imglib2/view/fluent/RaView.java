package net.imglib2.view.fluent;

import java.util.function.Function;
import java.util.function.Supplier;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.ClampingNLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.LanczosInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
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
 * {@code RandomAccessible< IntType > view =
 *                img.view()
 *                   .extend( Extension.mirrorSingle() )
 *                   .permute( 0, 1 )
 *                   .translate( 10, 10 );
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
	default RaView< T, ? > translate( long... translation )
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
	default RaView< T, ? > translateInverse( long... translation )
	{
		return wrap( Views.translateInverse( delegate(), translation ) );
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
	 * 		the subsampling step sizes
	 *
	 * @return a subsampled view
	 */
	default RaView< T, ? > subsample( final long... steps )
	{
		return wrap( Views.subsample( delegate(), Util.expandArray( steps, numDimensions() ) ) );
	}

	/**
	 * Create a view rotated 90 degrees, mapping {@code fromAxis} to {@code
	 * toAxis}.
	 * <p>
	 * For example, {@code fromAxis=0, toAxis=1} means that the {@code X} axis
	 * of this {@code RandomAccessible} is mapped to the {@code Y} axis of the
	 * rotated view. Correspondingly, the {@code Y} axis is mapped to {@code
	 * -X}. All other axes remain unchanged. This corresponds to a 90 degree
	 * clock-wise rotation of this {@code RandomAccessible} in the {@code XY}
	 * plane.
	 *
	 * @param fromAxis
	 * 		axis index
	 * @param toAxis
	 * 		axis index that {@code fromAxis} should be rotated to
	 *
	 * @return a view rotated 90 degrees
	 */
	default RaView< T, ? > rotate( int fromAxis, int toAxis )
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
	default RaView< T, ? > permute( int fromAxis, int toAxis )
	{
		return wrap( Views.permute( delegate(), fromAxis, toAxis ) );
	}

	/**
	 * Create a view with permuted axes where the specified {@code fromAxis} is
	 * moved to index {@code toAxis} while the order of other axes is preserved.
	 * <p>
	 * For example, if {@code fromAxis=2, toAxis=4} and the axis order of this
	 * {@code RandomAccessible} is {@code XYCZT}, the resulting view will have
	 * the axis order {@code XYZTC}.
	 *
	 * @param fromAxis
	 * 		axis index
	 * @param toAxis
	 * 		axis index that {@code fromAxis} should be moved to
	 *
	 * @return a view with permuted axes
	 */
	default RaView< T, ? > moveAxis( int fromAxis, int toAxis )
	{
		return wrap( Views.moveAxis( delegate(), fromAxis, toAxis ) );
	}

	/**
	 * Invert the {@code axis} with the given index.
	 * <p>
	 * For example, if {@code axis=1}, then coordinate {@code (x,y,z)} in the
	 * resulting view corresponds to coordinate {@code (x,-y,z)} in this {@code
	 * RandomAccessible}.
	 *
	 * @param axis
	 * 		the axis to invert
	 *
	 * @return a view with {@code axis} inverted
	 */
	default RaView< T, ? > invertAxis( int axis )
	{
		return wrap( Views.invertAxis( delegate(), axis ) );
	}

	/**
	 * Interpolation method to use with {@link #interpolate}. {@code
	 * Interpolation} instances can be created using {@link #nearestNeighbor},
	 * {@link #nLinear}, {@link #clampingNLinear}, or {@link #lanczos}.
	 * <p>
	 * Usage example:
	 * <pre>
	 * {@code
	 * RealRandomAccessible<IntType> interpolated =
	 *                img.view()
	 *                   .extend( Extension.zero() )
	 *                   .interpolate( Interpolation.lanczos() );
	 * }
	 * </pre>
	 *
	 * @param <T>
	 *     pixel type ot the {@code RandomAccessible} to be interpolated
	 */
	class Interpolation< T >
	{
		final InterpolatorFactory< T, ? super RandomAccessible< T > > factory;

		private Interpolation( InterpolatorFactory< T, ? super RandomAccessible< T > > factory )
		{
			this.factory = factory;
		}

		/**
		 * Create {@code Interpolation} using {@link NearestNeighborInterpolatorFactory}.
		 */
		public static < T > Interpolation< T > nearestNeighbor()
		{
			return new Interpolation<>( new NearestNeighborInterpolatorFactory<>() );
		}

		/**
		 * Create {@code Interpolation} using {@link NLinearInterpolatorFactory}.
		 */
		public static < T extends NumericType< T > > Interpolation< T > nLinear()
		{
			return new Interpolation< T >( new NLinearInterpolatorFactory<>() );
		}

		/**
		 * Create {@code Interpolation} using {@link ClampingNLinearInterpolatorFactory}.
		 */
		public static < T extends NumericType< T > > Interpolation< T > clampingNLinear()
		{
			return new Interpolation< T >( new ClampingNLinearInterpolatorFactory<>() );
		}

		/**
		 * Create {@code Interpolation} using {@link LanczosInterpolatorFactory}.
		 */
		public static < T extends RealType< T > > Interpolation< T > lanczos()
		{
			return new Interpolation< T >( new LanczosInterpolatorFactory<>() );
		}
	}

	/**
	 * Create a {@link RealRandomAccessible} view of this {@code RandomAccessible}
	 * using interpolation with the given {@code interpolation} method.
	 * <p>
	 * {@link Interpolation} can be created by one of its static factory
	 * methods. For example
	 * <pre>
	 * {@code
	 * RealRandomAccessible< IntType > interpolated =
	 *                img.view()
	 *                   .extend( Extension.zero() )
	 *                   .interpolate( Interpolation.lanczos() );
	 * }
	 * </pre>
	 *
	 * @param interpolation
	 * 		the {@link Interpolation} method
	 *
	 * @return an interpolated view
	 */
	default RraView< T > interpolate( final Interpolation< T > interpolation )
	{
		return RraView.wrap( Views.interpolate( delegate(), interpolation.factory ) );
	}

	/**
	 * Create a view of this {@code RandomAccessible} converted to pixel type
	 * {@code U}.
	 * <p>
	 * Pixel values {@code T} are converted to {@code U} using the given {@code
	 * converter}. A {@code Converter} is equivalent to a {@code BiConsumer<T,
	 * U>} that reads a value from its first argument and writes a converted
	 * value to its second argument.
	 *
	 * @param converter
	 * 		converts pixel values from {@code T} to {@code U}
	 * @param targetSupplier
	 * 		creates instances of {@code U} for storing converted values
	 * @param <U>
	 * 		target pixel type
	 *
	 * @return a converted view
	 */
	default < U > RaView< U, ? > convert(
			final Converter< ? super T, ? super U > converter,
			final Supplier< U > targetSupplier )
	{
		return wrap( Converters.convert2( delegate(), converter, targetSupplier ) );
	}

	/**
	 * Create a view of this {@code RandomAccessible} converted to pixel type
	 * {@code U}.
	 * <p>
	 * Pixel values {@code T} are converted to {@code U} using {@code
	 * Converter}s created by the given {@code converterSupplier}. A {@code
	 * Converter} is equivalent to a {@code BiConsumer<T, U>} that reads a value
	 * from its first argument and writes a converted value to its second
	 * argument.
	 *
	 * @param converterSupplier
	 * 		converts pixel values from {@code T} to {@code U}
	 * @param targetSupplier
	 * 		creates instances of {@code U} for storing converted values
	 * @param <U>
	 * 		target pixel type
	 *
	 * @return a converted view
	 */
	default < U > RaView< U, ? > convert(
			final Supplier< Converter< ? super T, ? super U > > converterSupplier,
			final Supplier< U > targetSupplier )
	{
		return wrap( Converters.convert2( delegate(), converterSupplier, targetSupplier ) );
	}


	// done until here
	//////////////////



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
