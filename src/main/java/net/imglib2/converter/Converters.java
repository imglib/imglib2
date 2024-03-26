/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealCursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.Sampler;
import net.imglib2.converter.read.BiConvertedIterableInterval;
import net.imglib2.converter.read.BiConvertedIterableRealInterval;
import net.imglib2.converter.read.BiConvertedRandomAccessible;
import net.imglib2.converter.read.BiConvertedRandomAccessibleInterval;
import net.imglib2.converter.read.BiConvertedRealRandomAccessible;
import net.imglib2.converter.read.BiConvertedRealRandomAccessibleRealInterval;
import net.imglib2.converter.read.ConvertedIterableInterval;
import net.imglib2.converter.read.ConvertedIterableRealInterval;
import net.imglib2.converter.read.ConvertedRandomAccessible;
import net.imglib2.converter.read.ConvertedRandomAccessibleInterval;
import net.imglib2.converter.read.ConvertedRealRandomAccessible;
import net.imglib2.converter.read.ConvertedRealRandomAccessibleRealInterval;
import net.imglib2.converter.readwrite.ARGBChannelSamplerConverter;
import net.imglib2.converter.readwrite.CompositeARGBSamplerConverter;
import net.imglib2.converter.readwrite.SamplerConverter;
import net.imglib2.converter.readwrite.WriteConvertedIterableInterval;
import net.imglib2.converter.readwrite.WriteConvertedIterableRandomAccessibleInterval;
import net.imglib2.converter.readwrite.WriteConvertedIterableRealInterval;
import net.imglib2.converter.readwrite.WriteConvertedRandomAccessible;
import net.imglib2.converter.readwrite.WriteConvertedRandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;
import net.imglib2.view.composite.Composite;
import net.imglib2.view.composite.NumericComposite;
import net.imglib2.view.composite.RealComposite;

