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

package net.imglib2.util;

import java.math.BigInteger;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Utility class for wrapping ImgLib2 images as read-only {@link Collection}s.
 * <p>
 * This is useful when you want to pass an ImgLib2 {@link IterableInterval} or
 * {@link RandomAccessibleInterval} to an API that works with Java
 * {@link Collection} objects, such as Google Guava's <a href=
 * "https://javadoc.scijava.org/Guava/?com/google/common/math/Quantiles.html">Quantiles</a>.
 *
 * @author Curtis Rueden
 */
public final class FlatCollections
{

	/**
	 * Wraps an {@link IterableInterval} as a {@link Collection}. The wrapped
	 * collection is read-only, throwing {@link UnsupportedOperationException}
	 * if the caller attempts to mutate it.
	 * <p>
	 * With this method, and <em>unlike
	 * {@link #list(RandomAccessibleInterval, Function)}</em>, the iteration
	 * order of the wrapped collection will <em>match that of the source
	 * image</em>. It is best not to make any assumptions about the iteration
	 * order of the collection&mdash;only that each element of the wrapped image
	 * will appear once in the iteration.
	 * </p>
	 * 
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @param converter
	 *            Conversion function to use for accessing elements of the
	 *            collection. This function will be transparently called on the
	 *            corresponding source sample from the ImgLib2 image.
	 * @return Wrapped {@link Collection} of the converted type.
	 */
	public static < T, E > Collection< E > collection( final IterableInterval< T > image, final Function< T, E > converter )
	{
		return new CollectionFromII<>( image, converter );
	}

	/**
	 * Wraps a {@link BooleanType} iterable image as a collection.
	 * <p>
	 * Warning: Don't make any assumption on the iteration order,
	 * only that each pixel will appear once. Performance may be
	 * low as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @return Wrapped {@link Collection} with {@link Boolean} elements.
	 * @see #collection(IterableInterval, Function)
	 */
	public static Collection< Boolean > booleanCollection( final IterableInterval< ? extends BooleanType< ? > > image )
	{
		return collection( image, t -> t.get() );
	}

	/**
	 * Wraps a {@link RealType} iterable image as a collection.
	 * <p>
	 * Warning: Don't make any assumption on the iteration order,
	 * only that each pixel will appear once. Performance may be
	 * low as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @return Wrapped {@link Collection} with {@link Double} elements.
	 * @see #collection(IterableInterval, Function)
	 */
	public static Collection< Double > doubleCollection( final IterableInterval< ? extends RealType< ? > > image )
	{
		return collection( image, t -> t.getRealDouble() );
	}

	/**
	 * Wraps a {@link RealType} iterable image as a collection.
	 * <p>
	 * Warning: Don't make any assumption on the iteration order,
	 * only that each pixel will appear once. Performance may be
	 * low as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @return Wrapped {@link Collection} with {@link Float} elements.
	 * @see #collection(IterableInterval, Function)
	 */
	public static Collection< Float > floatCollection( final IterableInterval< ? extends RealType< ? > > image )
	{
		return collection( image, t -> t.getRealFloat() );
	}

	/**
	 * Wraps an {@link IntegerType} iterable image as a collection.
	 * <p>
	 * Warning: Don't make any assumption on the iteration order,
	 * only that each pixel will appear once. Performance may be
	 * low as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @return Wrapped {@link Collection} with {@link Integer} elements.
	 * @see #collection(IterableInterval, Function)
	 */
	public static Collection< Integer > integerCollection( final IterableInterval< ? extends IntegerType< ? > > image )
	{
		return collection( image, t -> t.getInteger() );
	}

	/**
	 * Wraps an {@link IntegerType} iterable image as a collection.
	 * <p>
	 * Warning: Don't make any assumption on the iteration order,
	 * only that each pixel will appear once. Performance may be
	 * low as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @return Wrapped {@link Collection} with {@link Long} elements.
	 * @see #collection(IterableInterval, Function)
	 */
	public static Collection< Long > longCollection( final IterableInterval< ? extends IntegerType< ? > > image )
	{
		return collection( image, t -> t.getIntegerLong() );
	}

	/**
	 * Wraps an {@link IntegerType} iterable image as a collection.
	 * <p>
	 * Warning: Don't make any assumption on the iteration order,
	 * only that each pixel will appear once. Performance may be
	 * low as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link IterableInterval} to wrap as a Java collection.
	 * @return Wrapped {@link Collection} with {@link BigInteger} elements.
	 * @see #collection(IterableInterval, Function)
	 */
	public static Collection< BigInteger > bigIntegerCollection( final IterableInterval< ? extends IntegerType< ? > > image )
	{
		return collection( image, t -> t.getBigInteger() );
	}

	/**
	 * Wraps a {@link RandomAccessibleInterval} as a {@link List}. The wrapped
	 * list is read-only, throwing {@link UnsupportedOperationException} if the
	 * caller attempts to mutate it.
	 * <p>
	 * With this method, and <em>unlike
	 * {@link #collection(IterableInterval, Function)}</em>, the iteration order
	 * of the wrapped list will <em>always be the
	 * {@link net.imglib2.FlatIterationOrder flat iteration order}</em>, as
	 * though {@link Views#flatIterable} were used on the source
	 * {@link RandomAccessibleInterval} image. As such, iterating the original
	 * image and the wrapped {@link List} may result in differing sequences of
	 * elements.
	 * </p>
	 * 
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @param converter
	 *            Conversion function to use for accessing elements of the list.
	 *            This function will be transparently called on the
	 *            corresponding source sample from the ImgLib2 image.
	 * @return Wrapped {@link List} of the converted type.
	 */
	public static < T, E > List< E > list( final RandomAccessibleInterval< T > image, final Function< T, E > converter )
	{
		return new ListFromRAI<>( image, converter );
	}

