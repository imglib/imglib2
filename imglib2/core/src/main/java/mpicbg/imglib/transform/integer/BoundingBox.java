package mpicbg.imglib.transform.integer;

import mpicbg.imglib.FinalInterval;
import mpicbg.imglib.Interval;

public final class BoundingBox
{
	final public int n;

	final public long[] corner1;

	final public long[] corner2;

	public BoundingBox( final int n )
	{
		this.n = n;
		this.corner1 = new long[ n ];
		this.corner2 = new long[ n ];
	}

	public BoundingBox( final long[] corner1, final long[] corner2 )
	{
		assert corner1.length == corner2.length;

		this.n = corner1.length;
		this.corner1 = corner1.clone();
		this.corner2 = corner2.clone();
	}

	public BoundingBox( Interval interval )
	{
		this.n = interval.numDimensions();
		this.corner1 = new long[ n ];
		this.corner2 = new long[ n ];
		interval.min( corner1 );
		interval.max( corner2 );
	}

	public int numDimensions()
	{
		return n;
	}

	public void corner1( long[] c )
	{
		assert c.length >= n;
		for ( int d = 0; d < n; ++d )
			c[ d ] = this.corner1[ d ];
	}

	public void corner2( long[] c )
	{
		assert c.length >= n;
		for ( int d = 0; d < n; ++d )
			c[ d ] = this.corner2[ d ];
	}

	/**
	 * flip coordinates between corner1 and corner2 such that corner1 is the min
	 * of the bounding box and corner2 is the max.
	 */
	public void orderMinMax()
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( corner1[ d ] > corner2[ d ] )
			{
				final long tmp = corner1[ d ];
				corner1[ d ] = corner2[ d ];
				corner2[ d ] = tmp;
			}
		}
	}

	/**
	 * @return bounding box as an interval.
	 */
	public Interval getInterval()
	{
		orderMinMax();
		return new FinalInterval( corner1, corner2 );
	}
}
