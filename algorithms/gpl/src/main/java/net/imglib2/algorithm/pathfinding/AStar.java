package net.imglib2.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.util.Util;

/**
 * A skeleton of the A* algorithm that finds the path of least cost between two
 * points. See {@link http://en.wikipedia.org/wiki/A*_search_algorithm} for
 * details.
 * <p>
 * This abstract class provides only the main iteration loop. Concrete
 * implementations must specify:
 * <ul>
 * <li>How to expand a parent node and generate children nodes for the next
 * iteration. See {@link #expand(long[])}.
 * <li>The move cost, that is the cost to move from a parent location to a new
 * location. It is commonly denoted <i>g(x)</i>. See
 * {@link #moveCost(long[], long[])}.
 * <li>The future path cost: an admissible heuristic estimate of the cost from
 * the current location to the goal location. Obviously, only estimates can be
 * made for this quantity. It is commonly denoted <i>h(x)</i>. See
 * {@link #heuristic(long[])}.
 * </ul>
 * <p>
 * This class imposes locations that can be represented by <code>long[]</code>
 * arrays, ideally mapping locations in most imglib2 structures.
 * <p>
 * Contrary to verbatim implementations, it does not treat the special case
 * where an already visited location can be revisited given that the new visit
 * yields a lower cost. A node is expanded only once, and its cost is never
 * recalculated. This is not a problem for heuristics that are monotonic or
 * consistent.
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 */
public abstract class AStar implements Benchmark, Algorithm, OutputAlgorithm< List< long[] >>
{

	protected final long[] start;

	protected final long[] end;

	protected String errorMessage;

	private long processingTime;

	private List< long[] > output;

	private long expandedNodeNumber;

	public AStar( final long[] start, final long[] end )
	{
		this.start = start.clone();
		this.end = end.clone();
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

			final Collection< long[] > children = expand( node.coords );

			for ( final long[] coords : children )
			{

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
					output = path;
					final long endTime = System.currentTimeMillis();
					processingTime = endTime - startTime;
					return true;
				}

				// Is it in the CLOSED set? Or OPEN set?
				if ( closed.contains( child ) || openSet.contains( child ) )
				{
					continue;
				}

				// Compute tentative cost
				final double tentativeG = node.g + moveCost( node.coords, coords );
				final double tentativeF = tentativeG + heuristic( coords );

				child.f = tentativeF;
				child.g = tentativeG;
				openSet.add( child );
			}

		}

		final long endTime = System.currentTimeMillis();
		processingTime = endTime - startTime;
		errorMessage = "Exhausted navigable neighbors without meeting the goal location.";
		return false;
	}

	@Override
	public List< long[] > getResult()
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

	/**
	 * Returns the cost to move from location <code>from</code> to adjacent
	 * location <code>to</code>.
	 * 
	 * @param from
	 *            the parent location.
	 * @param to
	 *            the target location.
	 * @return the move cost, as a <code>double</code>.
	 */
	protected abstract double moveCost( long[] from, long[] to );

	/**
	 * Returns the children of a location, that will be added to the queue.
	 * <p>
	 * At this stage, one does not need to check whether the new locations have
	 * already been traversed. This will be done later. However, it is the
	 * responsibility of this method to return only locations on which costs are
	 * defined.
	 * 
	 * @param location
	 *            the parent location.
	 * @return the children locations, in a {@link Collection}.
	 */
	protected abstract Collection< long[] > expand( long[] location );

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
