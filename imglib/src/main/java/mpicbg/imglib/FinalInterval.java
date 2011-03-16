package mpicbg.imglib;


/**
 * Implementation of the {@link Interval} interface.
 * 
 * @author Tobias Pietzsch
 */
public final class FinalInterval implements Interval
{
	final protected int n;
	final protected long[] min;
	final protected long[] max;
	
	public FinalInterval (final long[] min, final long[] max)
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
	public void realMin( double[] minimum )
	{
		assert minimum.length == n;
		
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = this.min[ d ];
	}

	@Override
	public double realMax( int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void realMax( double[] maximum )
	{
		assert maximum.length == n;
		
		for ( int d = 0; d < n; ++d )
			maximum[ d ] = this.max[ d ];
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
	public void min( long[] minimum )
	{
		assert minimum.length == n;
		
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = this.min[ d ];
	}

	@Override
	public long max( int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void max( long[] maximum )
	{
		assert maximum.length == n;
		
		for ( int d = 0; d < n; ++d )
			maximum[ d ] = this.max[ d ];
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
