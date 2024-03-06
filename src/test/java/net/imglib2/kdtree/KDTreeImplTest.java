package net.imglib2.kdtree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.util.LinAlgHelpers;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Random;
import org.junit.Test;

public class KDTreeImplTest {

	public int n = 3;
	public int numDataVertices = 100;
	public int numTestVertices = 10;
	public double minCoordinateValue = -5;
	public double maxCoordinateValue = 5;

	public List< RealPoint > dataVertices;
	public List< RealPoint > testVertices;

    @Before
    public void init()
    {
		final double[] p = new double[ n ];
		final double size = ( maxCoordinateValue - minCoordinateValue );
		final Random rnd = new Random( 4379 );
		dataVertices = new ArrayList<>();
		for ( int i = 0; i < numDataVertices; ++i )
		{
			for ( int d = 0; d < n; ++d )
				p[ d ] = rnd.nextDouble() * size + minCoordinateValue;
			dataVertices.add( new RealPoint( p ) );
		}
		testVertices = new ArrayList<>();
		for ( int i = 0; i < numTestVertices; ++i )
		{
			for ( int d = 0; d < n; ++d )
				p[ d ] = rnd.nextDouble() * 2 * size + minCoordinateValue - size / 2;
			testVertices.add( new RealPoint( p ) );
		}
    }

	@Test
	public void testNearestNeighborSearch()
	{
		final double[][] points = KDTreeUtils.initPositions( n, numDataVertices, dataVertices );
		final int[] tree = KDTreeUtils.makeTree( points );
		final double[][] treePoints = KDTreeUtils.reorder( points, tree );
		final KDTreeImpl impl = new KDTreeImpl.Nested( treePoints );
		final NearestNeighborSearchImpl search = new NearestNeighborSearchImpl( impl );

		for ( RealPoint testVertex : testVertices )
		{
			final int expected = findNearestNeighborExhaustive( testVertex );

			search.search( testVertex );
			final int actual = tree[ search.bestIndex() ];

//			System.out.println( "actual = " + actual + ", expected = " + expected );
			Assert.assertEquals( expected, actual );
		}
	}

	@Test
	public void testKNearestNeighborSearch()
	{
		final int k = 10;

		final double[][] points = KDTreeUtils.initPositions( n, numDataVertices, dataVertices );
		final int[] tree = KDTreeUtils.makeTree( points );
		final double[][] treePoints = KDTreeUtils.reorder( points, tree );
		final KDTreeImpl impl = new KDTreeImpl.Nested( treePoints );
		final KNearestNeighborSearchImpl search = new KNearestNeighborSearchImpl( impl, k );

		for ( RealPoint testVertex : testVertices )
		{
			final int[] expecteds = findNearestNeighborsExhaustive( testVertex, k );

			search.search( testVertex );
			final int[] actuals = new int[ k ];
			Arrays.setAll( actuals, i -> tree[ search.bestIndex( i ) ] );

//			System.out.println( "actual = " + actual + ", expected = " + expected );
			Assert.assertArrayEquals( expecteds, actuals );
		}
	}

	@Test
	public void testRadiusNeighborSearch()
	{
		final double radius = 7;

		final double[][] points = KDTreeUtils.initPositions( n, numDataVertices, dataVertices );
		final int[] tree = KDTreeUtils.makeTree( points );
		final double[][] treePoints = KDTreeUtils.reorder( points, tree );
		final KDTreeImpl impl = new KDTreeImpl.Nested( treePoints );
		final RadiusNeighborSearchImpl search = new RadiusNeighborSearchImpl( impl );

		for ( RealPoint testVertex : testVertices )
		{
			final int[] expecteds = findRadiusNeighborsExhaustive( testVertex, radius );

			search.search( testVertex, radius, true );
			final int[] actuals = new int[ search.numNeighbors() ];
			Arrays.setAll( actuals, i -> tree[ search.bestIndex( i ) ] );

			Assert.assertArrayEquals( expecteds, actuals );
		}
	}

	private int findNearestNeighborExhaustive( final RealLocalizable point )
	{
		return findNearestNeighborsExhaustive( point, 1 )[ 0 ];
	}

	private int[] findNearestNeighborsExhaustive( final RealLocalizable point, final int k )
	{
		final List< RealPoint > sorted = new ArrayList<>();
		sorted.addAll( dataVertices );
		sorted.sort( Comparator.comparing( p -> distance( point, p ) ) );
		final int[] neighbors = new int[ k ];
		Arrays.setAll(neighbors, i -> dataVertices.indexOf( sorted.get( i ) ) );
		return neighbors;
	}

	private static double distance( final RealLocalizable p1, final RealLocalizable p2 )
	{
		return LinAlgHelpers.distance( p2.positionAsDoubleArray(), p1.positionAsDoubleArray() );
	}

	private int[] findRadiusNeighborsExhaustive( final RealLocalizable point, final double radius )
	{
		final List< RealPoint > sorted = new ArrayList<>();
		dataVertices.forEach( p -> {
			if ( distance( point, p ) <= radius )
			{
				sorted.add( p );
			}
		} );
		sorted.sort( Comparator.comparing( p -> distance( point, p ) ) );
		final int[] neighbors = new int[ sorted.size() ];
		Arrays.setAll(neighbors, i -> dataVertices.indexOf( sorted.get( i ) ) );
		return neighbors;
	}
}
