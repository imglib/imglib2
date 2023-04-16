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
