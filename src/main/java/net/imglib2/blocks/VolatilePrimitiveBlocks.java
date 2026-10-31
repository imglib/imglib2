/*-
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
package net.imglib2.blocks;

import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.FAIL;
import static net.imglib2.blocks.PrimitiveBlocks.OnFallback.WARN;

import net.imglib2.EuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.Typed;
import net.imglib2.Volatile;
import net.imglib2.blocks.PrimitiveBlocks.OnFallback;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.type.NativeType;
import net.imglib2.util.Cast;
import net.imglib2.util.Util;


/**
 * Copy blocks of data out of a {@code Volatile} {@code NativeType<T>} source
 * into {@link VolatileArray}s: primitive data (of the appropriate type) plus
 * the validity of every pixel.
 * <p>
 * This is the {@code Volatile} counterpart of {@link PrimitiveBlocks}. The two
 * are deliberately unrelated types: a {@code VolatilePrimitiveBlocks} cannot be
 * passed where a {@code PrimitiveBlocks} is expected, or vice versa, so the
 * kind of destination buffer ({@code VolatileArray} or primitive array) is
 * checked by the compiler rather than failing with a {@code
 * ClassCastException} at copy time.
 * <p>
 * Use the static method {@link VolatilePrimitiveBlocks#of(RandomAccessible)
 * VolatilePrimitiveBlocks.of} to create a {@code VolatilePrimitiveBlocks}
 * accessor for an arbitrary {@code RandomAccessible} source.
 * <p>
 * Implementations are not thread-safe in general. Use {@link #threadSafe()} to
 * obtain a thread-safe instance (implemented using {@link ThreadLocal} copies).
 *
 * @param <T>
 * 		volatile pixel type
 */
public interface VolatilePrimitiveBlocks< T extends Volatile< ? > & NativeType< T > > extends Typed< T >, EuclideanSpace
{
	/**
	 * Copy a block from the ({@code T}-typed) source into a {@link
	 * VolatileArray}.
	 *
	 * @param interval
	 * 		position and size of the block to copy
	 * @param dest
	 * 		{@link VolatileArray} to copy into. Its {@link VolatileArray#data()
	 * 		data} must correspond to {@code T}, for example, if {@code T} is
	 * 		{@code VolatileUnsignedByteType} then it must be {@code byte[]}. Its
	 * 		validity ({@link VolatileArray#allValid()}, {@link
	 * 		VolatileArray#valid()}) is set to the {@link Volatile#isValid()
	 * 		validity} of the copied block.
	 */
	void copy( Interval interval, VolatileArray< ? > dest );

	/**
	 * Copy a block from the ({@code T}-typed) source into a {@link
	 * VolatileArray}.
	 *
	 * @param srcPos
	 * 		min coordinate of the block to copy
	 * @param dest
	 * 		{@link VolatileArray} to copy into.
	 * @param size
	 * 		the size of the block to copy
	 */
	default void copy( long[] srcPos, VolatileArray< ? > dest, int[] size )
	{
		copy( BlockInterval.wrap( srcPos, size ), dest );
	}

	/**
	 * Copy a block from the ({@code T}-typed) source into a {@link
	 * VolatileArray}.
	 *
	 * @param srcPos
	 * 		min coordinate of the block to copy
	 * @param dest
	 * 		{@link VolatileArray} to copy into.
	 * @param size
	 * 		the size of the block to copy
	 */
	default void copy( int[] srcPos, VolatileArray< ? > dest, int[] size )
	{
		copy( Util.int2long( srcPos ), dest, size );
	}

	/**
	 * Whether every pixel of the given block is valid, without copying any
	 * data.
	 * <p>
	 * Pixels that would be filled with the out-of-bounds value count as valid.
	 *
	 * @param interval
	 * 		position and size of the block to ask about
	 * @param failFast
	 * 		if {@code true}, return as soon as the answer is known to be {@code
	 * 		FALSE}. If {@code false}, the whole block is inspected, which for a
	 * 		cached source has the side effect of requesting all the data that
	 * 		the block reads from.
	 */
	AllValid allValid( Interval interval, boolean failFast );

	/**
	 * Get a thread-safe version of this {@code VolatilePrimitiveBlocks}.
	 * (Implemented as a wrapper that makes {@link ThreadLocal} copies).
	 */
	VolatilePrimitiveBlocks< T > threadSafe();

	VolatilePrimitiveBlocks< T > independentCopy();

	/**
	 * Answer to "is every pixel of this block valid?", asked without copying
	 * the block ({@link #allValid(Interval, boolean)}.
	 * <p>
	 * The {@code UNKNOWN} case exists because this is re-used in
	 * imglib2-algorithm's {@code VolatileBlockSupplier}. There the question is
	 * asked of a pipeline of operators. A stage that cannot map the block it is
	 * asked about to the block it would read cannot answer (without actually
	 * doing the work) and therefore answers {@code #UNKNOWN}.
	 * <p>
	 * A caller that uses {@code AllValid} to decide whether to attempt work
	 * should go ahead on {@code UNKNOWN}, as it would on {@code TRUE}. Only
	 * {@code FALSE} says that the data is not all there.
	 */
	enum AllValid
	{
		/**
		 * Every pixel of the block is valid.
		 */
		TRUE,

		/**
		 * At least one pixel of the block is not valid.
		 */
		FALSE,

		/**
		 * Could not be determined without doing the work that asking was meant
		 * to avoid.
		 */
		UNKNOWN;

		public static AllValid of( final boolean allValid )
		{
			return allValid ? TRUE : FALSE;
		}
	}

