package net.imglib2.iterator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RealIntervalIteratorTests
{

	@Test
	public void realIntervalIteratorTest()
	{
		final double eps = 1e-6;
		final double[] min = new double[] { -1, -1 };
		final double[] max = new double[] { 1 + eps / 2, 1 + eps / 2 };

		final LocalizingRealIntervalIterator it = new LocalizingRealIntervalIterator(
				min, max, new double[] { 1, 0.5 } );

		int N = 0;
		while ( it.hasNext() )
		{
			it.fwd();

			int j = ( N - ( N % 3 ) ) / 3;
			assertEquals( ( N % 3 ) - 1, it.getDoublePosition( 0 ), eps );
			assertEquals( -1.0 + 0.5 * j, it.getDoublePosition( 1 ), eps );

			N++;
		}
		assertEquals( 15, N );
	}

}
