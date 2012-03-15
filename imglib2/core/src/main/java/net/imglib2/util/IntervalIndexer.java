package net.imglib2.util;


/**
 * N-dimensional data is often stored in a flat 1-dimensional array.
 * This class provides convenience methods to translate between N-dimensional
 * indices (positions) and 1-dimensional indices.
 *
 * @author Tobias Pietzsch
 */
public class IntervalIndexer
{
	final static public int positionToIndex( final int[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public int positionToIndex( final long[] position, final int[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		int i = ( int )position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + ( int )position[ d ];
		return i;
	}

	final static public long positionToIndex( final long[] position, final long[] dimensions )
	{
		final int maxDim = dimensions.length - 1;
		long i = position[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ];
		return i;
	}

	final static public long positionWithOffsetToIndex( final long[] position, final long[] dimensions, final long[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		long i = position[ maxDim ] - offsets[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ] - offsets[ d ];
		return i;
	}

	final static public int positionWithOffsetToIndex( final int[] position, final int[] dimensions, final int[] offsets )
	{
		final int maxDim = dimensions.length - 1;
		int i = position[ maxDim ] - offsets[ maxDim ];
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * dimensions[ d ] + position[ d ] - offsets[ d ];
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

	final static public void indexToPosition( long index, final long[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = ( int )( index - j * dimensions[ d ] );
			index = j;
		}
		position[ maxDim ] = ( int )index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final long[] position )
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

	final static public void indexToPosition( long index, final long[] dimensions, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final float[] position )
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

	final static public void indexToPosition( long index, final long[] dimensions, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPosition( int index, final int[] dimensions, final double[] position )
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

	final static public void indexToPosition( long index, final long[] dimensions, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	final static public void indexToPositionWithOffset( int index, final int[] dimensions, final int[] offsets, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final long[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = ( int )( index - j * dimensions[ d ] + offsets[ d ] );
			index = j;
		}
		position[ maxDim ] = ( int )( index + offsets[ maxDim ] );
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final float[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public void indexToPositionWithOffset( long index, final long[] dimensions, final long[] offsets, final double[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final long j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ] + offsets[ d ];
			index = j;
		}
		position[ maxDim ] = index + offsets[ maxDim ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public long indexToPosition( final long index, final long[] dimensions, final int dimension )
	{
		int step = 1;
		for ( int d = 0; d < dimension; ++d )
			step *= dimensions[ d ];
		return ( index / step ) % dimensions[ dimension ];
	}

	final static public int indexToPositionWithOffset( final int index, final int[] dimensions, final int[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, dimension ) - offsets[ dimension ];
	}

	final static public int indexToPosition( final int index, final int[] dimensions, final int[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	final static public long indexToPosition( final long index, final long[] dimensions, final long[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	final static public int indexToPositionWithOffset( final int index, final int[] dimensions, final int[] steps, final int[] offset, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) - offset[ dimension ];
	}

	final static public long indexToPositionWithOffset( final long index, final long[] dimensions, final long[] steps, final long[] offsets, final int dimension )
	{
		return indexToPosition( index, dimensions, steps, dimension ) - offsets[ dimension ];
	}




	/**
	 * Create allocation step array from the dimensions of an N-dimensional array.
	 *
	 * @param dimensions
	 * @param steps
	 */
	public static void createAllocationSteps( final long[] dimensions, final long[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d - 1 ];
	}

	/**
	 * Create allocation step array from the dimensions of an N-dimensional array.
	 *
	 * @param dimensions
	 * @param steps
	 */
	public static void createAllocationSteps( final int[] dimensions, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d - 1 ];
	}



}