	/**
	 * Create a {@code VolatilePrimitiveBlocks} accessor for an arbitrary {@code
	 * RandomAccessible} source. Many View constructions (that ultimately end in
	 * {@code CellImg}) are understood and will be handled by an optimized
	 * copier.
	 * <p>
	 * If the source {@code RandomAccessible} cannot be understood, a warning is
	 * printed, and a fall-back implementation (based on {@code LoopBuilder}) is
	 * returned.
	 * <p>
	 * The returned {@code VolatilePrimitiveBlocks} is not thread-safe in general. Use
	 * {@link #threadSafe()} to obtain a thread-safe instance,
	 * e.g., {@code VolatilePrimitiveBlocks.of(view).threadSafe()}.
	 *
	 * @param ra
	 * 		the source
	 * @param <T>
	 * 		volatile pixel type
	 *
	 * @return a {@code VolatilePrimitiveBlocks} accessor for {@code ra}.
	 */
	static < T extends Volatile< ? > & NativeType< T > > VolatilePrimitiveBlocks< T > of( RandomAccessible< T > ra )
	{
		return of( ra, WARN );
	}

	/**
	 * Create a {@code VolatilePrimitiveBlocks} accessor for an arbitrary {@code
	 * RandomAccessible} source. Many View constructions (that ultimately end in
	 * {@code CellImg}) are understood and will be handled by an optimized
	 * copier.
	 * <p>
	 * If the source {@code RandomAccessible} cannot be understood, a fall-back
	 * implementation (based on {@code LoopBuilder}) has to be used. The {@code
	 * onFallback} argument specifies how to handle this case:
	 * <ul>
	 *     <li>{@link OnFallback#ACCEPT ACCEPT}: silently accept fall-back</li>
	 *     <li>{@link OnFallback#WARN WARN}: accept fall-back, but print a warning explaining why the input {@code ra} requires fall-back</li>
	 *     <li>{@link OnFallback#FAIL FAIL}: throw {@code IllegalArgumentException} explaining why the input {@code ra} requires fall-back</li>
	 * </ul>
	 * The returned {@code VolatilePrimitiveBlocks} is not thread-safe in general. Use
	 * {@link #threadSafe()} to obtain a thread-safe instance,
	 * e.g., {@code VolatilePrimitiveBlocks.of(view).threadSafe()}.
	 *
	 * @param ra
	 * 		the source
	 * @param <T>
	 * 		volatile pixel type
	 *
	 * @return a {@code VolatilePrimitiveBlocks} accessor for {@code ra}.
	 */
	static < T extends Volatile< ? > & NativeType< T >, R extends Volatile< ? > & NativeType< R > >
	VolatilePrimitiveBlocks< T > of(
			RandomAccessible< T > ra,
			OnFallback onFallback )
	{
		final ViewPropertiesOrError< T, R > props = ViewAnalyzer.getViewProperties( ra );

		// TODO: Currently only fully implemented for AbstractCellImg.
		//   First step to fix it would be to implement
		//   VolatileArrayImgRangeCopier and
		//   VolatilePlanarImgRangeCopier
		boolean fullySupported = props.isFullySupported();
		String errorMessage = props.getErrorMessage();
		if ( fullySupported && !( props.getViewProperties().getRoot() instanceof AbstractCellImg ) )
		{
			fullySupported = false;
			errorMessage = "Currently only AbstractCellImg root is supported (TODO).";
		}

		if ( fullySupported )
		{
			return new VolatileViewPrimitiveBlocks<>( props.getViewProperties() );
		}
		else if ( props.isSupported() && onFallback != FAIL )
		{
			if ( onFallback == WARN )
				new Throwable( "WARNING: " + errorMessage ).printStackTrace();
			return new VolatileFallbackPrimitiveBlocks<>( props.getFallbackProperties() );
		}
		throw new IllegalArgumentException( errorMessage );
	}

	/**
	 * Whether {@link #of(RandomAccessible, OnFallback)
	 * VolatilePrimitiveBlocks.of( ra, onFallback )} would succeed, that is, not
	 * throw {@code IllegalArgumentException}.
	 * <p>
	 * With {@link OnFallback#FAIL FAIL}, this is {@code true} iff {@code ra}
	 * can be handled by the optimized copier. Otherwise, it is {@code true} if
	 * {@code ra} can be handled at all, by the optimized copier or by the
	 * fall-back implementation.
	 * <p>
	 * This only analyzes the view; it is cheaper than {@code of}, which also
	 * builds the copier.
	 *
	 * @param ra
	 * 		the source
	 * @param onFallback
	 * 		how {@code of} would be asked to handle the fall-back case
	 *
	 * @return {@code true}, if {@code VolatilePrimitiveBlocks.of( ra, onFallback )} would succeed.
	 */
	static boolean isSupported( final RandomAccessible< ? > ra, final OnFallback onFallback )
	{
		if ( !( ra.getType() instanceof Volatile ) )
			return false;
		final ViewPropertiesOrError< ?, ? > props = ViewAnalyzer.getViewProperties( Cast.unchecked( ra ) );

		// TODO: Currently only fully implemented for AbstractCellImg.
		//   First step to fix it would be to implement
		//   VolatileArrayImgRangeCopier and
		//   VolatilePlanarImgRangeCopier
		boolean fullySupported = props.isFullySupported() && props.getViewProperties().getRoot() instanceof AbstractCellImg;

		return fullySupported || ( props.isSupported() && onFallback != FAIL );
	}
}
