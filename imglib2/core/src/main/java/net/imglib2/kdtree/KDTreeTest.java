package net.imglib2.kdtree;

import java.util.ArrayList;
import java.util.Random;

import net.imglib2.RealPoint;

import static org.junit.Assert.*;

import org.junit.Test;

public class KDTreeTest
{
	protected static boolean testNearestNeighbor( final int numDimensions, final int numPoints, final int numTests, final float min, final float max ) 
	{
		final ArrayList< RealPoint > points = new ArrayList< RealPoint >();
		final Random rnd = new Random(435435435);

		final float[] p = new float[numDimensions];
		
		final float size = (max - min);

		for (int i = 0; i < numPoints; ++i) {
			for (int d = 0; d < numDimensions; ++d)
				p[d] = rnd.nextFloat() * size + min;

			final RealPoint t = new RealPoint(p);
			points.add(t);
		}

		long start = System.currentTimeMillis();
		final KDTree< RealPoint > kdTree = new KDTree< RealPoint >( points, points );
		NearestNeighborSearchOnKDTree< RealPoint > kd = new NearestNeighborSearchOnKDTree< RealPoint >( kdTree );		
		final long kdSetupTime = System.currentTimeMillis() - start;
		System.out.println("kdtree setup took: " + (kdSetupTime) + " ms.");

		start = System.currentTimeMillis();
		final ArrayList< RealPoint > testpoints = new ArrayList< RealPoint >();
		for (int i = 0; i < numTests; ++i) {
			for (int d = 0; d < numDimensions; ++d)
				p[d] = rnd.nextFloat() * 2 * size + min - size / 2;

			final RealPoint t = new RealPoint(p);
			testpoints.add(t);
		}
		
		for ( RealPoint t : testpoints ) {
			final RealPoint nnKdtree = kd.search( t ).get();
			final RealPoint nnExhaustive = findNearestNeighborExhaustive(points, t);

			boolean equal = true;
			for (int d = 0; d < numDimensions; ++d)
				if( nnKdtree.getFloatPosition( d ) != nnExhaustive.getFloatPosition( d ) )
					equal = false;
			if ( ! equal ) {
				System.out.println("Nearest neighbor to: " + t);
				System.out.println("KD-Tree says: " + nnKdtree);
				System.out.println("Exhaustive says: " + nnExhaustive);
				return false;
			}
		}
		final long compareTime = System.currentTimeMillis() - start;
		System.out.println("comparison (kdtree <-> exhaustive) search took: " + (compareTime) + " ms.");

		start = System.currentTimeMillis();
		for ( RealPoint t : testpoints ) {
			final RealPoint nnKdtree = kd.search( t ).get();
			nnKdtree.getClass();
		}
		final long kdTime = System.currentTimeMillis() - start;
		System.out.println("kdtree search took: " + (kdTime) + " ms.");
		System.out.println("kdtree all together took: " + (kdSetupTime+kdTime) + " ms.");

		start = System.currentTimeMillis();
		for ( RealPoint t : testpoints ) {
			final RealPoint nnExhaustive = findNearestNeighborExhaustive(points, t);
			nnExhaustive.getClass();
		}
		final long exhaustiveTime = System.currentTimeMillis() - start;
		System.out.println("exhaustive search took: " + (exhaustiveTime) + " ms.");

		return true;
	}

