package net.imglib2.algorithm.region.localneighborhood;

import static org.junit.Assert.assertEquals;
import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class DiamondTipsNeighorhoodTest
{

	@Test
	public final void testBehavior() {
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 10, 10 );
		final DiamondTipsNeighborhood< UnsignedByteType > diamond = new DiamondTipsNeighborhood< UnsignedByteType >( new long[] { 4, 5 }, 3, img.randomAccess() );

		final Cursor< UnsignedByteType > cursor = diamond.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.get().set( 100 );
		}

		// Test
		final long[][] targetPos = new long[][] { { 1, 5 }, { 7, 5 }, { 4, 2 }, { 4, 8 } };
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		for ( int i = 0; i < targetPos.length; i++ )
		{
			final long[] pos = targetPos[ i ];
			ra.setPosition( pos );
			assertEquals( 100, ra.get().get() );
		}

	}

	@Test
	public final void testSize()
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 10, 10 );
		final DiamondTipsNeighborhood< UnsignedByteType > diamond = new DiamondTipsNeighborhood< UnsignedByteType >( new long[] { 4, 5 }, 3, img.randomAccess() );
		assertEquals( 4, diamond.size() );
	}

	@Test
	public final void testIntervals()
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 10, 10 );
		final DiamondTipsNeighborhood< UnsignedByteType > diamond = new DiamondTipsNeighborhood< UnsignedByteType >( new long[] { 4, 5 }, 3, img.randomAccess() );
		assertEquals( 1, diamond.min( 0 ) );
		assertEquals( 2, diamond.min( 1 ) );
		assertEquals( 7, diamond.max( 0 ) );
		assertEquals( 8, diamond.max( 1 ) );
	}

}
