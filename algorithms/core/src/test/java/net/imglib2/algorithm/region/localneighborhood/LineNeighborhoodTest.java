package net.imglib2.algorithm.region.localneighborhood;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.imglib2.Interval;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

import org.junit.Test;

public class LineNeighborhoodTest
{

	@Test
	public void testBehavior()
	{
		/*
		 * Create.
		 */

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 64, 64 );

		final long[][] positions = new long[][] { { 20, 20 }, { 40, 40 } };
		final long[] spans = new long[] { 3, 2 };
		final int[] dims = new int[] { 0, 1 };
		final boolean[] skipCenters = new boolean[] { true, false };
		final ArrayRandomAccess< UnsignedByteType > sourceRandomAccess = img.randomAccess();

		final ArrayList< Interval > intervals = new ArrayList< Interval >( 2 );

		for ( int i = 0; i < spans.length; i++ )
		{
			final HorizontalLineNeighborhood< UnsignedByteType > ln = new HorizontalLineNeighborhood< UnsignedByteType >( positions[ i ], spans[ i ], dims[ i ], skipCenters[ i ], sourceRandomAccess );

			for ( final UnsignedByteType t : ln )
			{
				t.setInteger( 255 );
			}

			intervals.add( ln.getStructuringElementBoundingBox() );
		}

		/*
		 * Test
		 */

		final ArrayCursor< UnsignedByteType > cursor = img.cursor();

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			boolean inside = false;
			for ( int i = 0; i < spans.length; i++ )
			{
				if ( !Intervals.contains( intervals.get( i ), cursor ) )
				{
					continue;
				}
				if ( skipCenters[ i ] )
				{
					boolean samePos = true;
					for ( int j = 0; j < img.numDimensions(); j++ )
					{
						if ( positions[ i ][ j ] != cursor.getLongPosition( j ) )
						{
							samePos = false;
							break;
						}
					}
					if ( samePos )
					{
						continue;
					}
				}
				inside = true;
				break;
			}
			if ( inside )
			{
				assertEquals( "At coordinates " + Util.printCoordinates( cursor ), 255, cursor.get().get() );
			}
			else
			{
				assertEquals( "At coordinates " + Util.printCoordinates( cursor ), 0, cursor.get().get() );
			}
		}
	}
}
