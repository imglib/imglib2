package net.imglib2.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

public abstract class AStar< T extends RealType< T >> implements Benchmark, Algorithm, OutputAlgorithm< PathIterable< T >>
{

	protected final long[] start;

	protected final long[] end;

	protected String errorMessage;

	private long processingTime;

	protected final RandomAccessibleInterval< T > source;

	private final RandomAccess< Neighborhood< T >> neighborhoods;

	private PathIterable< T > output;

	private long expandedNodeNumber;

	public AStar( final RandomAccessibleInterval< T > source, final long[] start, final long[] end )
	{
		this.source = source;
		this.start = start.clone();
		this.end = end.clone();
		this.neighborhoods = new RectangleShape( 1, true ).neighborhoodsRandomAccessible( source ).randomAccess( source );
	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public boolean process()
	{
		/*
		 * Initialize variables
		 */

		final long startTime = System.currentTimeMillis();
		expandedNodeNumber = 0;

		final Node startNode = new Node( start, null );
		startNode.g = 0;
		startNode.f = 0 + heuristic( start );

		final PriorityQueue< Node > openSet = new PriorityQueue< Node >();
		openSet.offer( startNode );

		final HashSet< Node > closed = new HashSet< Node >();

		while ( !openSet.isEmpty() )
		{

			final Node node = openSet.poll();
			expandedNodeNumber++;

			/*
			 * Put it in the CLOSED list
			 */

			closed.add( node );

			/*
			 * Get successors
			 */

			neighborhoods.setPosition( node.coords );
			final Neighborhood< T > neighborhood = neighborhoods.get();

			final Cursor< T > cursor = neighborhood.cursor();
			while ( cursor.hasNext() )
			{
				cursor.fwd();

				// Get current child node coordinates.
				final long[] coords = new long[ source.numDimensions() ];
				cursor.localize( coords );

				// Check if not out of bounds. Skip node if this is the case.
				boolean outOfBounds = false;
				for ( int d = 0; d < source.numDimensions(); d++ )
				{
					if ( coords[ d ] < 0 || coords[ d ] >= source.dimension( d ) )
					{
						outOfBounds = true;
						break;
					}
				}
				if ( outOfBounds )
				{
					continue;
				}

				// Make a blank (no cost yet) node, to see if we have not
				// visited this one already.
				final Node child = new Node( coords, node );

				// Did we reach the end?
				if ( Arrays.equals( end, coords ) )
				{
					// Yes. Prepare output and exit.
					Node pathNode = child;
					final List< long[] > path = new ArrayList< long[] >();
					while ( null != pathNode )
					{
						path.add( pathNode.coords );
						pathNode = pathNode.parent;
					}

					Collections.reverse( path );
					output = new ListPathIterable< T >( source, path );
					final long endTime = System.currentTimeMillis();
					processingTime = endTime - startTime;
					return true;
				}

				// Compute tentative cost
				final double tentativeG = node.g + cursor.get().getRealDouble();
				final double tentativeF = tentativeG + heuristic( coords );

				// Is it in the CLOSED set?
				if ( closed.contains( child ) )
				{
					continue;
				}

				child.f = tentativeF;
				child.g = tentativeG;

				if ( !openSet.contains( child ) )
				{
					openSet.add( child );
				}
			}

		}

		final long endTime = System.currentTimeMillis();
		processingTime = endTime - startTime;
		errorMessage = "Exhausted navigable neighbors without meeting the goal location.";
		return false;
	}

	@Override
	public PathIterable< T > getResult()
	{
		return output;
	}

	/**
	 * Returns the estimate of the total cost required to travel from the
	 * specified location to the end location.
	 *
	 * @param currentPoint
	 *            the location whose heuristic is to be estimated.
	 * @return the estimated cost, as a <code>double</code>.
	 */
	protected abstract double heuristic( final long[] currentPoint );

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	/**
	 * Returns the number of expanded nodes during the last search.
	 *
	 * @return the number of expanded nodes, as a <code>long</code>.
	 */
	public long getExpandedNodeNumber()
	{
		return expandedNodeNumber;
	}

	private static final class Node implements Comparable< Node >
	{

		private final long[] coords;

		private final int hash;

		private final Node parent;

		private double f;

		private double g;

		public Node( final long[] coords, final Node parent )
		{
			this.parent = parent;
			this.coords = coords; // Careful! This reference escapes. Do not use
									// reference again!
			this.hash = Arrays.hashCode( coords );
		}

		/**
		 * Hashcode is based on coordinates only.
		 */
		@Override
		public int hashCode()
		{
			return hash;
		}

		/**
		 * Equality is based solely on {@link #coords}, not on cost or parent,
		 * so that retrieval in a collection is based on pixel coordinates only.
		 */
		@Override
		public boolean equals( final Object obj )
		{
			final Node o = ( Node ) obj;
			return Arrays.equals( coords, o.coords );
		}

		/**
		 * Comparison is based on cost only.
		 */
		@Override
		public int compareTo( final Node o )
		{
			return ( f > o.f ? 1 : f < o.f ? -1 : 0 );
		}

		@Override
		public String toString()
		{
			final StringBuilder str = new StringBuilder();
			str.append( Util.printCoordinates( coords ) );
			str.append( " - g = " + String.format( "%.2f", g ) + ", f = " + String.format( "%.2f", f ) );
			return str.toString();
		}

	}
}
