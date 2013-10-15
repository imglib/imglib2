package net.imglib2.algorithm.pathfinding;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

public class EuclidianDistanceAStar< T extends RealType< T > > extends AStar< T >
{

	private final int heuristicStrength;

	public EuclidianDistanceAStar( final RandomAccessibleInterval< T > source, final long[] start, final long[] end, final int heuristicStrength )
	{
		super( source, start, end );
		this.heuristicStrength = heuristicStrength;
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
}
