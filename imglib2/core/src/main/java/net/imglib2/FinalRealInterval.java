package net.imglib2;

/**
 * Implementation of the {@link RealInterval} interface.
 * 
 * @author Stephan Preibisch
 */
public class FinalRealInterval extends AbstractRealInterval
{
	/**
	 * Creates a new {@link AbstractRealInterval} using an existing {@link RealInterval}
	 * 
	 * @param interval
	 */
	public FinalRealInterval( final RealInterval interval )
	{
		super( interval );
	}

	/**
	 * Creates a new {@link AbstractRealInterval} from min and max coordinates
	 * 
	 * @param min
	 * @param max
	 */
	public FinalRealInterval( final double[] min, final double[] max )
	{
		super( min, max );
	}
	
	/**
	 * Creates a new zero-bounded {@link AbstractRealInterval} with a certain size
	 * 
	 * @param dimensions
	 */
	public FinalRealInterval( final double[] dimensions )
	{
		super( dimensions );
	}	
}
