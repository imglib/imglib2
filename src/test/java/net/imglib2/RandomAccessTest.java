package net.imglib2;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.type.numeric.integer.ByteType;

public class RandomAccessTest
{

	@Test
	public void testSetPositionAndGet()
	{
		final ArrayImg< ByteType, ByteArray > img = ArrayImgs.bytes( 2, 3, 4 );
		new Random( 100 ).nextBytes( img.update( null ).getCurrentStorageArray() );
		final ArrayCursor< ByteType > cursor = img.cursor();
		final ArrayRandomAccess< ByteType > access = img.randomAccess();
		final long[] longPosition = new long[ img.numDimensions() ];
		final int[] intPosition = new int[ img.numDimensions() ];

		while ( cursor.hasNext() )
		{
			final ByteType reference = cursor.next();
			cursor.localize( longPosition );
			cursor.localize( intPosition );

			Assert.assertEquals( reference, access.setPositionAndGet( cursor ) );
			Assert.assertEquals( reference, access.setPositionAndGet( longPosition ) );
			Assert.assertEquals( reference, access.setPositionAndGet( intPosition ) );
		}

	}

}
