package net.imglib2.algorithm.region.localneighborhood;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Before;
import org.junit.Test;

public class PeriodicLineNeighborhoodTest
{

	private int[][][] expected;

	private ArrayImg< UnsignedByteType, ByteArray > img;

	private long[][] positions;

	private long[] spans;

	private int[][] increments;

	private PeriodicLineNeighborhood< UnsignedByteType >[] neighborhoods;

	@SuppressWarnings( "unchecked" )
	@Before
	public void setUp() throws Exception
	{
		img = ArrayImgs.unsignedBytes( 64, 64 );
		final ArrayRandomAccess< UnsignedByteType > sourceRandomAccess = img.randomAccess();
		positions = new long[][] { { 20, 20 }, { 40, 40 } };
		spans = new long[] { 3, 2 };
		increments = new int[][] { { 1, 2 }, { 3, -1 } };
		neighborhoods = new PeriodicLineNeighborhood[ spans.length ];

		for ( int i = 0; i < spans.length; i++ )
		{
			final PeriodicLineNeighborhood< UnsignedByteType > ln = new PeriodicLineNeighborhood< UnsignedByteType >( positions[ i ], spans[ i ], increments[ i ], sourceRandomAccess );
			for ( final UnsignedByteType t : ln )
			{
				t.setInteger( 255 );
			}
			neighborhoods[ i ] = ln;
		}

		expected = new int[][][] { { { 17, 14 }, { 18, 16 }, { 19, 18 }, { 20, 20 }, { 21, 22 }, { 22, 24 }, { 23, 26 } }, { { 34, 42 }, { 37, 41 }, { 40, 40 }, { 43, 39 }, { 46, 38 } } };

	}

	@Test
	public void testNIterated()
	{
		final ArrayCursor< UnsignedByteType > cursor = img.cursor();

		// How many pixels are on?
		int sum = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			if ( cursor.get().get() > 0 )
				sum++;
		}

		int expectedPix = 0;
		for ( final long span : spans )
		{
			expectedPix += 2 * span + 1;
		}
		assertEquals( expectedPix, sum );
	}

	@Test
	public void testIteration()
	{
		// One way: are the expected pixels on?
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		for ( final int[][] group : expected )
		{
			for ( final int[] coord : group )
			{
				ra.setPosition( coord );
				assertTrue( ra.get().get() > 0 );
			}
		}

		// The other way: if a pixel is on, is it expected?
		final ArrayCursor< UnsignedByteType > cursor = img.cursor();
		final int[] pos = new int[ img.numDimensions() ];
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			if ( cursor.get().get() > 0 )
			{
				cursor.localize( pos );
				boolean found = false;
				for ( final int[][] group : expected )
				{
					for ( final int[] coord : group )
					{
						if ( Arrays.equals( pos, coord ) )
						{
							found = true;
							break;
						}
					}
				}
				assertTrue( found );
			}

		}
	}

	@Test
	public void testSize()
	{
		for ( int i = 0; i < neighborhoods.length; i++ )
		{
			final PeriodicLineNeighborhood< UnsignedByteType > neighborhood = neighborhoods[ i ];
			assertEquals( 2 * spans[ i ] + 1, neighborhood.size() );
		}
	}

	@Test
	public void testMinInt()
	{
		for ( int i = 0; i < neighborhoods.length; i++ )
		{
			final PeriodicLineNeighborhood< UnsignedByteType > neighborhood = neighborhoods[ i ];
			for ( int d = 0; d < img.numDimensions(); d++ )
			{
				assertEquals( positions[ i ][ d ] - spans[ i ] * increments[ i ][ d ], neighborhood.min( d ) );
			}
		}
	}

	@Test
	public void testMaxInt()
	{
		for ( int i = 0; i < neighborhoods.length; i++ )
		{
			final PeriodicLineNeighborhood< UnsignedByteType > neighborhood = neighborhoods[ i ];
			for ( int d = 0; d < img.numDimensions(); d++ )
			{
				assertEquals( positions[ i ][ d ] + spans[ i ] * increments[ i ][ d ], neighborhood.max( d ) );
			}
		}
	}
}
