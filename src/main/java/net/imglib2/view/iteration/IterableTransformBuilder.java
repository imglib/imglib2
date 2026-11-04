/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2026 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.view.iteration;

import java.util.Arrays;
import java.util.function.Supplier;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.transform.integer.BoundingBox;
import net.imglib2.transform.integer.Mixed;
import net.imglib2.transform.integer.ReshapeTransform;
import net.imglib2.transform.integer.SlicingTransform;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.TransformBuilder;

/**
 * Simplifies View cascades to provide the most efficient {@link Cursor}.
 * 
 * @see #getEfficientIterableInterval(Interval, RandomAccessible)
 * 
 * @author Tobias Pietzsch
 */
public class IterableTransformBuilder< T > extends TransformBuilder< T >
{
	/**
	 * Create an {@link IterableInterval} that iterates an {@link Interval} of a
	 * {@link RandomAccessible}. If possible, this should return an optimized
	 * cursor. If not, falls back to creating an
	 * {@link IterableRandomAccessibleInterval}.
	 * 
	 * @param interval
	 *            the interval of {@code randomAccessible} which should be
	 *            iterated.
	 * @param randomAccessible
	 *            the {@link RandomAccessible} that should be iterated.
	 * @return an {@link IterableInterval} that iterates {@code interval} of
	 *         {@code randomAccessible}.
	 */
	public static < S > IterableInterval< S > getEfficientIterableInterval( final Interval interval, final RandomAccessible< S > randomAccessible )
	{
		return new IterableTransformBuilder< S >( interval, randomAccessible ).buildIterableInterval();
	}

	/**
	 * The interval which should be iterated.
	 * 
	 * <p>
	 * Currently, no transformations are done on this, because the cases where
	 * an optimized {@link IterableInterval} can be returned do not allow for
	 * any transformation except a single slicing. In the future, it may become
	 * necessary, to propagated the interval through the transforms down the
	 * view hierarchy.
	 */
	protected Interval interval;

	/**
	 * Create a new IterableTransformBuilder. This calls the the super
	 * constructor to gather and simplify transformations.
	 * 
	 * @param interval
	 *            the interval of {@code randomAccessible} which should be
	 *            iterated.
	 * @param randomAccessible
	 *            the {@link RandomAccessible} that should be iterated.
	 */
	public IterableTransformBuilder( final Interval interval, final RandomAccessible< T > randomAccessible )
	{
		super( interval, randomAccessible );
		this.interval = interval;
	}

	/**
	 * An {@link IterableInterval} on {@link IterableTransformBuilder#interval
	 * interval} of {@link SubIntervalIterable}.
	 */
	private class SubInterval extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		final long numElements;

		final SubIntervalIterable< T > iterableSource;

		public SubInterval( final SubIntervalIterable< T > iterableSource )
		{
			super( interval );
			numElements = Intervals.numElements( interval );
			this.iterableSource = iterableSource;
		}

		@Override
		public long size()
		{
			return numElements;
		}

		@Override
		public Object iterationOrder()
		{
			return iterableSource.subIntervalIterationOrder( interval );
		}

		@Override
		public Cursor< T > cursor()
		{
			return iterableSource.cursor( interval );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return iterableSource.localizingCursor( interval );
		}

