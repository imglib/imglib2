package net.imglib2.img.cell;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.util.IntervalIndexer;

public abstract class AbstractCell< A extends ArrayDataAccess< A > >
{
	final protected int n;

	final int[] dimensions;
	final int[] steps;
	final long[] min;
	final long[] max;

	protected int numPixels;

	public AbstractCell( final int[] dimensions, final long[] min )
	{
		this.n = dimensions.length;
		this.dimensions = dimensions.clone();
		this.steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( dimensions, steps );
		this.min = min.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d ) {
			max[ d ] = min[ d ] + dimensions[ d ] - 1;
		}

		int nPixels = dimensions[ 0 ];
		for ( int d = 1; d < n; ++d ) {
			nPixels *= dimensions[ d ];
		}
		numPixels = nPixels;
	}

	public abstract A getData();

	public long size()
	{
		return numPixels;
	}

	public long indexToGlobalPosition( final int index, final int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, d ) + min[ d ];
	}

	public void indexToGlobalPosition( final int index, final long[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += min[ d ];
	}

	/**
	 * compute the index in the underlying flat array of this cell
	 * which corresponds to a local position (i.e., relative to the
	 * origin of this cell).
	 *
	 * @param position   a local position
	 * @return corresponding index
	 */
	public int localPositionToIndex( final long[] position )
	{
		return IntervalIndexer.positionToIndex( position, dimensions );
	}

	/**
	 *
	 * @param d dimension
	 * @return minimum
	 */
	public long min( final int d )
	{
		return min[ d ];
	}

	/**
	 * Write the minimum of each dimension into long[].
	 *
	 * @param min
	 */
	public void min( final long[] minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = min[ d ];
	}

	/**
	 * Get the number of pixels in a given dimension <em>d</em>.
	 *
	 * @param d
	 */
	public int dimension( final int d )
	{
		return dimensions[ d ];
	}

	/**
	 * Write the number of pixels in each dimension into long[].
	 *
	 * @param dimensions
	 */
	public void dimensions( final int[] dim )
	{
		for ( int d = 0; d < n; ++d )
			dim[ d ] = dimensions[ d ];
	}
}
