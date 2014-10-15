/**
 * 
 */
package net.imglib2.realtransform;

import net.imglib2.RealPoint;
import net.imglib2.util.BenchmarkHelper;

/**
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 *
 */
public class ChainTransformTest
{
	final static int numRuns = 10;
	
	final static private long apply( final RealTransform transform, final int n )
	{
		final RealPoint p = new RealPoint( 0, 0, 0 );
		final long t = System.currentTimeMillis();
		for ( int i = 0; i < n; ++i )
		{
			transform.apply( p, p );
		}
		return System.currentTimeMillis() - t;
	}

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		final RealTransformSequence transform = new RealTransformSequence();
		transform.add( new Translation( 2, 5, 1 ) );
		transform.add( new Scale( 1, 2, 3 ) );
//		
//		final Matrix m = new Matrix(
//				new double[][]{
//						{ 1, 0, 0, 2 },
//						{ 0, 2, 0, 10 },
//						{ 0, 0, 3, 3 } } );
//		final RealTransform transform = new AffineTransform( m );
		
		BenchmarkHelper.benchmarkAndPrint(
				numRuns,
				true,
				new Runnable(){
					
					@Override
					final public void run()
					{
						apply( transform, 10000000 );
					}
				});
	}
}
