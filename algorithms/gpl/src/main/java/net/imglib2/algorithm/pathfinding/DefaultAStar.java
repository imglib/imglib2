package net.imglib2.algorithm.pathfinding;

import java.util.ArrayList;
import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.type.numeric.RealType;

/**
 * A default implementation of the {@link AStar} class, that operates on an
 * <code>imglib2</code> {@link RandomAccessibleInterval}. Locations are integer
 * pixel locations of this interval. The implementations details are:
 * <ul>
 * <li>Each location is expanded by taking its 8-connected (or N-dim equivalent)
 * neighborhood.
 * <li>The incremental node cost is simply taken to be the real pixel value of
 * the interval at target location.
 * <li>The heuristic estimate is simply proportional to the euclidian distance
 * from the current location to the goal location. An input parameter controls
 * the "strength" of this heuristic, and should be commensurate with the typical
 * source pixel value range.
 * </ul>
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 *
 * @param <T>
 *            the type of the source {@link RandomAccessibleInterval}. Must
 *            extends {@link RealType} for we sample pixel values to compute
 *            move costs.
 */
public class DefaultAStar< T extends RealType< T > > extends AStar
{

	private final int heuristicStrength;

	private final RandomAccess< Neighborhood< T >> neighborhoods;

	private final RandomAccessibleInterval< T > source;

	protected final RandomAccess< T > ra;

	/**
	 * Creates a new A* search algorithm, finding the path between the specified
	 * start and end point, with cost taken from the source interval.
	 *
	 * @param source
	 *            the source interval, from which costs will be drawn.
	 * @param start
	 *            the start location.
	 * @param end
	 *            the goal location.
	 * @param heuristicStrength
	 *            the strength of the euclidian distance heuristics to use. This
	 *            value should be commensurate with the typical pixel value
	 *            range of the source (for instance, start with 10 for 8-bit
	 *            images).
	 */
	public DefaultAStar( final RandomAccessibleInterval< T > source, final long[] start, final long[] end, final int heuristicStrength )
	{
		super( start, end );
		this.source = source;
		this.ra = source.randomAccess( source );
		this.heuristicStrength = heuristicStrength;
		this.neighborhoods = new RectangleShape( 1, true ).neighborhoodsRandomAccessible( source ).randomAccess( source );

	}

	@Override
	protected double heuristic( final long[] currentPoint )
	{
		double dsquare = 0;
		for ( int d = 0; d < currentPoint.length; d++ )
		{
			final double di = ( currentPoint[ d ] - end[ d ] );
			dsquare += di * di;
		}
		return heuristicStrength * Math.sqrt( dsquare );
	}

	@Override
	protected double moveCost( final long[] from, final long[] to )
	{
		ra.setPosition( to );
		return ra.get().getRealDouble();
	}

	@Override
	protected Collection< long[] > expand( final long[] location )
	{
		neighborhoods.setPosition( location );
		final Neighborhood< T > neighborhood = neighborhoods.get();
		final Cursor< T > cursor = neighborhood.cursor();

		final ArrayList< long[] > children = new ArrayList< long[] >( ( int ) neighborhood.size() );
		while ( cursor.hasNext() )
		{
			cursor.fwd();

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

			children.add( coords );
		}
		return children;
	}
}
