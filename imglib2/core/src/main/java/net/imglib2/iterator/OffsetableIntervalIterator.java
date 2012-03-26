package net.imglib2.iterator;

import net.imglib2.Interval;

/**
 * A {@link IntervalIterator} that has an adjustable offset 
 * 
 * @author Stephan Preibisch
 */
public class OffsetableIntervalIterator extends IntervalIterator
{
	public OffsetableIntervalIterator( final long[] dimensions )
	{
		super( dimensions );
	}

	public OffsetableIntervalIterator( final int[] dimensions )
	{
		super( dimensions );
	}

	public OffsetableIntervalIterator( final long[] min, final long[] max )
	{
		super( min, max );
	}

	public OffsetableIntervalIterator( final int[] min, final int[] max )
	{
		super( min, max );
	}
	
	public OffsetableIntervalIterator( final Interval interval )
	{
		super( interval );
	}

	/**
	 * Adjust the offset and reset the iterator
	 * 
	 * @param min - new offset
	 */
	public void setMin( final int[] min )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.min[ d ] = min[ d ];
			this.max[ d ] = this.dimensions[ d ] + min[ d ] - 1;
		}
		
		reset();
	}
	
	/**
	 * Adjust the offset and reset the iterator
	 * 
	 * @param min - new offset
	 */
	public void setMin( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.min[ d ] = min[ d ];
			this.max[ d ] = this.dimensions[ d ] + min[ d ] - 1;
		}
		
		reset();
	}
}
