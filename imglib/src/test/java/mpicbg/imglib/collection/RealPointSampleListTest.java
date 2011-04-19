package mpicbg.imglib.collection;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import mpicbg.imglib.RealCursor;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPoint;
import mpicbg.imglib.collection.RealPointSampleList;
import mpicbg.imglib.type.numeric.real.DoubleType;

import org.junit.Before;
import org.junit.Test;

public class RealPointSampleListTest
{
	final static private int n = 10;
	final static private int m = 10000;
	final static private Random rnd = new Random( 123456 );
	final static private RealPointSampleList< DoubleType > realPointSampleList = new RealPointSampleList< DoubleType >( n );
	final static private ArrayList< RealPoint > realPointList = new ArrayList< RealPoint >();
	final static private ArrayList< DoubleType > sampleList = new ArrayList< DoubleType >();
	
	final static private boolean positionEquals(
			final RealLocalizable a,
			final RealLocalizable b )
	{
		final int n = a.numDimensions();
		if ( n != b.numDimensions() )
			return false;
		for ( int d = 0; d < n; ++d )
		{
			if ( a.getDoublePosition( d ) != b.getDoublePosition( d ) )
				return false;
		}
		return true;
	}
	
	@Before
	public void init()
	{
		for ( int i = 0; i < m; ++i )
		{
			final double[] position = new double[ n ];
			for ( int d =0; d < n; ++d )
				position[ d ] = rnd.nextDouble();
			
			final RealPoint realPoint = new RealPoint( position );
			final DoubleType sample = new DoubleType( rnd.nextDouble() );
			
			realPointList.add( realPoint );
			sampleList.add( sample );
			realPointSampleList.add( realPoint, sample );
		}
	}
	
	@Test
	public void testIteration()
	{
		final Iterator< DoubleType > sampleIterator = sampleList.iterator();
		for ( final DoubleType t : realPointSampleList )
			assertTrue( "Samples differ ", t == sampleIterator.next() );
	}
	
	@Test
	public void testPosition()
	{
		final Iterator< RealPoint > realPointIterator = realPointList.iterator();
		final RealCursor< DoubleType > realPointSampleCursor = realPointSampleList.cursor();
		
		while ( realPointSampleCursor.hasNext() )
		{
			realPointSampleCursor.fwd();
			assertTrue( "Positions differ ", positionEquals( realPointIterator.next(), realPointSampleCursor ) );
		}
	}
	
	@Test
	public void testCopy()
	{
		final ArrayList< RealCursor< DoubleType > > copies = new ArrayList< RealCursor< DoubleType > >();
		final RealCursor< DoubleType > cursor = realPointSampleList.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			copies.add( cursor.copy() );
		}
		
		cursor.reset();
		final Iterator< RealCursor< DoubleType > > copyIterator = copies.iterator();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final RealCursor< DoubleType > copy = copyIterator.next();
			assertTrue( "Copy failed at sample ", copy.get() == cursor.get() );
			assertTrue( "Copy failed at position ", positionEquals( copy, cursor ) );
		}
		
		
	}
}
