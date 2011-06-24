package net.imglib2;


/**
 * Implementation of the {@link Interval} interface.
 * 
 * @author Tobias Pietzsch, Stephan Preibisch
 */
public final class FinalInterval extends AbstractInterval
{
	/**
	 * Creates a {@link AbstractInterval} from another {@link Interval} 
	 * 
	 * @param interval - another {@link Interval}
	 */
	public FinalInterval ( final Interval interval )
	{
		super( interval );
	}

	/**
	 * Creates an Interval with the boundaries [min, max] (both including) 
	 * 
	 * @param min - the position of the first elements in each dimension
	 * @param max - the position of the last elements in each dimension
	 */
	public FinalInterval ( final long[] min, final long[] max )
	{
		super( min, max );
	}

	/**
	 * Creates an Interval with the boundaries [0, dimensions-1] 
	 * 
	 * @param dimensions - the size of the interval
	 */
	public FinalInterval ( final long[] dimensions )
	{
		super( dimensions );
	}
}
