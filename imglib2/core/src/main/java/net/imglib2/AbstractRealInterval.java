package net.imglib2;

/**
 * Implementation of the {@link RealInterval} interface.
 * 
 * @author Stephan Preibisch
 */
public class AbstractRealInterval implements RealInterval
{
	final protected int n;
	final protected double[] min;
	final protected double[] max;

	/**
	 * Creates a new {@link AbstractRealInterval} using an existing {@link RealInterval}
	 * 
	 * @param interval
	 */
	public AbstractRealInterval( final RealInterval interval )
	{
		this.n = interval.numDimensions();
		this.min = new double[ n ];
		this.max = new double[ n ];
		
		for ( int d = 0; d < n; ++d )
		{
			this.min[ d ] = min[ d ];
			this.max[ d ] = max[ d ];
		}
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 * 
	 * @param min
	 * @param max
	 */
	public AbstractRealInterval( final double[] min, final double[] max )
	{
		this.n = min.length;
		this.min = min.clone();
		this.max = max.clone();
	}
	
	/**
	 * Creates a new zero-bounded {@link AbstractRealInterval} with a certain size
	 * 
	 * @param dimensions
	 */
	public AbstractRealInterval( final double[] dimensions )
	{
		this.n = dimensions.length;
		this.min = new double[ n ];
		this.max = new double[ n ];
		
		for ( int d = 0; d < n; ++d )
			this.max[ d ] = dimensions[ d ] - 1;
	}	

	@Override
	public int numDimensions() { return n; }

	@Override
	public double realMin( final int d) { return min[ d ]; }

	@Override
	public void realMin( final double[] min ) 
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = this.min[ d ];
	}

	@Override
	public void realMin( final RealPositionable min ) 
	{
		for ( int d = 0; d < n; ++d )
			min.setPosition( this.min[ d ], d );
	}

	@Override
	public double realMax( final int d ) { return max[ d ]; }

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < n; ++d )
			max.setPosition( this.max[ d ], d );
	}
}
