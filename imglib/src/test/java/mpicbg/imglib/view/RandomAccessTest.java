package mpicbg.imglib.view;

import static org.junit.Assert.assertTrue;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RandomAccessTest
{
	Img< UnsignedByteType > img;

	@Before
	public void setUp()
	{
		long[] dimension = new long[] {100, 60, 10, 30, 50 };
		img = new ArrayImgFactory< UnsignedByteType >().create( dimension, new UnsignedByteType() );
	}

	@Test
	public void testRandomAccess()
	{
		RandomAccess< UnsignedByteType > a = img.randomAccess();

		long[] pos = new long[] { 28, 30, 5, 5, 12 };
		long[] dist = new long[] { 2, 3, 4, 2, 1 };
		
		testlocalize( a, pos );
		testfwd( a, pos );
		testbck( a, pos );
		testmove( a, pos, 3 );
		testmove( a, pos, dist );
	}

	@Test
	public void testFullSourceMapMixedAccess()
	{
		long[] offset = new long[] { 1, 10, 0, -5 };
		long[] dim = new long[] { 10, 10, 10, 10 };
		RandomAccess< UnsignedByteType > a = Views.superIntervalView( Views.flippedView( Views.hyperSlice( img, 2, 2 ), 3 ), offset, dim ).randomAccess();
		
		assertTrue( FullSourceMapMixedRandomAccess.class.isInstance( a ) );

		long[] pos = new long[] { 28, 30, 2, 15 };
		long[] dist = new long[] { 2, 3, 4, 1 };
		
		testlocalize( a, pos );
		testfwd( a, pos );
		testbck( a, pos );
		testmove( a, pos, 3 );
		testmove( a, pos, -2 );
		testmove( a, pos, dist );
	}

	public < T > void testlocalize( RandomAccess< T > a, final long[] pos )
	{
		long[] loc = new long[ pos.length ];
		long[] expected = pos.clone();

		a.setPosition( pos );
		a.localize( loc );			
		Assert.assertArrayEquals( expected, loc );
		
		for( int d = 0; d < a.numDimensions(); ++d )
		{
			Assert.assertTrue( expected[ d ] == a.getLongPosition( d ) );
			Assert.assertTrue( expected[ d ] == (long) a.getIntPosition( d ) );
			Assert.assertTrue( expected[ d ] == (long) a.getFloatPosition( d ) );
			Assert.assertTrue( expected[ d ] == (long) a.getDoublePosition( d ) );
		}
	}

	public < T > void testfwd( RandomAccess< T > a, final long[] pos )
	{
		long[] loc = new long[ pos.length ];
		long[] expected = new long[ pos.length ];

		for( int d = 0; d < a.numDimensions(); ++d )
		{
			a.setPosition( pos );
			a.fwd( d );
			a.localize( loc );
			
			for ( int i = 0; i < pos.length; ++i )
				expected[ i ] = pos[ i ];
			expected[ d ] += 1;

			Assert.assertArrayEquals( expected, loc );
		}
	}

	public < T > void testbck( RandomAccess< T > a, final long[] pos )
	{
		long[] loc = new long[ pos.length ];
		long[] expected = new long[ pos.length ];

		for( int d = 0; d < a.numDimensions(); ++d )
		{
			a.setPosition( pos );
			a.bck( d );
			a.localize( loc );
			
			for ( int i = 0; i < pos.length; ++i )
				expected[ i ] = pos[ i ];
			expected[ d ] -= 1;

			Assert.assertArrayEquals( expected, loc );
		}
	}

	public < T > void testmove( RandomAccess< T > a, final long[] pos, final long distance )
	{
		long[] loc = new long[ pos.length ];
		long[] expected = new long[ pos.length ];

		for( int d = 0; d < a.numDimensions(); ++d )
		{
			a.setPosition( pos );
			a.move( distance, d );
			a.localize( loc );
			
			for ( int i = 0; i < pos.length; ++i )
				expected[ i ] = pos[ i ];
			expected[ d ] += distance;

			Assert.assertArrayEquals( expected, loc );
		}
	}

	public < T > void testmove( RandomAccess< T > a, final long[] pos, final long[] distance )
	{
		long[] loc = new long[ pos.length ];
		long[] expected = new long[ pos.length ];

		for ( int d = 0; d < pos.length; ++d )
			expected[ d ] = pos[ d ] + distance[ d ];

		a.setPosition( pos );
		a.move( distance );
		a.localize( loc );
		
		Assert.assertArrayEquals( expected, loc );
	}
}
