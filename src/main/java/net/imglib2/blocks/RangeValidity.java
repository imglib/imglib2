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

import java.util.Arrays;
import java.util.List;

/**
 * Per-range validity, that is, validity in <em>range space</em>.
 * <p>
 * A block copy is decomposed into {@link Ranges.Range Ranges} per dimension.
 * This tiles the target block into rectangular boxes. All pixels in a box come
 * from the same {@code Cell} (or are out-of-bounds). Validity is constant
 * in each box, and is therefore stored with one {@code byte} per box instead
 * of per pixel.
 * <p>
 * Consumers that only need to know whether the block was fully loaded use
 * {@link #allValid()} and never pay for per-pixel validity. Consumers that need
 * random access to per-pixel validity (interpolation, for example) call {@link
 * #expandTo(byte[])}.
 */
final class RangeValidity implements Validity
{
	/**
	 * {@code widths[d][i]} is the width (in pixels) of the {@code i}-th range
	 * in dimension {@code d}. The ranges of a dimension tile the block in that
	 * dimension without gaps or overlap.
	 * <p>
	 * {@code widths[d].length} is the number of ranges in dimension {@code d}.
	 */
	private final int[][] widths;

	/**
	 * Flattened strides in range space. {@code rangeSteps()[d]} is the
	 * distance, in {@link #values()}, between successive ranges of dimension
	 * {@code d}.
	 */
	private final int[] rangeSteps;

	/**
	 * One entry per range combination, flattened with {@link #rangeSteps}.
	 */
	private final byte[] valid;

	/**
	 * Uses {@code widths} and {@code valid} (no copies!)
	 * and populates {@link #rangeSteps} from the widths.
	 * <p>
	 * {@code widths[d][i]} is the width (in pixels) of the {@code i}-th range
	 * in dimension {@code d}. The ranges of a dimension tile the block in that
	 * dimension without gaps or overlap.
	 * <p>
	 * {@code widths[d].length} is the number of ranges in dimension {@code d}.
	 *
	 * @param widths
	 * 		per-dimension range widths
	 * @param valid
	 * 		validity per range combination. Length must be exactly the number
	 *      of range combinations.
	 */
	RangeValidity( final int[][] widths, final byte[] valid )
	{
		final int n = widths.length;
		this.widths = widths;
		this.valid = valid;

		rangeSteps = rangeSteps( widths );
		final int len = rangeSteps[ n - 1 ] * widths[ n - 1 ].length;
		if ( valid.length != len )
			throw new IllegalArgumentException( "valid.length should be " + len );
	}

	/**
	 * Construct from the per-dimension range lists of a block copy. All
	 * validity values start out {@code 0}; the copier fills them in.
	 */
	RangeValidity( final List< Ranges.Range >[] rangesPerDimension )
	{
		final int n = rangesPerDimension.length;

		widths = new int[ n ][];
		for ( int d = 0; d < n; ++d )
			widths[ d ] = widths(rangesPerDimension[ d ] );

		rangeSteps = rangeSteps( widths );
		final int len = rangeSteps[ n - 1 ] * widths[ n - 1 ].length;

		valid = new byte[ len ];
	}

	private static int[] widths(final List< Ranges.Range > ranges) {
		final int len = ranges.size();
		final int[] w = new int[ len ];
		for ( int i = 0; i < len; ++i )
			w[ i ] = ranges.get( i ).w;
		return w;
	}

	private static int[] rangeSteps(final int[][] widths) {
		final int n = widths.length;
		final int[] rsteps = new int[ n ];
		rsteps[ 0 ] = 1;
		for ( int d = 1; d < n; ++d )
			rsteps[ d ] = rsteps[ d - 1 ] * widths[ d - 1 ].length;
		return rsteps;
	}

	/**
	 * Number of pixels of the block this describes. Derived from the range
	 * widths on each call.
	 */
	@Override
	public int length()
	{
		if ( widths.length == 0 )
			return 0;
		int len = 1;
		for ( int d = 0; d < widths.length; ++d )
			len *= size( d );
		return len;
	}

	/**
	 * @return size of the block (in pixels) in dimension {@code d}.
	 */
	private int size( final int d )
	{
		int s = 0;
		for ( final int w : widths[ d ] )
			s += w;
		return s;
	}

	/**
	 * @return {@code true}, if every pixel of the block is valid.
	 */
	@Override
	public boolean allValid()
	{
		for ( final byte b : valid )
			if ( b == 0 )
				return false;
		return true;
	}

	/**
	 * Expand into per-pixel validity. {@code dest} must have at least {@link
	 * #length()} elements.
	 */
	@Override
	public void expandTo( final byte[] dest )
	{
		final int n = widths.length;
		if ( n == 0 )
			return;

		// Flattened strides in pixel space. Only expansion needs these, so they
		// are derived here instead of being stored. After the loop, pstep is
		// the number of pixels.
		final int[] pixelSteps = new int[ n ];
		int pstep = 1;
		for ( int d = 0; d < n; ++d )
		{
			pixelSteps[ d ] = pstep;
			pstep *= size( d );
		}

		if ( dest.length < pstep )
			throw new IllegalArgumentException( "dest is too small: need " + pstep + ", got " + dest.length );

		expand( dest, pixelSteps, n - 1, 0, 0 );
	}

	private void expand( final byte[] dest, final int[] pixelSteps, final int d, final int rangeOffset, final int destOffset )
	{
		final int[] w_d = widths[ d ];
		final int nr = w_d.length;
		final int rstep = rangeSteps[ d ];
		final int pstep = pixelSteps[ d ];
		int x = 0;
		for ( int i = 0; i < nr; ++i )
		{
			final int w = w_d[ i ];
			final int ro = rangeOffset + i * rstep;
			final int po = destOffset + x * pstep;
			if ( d == 0 )
				Arrays.fill( dest, po, po + w, valid[ ro ] );
			else
			{
				expand( dest, pixelSteps, d - 1, ro, po );
				for ( int j = 1; j < w; ++j )
					System.arraycopy( dest, po, dest, po + j * pstep, pstep );
			}
			x += w;
		}
	}

	/**
	 * Per-dimension range widths. {@code widths()[d][i]} is the width (in
	 * pixels) of the {@code i}-th range in dimension {@code d}.
	 */
	int[][] widths()
	{
		return widths;
	}

	/**
	 * Flattened strides in range space. {@code rangeSteps()[d]} is the
	 * distance, in {@link #values()}, between successive ranges of dimension
	 * {@code d}.
	 */
	int[] rangeSteps()
	{
		return rangeSteps;
	}

	/**
	 * The validity values, one per range combination, flattened with {@link
	 * #rangeSteps()}. The copier writes into this directly, immediately after
	 * construction.
	 */
	byte[] values()
	{
		return valid;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder( "RangeValidity{widths=[" );
		for ( final int[] width : widths )
			sb.append( Arrays.toString( width ) );
		sb.append( "], valid=" ).append( Arrays.toString( valid ) ).append( '}' );
		return sb.toString();
	}
}
