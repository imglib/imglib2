/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.kdtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.neighborsearch.KNearestNeighborSearchOnKDTree;
import net.imglib2.neighborsearch.RadiusNeighborSearchOnKDTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.annotations.Setup;

@State( Scope.Benchmark )
public class KDTreeBenchmark
{
//	@Param({"3"})
//	public int n;
//
//	@Param({"10000", "100000", "1000000"})
//	public int numDataVertices;
//
//	@Param({"1000"})
//	public int numTestVertices;
//
	public int n = 3;
	public int k = 10;
	public int radius = 1;
	public int numDataVertices = 100000;
	public int numTestVertices = 1000;
	public double minCoordinateValue = -5;
	public double maxCoordinateValue = 5;

	List< RealPoint > dataVertices;
	List< RealPoint > testVertices;

	private KDTree< RealPoint > kdtree;

	@Setup
	public void setup()
	{
		createVertices();
//		createVerticesSeqTest();
		kdtree = new KDTree<>( dataVertices, dataVertices );
//		spoil();
	}

	public void spoil() {
		final double[][] points = KDTreeUtils.initPositions( n, numDataVertices, dataVertices );
		final int[] tree = KDTreeUtils.makeTree( points );
		final double[][] treePoints = KDTreeUtils.reorder( points, tree );
		final KDTreeImpl impl = new KDTreeImpl( KDTreePositions.createNested( treePoints ) );
		final NearestNeighborSearchImpl search = new NearestNeighborSearchImpl( impl );
		for ( RealPoint testVertex : testVertices )
			search.search( testVertex );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void createKDTree()
	{
		new KDTree<>( dataVertices, dataVertices );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void nearestNeighborSearch()
	{
		final NearestNeighborSearchOnKDTree< RealPoint > kd = new NearestNeighborSearchOnKDTree<>( kdtree );
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t );
			kd.getSampler().get();
		}
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void kNearestNeighborSearch()
	{
		final KNearestNeighborSearchOnKDTree< RealPoint > kd = new KNearestNeighborSearchOnKDTree<>( kdtree, k );
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t );
			kd.getSampler().get();
//			for ( int i = 0; i < k; i++ )
//			{
//				kd.getSampler( i ).get();
//			}
		}
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void radiusNeighborSearch()
	{
		final RadiusNeighborSearchOnKDTree< RealPoint > kd = new RadiusNeighborSearchOnKDTree<>( kdtree );
		for ( final RealLocalizable t : testVertices )
		{
			kd.search( t, radius, true );
			for ( int i = 0; i < Math.min( kd.numNeighbors(), k ); i++ )
			{
				kd.getSampler( i ).get();
			}
		}
	}

	private void createVertices()
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
			testVertices.add( new RealPoint( p )  );
		}
	}

	private void createVerticesSeqTest()
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
			if ( rnd.nextDouble() < 0.8 )
			{
				int d = rnd.nextInt( n );
				p[ d ] += size / 10000.0;
			}
			else
			{
				for ( int d = 0; d < n; ++d )
					p[ d ] = rnd.nextDouble() * 2 * size + minCoordinateValue - size / 2;
			}
			testVertices.add( new RealPoint( p )  );
		}
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( KDTreeBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 500 ) )
				.measurementTime( TimeValue.milliseconds( 500 ) )
				.build();
		new Runner( opt ).run();
	}
}
