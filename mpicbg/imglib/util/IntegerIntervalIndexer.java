package mpicbg.imglib.util;


/**
 * @author Tobias Pietzsch
 *
 * N-dimensional data is often stored in a flat 1-dimensional array.
 * This class provides convenience methods to translate between N-dimensional
 * indices (positions) and 1-dimensional indices.
 */
public class IntegerIntervalIndexer
{
	final static public int positionToIndex( final int[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}
	
	final static public int indexToPosition( final int index, final int[] dimensions, final int[] steps, final int[] offset, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) - offset[ dimension ];
	}
	
	
	
	
	

	public final long positionToIndex( final long[] position, final long[] dims )
	{
		final int maxDim = dims.length - 1;
		long i = position[ maxDim ];
		for ( int d = maxDim; d > 0; --d )
			i = i * dims[ d - 1 ] + position[ d ];
		return i + position[ 0 ];
	}
	
	
	
	
	
	/**
	 * Create allocation step array from the dimensions of an N-dimensional array.
	 *  
	 * @param dim
	 * @param steps
	 */
	public static void createAllocationSteps( final long[] dim, final long[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dim.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dim[ d - 1 ];
	}

	public static void createAllocationSteps( final int[] dim, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dim.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dim[ d - 1 ];
	}
	


}