	private static RealPoint findNearestNeighborExhaustive(final ArrayList< RealPoint > points, final RealPoint t) {
		float minDistance = Float.MAX_VALUE;
		RealPoint nearest = null;

		final int n = t.numDimensions();
		final float[] tpos = new float[ n ];
		final float[] ppos = new float[ n ];
		t.localize( tpos );
		
		for (final RealPoint p : points) {
			p.localize( ppos );
			float dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += (tpos[i] - ppos[i]) * (tpos[i] - ppos[i]);
			if (dist < minDistance) {
				minDistance = dist;
				nearest = p;
			}
		}

		return nearest;
	}

	
	protected static boolean testKNearestNeighbor( final int neighbors, final int numDimensions, final int numPoints, final int numTests, final float min, final float max ) 
	{
		final ArrayList< RealPoint > points = new ArrayList< RealPoint >();
		final Random rnd = new Random(435435435);

		final float[] p = new float[numDimensions];
		
		final float size = (max - min);

		for (int i = 0; i < numPoints; ++i) {
			for (int d = 0; d < numDimensions; ++d)
				p[d] = rnd.nextFloat() * size + min;

			final RealPoint t = new RealPoint(p);
			points.add(t);
		}

		long start = System.currentTimeMillis();
		final KDTree< RealPoint > kdTree = new KDTree< RealPoint >( points, points );
		KNearestNeighborSearchOnKDTree< RealPoint > kd = new KNearestNeighborSearchOnKDTree< RealPoint >( kdTree, neighbors );		
		final long kdSetupTime = System.currentTimeMillis() - start;
		System.out.println("kdtree setup took: " + (kdSetupTime) + " ms.");

		start = System.currentTimeMillis();
		final ArrayList< RealPoint > testpoints = new ArrayList< RealPoint >();
		for (int i = 0; i < numTests; ++i) {
			for (int d = 0; d < numDimensions; ++d)
				p[d] = rnd.nextFloat() * 2 * size + min - size / 2;

			final RealPoint t = new RealPoint(p);
			testpoints.add(t);
		}
		
		final RealPoint[] nnKdtree = new RealPoint[ neighbors ];		
		for ( RealPoint t : testpoints ) {
			kd.search( t );
			for ( int i = 0; i < neighbors; ++i )
			{
				nnKdtree[ i ] = kd.getSampler( i ).get();
			}
			final RealPoint[] nnExhaustive = findKNearestNeighborExhaustive(points, t, neighbors);

			for ( int i = 0; i < neighbors; ++i )
			{
				boolean equal = true;
				for (int d = 0; d < numDimensions; ++d)
					if( nnKdtree[ i ].getFloatPosition( d ) != nnExhaustive[ i ].getFloatPosition( d ) )
						equal = false;
				if ( ! equal ) {
					System.out.println((i+1) + "-nearest neighbor to: " + t);
					System.out.println("KD-Tree says: " + nnKdtree[i]);
					System.out.println("Exhaustive says: " + nnExhaustive[i]);
					return false;
				}
			}
		}
		final long compareTime = System.currentTimeMillis() - start;
		System.out.println("comparison (kdtree <-> exhaustive) search took: " + (compareTime) + " ms.");

		start = System.currentTimeMillis();
		for ( RealPoint t : testpoints ) {
			kd.search( t );
			for ( int i = 0; i < neighbors; ++ i )
			{
				nnKdtree[ i ] = kd.getSampler( i ).get();
				nnKdtree[ i ].getClass();
			}
		}
		final long kdTime = System.currentTimeMillis() - start;
		System.out.println("kdtree search took: " + (kdTime) + " ms.");
		System.out.println("kdtree all together took: " + (kdSetupTime+kdTime) + " ms.");

		start = System.currentTimeMillis();
		for ( RealPoint t : testpoints ) {
			final RealPoint[] nnExhaustive = findKNearestNeighborExhaustive(points, t, neighbors);
			nnExhaustive[ 0 ].getClass();
		}
		final long exhaustiveTime = System.currentTimeMillis() - start;
		System.out.println("exhaustive search took: " + (exhaustiveTime) + " ms.");

		return true;
	}

	private static RealPoint[] findKNearestNeighborExhaustive( final ArrayList< RealPoint > points, final RealPoint t, final int k )
	{
		final RealPoint[] nearest = new RealPoint[ k ];
		final float[] minDistance = new float[ k ];
		for (int i = 0; i < k; ++i)
			minDistance[i] = Float.MAX_VALUE;

		final int n = t.numDimensions();
		final float[] tpos = new float[ n ];
		final float[] ppos = new float[ n ];
		t.localize( tpos );		
		
		for (final RealPoint p : points) {
			p.localize( ppos );
			float dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += (tpos[i] - ppos[i]) * (tpos[i] - ppos[i]);
			
			if ( dist < minDistance[ k - 1 ] )
			{
				int i = k - 1;
	            for ( int j = i - 1; i > 0 && dist < minDistance[ j ]; --i, --j )
	            {
	            	minDistance[ i ] = minDistance[ j ];
	            	nearest[ i ] = nearest[ j ];
	            }
	            minDistance[ i ] = dist;
	            nearest[ i ] = p;
			}
		}

		return nearest;
	}

	
	@Test
	public void testKDTreeKNearestNeighborSearch()
	{
		assertTrue( testKNearestNeighbor( 3, 3, 1000, 100, -5, 5 ) );		
	}
	
	@Test
	public void testKDTreeNearestNeighborSearch()
	{
		assertTrue( testNearestNeighbor( 3, 1000, 100, -5, 5 ) );		
	}
	
	public static void main( String[] args )
	{
		for ( int i = 0; i < 5; ++i )
		{
			if ( testKNearestNeighbor( 3, 3, 100000, 1000, -5, 5 ) )
				System.out.println( "N-Nearest neighbor test (3) successfull\n" );
		}

		for ( int i = 0; i < 5; ++i )
		{
			if ( testNearestNeighbor( 3, 100000, 1000, -5, 5 ) )
				System.out.println( "Nearest neighbor test successfull\n" );
		}
	}
}
