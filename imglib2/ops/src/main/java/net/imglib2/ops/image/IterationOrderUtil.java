package net.imglib2.ops.image;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealInterval;

/**
 * 
 * @author dietzc, hornm University of Konstanz
 */
public class IterationOrderUtil
{

	/**
	 * Helper to set positions even though the position-array dimension and
	 * cursor dimension don't match.
	 * 
	 * @param pos
	 * @param access
	 */

	public static boolean equalIterationOrder( IterableRealInterval< ? > a, IterableRealInterval< ? >... bs )
	{
		for ( int i = 1; i < bs.length; i++ )
		{
			if ( !equalIterationOrder( a, bs[ i ] ) )
				return false;
		}
		return true;
	}

	public static boolean equalInterval( RealInterval a, RealInterval... bs )
	{
		for ( int i = 1; i < bs.length; i++ )
		{
			if ( !equalInterval( a, bs[ i ] ) )
				return false;
		}
		return true;
	}

	public static boolean equalInterval( RealInterval a, RealInterval b )
	{
		for ( int i = 0; i < a.numDimensions(); i++ )
		{
			if ( a.realMin( i ) != b.realMin( i ) )
				return false;
			if ( a.realMax( i ) != b.realMax( i ) )
				return false;
		}
		return true;
	}
}
