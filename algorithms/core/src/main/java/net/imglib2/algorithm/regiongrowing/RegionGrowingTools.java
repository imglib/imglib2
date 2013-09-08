package net.imglib2.algorithm.regiongrowing;

import java.util.Arrays;

public class RegionGrowingTools
{

	public static enum GrowingMode
	{
		/**
		 * In sequential mode, the seed points are grown after each other.
		 */
		SEQUENTIAL,

		/**
		 * In parallel mode, each seed point is grown one step before switching
		 * to the next one.
		 */
		PARALLEL;
	}

	/**
	 * Returns an array of offsets to the 8x-connected (or N-d equivalent)
	 * structuring element for the dimension space. The structuring element is
	 * the list of offsets from the center to the pixels to be examined.
	 * 
	 * @param nDims
	 *            number of dimensions.
	 * @return the structuring element as an array of <code>long[]</code>.
	 */
	public static final long[][] get8ConStructuringElement( final int nDims )
	{
		int nElements = 1;
		for ( int i = 0; i < nDims; i++ )
		{
			nElements *= 3;
		}
		nElements--;
		final long[][] result = new long[ nElements ][ nDims ];
		final long[] position = new long[ nDims ];
		Arrays.fill( position, -1 );
		for ( int i = 0; i < nElements; i++ )
		{
			System.arraycopy( position, 0, result[ i ], 0, nDims );
			/*
			 * Special case - skip the center element.
			 */
			if ( i == nElements / 2 - 1 )
			{
				position[ 0 ] += 2;
			}
			else
			{
				for ( int j = 0; j < nDims; j++ )
				{
					if ( position[ j ] == 1 )
					{
						position[ j ] = -1;
					}
					else
					{
						position[ j ]++;
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns an array of offsets to the 4x-connected (or N-d equivalent)
	 * structuring element for the dimension space. The structuring element is
	 * the list of offsets from the center to the pixels to be examined.
	 * 
	 * @param nDims
	 *            the number of dimensions.
	 * @return the structuring element as an array of <code>long[]</code>
	 */
	public static final long[][] get4ConStructuringElement( final int nDims )
	{
		final int nElements = nDims * 2;

		final long[][] result = new long[ nElements ][ nDims ];
		for ( int d = 0; d < nDims; d++ )
		{
			result[ d * 2 ] = new long[ nDims ];
			result[ d * 2 + 1 ] = new long[ nDims ];
			result[ d * 2 ][ d ] = -1;
			result[ d * 2 + 1 ][ d ] = 1;

		}
		return result;
	}

}