/**
 * Convenience factory methods for sample conversion.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class Converters
{
	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B extends Type< B > > RandomAccessible< B > convert(
			final RandomAccessible< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return convert2( source, converter, () -> b.copy() );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A, B > RandomAccessible< B > convert2(
			final RandomAccessible< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		if ( TypeIdentity.class.isInstance( converter ) )
			return ( RandomAccessible< B > ) source;
		return new ConvertedRandomAccessible<>( source, converter, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B extends Type< B > > RandomAccessible< B > convert(
			final RandomAccessible< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return convert2( source, converterSupplier, () -> b.copy() );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B > RandomAccessible< B > convert2(
			final RandomAccessible< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return new ConvertedRandomAccessible<>( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedRandomAccessible< A, B > convert(
			final RandomAccessible< A > source,
			final SamplerConverter< ? super A, B > converter )
	{
		return new WriteConvertedRandomAccessible<>( source, converter );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converterSupplier
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedRandomAccessible< A, B > convert(
			final RandomAccessible< A > source,
			final Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		return new WriteConvertedRandomAccessible<>( source, converterSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > RandomAccessibleInterval< B > convert(
			final RandomAccessibleInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return convert2( source, converter, () -> b.copy() );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A, B > RandomAccessibleInterval< B > convert2(
			final RandomAccessibleInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		if ( TypeIdentity.class.isInstance( converter ) )
			return ( RandomAccessibleInterval< B > ) source;
		return new ConvertedRandomAccessibleInterval<>( source, converter, targetSupplier );
	}


	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > RandomAccessibleInterval< B > convert(
			final RandomAccessibleInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return convert2( source, converterSupplier, () -> b.copy() );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B > RandomAccessibleInterval< B > convert2(
			final RandomAccessibleInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return new ConvertedRandomAccessibleInterval<>( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > RandomAccessibleInterval< B > convertRAI(
			final RandomAccessibleInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return Converters.convert( source, converter, b );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert2(RandomAccessibleInterval, Converter, Supplier)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * The method signature is <code>convertRAI2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B > RandomAccessibleInterval< B > convertRAI2(
			final RandomAccessibleInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		return Converters.convert2( source, converter, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > RandomAccessibleInterval< B > convertRAI(
			final RandomAccessibleInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return Converters.convert( source, converterSupplier, b );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert2(RandomAccessibleInterval, Converter, Supplier)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * The method signature is <code>convertRAI2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B > RandomAccessibleInterval< B > convertRAI2(
			final RandomAccessibleInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return Converters.convert2( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedRandomAccessibleInterval< A, B > convert(
			final RandomAccessibleInterval< A > source,
			final SamplerConverter< ? super A, B > converter )
	{
		return new WriteConvertedRandomAccessibleInterval<>( source, converter );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converterSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedRandomAccessibleInterval< A, B > convert(
			final RandomAccessibleInterval< A > source,
			final Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		return new WriteConvertedRandomAccessibleInterval<>( source, converterSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly both when reading and writing values.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, SamplerConverter)}.
	 * The different name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedRandomAccessibleInterval< A, B > convertRAI(
			final RandomAccessibleInterval< A > source,
			final SamplerConverter< ? super A, B > converter )
	{
		return Converters.convert( source, converter );
	}

	/**
	 * Create a {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableRealInterval} has no effect.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > IterableRealInterval< B > convert(
			final IterableRealInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return convert2( source, converter, () -> b.copy() );
	}

	/**
	 * Create a {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A, B > IterableRealInterval< B > convert2(
			final IterableRealInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		if ( TypeIdentity.class.isInstance( converter ) )
			return ( IterableRealInterval< B > ) source;
		return new ConvertedIterableRealInterval<>( source, converter, targetSupplier );
	}

	/**
	 * Create a {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableRealInterval} has no effect.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > IterableRealInterval< B > convert(
			final IterableRealInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return convert2( source, converterSupplier, () -> b.copy() );
	}

	/**
	 * Create a {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B > IterableRealInterval< B > convert2(
			final IterableRealInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return new ConvertedIterableRealInterval<>( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly both when reading and writing values.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, SamplerConverter)}.
	 * The different name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * @param source
	 * @param converterSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedRandomAccessibleInterval< A, B > convertRAI(
			final RandomAccessibleInterval< A > source,
			final Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		return Converters.convert( source, converterSupplier );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > IterableInterval< B > convert(
			final IterableInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return convert2( source, converter, () -> b.copy() );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A, B > IterableInterval< B > convert2(
			final IterableInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		if ( TypeIdentity.class.isInstance( converter ) )
			return ( IterableInterval< B > ) source;
		return new ConvertedIterableInterval<>( source, converter, targetSupplier );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > IterableInterval< B > convert(
			final IterableInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return convert2( source, converterSupplier, () -> b.copy() );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B > IterableInterval< B > convert2(
			final IterableInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return new ConvertedIterableInterval<>( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create an {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedIterableInterval< A, B > convert(
			final IterableInterval< A > source,
			final SamplerConverter< ? super A, B > converter )
	{
		return new WriteConvertedIterableInterval<>( source, converter );
	}

	/**
	 * Create an {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedIterableRealInterval< A, B > convert(
			final IterableRealInterval< A > source,
			final SamplerConverter< ? super A, B > converter )
	{
		return new WriteConvertedIterableRealInterval<>( source, converter );
	}

	/**
	 * Create an {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converterSupplier
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > WriteConvertedIterableInterval< A, B > convert(
			final IterableInterval< A > source,
			final Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		return new WriteConvertedIterableInterval<>( source, converterSupplier );
	}

	/**
	 * Create an {@link WriteConvertedIterableRandomAccessibleInterval} whose
	 * {@link RandomAccess RandomAccesses} and {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @return a {@link WriteConvertedIterableRandomAccessibleInterval} whose
	 *         {@link Sampler Samplers} perform on-the-fly value conversion
	 *         using the provided converter.
	 */
	public static < A, B extends Type< B >, S extends RandomAccessible< A > & IterableInterval< A > >
			WriteConvertedIterableRandomAccessibleInterval< A, B, S > convertRandomAccessibleIterableInterval(
					final S source,
					final SamplerConverter< ? super A, B > converter )
	{
		return new WriteConvertedIterableRandomAccessibleInterval<>( source, converter );
	}

	/**
	 * Create an {@link WriteConvertedIterableRandomAccessibleInterval} whose
	 * {@link RandomAccess RandomAccesses} and {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly both when reading and writing values.
	 *
	 * @param source
	 * @param converterSupplier
	 * @return a {@link WriteConvertedIterableRandomAccessibleInterval} whose
	 *         {@link Sampler Samplers} perform on-the-fly value conversion
	 *         using the provided converter.
	 */
	public static < A, B extends Type< B >, S extends RandomAccessible< A > & IterableInterval< A > >
			WriteConvertedIterableRandomAccessibleInterval< A, B, S > convertRandomAccessibleIterableInterval(
					final S source,
					final Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		return new WriteConvertedIterableRandomAccessibleInterval<>( source, converterSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > RealRandomAccessibleRealInterval< B > convert(
			final RealRandomAccessibleRealInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return convert2( source, converter, () -> b.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A, B > RealRandomAccessibleRealInterval< B > convert2(
			final RealRandomAccessibleRealInterval< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		if ( TypeIdentity.class.isInstance( converter ) )
			return ( RealRandomAccessibleRealInterval< B > ) source;
		return new ConvertedRealRandomAccessibleRealInterval<>( source, converter, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B extends Type< B > > RealRandomAccessibleRealInterval< B > convert(
			final RealRandomAccessibleRealInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return convert2( source, converterSupplier, () -> b.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B > RealRandomAccessibleRealInterval< B > convert2(
			final RealRandomAccessibleRealInterval< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return new ConvertedRealRandomAccessibleRealInterval<>( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param b
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B extends Type< B > > RealRandomAccessible< B > convert(
			final RealRandomAccessible< A > source,
			final Converter< ? super A, ? super B > converter,
			final B b )
	{
		return convert2( source, converter, () -> b.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converter must be stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	@SuppressWarnings( "unchecked" )
	public static < A, B > RealRandomAccessible< B > convert2(
			final RealRandomAccessible< A > source,
			final Converter< ? super A, ? super B > converter,
			final Supplier< B > targetSupplier )
	{
		if ( TypeIdentity.class.isInstance( converter ) )
			return ( RealRandomAccessible< B > ) source;
		return new ConvertedRealRandomAccessible<>( source, converter, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param b
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B extends Type< B > > RealRandomAccessible< B > convert(
			final RealRandomAccessible< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final B b )
	{
		return convert2( source, converterSupplier, () -> b.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param source
	 * @param converterSupplier
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B > RealRandomAccessible< B > convert2(
			final RealRandomAccessible< A > source,
			final Supplier< Converter< ? super A, ? super B > > converterSupplier,
			final Supplier< B > targetSupplier )
	{
		return new ConvertedRealRandomAccessible<>( source, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link WriteConvertedRandomAccessibleInterval} to one of the
	 * four channels encoded in a {@link RandomAccessibleInterval} of
	 * {@link ARGBType}.  The source is being modified as expected by writing
	 * into the converted channels.
	 *
	 * @param source
	 * @param channel 0 = alpha, 1 = red, 2 = green, 3 = blue
	 *
	 * @return a converted {@link WriteConvertedRandomAccessibleInterval} whose
	 *         {@link Sampler Samplers} perform on-the-fly value conversion
	 *         into and from one channel of the original {@link ARGBType}.
	 */
	public static WriteConvertedRandomAccessibleInterval< ARGBType, UnsignedByteType > argbChannel(
			final RandomAccessibleInterval< ARGBType > source,
			final int channel )
	{
		return convert(
				source,
				new ARGBChannelSamplerConverter( channel ) );
	}

	/**
	 * Create a {@link WriteConvertedRandomAccessible} to one of the four
	 * channels encoded in a {@link RandomAccessible} of {@link ARGBType}.
	 * The source is being modified as expected by writing into the converted
	 * channels.
	 *
	 * @param source
	 * @param channel 0 = alpha, 1 = red, 2 = green, 3 = blue
	 *
	 * @return a converted {@link WriteConvertedRandomAccessible} whose
	 *         {@link Sampler Samplers} perform on-the-fly value conversion
	 *         into and from one channel of the original {@link ARGBType}.
	 */
	public static WriteConvertedRandomAccessible< ARGBType, UnsignedByteType > argbChannel(
			final RandomAccessible< ARGBType > source,
			final int channel )
	{
		return convert(
				source,
				new ARGBChannelSamplerConverter( channel ) );
	}

	/**
	 * Create an (<em>n</em>+1)-dimensional {@link RandomAccessible} of an
	 * <em>n</em>-dimensional {@link RandomAccessible} that maps the four
	 * channels encoded in {@link ARGBType} into a dimension.  The source is
	 * being modified as expected by writing into the converted channels.
	 *
	 * @param source
	 *
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link Sampler Samplers} perform on-the-fly value conversion
	 *         into and from the corresponding channels of the original
	 *         {@link ARGBType}.
	 */
	public static RandomAccessibleInterval< UnsignedByteType > argbChannels( final RandomAccessibleInterval< ARGBType > source )
	{
		return Views.stack(
				argbChannel( source, 0 ),
				argbChannel( source, 1 ),
				argbChannel( source, 2 ),
				argbChannel( source, 3 ) );
	}

	/**
	 * Create an (<em>n</em>+1)-dimensional {@link RandomAccessible} of an
	 * <em>n</em>-dimensional {@link RandomAccessible} that maps the four
	 * channels encoded in {@link ARGBType} into a dimension.  The order
	 * of the channels passed as arguments is preserved.  The source is being
	 * modified as expected by writing into the converted channels.
	 *
	 * @param source
	 * @param channels 0 = alpha, 1 = red, 2 = green, 3 = blue
	 *
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link Sampler Samplers} perform on-the-fly value conversion
	 *         into and from the corresponding channels of the original
	 *         {@link ARGBType}.
	 */
	public static RandomAccessibleInterval< UnsignedByteType > argbChannels( final RandomAccessibleInterval< ARGBType > source, final int... channels )
	{
		final ArrayList< RandomAccessibleInterval< UnsignedByteType > > hyperSlices = new ArrayList<>();
		for ( final int channel : channels )
			hyperSlices.add( argbChannel( source, channel ) );

		return Views.stack( hyperSlices );
	}

	/**
	 * Create an <em>n</em>-dimensional color image from an
	 * (<em>n</em>+1)-dimensional image of {@link UnsignedByteType}.
	 * @param source The last dimension of the image must be the color channel.
	 *               {@link Views#stack} could be used to create the source, if
	 *               there is a separate image for each color channel.
	 * @param channelOrder Order of the color channels.
	 * @return Color view to the source image that can be used for reading and writing.
	 */
	public static RandomAccessible< ARGBType > mergeARGB( final RandomAccessible< UnsignedByteType > source, final ColorChannelOrder channelOrder )
	{
		return Converters.convert( Views.collapse( source ), new CompositeARGBSamplerConverter( channelOrder ) );
	}

	/**
	 * Create an <em>n</em>-dimensional color image from an
	 * (<em>n</em>+1)-dimensional image of {@link UnsignedByteType}.
	 * @param source The last dimension of the image must be the color channel.
	 *               {@link Views#stack} could be used to create the source, if
	 *               there is a separate image for each color channel.
	 * @param channelOrder Order of the color channels.
	 * @return Color view to the source image that can be used for reading and writing.
	 */
	public static RandomAccessibleInterval< ARGBType > mergeARGB( final RandomAccessibleInterval< UnsignedByteType > source, final ColorChannelOrder channelOrder )
	{
		final int channelAxis = source.numDimensions() - 1;
		if ( source.min( channelAxis ) > 0 || source.max( channelAxis ) < channelOrder.channelCount() - 1 )
			throw new IllegalArgumentException();
		return Converters.convert( Views.collapse( source ), new CompositeARGBSamplerConverter( channelOrder ) );
	}

	/**
	 * Compose a list of same {@link Interval} and same {@link RealType} A
	 * {@link RandomAccessibleInterval RandomAccessibleIntervals} into a
	 * {@link RandomAccessibleInterval} of some target {@link Type} B using a
	 * {@link Converter} from {@link Composite} of A to B.
	 *
	 * @param components
	 * @param composer must be stateless when multi-threading
	 * @param targetType
	 * @return
	 */
	public static < A extends RealType< A >, B extends Type< B > > RandomAccessibleInterval< B > composeReal(
			final List< RandomAccessibleInterval< A > > components,
			final Converter< RealComposite< A >, B > composer,
			final B targetType )
	{
		return composeReal2(
				components,
				composer,
				() -> targetType.copy() );
	}

	/**
	 * Compose a list of same {@link Interval} and same {@link RealType} A
	 * {@link RandomAccessibleInterval RandomAccessibleIntervals} into a
	 * {@link RandomAccessibleInterval} of some target {@link Type} B using a
	 * {@link Converter} from {@link Composite} of A to B.
	 *
	 * The method signature is <code>composeReal2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param components
	 * @param composer must be stateless when multi-threading
	 * @param targetSupplier
	 * @return
	 */
	public static < A extends RealType< A >, B > RandomAccessibleInterval< B > composeReal2(
			final List< RandomAccessibleInterval< A > > components,
			final Converter< RealComposite< A >, B > composer,
			final Supplier< B > targetSupplier )
	{
		return convert2(
				Views.collapseReal( Views.stack( components ) ),
				composer,
				targetSupplier );
	}

	/**
	 * Compose a list of same {@link Interval} and same {@link NumericType} A
	 * {@link RandomAccessibleInterval RandomAccessibleIntervals} into a
	 * {@link RandomAccessibleInterval} of some target {@link Type} B using a
	 * {@link Converter} from {@link Composite} of A to B.
	 *
	 * @param components
	 * @param composer must be stateless when multi-threading
	 * @param targetType
	 * @return
	 */
	public static < A extends NumericType< A >, B extends Type< B > > RandomAccessibleInterval< B > composeNumeric(
			final List< RandomAccessibleInterval< A > > components,
			final Converter< NumericComposite< A >, B > composer,
			final B targetType )
	{
		return composeNumeric2(
				components,
				composer,
				() -> targetType.copy() );
	}

	/**
	 * Compose a list of same {@link Interval} and same {@link NumericType} A
	 * {@link RandomAccessibleInterval RandomAccessibleIntervals} into a
	 * {@link RandomAccessibleInterval} of some target {@link Type} B using a
	 * {@link Converter} from {@link Composite} of A to B.
	 *
	 * The method signature is <code>composeNumeric2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param components
	 * @param composer must be stateless when multi-threading
	 * @param targetSupplier
	 * @return
	 */
	public static < A extends NumericType< A >, B > RandomAccessibleInterval< B > composeNumeric2(
			final List< RandomAccessibleInterval< A > > components,
			final Converter< NumericComposite< A >, B > composer,
			final Supplier< B > targetSupplier )
	{
		return convert2(
				Views.collapseNumeric( Views.stack( components ) ),
				composer,
				targetSupplier );
	}

	/**
	 * Compose a list of same {@link Interval} and same {@link Type} A
	 * {@link RandomAccessibleInterval RandomAccessibleIntervals} into a
	 * {@link RandomAccessibleInterval} of some target {@link Type} B using a
	 * {@link Converter} from {@link Composite} of A to B.
	 *
	 * @param components
	 * @param composer must be stateless when multi-threading
	 * @param targetType
	 * @return
	 */
	public static < A, B extends Type< B > > RandomAccessibleInterval< B > compose(
			final List< RandomAccessibleInterval< A > > components,
			final Converter< Composite< A >, B > composer,
			final B targetType )
	{
		return compose2(
				components,
				composer,
				() -> targetType.copy() );
	}

	/**
	 * Compose a list of same {@link Interval} and same {@link Type} A
	 * {@link RandomAccessibleInterval RandomAccessibleIntervals} into a
	 * {@link RandomAccessibleInterval} of some target {@link Type} B using a
	 * {@link Converter} from {@link Composite} of A to B.
	 *
	 * The method signature is <code>compose2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param components
	 * @param composer must be stateless when multi-threading
	 * @param targetSupplier
	 * @return
	 */
	public static < A, B extends Type< B > > RandomAccessibleInterval< B > compose2(
			final List< RandomAccessibleInterval< A > > components,
			final Converter< Composite< A >, B > composer,
			final Supplier< B > targetSupplier )
	{
		return convert2(
				Views.collapse( Views.stack( components ) ),
				composer,
				targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C extends Type< C > > RandomAccessible< C > convert(
			final RandomAccessible< A > sourceA,
			final RandomAccessible< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return convert2( sourceA, sourceB, converter, () -> c.copy() );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C > RandomAccessible< C > convert2(
			final RandomAccessible< A > sourceA,
			final RandomAccessible< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRandomAccessible<>( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C extends Type< C > > RandomAccessible< C > convert(
			final RandomAccessible< A > sourceA,
			final RandomAccessible< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return convert2( sourceA, sourceB, converterSupplier, () -> c.copy() );
	}

	/**
	 * Create a {@link RandomAccessible} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessible} whose {@link RandomAccess
	 *         RandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C > RandomAccessible< C > convert2(
			final RandomAccessible< A > sourceA,
			final RandomAccessible< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRandomAccessible<>( sourceA, sourceB, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided {@link BiConverter}.
	 */
	public static < A, B, C extends Type< C > > RandomAccessibleInterval< C > convert(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return convert2( sourceA, sourceB, converter, () -> c.copy() );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided {@link BiConverter}.
	 */
	public static < A, B, C > RandomAccessibleInterval< C > convert2(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRandomAccessibleInterval<>( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided {@link BiConverter}.
	 */
	public static < A, B, C extends Type< C > > RandomAccessibleInterval< C > convert(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return convert2( sourceA, sourceB, converterSupplier, () -> c.copy() );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided {@link BiConverter}.
	 */
	public static < A, B, C > RandomAccessibleInterval< C > convert2(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRandomAccessibleInterval<>( sourceA, sourceB, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > RandomAccessibleInterval< C > convertRAI(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return Converters.convert( sourceA, sourceB, converter, c );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * The method signature is <code>convertRAI2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C > RandomAccessibleInterval< C > convertRAI2(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return Converters.convert2( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier s aupplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > RandomAccessibleInterval< C > convertRAI(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return Converters.convert( sourceA, sourceB, converterSupplier, c );
	}

	/**
	 * Create a {@link RandomAccessibleInterval} whose {@link RandomAccess
	 * RandomAccesses} {@link RandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * Delegates to {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}.
	 * The different method name avoids situations where the compiler
	 * or a scripting language interpreter picks the undesired method
	 * for an object that implements both {@link RandomAccessibleInterval}
	 * and {@link IterableInterval}.
	 *
	 * The method signature is <code>convertRAI2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier s aupplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link RandomAccessibleInterval} whose
	 *         {@link RandomAccess RandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C > RandomAccessibleInterval< C > convertRAI2(
			final RandomAccessibleInterval< A > sourceA,
			final RandomAccessibleInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return Converters.convert2( sourceA, sourceB, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > IterableInterval< C > convert(
			final IterableInterval< A > sourceA,
			final IterableInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return convert2( sourceA, sourceB, converter, () -> c.copy() );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C > IterableInterval< C > convert2(
			final IterableInterval< A > sourceA,
			final IterableInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedIterableInterval<>( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier supplies a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > IterableInterval< C > convert(
			final IterableInterval< A > sourceA,
			final IterableInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return convert2( sourceA, sourceB, converterSupplier, () -> c.copy() );
	}

	/**
	 * Create a {@link IterableInterval} whose {@link Cursor Cursors}
	 * {@link Cursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier supplies a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link IterableInterval} whose {@link Cursor Cursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C > IterableInterval< C > convert2(
			final IterableInterval< A > sourceA,
			final IterableInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedIterableInterval<>( sourceA, sourceB, converterSupplier, targetSupplier );
	}

	/**
	 * Create an {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableRealInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > IterableRealInterval< C > convert(
			final IterableRealInterval< A > sourceA,
			final IterableRealInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return convert2( sourceA, sourceB, converter, () -> c.copy() );
	}

	/**
	 * Create an {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C > IterableRealInterval< C > convert2(
			final IterableRealInterval< A > sourceA,
			final IterableRealInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedIterableRealInterval<>( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier supplies a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > IterableRealInterval< C > convert(
			final IterableRealInterval< A > sourceA,
			final IterableRealInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return convert2( sourceA, sourceB, converterSupplier, () -> c.copy() );
	}

	/**
	 * Create a {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 * {@link RealCursor#get()} you a converted sample. Conversion is done
	 * on-the-fly when reading values. Writing to the converted
	 * {@link IterableInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier supplies a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link IterableRealInterval} whose {@link RealCursor RealCursors}
	 *         perform on-the-fly value conversion using the provided converter.
	 */
	public static < A, B, C > IterableRealInterval< C > convert2(
			final IterableRealInterval< A > sourceA,
			final IterableRealInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedIterableRealInterval<>( sourceA, sourceB, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > RealRandomAccessibleRealInterval< C > convert(
			final RealRandomAccessibleRealInterval< A > sourceA,
			final RealRandomAccessibleRealInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return convert2( sourceA, sourceB, converter, () -> c.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C > RealRandomAccessibleRealInterval< C > convert2(
			final RealRandomAccessibleRealInterval< A > sourceA,
			final RealRandomAccessibleRealInterval< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRealRandomAccessibleRealInterval<>( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C extends Type< C > > RealRandomAccessibleRealInterval< C > convert(
			final RealRandomAccessibleRealInterval< A > sourceA,
			final RealRandomAccessibleRealInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return convert2( sourceA, sourceB, converterSupplier, () -> c.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessibleRealInterval} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RealRandomAccessibleRealInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessibleRealInterval} whose
	 *         {@link RealRandomAccess RealRandomAccesses} perform on-the-fly value
	 *         conversion using the provided converter.
	 */
	public static < A, B, C > RealRandomAccessibleRealInterval< C > convert2(
			final RealRandomAccessibleRealInterval< A > sourceA,
			final RealRandomAccessibleRealInterval< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRealRandomAccessibleRealInterval<>( sourceA, sourceB, converterSupplier, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param c
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C extends Type< C > > RealRandomAccessible< C > convert(
			final RealRandomAccessible< A > sourceA,
			final RealRandomAccessible< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final C c )
	{
		return convert2( sourceA, sourceB, converter, () -> c.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converter a two variable function into a preallocated output,
	 *     e.g. {@code (a, b, c) -> c.set(a.get() + b.get())} must be
	 *     stateless when multi-threading
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C > RealRandomAccessible< C > convert2(
			final RealRandomAccessible< A > sourceA,
			final RealRandomAccessible< B > sourceB,
			final BiConverter< ? super A, ? super B, ? super C > converter,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRealRandomAccessible<>( sourceA, sourceB, converter, targetSupplier );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param c
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C extends Type< C > > RealRandomAccessible< C > convert(
			final RealRandomAccessible< A > sourceA,
			final RealRandomAccessible< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final C c )
	{
		return convert2( sourceA, sourceB, converterSupplier, () -> c.copy() );
	}

	/**
	 * Create a {@link RealRandomAccessible} whose {@link RealRandomAccess
	 * RealRandomAccesses} {@link RealRandomAccess#get()} you a converted sample.
	 * Conversion is done on-the-fly when reading values. Writing to the
	 * converted {@link RandomAccessibleInterval} has no effect.
	 *
	 * The method signature is <code>convert2</code> because many Java
	 * compilers cannot resolve the correct overload when the target
	 * {@link Supplier} is passed as a lambda and cause spurious and
	 * seemingly false ambiguous method errors.
	 *
	 * @param sourceA
	 * @param sourceB
	 * @param converterSupplier a supplier of a two variable function into a
	 * 		preallocated output, e.g.
	 * 		{@code () -> (a, b, c) -> c.set(a.get() + b.get())}
	 * @param targetSupplier
	 * @return a converted {@link RealRandomAccessible} whose {@link RealRandomAccess
	 *         RealRandomAccesses} perform on-the-fly value conversion using the
	 *         provided converter.
	 */
	public static < A, B, C > RealRandomAccessible< C > convert2(
			final RealRandomAccessible< A > sourceA,
			final RealRandomAccessible< B > sourceB,
			final Supplier< BiConverter< ? super A, ? super B, ? super C > > converterSupplier,
			final Supplier< C > targetSupplier )
	{
		return new BiConvertedRealRandomAccessible<>( sourceA, sourceB, converterSupplier, targetSupplier );
	}
}