		@Override
		public T getType()
		{
			return iterableSource.getType();
		}
	}

	/**
	 * An {@link IterableInterval} on a slice of a {@link SubIntervalIterable}.
	 */
	private class Slice extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		final long numElements;

		final SubIntervalIterable< T > iterableSource;

		final Interval sourceInterval;

		final SlicingTransform transformToSource;

		final boolean hasFlatIterationOrder;

		public Slice( final SubIntervalIterable< T > iterableSource, final Interval sourceInterval, final SlicingTransform transformToSource, final boolean hasFlatIterationOrder )
		{
			super( interval );
			numElements = Intervals.numElements( interval );
			this.iterableSource = iterableSource;
			this.sourceInterval = sourceInterval;
			this.transformToSource = transformToSource;
			this.hasFlatIterationOrder = hasFlatIterationOrder;
		}

		@Override
		public long size()
		{
			return numElements;
		}

		@Override
		public Object iterationOrder()
		{
			return hasFlatIterationOrder ? new FlatIterationOrder( interval ) : this;
		}

		@Override
		public Cursor< T > cursor()
		{
			return new SlicingCursor< T >( iterableSource.cursor( sourceInterval ), transformToSource );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return new SlicingCursor< T >( iterableSource.localizingCursor( sourceInterval ), transformToSource );
		}

		@Override
		public T getType()
		{
			return iterableSource.getType();
		}
	}

	private class IterableIntervalView extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		private final RandomAccessible< T > randomAccessible;

		private final long size;

		public IterableIntervalView( final RandomAccessible< T > randomAccessible, final Interval interval )
		{
			super( interval );
			this.randomAccessible = randomAccessible;
			size = Intervals.numElements( interval );
		}

		@Override
		public long size()
		{
			return size;
		}

		@Override
		public FlatIterationOrder iterationOrder()
		{
			return new FlatIterationOrder( sourceInterval );
		}

		@Override
		public Cursor< T > cursor()
		{
			return new RandomAccessibleIntervalCursor<>( randomAccessible, sourceInterval );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return cursor();
		}
	}

	/**
	 * Create an {@link IterableInterval} on the {@link Interval} specified in
	 * the constructor of the {@link RandomAccessible} specified in the
	 * constructor.
	 */
	public IterableInterval< T > buildIterableInterval()
	{
		if ( boundingBox != null && SubIntervalIterable.class.isInstance( source ) )
		{
			@SuppressWarnings( "unchecked" )
			final SubIntervalIterable< T > iterableSource = ( SubIntervalIterable< T > ) source;
			if ( transforms.isEmpty() )
			{
				if ( iterableSource.supportsOptimizedCursor( interval ) )
					return new SubInterval( iterableSource );
			}
			else if ( transforms.size() == 1 && SlicingTransform.class.isInstance( transforms.get( 0 ) ) )
			{
				final SlicingTransform t = ( SlicingTransform ) transforms.get( 0 );
				final int m = t.numTargetDimensions();
				final int n = t.numSourceDimensions();

				// Check whether the slicing can be potentially optimized.
				boolean optimizable = true;

				// 1.) Slice dimensions must be mapped to a contiguous range of
				// target dimensions starting with dimension 0.
				int firstZeroDim = 0;
				for ( ; firstZeroDim < m && !t.getComponentZero( firstZeroDim ); ++firstZeroDim );
				for ( int d = firstZeroDim + 1; d < m && optimizable; ++d )
					if ( !t.getComponentZero( d ) )
						optimizable = false;

				// 2.) All slice dimensions must be mapped to a target dimension
				final int[] sourceComponent = new int[ n ];
				if ( optimizable )
				{
					Arrays.fill( sourceComponent, -1 );
					for ( int d = 0; d < m; ++d )
						if ( !t.getComponentZero( d ) )
							sourceComponent[ t.getComponentMapping( d ) ] = d;
					for ( int d = 0; d < n && optimizable; ++d )
						if ( sourceComponent[ d ] < 0 )
							optimizable = false;
				}

				if ( optimizable )
				{
//					System.out.println( "interval = " + Util.printInterval( interval ) );
					final Interval sliceInterval = t.transform( new BoundingBox( interval ) ).getInterval();
//					System.out.println( "transformed interval = " + Util.printInterval( sliceInterval ) );
					if ( iterableSource.supportsOptimizedCursor( sliceInterval ) )
					{
						// check for FlatIterationOrder
						boolean flat = FlatIterationOrder.class.isInstance( iterableSource.subIntervalIterationOrder( sliceInterval ) );
						for ( int d = 0; d < n - 1; ++d )
							if ( sourceComponent[ d + 1 ] <= sourceComponent[ d ] )
								flat = false;
						return new Slice( iterableSource, sliceInterval, t, flat );
					}
				}
			}
		}

		final IterableInterval< T > flatRun = buildFlatRunIterableInterval();
		if ( flatRun != null )
			return flatRun;

		return new IterableIntervalView( build(), interval );
	}

	/**
	 * An {@link IterableInterval} on {@link IterableTransformBuilder#interval
	 * interval}, whose elements are a box of {@link #source} elements, in the
	 * same flat order. Iterates by forwarding to a flat-order cursor on the
	 * source box.
	 */
	private class FlatRun extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		private final long numElements;

		private final Supplier< Cursor< T > > sourceCursors;

		FlatRun( final Supplier< Cursor< T > > sourceCursors )
		{
			super( interval );
			numElements = Intervals.numElements( interval );
			this.sourceCursors = sourceCursors;
		}

		@Override
		public long size()
		{
			return numElements;
		}

		@Override
		public FlatIterationOrder iterationOrder()
		{
			return new FlatIterationOrder( interval );
		}

		@Override
		public Cursor< T > cursor()
		{
			return new FlatIndexCursor<>( sourceCursors.get(), interval );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return new FlatIndexLocalizingCursor<>( sourceCursors.get(), interval );
		}

		@Override
		public T getType()
		{
			return source.getType();
		}
	}

	/**
	 * If {@link #interval} maps to a box of {@link #source} elements, in the
	 * same flat order, return an {@link IterableInterval} that forwards to a
	 * cursor that iterates that box of the source in flat order. Otherwise
	 * return {@code null}.
	 * <p>
	 * This is the case if {@link #transforms} is either
	 * <ul>
	 * <li>a single {@link Mixed} transform that {@link #isOrderPreserving(Mixed,
	 * Interval) preserves flat order} on {@link #interval}, or</li>
	 * <li>a {@link ReshapeTransform}, possibly between order-preserving {@link
	 * Mixed} transforms, and the image of {@link #interval} is a box. (A reshape
	 * preserves flat order: if the image of {@link #interval} inside the
	 * reshape's domain has as many elements as its bounding box, the image is
	 * exactly the bounding box, and both are iterated in the same order.)</li>
	 * </ul>
	 */
	private IterableInterval< T > buildFlatRunIterableInterval()
	{
		if ( boundingBox == null || transforms.isEmpty() || transforms.size() > 3 )
			return null;

		// transforms are ordered from source to view: [M] or [M?, R, M?]
		int r = -1;
		for ( int i = 0; i < transforms.size(); ++i )
		{
			if ( transforms.get( i ) instanceof ReshapeTransform )
			{
				if ( r >= 0 )
					return null;
				r = i;
			}
			else if ( !( transforms.get( i ) instanceof Mixed ) )
				return null;
		}

		if ( r < 0 )
		{
			if ( transforms.size() != 1 || !isOrderPreserving( ( Mixed ) transforms.get( 0 ), interval ) )
				return null;
		}
		else
		{
			if ( r > 1 || transforms.size() > r + 2 )
				return null;
			if ( r == 1 && !isOrderPreserving( ( Mixed ) transforms.get( 0 ), null ) )
				return null;
			final ReshapeTransform reshape = ( ReshapeTransform ) transforms.get( r );

			// interval must be inside the domain of the reshape
			long[] min = interval.minAsLongArray();
			long[] max = interval.maxAsLongArray();
			if ( transforms.size() == r + 2 )
			{
				final Mixed top = ( Mixed ) transforms.get( r + 1 );
				if ( !isOrderPreserving( top, interval ) )
					return null;
				final long[] tmin = new long[ top.numTargetDimensions() ];
				final long[] tmax = new long[ top.numTargetDimensions() ];
				top.apply( min, tmin );
				top.apply( max, tmax );
				min = tmin;
				max = tmax;
			}
			for ( int d = 0; d < min.length; ++d )
				if ( min[ d ] < 0 || max[ d ] >= reshape.getSourceDimension( d ) )
					return null;
		}

		final Interval sourceInterval = boundingBox.getInterval();
		if ( Intervals.numElements( sourceInterval ) != Intervals.numElements( interval ) )
			return null;

		if ( source instanceof SubIntervalIterable )
		{
			@SuppressWarnings( "unchecked" )
			final SubIntervalIterable< T > iterableSource = ( SubIntervalIterable< T > ) source;
			if ( iterableSource.supportsOptimizedCursor( sourceInterval )
					&& iterableSource.subIntervalIterationOrder( sourceInterval ) instanceof FlatIterationOrder )
				return new FlatRun( () -> iterableSource.cursor( sourceInterval ) );
		}
		if ( source instanceof IterableInterval )
		{
			@SuppressWarnings( "unchecked" )
			final IterableInterval< T > iterableSource = ( IterableInterval< T > ) source;
			if ( iterableSource.iterationOrder() instanceof FlatIterationOrder
					&& Intervals.equals( iterableSource, sourceInterval ) )
				return new FlatRun( iterableSource::cursor );
		}
		// Iterate the source interval in flat order by random access. This
		// avoids the transform arithmetic, and is how any source with a
		// different iteration order (for example a CellImg) would be iterated
		// in flat order.
		return new FlatRun( () -> new RandomAccessibleIntervalCursor<>( source, sourceInterval ) );
	}

	/**
	 * Does {@code t} map boxes to boxes one-to-one, preserving flat
	 * iteration order. That is:
	 * <ul>
	 * <li>no target dimension is inverted,</li>
	 * <li>the target dimensions that are taken from source dimensions are taken
	 * in increasing order (no permutation), and</li>
	 * <li>every source dimension that is not mapped to any target dimension has
	 * size 1 in {@code sourceInterval}.</li>
	 * </ul>
	 * If {@code sourceInterval == null}, every source dimension must be mapped.
	 * <p>
	 * Examples are translations, slicing, and inserting or removing dimensions
	 * of size 1.
	 */
	private static boolean isOrderPreserving( final Mixed t, final Interval sourceInterval )
	{
		final boolean[] mapped = new boolean[ t.numSourceDimensions() ];
		int previous = -1;
		for ( int d = 0; d < t.numTargetDimensions(); ++d )
		{
			if ( t.getComponentZero( d ) )
				continue;
			if ( t.getComponentInversion( d ) )
				return false;
			final int c = t.getComponentMapping( d );
			if ( c <= previous )
				return false;
			previous = c;
			mapped[ c ] = true;
		}
		for ( int c = 0; c < mapped.length; ++c )
			if ( !mapped[ c ] && ( sourceInterval == null || sourceInterval.dimension( c ) != 1 ) )
				return false;
		return true;
	}
}