	/**
	 * Wraps a {@link BooleanType} random-accessible image as a list.
	 * <p>
	 * Warning: Performance may be low, as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @return Wrapped {@link List} with {@link Boolean} elements.
	 * @see #list(RandomAccessibleInterval, Function)
	 */
	public static List< Boolean > booleanList( final RandomAccessibleInterval< ? extends BooleanType< ? > > image )
	{
		return list( image, t -> t.get() );
	}

	/**
	 * Wraps a {@link RealType} random-accessible image as a list.
	 * <p>
	 * Warning: Performance may be low, as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @return Wrapped {@link List} with {@link Double} elements.
	 * @see #list(RandomAccessibleInterval, Function)
	 */
	public static List< Double > doubleList( final RandomAccessibleInterval< ? extends RealType< ? > > image )
	{
		return list( image, t -> t.getRealDouble() );
	}

	/**
	 * Wraps a {@link RealType} random-accessible image as a list.
	 * <p>
	 * Warning: Performance may be low, as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @return Wrapped {@link List} with {@link Float} elements.
	 * @see #list(RandomAccessibleInterval, Function)
	 */
	public static List< Float > floatList( final RandomAccessibleInterval< ? extends RealType< ? > > image )
	{
		return list( image, t -> t.getRealFloat() );
	}

	/**
	 * Wraps an {@link IntegerType} random-accessible image as a list.
	 * <p>
	 * Warning: Performance may be low, as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @return Wrapped {@link List} with {@link Integer} elements.
	 * @see #list(RandomAccessibleInterval, Function)
	 */
	public static List< Integer > integerList( final RandomAccessibleInterval< ? extends IntegerType< ? > > image )
	{
		return list( image, t -> t.getInteger() );
	}

	/**
	 * Wraps an {@link IntegerType} random-accessible image as a list.
	 * <p>
	 * Warning: Performance may be low, as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @return Wrapped {@link List} with {@link Long} elements.
	 * @see #list(RandomAccessibleInterval, Function)
	 */
	public static List< Long > longList( final RandomAccessibleInterval< ? extends IntegerType< ? > > image )
	{
		return list( image, t -> t.getIntegerLong() );
	}

	/**
	 * Wraps a {@link IntegerType} random-accessible image as a list.
	 * <p>
	 * Warning: Performance may be low, as the collection uses boxed types.
	 *
	 * @param image
	 *            The {@link RandomAccessibleInterval} to wrap as a Java list.
	 * @return Wrapped {@link List} with {@link BigInteger} elements.
	 * @see #list(RandomAccessibleInterval, Function)
	 */
	public static List< BigInteger > bigIntegerList( final RandomAccessibleInterval< ? extends IntegerType< ? > > image )
	{
		return list( image, t -> t.getBigInteger() );
	}

	/** A {@link RandomAccessibleInterval} expressed as a {@link List}. */
	private static class ListFromRAI< T, E > extends AbstractList< E >
	{

		private final RandomAccessibleInterval< T > rai;

		private final Function< T, E > converter;

		private final int size;

		private final ThreadLocal< RandomAccess< T > > ra = new ThreadLocal< RandomAccess< T > >()
		{

			@Override
			public RandomAccess< T > initialValue()
			{
				return rai.randomAccess();
			}
		};

		public ListFromRAI( final RandomAccessibleInterval< T > rai, final Function< T, E > converter )
		{
			this.rai = rai;
			this.converter = converter;
			size = sizeAsInt( Intervals.numElements( rai ) );
		}

		@Override
		public E get( final int index )
		{
			final RandomAccess< T > access = ra.get();
			IntervalIndexer.indexToPositionForInterval( index, rai, access );
			return converter.apply( access.get() );
		}

		@Override
		public int size()
		{
			return size;
		}
	}

	/** An {@link IterableInterval} expressed as a {@link Collection}. */
	private static class CollectionFromII< T, E > extends AbstractCollection< E >
	{

		private final IterableInterval< T > image;

		private final Function< T, E > converter;

		private final int size;

		private CollectionFromII( final IterableInterval< T > image, final Function< T, E > converter )
		{
			this.image = image;
			this.converter = converter;
			size = sizeAsInt( Intervals.numElements( image ) );
		}

		@Override
		public int size()
		{
			return size;
		}

		@Override
		public Iterator< E > iterator()
		{
			return new Iterator< E >()
			{

				private final Cursor< T > cursor = image.cursor();

				@Override
				public boolean hasNext()
				{
					return cursor.hasNext();
				}

				@Override
				public E next()
				{
					return converter.apply( cursor.next() );
				}
			};
		}
	}

	private static int sizeAsInt( final long size )
	{
		if ( size < 0 )
			throw new IllegalArgumentException( "Negative size: " + size );
		if ( size > Integer.MAX_VALUE )
			throw new IllegalArgumentException( "Size too large: " + size );
		return ( int ) size;
	}
}
