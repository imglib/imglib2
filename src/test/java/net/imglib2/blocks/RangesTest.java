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
import net.imglib2.blocks.Ranges.Range;
import org.junit.Assert;
import org.junit.Test;

import static net.imglib2.blocks.Ranges.Direction.BACKWARD;
import static net.imglib2.blocks.Ranges.Direction.CONSTANT;
import static net.imglib2.blocks.Ranges.Direction.FORWARD;
import static net.imglib2.blocks.Ranges.Direction.STAY;

public class RangesTest
{

	// simplified 1D copy() for testing computed ranges
	// takes into account Range.dir copy direction
	static void copy( final List< Range > ranges, final int[][] data, final int[] dest )
	{
		int x = 0;
		for ( Range range : ranges )
		{
			if ( range.dir == CONSTANT )
			{
				for ( int i = 0; i < range.w; ++i )
					dest[ x++ ] = -1;
			}
			else
			{
				final int[] cell = data[ range.gridx ];
				if ( range.dir == FORWARD )
				{
					for ( int i = 0; i < range.w; ++i )
						dest[ x++ ] = cell[ range.cellx + i ];
				}
				else if ( range.dir == BACKWARD )
				{
					for ( int i = 0; i < range.w; ++i )
						dest[ x++ ] = cell[ range.cellx - i ];
				}
				else if ( range.dir == STAY )
				{
					for ( int i = 0; i < range.w; ++i )
						dest[ x++ ] = cell[ range.cellx ];
				}
			}
		}
	}


	@Test
	public void copyInBounds()
	{
		// test data:
		// image consisting of 3 cells with 5 elements each.
		// border cell is not truncated.
		int[][] data = {
				{ 0, 1, 2, 3, 4 },
				{ 5, 6, 7, 8, 9 },
				{ 10, 11, 12, 13, 14 }
		};
		final int iw = 15; // image width
		final int cw = 5; // cell width

		final int[] dest = new int[ 9 ];
		final int bw = dest.length;
		final List< Range > ranges = RangesImpl.findRanges_constant( 3, bw, iw, cw );
		copy( ranges, data, dest );

		final Range[] expectedRanges = {
				new Range( 0, 3, 2, FORWARD, 0 ),
				new Range( 1, 0, 5, FORWARD, 2 ),
				new Range( 2, 0, 2, FORWARD, 7 )
		};
		Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

		final int[] expectedDest = new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		Assert.assertArrayEquals( expectedDest, dest );
	}

	@Test
	public void copyMirrorSingle()
	{
		// test data:
		// image consisting of 2 cells with 4 elements each.
		// border cell is truncated.
		int[][] data = {
				{ 0, 1, 2, 3 },
				{ 4, 5 }
		};
		final int iw = 6; // image width
		final int cw = 4; // cell width

		// singled mirrored it looks like this:
		//   4   5   4   3   2   1   0   1   2   3   4   5   4   3   2   1   0   1
		// ----|-------|-----------|===============|=======|---|---------------|----


		final int[] dest = new int[ 7 ];
		final int bw = dest.length;

		{
			final List< Range > ranges = RangesImpl.findRanges_mirror_single( -3, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range(0, 3, 3, BACKWARD, 0),
					new Range(0, 0, 4, FORWARD, 3),
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 3, 2, 1, 0, 1, 2, 3 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
		{
			final List< Range > ranges = RangesImpl.findRanges_mirror_single( 5, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range( 1, 1, 1, FORWARD, 0 ),
					new Range( 1, 0, 1, BACKWARD, 1 ),
					new Range( 0, 3, 3, BACKWARD, 2 ),
					new Range( 0, 0, 2, FORWARD, 5 )
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 5, 4, 3, 2, 1, 0, 1};
			Assert.assertArrayEquals( expectedDest, dest );
		}
	}

	@Test
	public void copyMirrorDouble()
	{
		// test data:
		// image consisting of 2 cells with 4 elements each.
		// border cell is truncated.
		int[][] data = {
				{ 0, 1, 2, 3 },
				{ 4, 5 }
		};
		final int iw = 6; // image width
		final int cw = 4; // cell width

		// double mirrored it looks like this:
		//   5   4   3   2   1   0   0   1   2   3   4   5   5   4   3   2   1   0
		// |-------|---------------|===============|=======|-------|---------------|

		final int[] dest = new int[ 7 ];
		final int bw = dest.length;

		{
			final List< Range > ranges = RangesImpl.findRanges_mirror_double( -3, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range( 0, 2, 3, BACKWARD, 0 ),
					new Range( 0, 0, 4, FORWARD, 3 )
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 2, 1, 0, 0, 1, 2, 3 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
		{
			final List< Range > ranges = RangesImpl.findRanges_mirror_double( 3, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range(0, 3, 1, FORWARD, 0),
					new Range(1, 0, 2, FORWARD, 1),
					new Range(1, 1, 2, BACKWARD, 3),
					new Range(0, 3, 2, BACKWARD, 5),
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 3, 4, 5, 5, 4, 3, 2 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
	}

	@Test
	public void copyBorder()
	{
		// test data:
		// image consisting of 2 cells with 4 elements each.
		// border cell is truncated.
		int[][] data = {
				{ 0, 1, 2, 3 },
				{ 4, 5 }
		};
		final int iw = 6; // image width
		final int cw = 4; // cell width

		// border extended it looks like this:
		//   0   0   0   0   1   2   3   4   5   5   5   5
		// ------------|===============|=======|------------

		final int[] dest = new int[ 7 ];
		final int bw = dest.length;

		{
			final List< Range > ranges = RangesImpl.findRanges_border( -3, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range( 0, 0, 3, STAY, 0 ),
					new Range( 0, 0, 4, FORWARD, 3 )
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 0, 0, 0, 0, 1, 2, 3 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
		{
			final List< Range > ranges = RangesImpl.findRanges_border( 1, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range( 0, 1, 3, FORWARD, 0 ),
					new Range( 1, 0, 2, FORWARD, 3 ),
					new Range( 1, 1, 2, STAY, 5 )
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 1, 2, 3, 4, 5, 5, 5 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
	}

	@Test
	public void copyConstant()
	{
		// test data:
		// image consisting of 2 cells with 4 elements each.
		// border cell is truncated.
		int[][] data = {
				{ 0, 1, 2, 3 },
				{ 4, 5 }
		};
		final int iw = 6; // image width
		final int cw = 4; // cell width

		// border extended it looks like this:
		//  -1  -1  -1   0   1   2   3   4   5  -1  -1  -1
		// ------------|===============|=======|------------

		final int[] dest = new int[ 7 ];
		final int bw = dest.length;

		{
			final List< Range > ranges = RangesImpl.findRanges_constant( -3, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range( -1, -1, 3, CONSTANT, 0 ),
					new Range( 0, 0, 4, FORWARD, 3 )
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { -1, -1, -1, 0, 1, 2, 3 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
		{
			final List< Range > ranges = RangesImpl.findRanges_constant( 1, bw, iw, cw );
			copy( ranges, data, dest );

			final Range[] expectedRanges = {
					new Range( 0, 1, 3, FORWARD, 0 ),
					new Range( 1, 0, 2, FORWARD, 3 ),
					new Range( -1, -1, 2, CONSTANT, 5 )
			};
			Assert.assertArrayEquals( expectedRanges, ranges.toArray() );

			final int[] expectedDest = new int[] { 1, 2, 3, 4, 5, -1, -1 };
			Assert.assertArrayEquals( expectedDest, dest );
		}
	}
}
