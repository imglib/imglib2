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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Random;

import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.PrimitiveType;

import org.junit.Test;

/**
 * {@link PermuteInvert#permuteAndInvert(RangeValidity)} works in range space. It
 * must produce the same per-pixel validity as expanding first and permuting the
 * pixels, which is what {@link PermuteInvert#permuteAndInvert(Object, Object,
 * int[])} does.
 */
public class PermuteInvertRangeValidityTest
{
	private static final int[][] PERMUTATIONS_3D = {
			{ 0, 1, 2 }, { 0, 2, 1 }, { 1, 0, 2 }, { 1, 2, 0 }, { 2, 0, 1 }, { 2, 1, 0 } };

	@Test
	public void rangeSpaceAgreesWithPerPixel3D()
	{
		final Random random = new Random( 1L );
		for ( final int[] permutation : PERMUTATIONS_3D )
			for ( int invMask = 0; invMask < 8; ++invMask )
				for ( int trial = 0; trial < 5; ++trial )
					check( permutation, inversions( invMask, 3 ), randomWidths( random, 3 ), random );
	}

	@Test
	public void rangeSpaceAgreesWithPerPixel1DAnd2D()
	{
		final Random random = new Random( 2L );
		// 1D: identity and inversion
		for ( int invMask = 0; invMask < 2; ++invMask )
			check( new int[] { 0 }, inversions( invMask, 1 ), randomWidths( random, 1 ), random );
		// 2D: both permutations, all inversions
		for ( final int[] permutation : new int[][] { { 0, 1 }, { 1, 0 } } )
			for ( int invMask = 0; invMask < 4; ++invMask )
				check( permutation, inversions( invMask, 2 ), randomWidths( random, 2 ), random );
	}

	/**
	 * A single range per dimension means range space and pixel space differ by
	 * pure scaling, which is the degenerate case most likely to hide an index
	 * mistake.
	 */
	@Test
	public void singleRangePerDimension()
	{
		final Random random = new Random( 3L );
		final int[][] widths = { { 4 }, { 3 }, { 2 } };
		for ( final int[] permutation : PERMUTATIONS_3D )
			for ( int invMask = 0; invMask < 8; ++invMask )
				check( permutation, inversions( invMask, 3 ), widths, random );
	}

	private static void check(
			final int[] permutation,
			final boolean[] inversion,
			final int[][] widths,
			final Random random )
	{
		final int n = permutation.length;

		final MixedTransform transform = new MixedTransform( n, n );
		transform.setComponentMapping( permutation );
		transform.setComponentInversion( inversion );
		final PermuteInvert permuteInvert =
				new PermuteInvert( MemCopy.forPrimitiveType( PrimitiveType.BYTE ), transform );

		int numCombinations = 1;
		for ( final int[] w_d : widths )
			numCombinations *= w_d.length;
		final byte[] values = new byte[ numCombinations ];
		for ( int i = 0; i < values.length; ++i )
			values[ i ] = ( byte ) random.nextInt( 2 );

		final RangeValidity src = new RangeValidity( deepClone( widths ), values.clone() );

		// path 1: permute in range space, then expand
		final RangeValidity permuted = permuteInvert.permuteAndInvert( src );
		final byte[] viaRangeSpace = new byte[ permuted.length() ];
		permuted.expandTo( viaRangeSpace );

		// path 2: expand, then permute per pixel
		final int[] srcSize = new int[ n ];
		for ( int d = 0; d < n; ++d )
			for ( final int w : widths[ d ] )
				srcSize[ d ] += w;
		final int[] destSize = new int[ n ];
		permuteInvert.permuteSize( srcSize, destSize );

		final byte[] srcExpanded = new byte[ src.length() ];
		src.expandTo( srcExpanded );
		final byte[] viaPixels = new byte[ src.length() ];
		permuteInvert.permuteAndInvert( srcExpanded, viaPixels, destSize );

		final String msg = "permutation=" + Arrays.toString( permutation )
				+ " inversion=" + Arrays.toString( inversion )
				+ " widths=" + Arrays.deepToString( widths );
		assertEquals( msg + " (pixel count)", src.length(), permuted.length() );
		assertArrayEquals( msg, viaPixels, viaRangeSpace );
	}

	private static boolean[] inversions( final int mask, final int n )
	{
		final boolean[] inversion = new boolean[ n ];
		for ( int d = 0; d < n; ++d )
			inversion[ d ] = ( mask & ( 1 << d ) ) != 0;
		return inversion;
	}

	private static int[][] randomWidths( final Random random, final int n )
	{
		final int[][] widths = new int[ n ][];
		for ( int d = 0; d < n; ++d )
		{
			widths[ d ] = new int[ 1 + random.nextInt( 3 ) ];
			for ( int i = 0; i < widths[ d ].length; ++i )
				widths[ d ][ i ] = 1 + random.nextInt( 3 );
		}
		return widths;
	}

	private static int[][] deepClone( final int[][] widths )
	{
		final int[][] copy = new int[ widths.length ][];
		for ( int d = 0; d < widths.length; ++d )
			copy[ d ] = widths[ d ].clone();
		return copy;
	}
}
