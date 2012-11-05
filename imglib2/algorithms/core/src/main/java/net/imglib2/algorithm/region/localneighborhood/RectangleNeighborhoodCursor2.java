package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

public class RectangleNeighborhoodCursor2< T > extends AbstractInterval implements Cursor< Neighborhood< T > >
{
	RectangleNeighborhoodRandomAccess< T > a;

	private final long[] dimensions;

	private final long[] tmp;

	private long index;

	private final long maxIndex;

	private long maxIndexOnLine;

	public RectangleNeighborhoodCursor2( final RandomAccessibleInterval< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory )
	{
		super( source );
		a = new RectangleNeighborhoodRandomAccess< T >( source, span, factory );
		dimensions = new long[ source.numDimensions() ];
		source.dimensions( dimensions );
		tmp = new long[ source.numDimensions() ];
		maxIndex = Intervals.numElements( source );
		reset();
	}

	private RectangleNeighborhoodCursor2( final RectangleNeighborhoodCursor2< T > c )
	{
		super( c );
		a = c.a.copy();
		dimensions = c.dimensions.clone();
		tmp = c.tmp.clone();
		maxIndex = c.maxIndex;
		index = c.index;
		maxIndexOnLine = c.maxIndexOnLine;
	}

	@Override
	public void fwd()
	{
		a.fwd( 0 );
		if ( ++index > maxIndexOnLine )
			nextLine();
	}

	private void nextLine()
	{
		a.setPosition( min[ 0 ], 0 );
		maxIndexOnLine += dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
		{
			a.fwd( d );
			if ( a.getLongPosition( d ) > max[ d ] )
				a.setPosition( min[ d ], d );
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = 0;
		maxIndexOnLine = dimensions[ 0 ];
		a.setPosition( min );
		a.bck( 0 );
	}

	@Override
	public boolean hasNext()
	{
		return index < maxIndex;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		index += steps;
		maxIndexOnLine = ( index < 0 ) ? dimensions[ 0 ] : ( 1 + index / dimensions[ 0 ] ) * dimensions[ 0 ];
		IntervalIndexer.indexToPositionWithOffset( index + 1, dimensions, min, tmp );
		a.setPosition( tmp );
	}

	@Override
	public Neighborhood< T > next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		// NB: no action.
	}

	@Override
	public RectangleNeighborhoodCursor2< T > copy()
	{
		return new RectangleNeighborhoodCursor2< T >( this );
	}

	@Override
	public RectangleNeighborhoodCursor2< T > copyCursor()
	{
		return copy();
	}

	@Override
	public void localize( final float[] position )
	{
		a.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		a.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return a.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return a.getDoublePosition( d );
	}

	@Override
	public Neighborhood< T > get()
	{
		return a.get();
	}

	@Override
	public void localize( final int[] position )
	{
		a.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		a.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return a.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return a.getLongPosition( d );
	}
}
