/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.view.Views;

/**
 * Gateway for creating light-weight views on a {@code RealRandomAccessible}.
 * <p>
 * A view is itself a {@code RealRandomAccessible} or {@code RandomAccessible}
 * whose accessors transform coordinates and/or values on-the-fly without
 * copying the underlying data. Consecutive transformations are concatenated and
 * simplified to provide optimally efficient accessors.
 * <p>
 * The {@code RealRandomAccessibleView} gateway implements {@code
 * RealRandomAccessible}, forwarding all methods to its {@link #delegate}.
 * Additionally, it provides methods analogous to the {@code static} {@link
 * Views} methods that operate on its {@link #delegate} and return {@code
 * RandomAccessibleIntervalView}, {@code RandomAccessibleView}, or {@code
 * RealRandomAccessibleView} wrappers.
 * <p>
 * This provides a fluent API for conveniently chaining {@code Views} methods.
 * For example
 * <pre>
 * {@code RealRandomAccessible< IntType > view =
 *                img.view()
 *                   .extend( Extension.border() )
 *                   .interpolate( Interpolation.nLinear() );
 * }
 * </pre>
 *
 * @author Tobias Pietzsch
 * @author Michael Innerberger
 * @see Views
 */
public interface RealRandomAccessibleView< T, V extends RealRandomAccessibleView< T, V > > extends RealRandomAccessible< T >
{
	RealRandomAccessible< T > delegate();

	static < T, V extends RealRandomAccessibleView< T, V >> RealRandomAccessibleView< T, ? > wrap( final RealRandomAccessible< T > delegate )
	{
		return (RealRandomAccessibleView< T, V >) () -> delegate;
	}

	// -- Views methods -------------------------------------------------------

	/**
	 * Create a rasterized {@code RandomAccessible} view of this {@code
	 * RealRandomAccessible} by providing {@code RandomAccess} at integer
	 * coordinates.
	 *
	 * @return a rasterized view
	 */
	default RandomAccessibleView< T, ? > raster()
	{
		return RandomAccessibleView.wrap( Views.raster( delegate() ) );
	}

	/**
	 * Create a view of this {@code RealRandomAccessible} converted to pixel
	 * type {@code U}.
	 * <p>
	 * Pixel values {@code T} are converted to {@code U} using the given {@code
	 * converter}. A {@code Converter} is equivalent to a {@code BiConsumer<T,
	 * U>} that reads a value from its first argument and writes a converted
	 * value to its second argument.
	 *
	 * @param targetSupplier
	 * 		creates instances of {@code U} for storing converted values
	 * @param converter
	 * 		converts pixel values from {@code T} to {@code U}
	 * @param <U>
	 * 		target pixel type
	 *
	 * @return a converted view
	 */
	default < U > RealRandomAccessibleView< U, ? > convert(
			final Supplier< U > targetSupplier,
			final Converter< ? super T, ? super U > converter)
	{
		return wrap( Converters.convert2( delegate(), converter, targetSupplier ) );
	}

	/**
	 * Create a view of this {@code RealRandomAccessible} converted to pixel
	 * type {@code U}.
	 * <p>
	 * Pixel values {@code T} are converted to {@code U} using {@code
	 * Converter}s created by the given {@code converterSupplier}. A {@code
	 * Converter} is equivalent to a {@code BiConsumer<T, U>} that reads a value
	 * from its first argument and writes a converted value to its second
	 * argument.
	 *
	 * @param targetSupplier
	 * 		creates instances of {@code U} for storing converted values
	 * @param converterSupplier
	 * 		converts pixel values from {@code T} to {@code U}
	 * @param <U>
	 * 		target pixel type
	 *
	 * @return a converted view
	 */
	default < U > RealRandomAccessibleView< U, ? > convert(
			final Supplier< U > targetSupplier,
			final Supplier< Converter< ? super T, ? super U > > converterSupplier)
	{
		return wrap( Converters.convert2( delegate(), converterSupplier, targetSupplier ) );
	}

	/**
	 * Apply the specified {@code function} to this {@code RealRandomAccessible}
	 * and return the result.
	 *
	 * @param function
	 * 		function to evaluate on this {@code RealRandomAccessible}
	 * @param <U>
	 * 		the type of the result of the function
	 *
	 * @return {@code function.apply(this)}
	 */
	default < U > U use( Function< ? super V, U > function )
	{
		return function.apply( (V) this );
	}


	// -- RealRandomAccessible ------------------------------------------------

	@Override
	default RealRandomAccessibleView< T, ? > realView()
	{
		return this;
	}

	@Override
	default T getType()
	{
		return delegate().getType();
	}

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
