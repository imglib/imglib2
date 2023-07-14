/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.nearestneighbor;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import net.imglib2.KDTree;
import net.imglib2.RealPoint;
import net.imglib2.neighborsearch.KNearestNeighborSearchOnKDTree;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.neighborsearch.RadiusNeighborSearchOnKDTree;
import net.imglib2.util.ValuePair;

import org.junit.Test;

/**
 * TODO
 * 
 */
public class KDTreeTest
{
	protected static boolean testNearestNeighbor( final int numDimensions, final int numPoints, final int numTests, final float min, final float max )
	{
		final ArrayList< RealPoint > points = new ArrayList<>();
		final Random rnd = new Random( 435435435 );

		final float[] p = new float[ numDimensions ];

		final float size = ( max - min );

		for ( int i = 0; i < numPoints; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				p[ d ] = rnd.nextFloat() * size + min;

			final RealPoint t = new RealPoint( p );
			points.add( t );
		}

		long start = System.currentTimeMillis();
		final KDTree< RealPoint > kdTree = new KDTree<>( points, points );
		final NearestNeighborSearchOnKDTree< RealPoint > kd = new NearestNeighborSearchOnKDTree<>( kdTree );
		final long kdSetupTime = System.currentTimeMillis() - start;
		System.out.println( "kdtree setup took: " + ( kdSetupTime ) + " ms." );

		start = System.currentTimeMillis();
		final ArrayList< RealPoint > testpoints = new ArrayList<>();
		for ( int i = 0; i < numTests; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				p[ d ] = rnd.nextFloat() * 2 * size + min - size / 2;

			final RealPoint t = new RealPoint( p );
			testpoints.add( t );
		}

		for ( final RealPoint t : testpoints )
		{
			kd.search( t );
			final RealPoint nnKdtree = kd.getSampler().get();
			final RealPoint nnExhaustive = findNearestNeighborExhaustive( points, t );

			boolean equal = true;
			for ( int d = 0; d < numDimensions; ++d )
				if ( nnKdtree.getFloatPosition( d ) != nnExhaustive.getFloatPosition( d ) )
					equal = false;
			if ( !equal )
			{
				System.out.println( "Nearest neighbor to: " + t );
				System.out.println( "KD-Tree says: " + nnKdtree );
				System.out.println( "Exhaustive says: " + nnExhaustive );
				return false;
			}
		}
		final long compareTime = System.currentTimeMillis() - start;
		System.out.println( "comparison (kdtree <-> exhaustive) search took: " + ( compareTime ) + " ms." );

		start = System.currentTimeMillis();
		for ( final RealPoint t : testpoints )
		{
			kd.search( t );
			final RealPoint nnKdtree = kd.getSampler().get();
			nnKdtree.getClass();
		}
		final long kdTime = System.currentTimeMillis() - start;
		System.out.println( "kdtree search took: " + ( kdTime ) + " ms." );
		System.out.println( "kdtree all together took: " + ( kdSetupTime + kdTime ) + " ms." );

		start = System.currentTimeMillis();
		for ( final RealPoint t : testpoints )
		{
			final RealPoint nnExhaustive = findNearestNeighborExhaustive( points, t );
			nnExhaustive.getClass();
		}
		final long exhaustiveTime = System.currentTimeMillis() - start;
		System.out.println( "exhaustive search took: " + ( exhaustiveTime ) + " ms." );

		return true;
	}

	private static RealPoint findNearestNeighborExhaustive( final ArrayList< RealPoint > points, final RealPoint t )
	{
		float minDistance = Float.MAX_VALUE;
		RealPoint nearest = null;

		final int n = t.numDimensions();
		final float[] tpos = new float[ n ];
		final float[] ppos = new float[ n ];
		t.localize( tpos );

		for ( final RealPoint p : points )
		{
			p.localize( ppos );
			float dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += ( tpos[ i ] - ppos[ i ] ) * ( tpos[ i ] - ppos[ i ] );
			if ( dist < minDistance )
			{
				minDistance = dist;
				nearest = p;
			}
		}

		return nearest;
	}

