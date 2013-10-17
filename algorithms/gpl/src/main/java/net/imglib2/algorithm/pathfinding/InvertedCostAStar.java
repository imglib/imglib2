package net.imglib2.algorithm.pathfinding;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * A simple modification of the {@link DefaultAStar} path-finding
 * implementation, that find path along high pixel values of the source image.
 * Because we are very lazy, we request the maximal pixel value of the source
 * image at construction, rather than computing it ourselves.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 *
 * @see DefaultAStar
 *
 * @param <T>
 *            the type of the source {@link RandomAccessibleInterval}. Must
 *            extends {@link RealType} for we sample pixel values to compute
 *            move costs.
 */
public class InvertedCostAStar< T extends RealType< T >> extends DefaultAStar< T >
{

	private final double maxVal;

	/**
	 * Creates a new A* search algorithm, finding the path between the specified
	 * start and end point, with cost taken from the inversion of the source
	 * pixel values.
	 * 
	 * @param source
	 *            the source interval, from which costs will be drawn. Costs are
	 *            calculated frollowing <code>cost = maxVal - T</code>, where
	 *            <code>T</code> is the current pixel value.
	 * @param start
	 *            the start location.
	 * @param end
	 *            the goal location.
	 * @param heuristicStrength
	 *            the strength of the euclidian distance heuristics to use. This
	 *            value should be commensurate with the typical pixel value
	 *            range of the source (for instance, start with 10 for 8-bit
	 *            images).
	 * @param maxVal
	 *            the maximal pixel value in the source image, as a
	 *            <code>double</code>.
	 */
	public InvertedCostAStar( final RandomAccessibleInterval< T > source, final long[] start, final long[] end, final int heuristicStrength, final double maxVal )
	{
		super( source, start, end, heuristicStrength );
		this.maxVal = maxVal;
	}

	@Override
	protected double moveCost( final long[] from, final long[] to )
	{
		ra.setPosition( to );
		return maxVal - ra.get().getRealDouble();
	}

}
