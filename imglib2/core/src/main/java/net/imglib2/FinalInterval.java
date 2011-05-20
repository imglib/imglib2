package net.imglib2;


/**
 * Implementation of the {@link Interval} interface.
 * 
 * @author Tobias Pietzsch, Stephan Preibisch
 */
public final class FinalInterval implements Interval
{
	final protected int n;
	final protected long[] min;
	final protected long[] max;

	/**
	 * Creates a {@link FinalInterval} from another {@link Interval} 
	 * 
	 * @param interval - another {@link Interval}
	 * @param max - the position of the last elements in each dimension
	 */
	public FinalInterval ( final Interval interval )
	{
		this.n = interval.numDimensions();
		this.min = new long[ n ];
		this.max = new long[ n ];
		
		interval.min( min );
		interval.max( max );
	}

	/**
	 * Creates an Interval with the boundaries [min, max] (both including) 
	 * 
	 * @param min - the position of the first elements in each dimension
	 * @param max - the position of the last elements in each dimension
	 */
	public FinalInterval ( final long[] min, final long[] max )
	{
		assert min.length == max.length;

		this.n = min.length;
		this.min = min.clone();
		this.max = max.clone();
	}

	/**
	 * Creates an Interval with the boundaries [0, dimensions-1] 
	 * 
	 * @param dimensions - the size of the interval
	 */
	public FinalInterval ( final long[] dimensions )
	{
		this.n = dimensions.length;
		this.min = new long[ n ];
		this.max = new long[ n ];
		
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = dimensions[ d ] - 1;
	}

	@Override
	public double realMin( final int d )
	{
		assert d >= 0;
		assert d < n;

		return min[ d ];
	}

	@Override
	public void realMin( final double[] minimum )
	{
		assert minimum.length == n;
		
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = this.min[ d ];
	}

	@Override
	public void realMin( final RealPositionable minimum )
	{
		assert minimum.numDimensions() == n;
		
		minimum.setPosition( this.min ); 
	}

	@Override
	public double realMax( final int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void realMax( final double[] maximum )
	{
		assert maximum.length == n;
		
		for ( int d = 0; d < n; ++d )
			maximum[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		assert m.numDimensions() == n;
		
		m.setPosition( this.max ); 
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public long min( final int d )
	{
		assert d >= 0;
		assert d < n;

		return min[ d ];
	}

	@Override
	public void min( final long[] minimum )
	{
		assert minimum.length == n;
		
		for ( int d = 0; d < n; ++d )
			minimum[ d ] = this.min[ d ];
	}

	@Override
	public void min( final Positionable m )
	{
		assert m.numDimensions() == n;
		
		m.setPosition( this.min );
	}

	@Override
	public long max( final int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ];
	}

	@Override
	public void max( final long[] maximum )
	{
		assert maximum.length == n;
		
		for ( int d = 0; d < n; ++d )
			maximum[ d ] = this.max[ d ];
	}

	@Override
	public void max( final Positionable m )
	{
		assert m.numDimensions() == n;
		
		m.setPosition( this.max );
	}
	
	@Override
	public void dimensions( final long[] dimensions )
	{
		assert dimensions.length == n;
		
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = max[ d ] - min[ d ] + 1;
	}

	@Override
	public long dimension( final int d )
	{
		assert d >= 0;
		assert d < n;

		return max[ d ] - min[ d ] + 1;
	}
}