	protected static boolean testKNearestNeighbor( final int neighbors, final int numDimensions, final int numPoints, final int numTests, final float min, final float max )
	{
		final ArrayList< RealPoint > points = new ArrayList<>();
		final Random rnd = new Random( 435435435 );

		final float[] p = new float[ numDimensions ];

		final float size = ( max - min );

		for ( int i = 0; i < numPoints; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				p[ d ] = rnd.nextFloat() * size + min;

			final RealPoint t = new RealPoint( p );
			points.add( t );
		}

		long start = System.currentTimeMillis();
		final KDTree< RealPoint > kdTree = new KDTree<>( points, points );
		final KNearestNeighborSearchOnKDTree< RealPoint > kd = new KNearestNeighborSearchOnKDTree<>( kdTree, neighbors );
		final long kdSetupTime = System.currentTimeMillis() - start;
		System.out.println( "kdtree setup took: " + ( kdSetupTime ) + " ms." );

		start = System.currentTimeMillis();
		final ArrayList< RealPoint > testpoints = new ArrayList<>();
		for ( int i = 0; i < numTests; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				p[ d ] = rnd.nextFloat() * 2 * size + min - size / 2;

			final RealPoint t = new RealPoint( p );
			testpoints.add( t );
		}

		final RealPoint[] nnKdtree = new RealPoint[ neighbors ];
		for ( final RealPoint t : testpoints )
		{
			kd.search( t );
			for ( int i = 0; i < neighbors; ++i )
			{
				nnKdtree[ i ] = kd.getSampler( i ).get();
			}
			final RealPoint[] nnExhaustive = findKNearestNeighborExhaustive( points, t, neighbors );

			for ( int i = 0; i < neighbors; ++i )
			{
				boolean equal = true;
				for ( int d = 0; d < numDimensions; ++d )
					if ( nnKdtree[ i ].getFloatPosition( d ) != nnExhaustive[ i ].getFloatPosition( d ) )
						equal = false;
				if ( !equal )
				{
					System.out.println( ( i + 1 ) + "-nearest neighbor to: " + t );
					System.out.println( "KD-Tree says: " + nnKdtree[ i ] );
					System.out.println( "Exhaustive says: " + nnExhaustive[ i ] );
					return false;
				}
			}
		}
		final long compareTime = System.currentTimeMillis() - start;
		System.out.println( "comparison (kdtree <-> exhaustive) search took: " + ( compareTime ) + " ms." );

		start = System.currentTimeMillis();
		for ( final RealPoint t : testpoints )
		{
			kd.search( t );
			for ( int i = 0; i < neighbors; ++i )
			{
				nnKdtree[ i ] = kd.getSampler( i ).get();
				nnKdtree[ i ].getClass();
			}
		}
		final long kdTime = System.currentTimeMillis() - start;
		System.out.println( "kdtree search took: " + ( kdTime ) + " ms." );
		System.out.println( "kdtree all together took: " + ( kdSetupTime + kdTime ) + " ms." );

		start = System.currentTimeMillis();
		for ( final RealPoint t : testpoints )
		{
			final RealPoint[] nnExhaustive = findKNearestNeighborExhaustive( points, t, neighbors );
			nnExhaustive[ 0 ].getClass();
		}
		final long exhaustiveTime = System.currentTimeMillis() - start;
		System.out.println( "exhaustive search took: " + ( exhaustiveTime ) + " ms." );

		return true;
	}

