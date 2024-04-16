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
package net.imglib2.blocks;

import java.util.ArrayList;
import java.util.List;

import static net.imglib2.blocks.Ranges.Direction.BACKWARD;
import static net.imglib2.blocks.Ranges.Direction.CONSTANT;
import static net.imglib2.blocks.Ranges.Direction.FORWARD;
import static net.imglib2.blocks.Ranges.Direction.STAY;

class RangesImpl
{
	static Ranges FIND_RANGES_CONSTANT = RangesImpl::findRanges_constant;
	static Ranges FIND_RANGES_MIRROR_SINGLE = RangesImpl::findRanges_mirror_single;
	static Ranges FIND_RANGES_MIRROR_DOUBLE = RangesImpl::findRanges_mirror_double;
	static Ranges FIND_RANGES_BORDER = RangesImpl::findRanges_border;

	/**
	 * Find ranges for one dimension.
	 * <p>
	 * Out-of-bounds values are set to a constant.
	 * <p>
	 * Split the requested interval into ranges covering (possibly partial)
	 * cells of the input image. The requested interval is given by start
	 * coordinate {@code bx} (in the extended source image) and size of the
	 * block to copy {@code bw}, in a particular dimension. The full size of the
	 * (non-extended) image in this dimension is given by {@code iw}, the size
	 * of a (non-truncated) cell in this dimension is given by {@code cw}.
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
	static List< Ranges.Range > findRanges_constant(
			long bx, // start of block in source coordinates (in pixels)
			int bw, // width of block to copy (in pixels)
			final long iw, // source image width (in pixels)
			final int cw  // source cell width (in pixels)
	)
	{
		List< Ranges.Range > ranges = new ArrayList<>();

		if ( bw <= 0 )
			return ranges;

		int x = 0;
		if ( bx < 0 )
		{
			int w = ( int ) Math.min( bw, -bx );
			ranges.add( new Ranges.Range( -1, -1, w, CONSTANT, x ) );
			bw -= w;
			bx += w; // = 0
			x += w;
		}

		if ( bw <= 0 )
			return ranges;

		int gx = ( int ) ( bx / cw );
		int cx = ( int ) ( bx - ( ( long ) gx * cw ) );
		while ( bw > 0 && bx < iw )
		{
			final int w = Math.min( bw, cellWidth( gx, cw, iw ) - cx );
			ranges.add( new Ranges.Range( gx, cx, w, FORWARD, x ) );
			bw -= w;
			bx += w;
			x += w;
			++gx;
			cx = 0;
		}

		if ( bw > 0 )
			ranges.add( new Ranges.Range( -1, -1, bw, CONSTANT, x ) );

		return ranges;
	}

	/**
	 * Find ranges for one dimension.
	 * <p>
	 * Out-of-bounds values are determined by border extension (clamping to
	 * the nearest pixel in the image).
	 * <p>
	 * Split the requested interval into ranges covering (possibly partial)
	 * cells of the input image. The requested interval is given by start
	 * coordinate {@code bx} (in the extended source image) and size of the
	 * block to copy {@code bw}, in a particular dimension. The full size of the
	 * (non-extended) image in this dimension is given by {@code iw}, the size
	 * of a (non-truncated) cell in this dimension is given by {@code cw}.
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
	static List< Ranges.Range > findRanges_border(
			long bx, // start of block in source coordinates (in pixels)
			int bw, // width of block to copy (in pixels)
			final long iw, // source image width (in pixels)
			final int cw  // source cell width (in pixels)
	)
	{
		List< Ranges.Range > ranges = new ArrayList<>();

		if ( bw <= 0 )
			return ranges;

		int x = 0;
		if ( bx < 0 )
		{
			int w = ( int ) Math.min( bw, -bx );
			ranges.add( new Ranges.Range( 0, 0, w, STAY, x ) );
			bw -= w;
			bx += w; // = 0
			x += w;
		}

		if ( bw <= 0 )
			return ranges;

		int gx = ( int ) ( bx / cw );
		int cx = ( int ) ( bx - ( ( long ) gx * cw ) );
		while ( bw > 0 && bx < iw )
		{
			final int w = Math.min( bw, cellWidth( gx, cw, iw ) - cx );
			ranges.add( new Ranges.Range( gx, cx, w, FORWARD, x ) );
			bw -= w;
			bx += w;
			x += w;
			++gx;
			cx = 0;
		}

		if ( bw <= 0 )
			return ranges;

		gx = ( int ) ( ( iw - 1 ) / cw );
		cx = cellWidth( gx, cw, iw ) - 1;
		ranges.add( new Ranges.Range( gx, cx, bw, STAY, x ) );

		return ranges;
	}

	/**
	 * Find ranges for one dimension.
	 * <p>
	 * Out-of-bounds values are determined by mirroring with double boundary,
	 * i.e., border pixels are repeated.
	 * <p>
	 * Split the requested interval into ranges covering (possibly partial)
	 * cells of the input image. The requested interval is given by start
	 * coordinate {@code bx} (in the extended source image) and size of the
	 * block to copy {@code bw}, in a particular dimension. The full size of the
	 * (non-extended) image in this dimension is given by {@code iw}, the size
	 * of a (non-truncated) cell in this dimension is given by {@code cw}.
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
	static List< Ranges.Range > findRanges_mirror_double(
			long bx, // start of block in source coordinates (in pixels)
			int bw, // width of block to copy (in pixels)
			final long iw, // source image width (in pixels)
			final int cw  // source cell width (in pixels)
	)
	{
		List< Ranges.Range > ranges = new ArrayList<>();

		final long pi = 2 * iw;
		bx = ( bx < 0 )
				? ( bx + 1 ) % pi + pi - 1
				: bx % pi;
		Ranges.Direction dir = FORWARD;
		if ( bx >= iw )
		{
			bx = pi - 1 - bx;
			dir = BACKWARD;
		}

		int gx = ( int ) ( bx / cw );
		int cx = ( int ) ( bx - ( ( long ) gx * cw ) );
		int x = 0;
		while ( bw > 0 )
		{
			if ( dir == FORWARD )
			{
				final int gxw = cellWidth( gx, cw, iw );
				final int w = Math.min( bw, gxw - cx );
				final Ranges.Range range = new Ranges.Range( gx, cx, w, FORWARD, x );
				ranges.add( range );

				bw -= w;
				x += w;

				if ( ( long ) ++gx * cw >= iw ) // moving out of bounds
				{
					--gx;
					cx = gxw - 1;
					dir = BACKWARD;
				}
				else
				{
					cx = 0;
				}
			}
			else // dir == BACKWARD
			{
				final int w = Math.min( bw, cx + 1 );
				final Ranges.Range range = new Ranges.Range( gx, cx, w, BACKWARD, x );
				ranges.add( range );

				bw -= w;
				x += w;

				if ( gx == 0 ) // moving into bounds
				{
					cx = 0;
					dir = FORWARD;
				}
				else
				{
					cx = cellWidth( --gx, cw, iw ) - 1;
				}
			}

		}
		return ranges;
	}

	/**
	 * Find ranges for one dimension.
	 * <p>
	 * Out-of-bounds values are determined by mirroring with single boundary,
	 * i.e., border pixels are not repeated.
	 * <p>
	 * Split the requested interval into ranges covering (possibly partial)
	 * cells of the input image. The requested interval is given by start
	 * coordinate {@code bx} (in the extended source image) and size of the
	 * block to copy {@code bw}, in a particular dimension. The full size of the
	 * (non-extended) image in this dimension is given by {@code iw}, the size
	 * of a (non-truncated) cell in this dimension is given by {@code cw}.
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
	static List< Ranges.Range > findRanges_mirror_single(
			long bx, // start of block in source coordinates (in pixels)
			int bw, // width of block to copy (in pixels)
			final long iw, // source image width (in pixels)
			final int cw  // source cell width (in pixels)
	)
	{
		List< Ranges.Range > ranges = new ArrayList<>();

		final long pi = 2 * iw - 2;
		bx = ( bx < 0 )
				? ( bx + 1 ) % pi + pi - 1
				: bx % pi;
		Ranges.Direction dir = FORWARD;
		if ( bx >= iw )
		{
			bx = pi - bx;
			dir = BACKWARD;
		}

		int gx = ( int ) ( bx / cw );
		int cx = ( int ) ( bx - ( ( long ) gx * cw ) );
		int x = 0;
		while ( bw > 0 )
		{
			if ( dir == FORWARD )
			{
				final int gxw = cellWidth( gx, cw, iw );
				final int w = Math.min( bw, gxw - cx );
				final Ranges.Range range = new Ranges.Range( gx, cx, w, FORWARD, x );
				ranges.add( range );

				bw -= w;
				x += w;

				if ( ( long ) ++gx * cw >= iw ) // moving out of bounds
				{
					--gx;
					cx = gxw - 2;
					dir = BACKWARD;
				}
				else
				{
					cx = 0;
				}
			}
			else // dir == BACKWARD
			{
				final int w = Math.min( bw, gx == 0 ? cx : ( cx + 1 ) );
				final Ranges.Range range = new Ranges.Range( gx, cx, w, BACKWARD, x );
				ranges.add( range );

				bw -= w;
				x += w;

				if ( gx == 0 ) // moving into bounds
				{
					cx = 0;
					dir = FORWARD;
				}
				else
				{
					cx = cellWidth( --gx, cw, iw ) - 1;
				}
			}

		}
		return ranges;
	}

	/**
	 * Get width of a cell (depending on whether it's an inner cell or a border cell).
	 *
	 * @param gx
	 * 		grid coordinate of the cell
	 * @param cw
	 * 		cell width (of grid)
	 * @param iw
	 * 		image width
	 *
	 * @return cell width
	 */
	private static int cellWidth( final int gx, final int cw, final long iw )
	{
		final int gw = ( int ) ( iw / cw );
		if ( gx < gw )
			return cw;
		else if ( gx == gw )
			return ( int ) ( iw - cw * gw );
		else
			throw new IllegalArgumentException();
	}
}
