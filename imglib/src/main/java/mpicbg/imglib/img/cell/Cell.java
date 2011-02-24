package mpicbg.imglib.img.cell;

import mpicbg.imglib.img.basictypeaccess.array.ArrayDataAccess;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.IntervalIndexer;

public class Cell< A extends ArrayDataAccess< A > > implements Type< Cell< A > > 
{
	final protected int n;

	final int[] dimensions;
	final int[] steps;
	final long[] offset;
	final long[] max;

	protected int numPixels;

	private A data;
	
	public Cell( final A creator, final int[] dim, final long[] offset, final int entitiesPerPixel )
	{
		dimensions = dim.clone();
		n = dimensions.length;		
		steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( dimensions, steps );
		this.offset = offset.clone();

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

	public Cell(final int numDimensions)
	{
		n = numDimensions;

		dimensions = new int[ n ];
		steps = new int[ n ];
		offset = new long[ n ];
		max = new long[ n ];

		numPixels = 0;

		this.data = null;
	}

	public A getData()
	{
		return data;
	}


	@Override
	public Cell<A> copy()
	{
		Cell<A> c = new Cell<A>( n );
		c.set( this );
		return c;
	}

	@Override
	public void set( Cell<A> c )
	{
		assert( n == c.n );

		for ( int d = 0; d < n; ++d ) {
			dimensions[ d ] = c.dimensions[ d ];
			steps[ d ] = c.steps[ d ];
			offset[ d ] = c.offset[ d ];
			max[ d ] = c.max[ d ];
		}
		numPixels = c.numPixels;

		this.data = c.data;		
	}

	@Override
	public Cell<A> createVariable()
	{
		return new Cell<A>( n );
	}

	public long size()
	{
		return numPixels;
	}

	public long indexToGlobalPosition( int index, int dimension )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, dimension ) + offset[ dimension ];
	}

	public void indexToGlobalPosition( int index, final long[] position )
	{
		IntervalIndexer.indexToPosition( index, dimensions, position );
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += offset[ d ];
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
}
