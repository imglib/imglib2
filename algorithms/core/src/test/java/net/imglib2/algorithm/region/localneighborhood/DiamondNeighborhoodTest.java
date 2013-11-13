package net.imglib2.algorithm.region.localneighborhood;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class DiamondNeighborhoodTest
{

	@Test
	public final void testSize()
	{

		final int[] dims = new int[] { 1, 2, 3, 4, 5 };
		final long[] radiuses = new long[] { 3, 5, 7 };
		for ( final int d : dims )
		{
			for ( final long r : radiuses )
			{
				final long[] size = new long[ d ];
				Arrays.fill( size, 2 * r + 1 );
				final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( size );
				final RandomAccess< UnsignedByteType > ra = img.randomAccess();

				final long[] pos = new long[ d ];
				Arrays.fill( pos, r - 1 );

				final DiamondNeighborhood< UnsignedByteType > n = new DiamondNeighborhood< UnsignedByteType >( pos, r, ra );
				long iterated = 0;
				for ( @SuppressWarnings( "unused" )
				final UnsignedByteType p : n )
				{
					iterated++;
				}
				assertEquals( "For dim = " + d + ", radius = " + r, n.size(), iterated );

			}
		}
	}

}
