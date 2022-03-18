package net.imglib2.type.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.stream.LongStream;

import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.numeric.LongVectorType;
import net.imglib2.util.Fraction;

public class LongVectorTypeTest
{

	@Test
	public void testLongVectorType()
	{
		final LongVectorType x = new LongVectorType( 3 );
		final LongVectorType y = new LongVectorType( 3 );
		final long[] arr = new long[] { 1, 2, 3 };
		final long[] pos = new long[] { 10, 11, 12 };
		final long[] result = new long[ 3 ];

		// test set and get
		x.set( arr );
		y.set( x );
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( arr[ i ], x.getLong( i ) );
			assertEquals( arr[ i ], y.getLong( i ) );
		}

		// test read
		x.read( result );
		assertArrayEquals( arr, result );

		// zero and one
		x.setZero();
		y.setOne();
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( 0, x.getLong( i ) );
			assertEquals( 1, y.getLong( i ) );
		}

		// localizable and positionable methods
		x.setPosition( pos );
		x.read( result );
		assertArrayEquals( pos, result );

		y.setPosition( x );
		y.read( result );
		assertArrayEquals( pos, result );
	}

	@Test
	public void testLongVectorTypeImage()
	{
		final int vecElements = 5;
		final long numSamples = 32;

		// make an image
		final LongArray dataAccess = new LongArray( LongStream.range( 0, vecElements * numSamples ).toArray() );
		final ArrayImg< LongVectorType, LongArray > img = new ArrayImg<>( dataAccess, new long[] { numSamples }, new Fraction( vecElements, 1 ) );
		final LongVectorType t = new LongVectorType( img, vecElements );
		img.setLinkedType( t );

		long trueval = 0;
		ArrayCursor< LongVectorType > c = img.cursor();
		while ( c.hasNext() )
		{
			LongVectorType v = c.next();
			for ( int i = 0; i < vecElements; i++ )
				assertEquals( trueval++, v.getLong( i ) );
		}

	}

}
