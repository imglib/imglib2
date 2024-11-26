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

import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.planar.PlanarImg;

/**
 * {@code RangeCopier} does the actual copying work from a {@code NativeImg}
 * into a primitive array.
 * <p>
 * The static {@link RangeCopier#create} method will pick the correct
 * implementation for a given {@link NativeImg}.
 *
 * @param <T> a primitive array type, e.g., {@code byte[]}.
 */
interface RangeCopier< T >
{
	/**
	 * Copy the block starting at {@code srcPos} with the given {@code size}
	 * into the (appropriately sized) {@code dest} array.
	 *
	 * @param srcPos
	 * 		min coordinates of block to copy from src Img.
	 * @param dest
	 * 		destination array. Type is {@code byte[]}, {@code float[]},
	 * 		etc, corresponding to the src Img's native type.
	 * @param size
	 * 		dimensions of block to copy from src Img.
	 */
	void copy( long[] srcPos, T dest, int[] size );

	/**
	 * Return a new independent instance of this {@code RangeCopier}. This is
	 * used for multi-threading. The new instance works on the same source
	 * image, but has independent internal state.
	 *
	 * @return new independent instance of this {@code RangeCopier}
	 */
	RangeCopier< T > newInstance();

	static < S, T > RangeCopier< T > create(
			final NativeImg< ?, ? > img,
			final Ranges findRanges,
			final MemCopy< S, T > memCopy,
			final S oob )
	{
		if ( img instanceof AbstractCellImg )
			return new CellImgRangeCopier<>( ( AbstractCellImg< ?, ?, ? extends Cell< ? >, ? > ) img, findRanges, memCopy, oob );
		else if ( img instanceof PlanarImg )
			return new PlanarImgRangeCopier<>( ( PlanarImg< ?, ? > ) img, findRanges, memCopy, oob );
		else if ( img instanceof ArrayImg )
			return new ArrayImgRangeCopier<>( ( ArrayImg< ?, ? > ) img, findRanges, memCopy, oob );
		else
			throw new IllegalArgumentException();
	}
}
