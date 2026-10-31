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

import net.imglib2.img.NativeImg;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.img.cell.Cell;

/**
 * {@code VolatileRangeCopier} does the actual copying work from a
 * {@code Volatile}-typed {@code NativeImg} into a primitive array.
 * <p>
 * The static {@link VolatileRangeCopier#create} method will pick the correct
 * implementation for a given {@link NativeImg}.
 *
 * @param <T> a primitive array type, e.g., {@code byte[]}.
 */
interface VolatileRangeCopier< T >
{
	/**
	 * Copy the block starting at {@code srcPos} with the given {@code size}
	 * into the (appropriately sized) {@code dest} array.
	 * <p>
	 * Pixels that come from an invalid {@code Cell} are not copied: they are
	 * set to zero, and marked invalid in the returned {@code RangeValidity}.
	 * Out-of-bounds pixels are filled with the out-of-bounds value and count as
	 * valid.
	 *
	 * @param srcPos
	 * 		min coordinates of block to copy from src Img.
	 * @param dest
	 * 		destination array. Type is {@code byte[]}, {@code float[]},
	 * 		etc, corresponding to the src Img's native type.
	 * @param size
	 * 		dimensions of block to copy from src Img.
	 *
	 * @return validity of the copied block, in range space (roughly: one value
	 * 		per cell instead of one value per pixel).
	 */
	RangeValidity copy( long[] srcPos, T dest, int[] size );

	/**
	 * Whether every pixel of the block at {@code srcPos} with the given {@code
	 * size} is valid, without copying any data.
	 * <p>
	 * Pixels that would be filled with Out-of-bounds value count as valid.
	 *
	 * @param failFast
	 * 		if {@code true}, return as soon as an invalid cell is found. If
	 * 		{@code false}, every cell the block touches is visited, which for a
	 * 		cached source has the side effect of requesting all of them.
	 */
	boolean allValid( long[] srcPos, int[] size, boolean failFast );

	/**
	 * Return a new independent instance of this {@code VolatileRangeCopier}.
	 * This is used for multi-threading. The new instance works on the same
	 * source image, but has independent internal state.
	 *
	 * @return new independent instance of this {@code VolatileRangeCopier}
	 */
	VolatileRangeCopier< T > newInstance();

	/**
	 * Create a {@code VolatileRangeCopier} for the given {@code NativeImg}.
	 * <p>
	 * TODO: Currently only AbstractCellImg is supported. This is probably the
	 *   only case that will happen, but we should support ArrayImg and PlanarImg
	 *   for completeness...
	 *
	 * @param img
	 * 		the source image
	 * @param findRanges
	 * 		finds {@link Ranges.Range ranges} for one dimension, including
	 * 		out-of-bounds, so it depends on extension method applied that should
	 * 		be applied to the {@code img}
	 * @param memCopy
	 * 		copies pixel data out of the image. Its source type {@code S} is the
	 * 		image's access type, which may be a {@code Buffer}.
	 * @param fillCopy
	 * 		fills out-of-bounds regions from {@code oob}. Unlike {@code memCopy}, it
	 * 		always copies array to array: {@code oob} has the type {@code T} of
	 * 		the destination, regardless of the image's access type.
	 * @param oob
	 * 		primitive array holding the out-of-bounds value (or {@code null}, if
	 * 		the extension method does not use a constant value).
	 * @param <S>
	 * 		backs the source image's accesses, may be a primitive array or a {@code Buffer}.
	 * @param <T>
	 * 		the destination type, always a primitive array type
	 */
	static < S, T > VolatileRangeCopier< T > create(
			final NativeImg< ?, ? > img,
			final Ranges findRanges,
			final MemCopy< S, T > memCopy,
			final MemCopy< T, T > fillCopy,
			final T oob )
	{
		if ( img instanceof AbstractCellImg )
			return new VolatileCellImgRangeCopier<>( ( AbstractCellImg< ?, ?, ? extends Cell< ? >, ? > ) img, findRanges, memCopy, fillCopy, oob );
		else
			throw new IllegalArgumentException();
	}
}
