package mpicbg.imglib;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.type.numeric.integer.LongType;

import org.junit.Test;

public class IterableIntervalSubsetTest
{
	final static private Random rnd = new Random( 1234567 );
	
	final static private long[][] sizes = 
		new long[][]{
			{ 127 },
			{ 288 },	
			{ 135, 111 },
			{ 172, 131 },
			{  15,  13, 33 },		
			{ 110,  38, 30 },
			{ 109,  34, 111 },
			{  12,  43,  92, 10 },
			{  21,  34,  29, 13 },
			{   5,  12,  30,  4,  21 },
			{  14,  21,  13,  9,  12 }
		}; 
	
	final static private void print( final Object str )
	{
		System.out.println( str );
	}
	
	@Test
	public void testIntervalIterator()
	{
		for ( int i = 0; i < sizes.length; ++i )
		{
			print( "testcase " + i + " ( n=" + sizes[ i ].length + " )" );
			
			/* create and fill interval */
			long a = 0;
			final IterableInterval< LongType > iterable = new ArrayImgFactory< LongType >().create( sizes[ i ], new LongType() );
			for ( final LongType t : iterable )
				t.set( a++ );
			
			assertTrue( test( iterable ) );
		}
	}
	
	final static private boolean test( IterableInterval< LongType > iterable )
	{
		/* split into iterable interval subsets */
		long b = 0;
		long firstIndex = 0;
		long lastIndex = 0;
		while ( lastIndex < iterable.size() )
		{
			final int size = rnd.nextInt( ( int )iterable.size() / 5 );
			firstIndex = lastIndex + 1;
			lastIndex = firstIndex + size - 1;
			
			print( "testing subset [ " + firstIndex + ", " + lastIndex + " ] size=" + size );
			
			final IterableIntervalSubset< LongType > iterableIntervalSubset = new IterableIntervalSubset< LongType >( iterable, firstIndex, size );
			for ( final LongType t : iterableIntervalSubset )
				if ( ++b != t.get() )
					return false;
		}
		return true;
	}
}
