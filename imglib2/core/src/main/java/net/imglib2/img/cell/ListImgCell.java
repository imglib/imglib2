package net.imglib2.img.cell;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;

public final class ListImgCell< A extends ArrayDataAccess< A > > implements Cell< A >, Type< ListImgCell< A > > 
{
	final protected int n;

	final int[] dimensions;
	final int[] steps;
	final long[] min;
	final long[] max;

	protected int numPixels;

	private A data;

	public ListImgCell( final A creator, final int[] dim, final long[] offset, final int entitiesPerPixel )
	{
		dimensions = dim.clone();
		n = dimensions.length;
		steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( dimensions, steps );
		this.min = offset.clone();

		max = new long[ n ];
		for ( int d = 0; d < n; ++d ) {
			max[ d ] = offset[ d ] + dimensions[ d ] - 1;
		}

		int nPixels = 1;
		for ( int d = 0; d < n; ++d ) {
			nPixels *= dimensions[ d ];
		}
		numPixels = nPixels;

		this.data = creator.createArray( numPixels * entitiesPerPixel );
	}

	public ListImgCell(final int numDimensions)
	{
		n = numDimensions;

		dimensions = new int[ n ];
		steps = new int[ n ];
		min = new long[ n ];
		max = new long[ n ];

		numPixels = 0;

		this.data = null;
	}

	@Override
	public A getData()
	{
		return data;
	}


	@Override
	public ListImgCell<A> copy()
	{
		ListImgCell<A> c = new ListImgCell<A>( n );
		c.set( this );
		return c;
	}

	@Override
	public void set( ListImgCell<A> c )
	{
		assert( n == c.n );

		for ( int d = 0; d < n; ++d ) {
			dimensions[ d ] = c.dimensions[ d ];
			steps[ d ] = c.steps[ d ];
			min[ d ] = c.min[ d ];
			max[ d ] = c.max[ d ];
		}
		numPixels = c.numPixels;

		this.data = c.data;
	}

	@Override
	public ListImgCell<A> createVariable()
	{
		return new ListImgCell<A>( n );
	}

	@Override
	public long size()
	{
		return numPixels;
	}

	@Override
	public long indexToGlobalPosition( int index, int d )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, d ) + min[ d ];
	}

	@Override
	public void indexToGlobalPosition( int index, final long[] position )
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
	@Override
	public int localPositionToIndex( final long[] position )
	{
		return IntervalIndexer.positionToIndex( position, dimensions );
	}

	@Override
	public long min( int d )
	{
		return min[ d ];
	}

	@Override
	public void min( long[] minimum )
	{
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = min[ d ];
	}

	@Override
	public int dimension( int d )
	{
		return dimensions[ d ];
	}

	@Override
	public void dimensions( int[] dim )
	{
		for ( int d = 0; d < n; ++d )
			dim[ d ] = dimensions[ d ];
	}

	@Override
	public int[] getStepsArray()
	{
		return steps;
	}

	@Override
	public long[] getMinArray()
	{
		return min;
	}

	@Override
	public long[] getMaxArray()
	{
		return max;
	}
}