	private static RealPoint[] findKNearestNeighborExhaustive( final ArrayList< RealPoint > points, final RealPoint t, final int k )
	{
		final RealPoint[] nearest = new RealPoint[ k ];
		final float[] minDistance = new float[ k ];
		for ( int i = 0; i < k; ++i )
			minDistance[ i ] = Float.MAX_VALUE;

		final int n = t.numDimensions();
		final float[] tpos = new float[ n ];
		final float[] ppos = new float[ n ];
		t.localize( tpos );

		for ( final RealPoint p : points )
		{
			p.localize( ppos );
			float dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += ( tpos[ i ] - ppos[ i ] ) * ( tpos[ i ] - ppos[ i ] );

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

	protected static boolean testRadiusNeighbor( final int numDimensions, final int numPoints, final int numTests, final float min, final float max )
	{
		final ArrayList< RealPoint > points = new ArrayList<>();
		final Random rnd = new Random( 435435435 );

		final float[] p = new float[ numDimensions ];

		final float size = ( max - min );

		for ( int i = 0; i < numPoints; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				p[ d ] = rnd.nextFloat() * size + min;

			final RealPoint t = new RealPoint( p );
			points.add( t );
		}

		final double radius = rnd.nextDouble() * size / 10;

		long start = System.currentTimeMillis();
		final KDTree< RealPoint > kdTree = new KDTree<>( points, points );
		final RadiusNeighborSearchOnKDTree< RealPoint > kd = new RadiusNeighborSearchOnKDTree<>( kdTree );
		final long kdSetupTime = System.currentTimeMillis() - start;
		System.out.println( "kdtree setup took: " + ( kdSetupTime ) + " ms." );

		start = System.currentTimeMillis();
		final ArrayList< RealPoint > testpoints = new ArrayList<>();
		for ( int i = 0; i < numTests; ++i )
		{
			for ( int d = 0; d < numDimensions; ++d )
				p[ d ] = rnd.nextFloat() * 2 * size + min - size / 2;

			final RealPoint t = new RealPoint( p );
			testpoints.add( t );
		}

		for ( final RealPoint t : testpoints )
		{
			kd.search( t, radius, true );
			final int neighbors = kd.numNeighbors();
			final ArrayList< ValuePair< RealPoint, Double > > radiusExhaustive = findNeighborsRadiusExhaustive( points, t, radius, true );

			if ( neighbors != radiusExhaustive.size() )
				return false;

			for ( int i = 0; i < neighbors; ++i )
			{
				boolean equal = true;

				for ( int d = 0; d < numDimensions; ++d )
					if ( kd.getPosition( i ).getFloatPosition( d ) != radiusExhaustive.get( i ).a.getFloatPosition( d ) )
						equal = false;

				if ( !equal )
				{
					System.out.println( ( i + 1 ) + "-radius neighbor to: " + t );
					System.out.println( "KD-Tree says: " + kd.getPosition( i ) );
					System.out.println( "Exhaustive says: " + radiusExhaustive.get( i ).a );

					if ( kd.getDistance( i ) == radiusExhaustive.get( i ).b )
						System.out.println( "different points but same distance" );
					else
						return false;
				}
			}
		}
		final long compareTime = System.currentTimeMillis() - start;
		System.out.println( "comparison (kdtree <-> exhaustive) search took: " + ( compareTime ) + " ms." );

		start = System.currentTimeMillis();
		for ( final RealPoint t : testpoints )
		{
			kd.search( t, radius, true );
			final int neighbors = kd.numNeighbors();
			for ( int i = 0; i < neighbors; ++i )
			{
				kd.getSampler( i ).get().getClass();
			}
		}
		final long kdTime = System.currentTimeMillis() - start;
		System.out.println( "kdtree search took: " + ( kdTime ) + " ms." );
		System.out.println( "kdtree all together took: " + ( kdSetupTime + kdTime ) + " ms." );

		start = System.currentTimeMillis();
		for ( final RealPoint t : testpoints )
		{
			final ArrayList< ValuePair< RealPoint, Double > > radiusExhaustive = findNeighborsRadiusExhaustive( points, t, radius, true );
			if ( radiusExhaustive.size() > 0 )
				radiusExhaustive.get( 0 ).getClass();
		}
		final long exhaustiveTime = System.currentTimeMillis() - start;
		System.out.println( "exhaustive search took: " + ( exhaustiveTime ) + " ms." );

		return true;
	}

	private static ArrayList< ValuePair< RealPoint, Double > > findNeighborsRadiusExhaustive( final ArrayList< RealPoint > points, final RealPoint t, final double radius, final boolean sortResults )
	{
		final ArrayList< ValuePair< RealPoint, Double > > withinRadius = new ArrayList<>();

		final int n = t.numDimensions();
		final float[] tpos = new float[ n ];
		final float[] ppos = new float[ n ];
		t.localize( tpos );

		for ( final RealPoint p : points )
		{
			p.localize( ppos );
			double dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += ( tpos[ i ] - ppos[ i ] ) * ( tpos[ i ] - ppos[ i ] );
			dist = Math.sqrt( dist );

			if ( dist <= radius )
				withinRadius.add( new ValuePair<>( p, dist ) );
		}

		if ( sortResults )
		{
			Collections.sort( withinRadius, new Comparator< ValuePair< RealPoint, Double > >()
			{
				@Override
				public int compare( final ValuePair< RealPoint, Double > o1, final ValuePair< RealPoint, Double > o2 )
				{
					return Double.compare( o1.b, o2.b );
				}
			} );
		}

		return withinRadius;
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

	@Test
	public void testKDTreeRadiusNeighborSearch()
	{
		assertTrue( testRadiusNeighbor( 3, 1000, 100, -5, 5 ) );
	}

	public static void main( final String[] args )
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

		for ( int i = 0; i < 5; ++i )
		{
			if ( testRadiusNeighbor( 3, 100000, 1000, -5, 5 ) )
				System.out.println( "Radius neighbor test successfull\n" );
		}
	}
}
