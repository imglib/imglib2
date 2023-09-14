/*-
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
package net.imglib2.blocks;

import java.util.List;
import java.util.Objects;

/**
 * Find {@link Ranges.Range ranges} for one dimension.
 * <p>
 * Split the requested interval into ranges covering (possibly partial)
 * cells of the input image. The requested interval is given by start
 * coordinate {@code bx} (in the extended source image) and size of the
 * block to copy {@code bw}, in a particular dimension. The full size of the
 * (non-extended) image in this dimension is given by {@code iw}, the size
 * of a (non-truncated) cell in this dimension is given by {@code cw}.
 * <p>
 * Out-of-bounds values are handled depending on {@code Extension} strategy.
 * Use {@link #forExtension(Extension)} to pick a particular strategy.
 */
@FunctionalInterface
interface Ranges
{
	/**
	 * Find ranges for one dimension.
	 * <p>
	 * Split the requested interval into ranges covering (possibly partial)
	 * cells of the input image. The requested interval is given by start
	 * coordinate {@code bx} (in the extended source image) and size of the
	 * block to copy {@code bw}, in a particular dimension. The full size of the
	 * (non-extended) image in this dimension is given by {@code iw}, the size
	 * of a (non-truncated) cell in this dimension is given by {@code cw}.
	 * <p>
	 * Out-of-bounds values are handled depending on {@code Extension} strategy.
	 * Use {@link #forExtension(Extension)} to pick a particular strategy.
	 *
	 * @param bx
	 * 		start of block in source coordinates (in pixels)
	 * @param bw
	 * 		width of block to copy (in pixels)
	 * @param iw
	 * 		source image width (in pixels)
	 * @param cw
	 * 		source cell width (in pixels)
	 */
	List< Range > findRanges( long bx, int bw, long iw, int cw );

	/**
	 *
	 * CONSTANT: Out-of-bounds values are set to a constant.
	 * @param extension
	 * @return
	 */
	static Ranges forExtension( Extension extension )
	{
		switch ( extension.type() )
		{
		case CONSTANT:
			return RangesImpl.FIND_RANGES_CONSTANT;
		case MIRROR_SINGLE:
			return RangesImpl.FIND_RANGES_MIRROR_SINGLE;
		case MIRROR_DOUBLE:
			return RangesImpl.FIND_RANGES_MIRROR_DOUBLE;
		case BORDER:
			return RangesImpl.FIND_RANGES_BORDER;
		default:
			throw new IllegalArgumentException( "Extension type not supported: " + extension.type() );
		}
	}

	// TODO javadoc
	//    FORWARD
	//    BACKWARD
	//    STAY do not move, always take the same input value (used for border extension)
	//    CONSTANT don't take value from input (use constant OOB value instead)
	enum Direction
	{
		FORWARD,
		BACKWARD,
		STAY,
		CONSTANT;
	}

	/**
	 * Instructions for copying along a particular dimension. (To copy an <em>n</em>-dimensional
	 * region, <em>n</em> Ranges are combined.)
	 * <p>
	 * Copy {@code w} elements to coordinates {@code x} through {@code x + w}
	 * (exclusive) in destination, from source cell with {@code gridx} grid
	 * coordinate, starting at coordinate {@code cellx} within cell, and from
	 * there moving in {@code dir} for successive source elements.
	 * <p>
	 * It is guaranteed that all {@code w} elements fall within the same cell
	 * (i.e., primitive array).
	 */
	class Range
	{
		final int gridx;
		final int cellx;
		final int w;
		final Direction dir;
		final int x;

		public Range( final int gridx, final int cellx, final int w, final Direction dir, final int x )
		{
			this.gridx = gridx;
			this.cellx = cellx;
			this.w = w;
			this.dir = dir;
			this.x = x;
		}

		@Override
		public String toString()
		{
			return "Range{gridx=" + gridx + ", cellx=" + cellx + ", w=" + w + ", dir=" + dir + ", x=" + x + '}';
		}

		@Override
		public boolean equals( final Object o )
		{
			if ( this == o )
				return true;
			if ( o == null || getClass() != o.getClass() )
				return false;
			final Range range = ( Range ) o;
			return gridx == range.gridx && cellx == range.cellx && w == range.w && x == range.x && dir == range.dir;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash( gridx, cellx, w, dir, x );
		}
	}
}
