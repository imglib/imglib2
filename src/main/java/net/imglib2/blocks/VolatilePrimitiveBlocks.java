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
package net.imglib2.blocks;

import net.imglib2.EuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.Typed;
import net.imglib2.Volatile;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;


/**
 * TODO: revise javadoc
 * <p>
 * Copy blocks of data out of a {@code NativeType<T>} source into primitive
 * arrays (of the appropriate type).
 * <p>
 * Use the static method {@link VolatilePrimitiveBlocks#of(RandomAccessible)
 * VolatilePrimitiveBlocks.of} to create a {@code VolatilePrimitiveBlocks}
 * accessor for an arbitrary {@code RandomAccessible} source.
 * Then use the {@link VolatilePrimitiveBlocks#copy} method, to copy blocks out
 * of the source into flat primitive arrays.
 * <p>
 * {@link VolatilePrimitiveBlocks#of(RandomAccessible)
 * VolatilePrimitiveBlocks.of} understands a lot of View constructions (that
 * ultimately end in {@code CellImg}, {@code ArrayImg}, etc) and will try to
 * build an optimized copier.
 * <p>
 * If a source {@code RandomAccessible} cannot be understood, {@link
 * VolatilePrimitiveBlocks#of(RandomAccessible) VolatilePrimitiveBlocks.of} will
 * throw an {@code IllegalArgumentException} explaining why the source {@code
 * RandomAccessible} is not suitable.
 * <p>
 * Implementations are not thread-safe in general. Use {@link #threadSafe()} to
 * obtain a thread-safe instance (implemented using {@link ThreadLocal} copies).
 * E.g.,
 * <pre>{@code
 * 		VolatilePrimitiveBlocks< T > blocks = VolatilePrimitiveBlocks.of( view ).threadSafe();
 * }</pre>
 *
 * @param <T>
 * 		volatile pixel type
 */
public interface VolatilePrimitiveBlocks< T extends Volatile< ? > & NativeType< T > > extends Typed< T >, EuclideanSpace
{
	/**
	 * Copy a block from the ({@code T}-typed) source into primitive arrays (of
	 * the appropriate type).
	 *
	 * @param interval
	 * 		position and size of the block to copy
	 * @param dest
	 * 		primitive array to copy into. Must correspond to {@code T}, for
	 *      example, if {@code T} is {@code VolatileUnsignedByteType} then
	 *      {@code dest} must be {@code byte[]}.
	 * @param destValid
	 * 		primitive {@code byte[]} array to copy {@link Volatile#isValid()
	 * 		validity} mask into.
	 */
	void copy( Interval interval, Object dest, byte[] destValid );

	/**
	 * Copy a block from the ({@code T}-typed) source into primitive arrays (of
	 * the appropriate type).
	 *
	 * @param srcPos
	 * 		min coordinate of the block to copy
	 * @param dest
	 * 		primitive array to copy into. Must correspond to {@code T}, for
	 *      example, if {@code T} is {@code VolatileUnsignedByteType} then
	 *      {@code dest} must be {@code byte[]}.
	 * @param destValid
	 * 		primitive {@code byte[]} array to copy {@link Volatile#isValid()
	 * 		validity} mask into.
	 * @param size
	 * 		the size of the block to copy
	 */
	default void copy( long[] srcPos, Object dest, byte[] destValid, int[] size )
	{
		copy( BlockInterval.wrap( srcPos, size ), dest, destValid );
	}

	/**
	 * Copy a block from the ({@code T}-typed) source into primitive arrays (of
	 * the appropriate type).
	 *
	 * @param srcPos
	 * 		min coordinate of the block to copy
	 * @param dest
	 * 		primitive array to copy into. Must correspond to {@code T}, for
	 *      example, if {@code T} is {@code VolatileUnsignedByteType} then
	 *      {@code dest} must be {@code byte[]}.
	 * @param destValid
	 * 		primitive {@code byte[]} array to copy {@link Volatile#isValid()
	 * 		validity} mask into.
	 * @param size
	 * 		the size of the block to copy
	 */
	default void copy( int[] srcPos, Object dest, byte[] destValid, int[] size )
	{
		copy( Util.int2long( srcPos ), dest, destValid, size );
	}

	/**
	 * Get a thread-safe version of this {@code VolatilePrimitiveBlocks}.
	 * (Implemented as a wrapper that makes {@link ThreadLocal} copies).
	 */
	VolatilePrimitiveBlocks< T > threadSafe();

	VolatilePrimitiveBlocks< T > independentCopy();

	/**
	 * Create a {@code VolatilePrimitiveBlocks} accessor for an arbitrary {@code
	 * RandomAccessible} source. Many View constructions (that ultimately end in
	 * {@code CellImg}, {@code ArrayImg}, etc.) are understood and will be
	 * handled by an optimized copier.
	 * <p>
	 * If the source {@code VolatilePrimitiveBlocks} cannot be understood, an
	 * {@code IllegalArgumentException} is thrown, explaining why the input
	 * {@code ra} is not suitable.
	 * <p>
	 * The returned {@code VolatilePrimitiveBlocks} is not thread-safe in
	 * general. Use {@link #threadSafe()} to obtain a thread-safe instance,
	 * e.g., {@code VolatilePrimitiveBlocks.of(view).threadSafe()}.
	 *
	 * @param ra the source
	 * @return a {@code VolatilePrimitiveBlocks} accessor for {@code ra}.
	 * @param <T> volatile pixel type
	 */
	static < T extends Volatile< ? > & NativeType< T > > VolatilePrimitiveBlocks< T > of(
			RandomAccessible< T > ra )
	{
		final ViewPropertiesOrError< T, ? > props = ViewAnalyzer.getViewProperties( ra );
		if ( props.isFullySupported() )
		{
			return new VolatileViewPrimitiveBlocks<>( props.getViewProperties() );
		}
		throw new IllegalArgumentException( props.getErrorMessage() );
	}

	// TODO: Consider adding a fallback implementation (like in PrimitiveBlocks)
}
