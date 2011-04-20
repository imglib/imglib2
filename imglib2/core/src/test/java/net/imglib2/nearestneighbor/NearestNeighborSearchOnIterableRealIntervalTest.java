package net.imglib2.nearestneighbor;

import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.collection.RealPointSampleList;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class NearestNeighborSearchOnIterableRealIntervalTest
{
	final static private RealPointSampleList< DoubleType > realPointSampleList = new RealPointSampleList< DoubleType >( 2 );
	final static private double[][] coordinates = new double[][]{
		{ 0, 0 },
		{ 0, 1 },
		{ 1, 0 },
		{ 1, 1 }
	};
	
	final static private double[] samples = new double[]{
		0, 1, 2, 3
	};
	
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
		for ( int i = 0; i < samples.length; ++i )
			realPointSampleList.add( new RealPoint( coordinates[ i ] ), new DoubleType( samples[ i ] ) );
	}
	
	@Test
	public void testKNearestNeighborSearch()
	{
		final RealCursor< DoubleType > cursor = realPointSampleList.cursor();
		final KNearestNeighborSearchOnIterableRealInterval< DoubleType > search1 = new KNearestNeighborSearchOnIterableRealInterval< DoubleType >( realPointSampleList, 1 );
		
		search1.search( new RealPoint( new double[]{ 0.1, 0.2 } ) );
		assertTrue( "Position mismatch ", positionEquals( search1.getPosition( 0 ), new RealPoint( coordinates[ 0 ] ) ) );
		assertTrue( "Sample mismatch ", search1.getSampler( 0 ).get() == cursor.next() );
		
		search1.search( new RealPoint( new double[]{ -1, 20 } ) );
		assertTrue( "Position mismatch ", positionEquals( search1.getPosition( 0 ), new RealPoint( coordinates[ 1 ] ) ) );
		assertTrue( "Sample mismatch ", search1.getSampler( 0 ).get() == cursor.next() );
	}	
}
