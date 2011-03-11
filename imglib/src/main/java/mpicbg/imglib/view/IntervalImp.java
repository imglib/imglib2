package mpicbg.imglib.view;

import mpicbg.imglib.Interval;

public class IntervalImp implements Interval
{
	final protected int n;
	final protected long[] min;
	final protected long[] max;
	
	public IntervalImp (final long[] min, final long[] max)
	{
		assert min.length == max.length;

		this.n = min.length;
		this.min = min.clone();
		this.max = max.clone();
	}

	@Override
	public double realMin( int d )
	{
		assert d >= 0;
		assert d < n;

		return min[ d ];
	}

	@Override
	public void realMin( double[] min )
	{
		assert min.length == n;
		
		for ( int d = 0; d < n; ++d )
			min[ d ] = this.min[ d ];
	}

	@Override
	public double realMax( int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void realMax( double[] max )
	{
		assert max.length == n;
		
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public long min( int d )
	{
		assert d >= 0;
		assert d < n;

		return min[ d ];
	}

	@Override
	public void min( long[] min )
	{
		assert min.length == n;
		
		for ( int d = 0; d < n; ++d )
			min[ d ] = this.min[ d ];
	}

	@Override
	public long max( int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void max( long[] max )
	{
		assert max.length == n;
		
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public void dimensions( long[] dimensions )
	{
		assert dimensions.length == n;
		
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = max[ d ] - min[ d ] + 1;
	}

	@Override
	public long dimension( int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ] - min[ d ] + 1;
	}
}
