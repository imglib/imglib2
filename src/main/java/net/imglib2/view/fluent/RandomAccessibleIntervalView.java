/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.view.fluent;

import java.util.function.Function;
import java.util.function.Supplier;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.outofbounds.OutOfBoundsZeroFactory;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.Type;
import net.imglib2.type.operators.SetZero;
import net.imglib2.util.Util;
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
 * The {@code RandomAccessibleIntervalView} gateway implements {@code
 * RandomAccessibleInterval}, forwarding all methods to its {@link #delegate}.
 * Additionally, it provides methods analogous to the {@code static} {@link
 * Views} methods that operate on its {@link #delegate} and return {@code
 * RandomAccessibleIntervalView}, {@code RandomAccessibleView}, or {@code
 * RealRandomAccessibleView} wrappers.
 * <p>
 * This provides a fluent API for conveniently chaining {@code Views} methods.
 * For example
 * <pre>
 * {@code RandomAccessibleInterval< IntType > view =
 *                img.view()
 *                   .permute( 0, 1 )
 *                   .expand( Extension.border(), 5 )
 *                   .zeroMin();
 * }
 * </pre>
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RandomAccessibleIntervalView< T > extends RandomAccessibleView< T, RandomAccessibleIntervalView< T > >, RandomAccessibleInterval< T >
{
	@Override
	RandomAccessibleInterval< T > delegate();

	static < T > RandomAccessibleIntervalView< T > wrap( final RandomAccessibleInterval< T > delegate )
	{
		return () -> delegate;
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
	default RandomAccessibleIntervalView< T > flatIterable()
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
	default RandomAccessibleIntervalView< T > slice( int d, long pos )
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
	default RandomAccessibleIntervalView< T > addDimension( long minOfNewDim, long maxOfNewDim )
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
	default RandomAccessibleIntervalView< T > translate( long... translation )
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
	default RandomAccessibleIntervalView< T > translateInverse( long... translation )
	{
		return wrap( Views.translateInverse( delegate(), translation ) );
	}

	/**
	 * Create a translated view such that the min (upper left) corner is at the
	 * origin.
	 *
	 * @return a view that is translated to the origin
	 */
	default RandomAccessibleIntervalView< T > zeroMin()
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
	default RandomAccessibleIntervalView< T > subsample( final long... steps )
	{
		return wrap( Views.subsample( delegate(), Util.expandArray( steps, numDimensions() ) ) );
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
	default RandomAccessibleIntervalView< T > rotate( int fromAxis, int toAxis )
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
	default RandomAccessibleIntervalView< T > permute( int fromAxis, int toAxis )
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
	default RandomAccessibleIntervalView< T > moveAxis( int fromAxis, int toAxis )
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
	default RandomAccessibleIntervalView< T > invertAxis( int axis )
	{
		return wrap( Views.invertAxis( delegate(), axis ) );
	}

	/**
	 * Extension method to use with {@link #extend} and {@link #expand}. {@code
	 * Extension} instances can be created using static methods {@link #border},
	 * {@link #value}, {@link #mirrorSingle}, etc.
	 * <p>
	 * Usage example:
	 * <pre>
	 * {@code
	 * RandomAccessible<IntType> extended =
	 *                img.view()
	 *                   .extend(Extension.border());
	 * }
	 * </pre>
	 *
	 * @param <T>
	 *     pixel type ot the {@code RandomAccessible} to be extended
	 */
	class Extension< T >
	{
		final OutOfBoundsFactory< T, RandomAccessibleIntervalView< T > > factory;

		private Extension( OutOfBoundsFactory< T, RandomAccessibleIntervalView< T > > factory )
		{
			this.factory = factory;
		}

		/**
		 * Create {@code Extension} using {@link OutOfBoundsBorderFactory}.
		 * <p>
		 * Out-of-bounds pixels are created by repeating border pixels.
		 */
		public static < T > Extension< T > border()
		{
			return new Extension<>(new OutOfBoundsBorderFactory<>() );
		}

		/**
		 * Create {@code Extension} using {@link OutOfBoundsZeroFactory}.
		 * <p>
		 * All out-of-bounds pixels have value zero.
		 */
		public static < T extends Type< T > & SetZero > Extension< T > zero()
		{
			return new Extension<>( new OutOfBoundsZeroFactory< T, RandomAccessibleIntervalView< T > >() );
		}

		/**
		 * Create {@code Extension} using {@link OutOfBoundsConstantValueFactory}.
		 * <p>
		 * All out-of-bounds pixels have the provided {@code value}.
		 */
		public static < T > Extension< T > value( T value )
		{
			return new Extension<>(new OutOfBoundsConstantValueFactory<>( value ) );
		}

		/**
		 * Create {@code Extension} using {@link OutOfBoundsMirrorFactory}.
		 * <p>
		 * Out-of-bounds pixels are created by mirroring, where boundary pixels
		 * are not repeated. Note that this requires that all dimensions of the
		 * source must be &gt; 1.
		 */
		public static < T > Extension< T > mirrorSingle()
		{
			return new Extension<>(new OutOfBoundsMirrorFactory<>( OutOfBoundsMirrorFactory.Boundary.SINGLE ) );
		}

		/**
		 * Create {@code Extension} using {@link OutOfBoundsMirrorFactory}.
		 * <p>
		 * Out-of-bounds pixels are created by mirroring, where boundary pixels
		 * are repeated.
		 */
		public static < T > Extension< T > mirrorDouble()
		{
			return new Extension<>(new OutOfBoundsMirrorFactory<>( OutOfBoundsMirrorFactory.Boundary.DOUBLE ) );
		}

		/**
		 * Create {@code Extension} using {@link OutOfBoundsPeriodicFactory}.
		 * <p>
		 * Out-of-bounds pixels are created by periodically repeating the source
		 * image.
		 */
		public static < T > Extension< T > periodic()
		{
			return new Extension<>(new OutOfBoundsPeriodicFactory<>() );
		}
	}

	/**
	 * Create an unbounded {@link RandomAccessible} view of this {@code
	 * RandomAccessibleInterval} using out-of-bounds extension with the given
	 * {@code extension} method.
	 * <p>
	 * {@link Extension} can be created by one of its static factory methods.
	 * For example
	 * <pre>
	 * {@code
	 * RandomAccessible<IntType> extended =
	 *                img.view()
	 *                   .extend(Extension.border());
	 * }
	 * </pre>
	 *
	 * @param extension
	 *            the out-of-bounds strategy to use
	 *
	 * @return an extended (unbounded) view
	 */
	default RandomAccessibleView< T, ? > extend( Extension< T > extension )
	{
		return RandomAccessibleView.wrap( Views.extend( this, extension.factory ) );
	}

	/**
	 * Create an expanded view of this {@code RandomAccessibleInterval} that
	 * using out-of-bounds extension with the given {@code extension} method.
	 * <p>
	 * The provided {@code border} vector is expanded or truncated to the
	 * dimensionality of this {@code RandomAccessibleInterval}. When expanding
	 * ({@code border.length < this.numDimensions()}), the last element is
	 * repeated.
	 * <p>
	 * For example
	 * <pre>
	 * {@code
	 * // expand by 10 pixels in every dimension
	 * RandomAccessibleInterval<IntType> expanded =
	 *                img.view()
	 *                   .expand(Extension.border(), 5);
	 *
     * // expand by 5 pixels in X and Y, don't expand higher dimensions
	 * RandomAccessibleInterval<IntType> expanded =
	 *                img.view()
	 *                   .expand(Extension.border(), 5, 5, 0);
	 * }
	 * </pre>
	 *
	 * @param extension
	 *            the out-of-bounds strategy to use
	 * @param border
	 *            the border to add to the image
	 *
	 * @return an expanded view
	 */
	default RandomAccessibleIntervalView< T > expand( Extension< T > extension, long... border )
	{
		return RandomAccessibleIntervalView.wrap( Views.expand( this, extension.factory, Util.expandArray( border, numDimensions() ) ) );
	}

	/**
	 * Create a view of this {@code RandomAccessibleInterval} converted to pixel
	 * type {@code U}.
	 * <p>
	 * Pixel values {@code T} are converted to {@code U} using the given {@code
	 * converter}. A {@code Converter} is equivalent to a {@code BiConsumer<T,
	 * U>} that reads a value from its first argument and writes a converted
	 * value to its second argument.
	 *
	 * @param <U>
	 * 		target pixel type
	 * @param targetSupplier
	 * 		creates instances of {@code U} for storing converted values
	 * @param converter
	 * 		converts pixel values from {@code T} to {@code U}
	 *
	 * @return a converted view
	 */
	@Override
	default < U > RandomAccessibleIntervalView< U > convert(
			final Supplier< U > targetSupplier, final Converter< ? super T, ? super U > converter )
	{
		return wrap( Converters.convert2( delegate(), converter, targetSupplier ) );
	}

	/**
	 * Create a view of this {@code RandomAccessibleInterval} converted to pixel
	 * type {@code U}.
	 * <p>
	 * Pixel values {@code T} are converted to {@code U} using {@code
	 * Converter}s created by the given {@code converterSupplier}. A {@code
	 * Converter} is equivalent to a {@code BiConsumer<T, U>} that reads a value
	 * from its first argument and writes a converted value to its second
	 * argument.
	 *
	 * @param <U>
	 * 		target pixel type
	 * @param targetSupplier
	 * 		creates instances of {@code U} for storing converted values
	 * @param converterSupplier
	 * 		converts pixel values from {@code T} to {@code U}
	 *
	 * @return a converted view
	 */
	@Override
	default < U > RandomAccessibleIntervalView< U > convert(
			final Supplier< U > targetSupplier, final Supplier< Converter< ? super T, ? super U > > converterSupplier )
	{
		return wrap( Converters.convert2( delegate(), converterSupplier, targetSupplier ) );
	}

	/**
	 * Apply the specified {@code function} to this {@code
	 * RandomAccessibleInterval} and return the result.
	 *
	 * @param function
	 * 		function to evaluate on this {@code RandomAccessibleInterval}
	 * @param <U>
	 * 		the type of the result of the function
	 *
	 * @return {@code function.apply(this)}
	 */
	@Override
	default < U > U use( Function< ? super RandomAccessibleIntervalView< T >, U > function )
	{
		return function.apply( this );
	}


	// -- RandomAccessibleInterval --------------------------------------------

	@Override
	default RandomAccessibleIntervalView< T > view()
	{
		return this;
	}

	@Override
	default T getType()
	{
		return delegate().getType();
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
	default long dimension( final int d )
	{
		return delegate().dimension( d );
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
	default LocalizableSpliterator< T > spliterator()
	{
		return delegate().spliterator();
	}

	@Override
	default LocalizableSpliterator< T > localizingSpliterator()
	{
		return delegate().localizingSpliterator();
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
