import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.operation.subset.views.IterableSubsetView;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;

import org.junit.Before;
import org.junit.Test;

public class IterableSubIntervalCursorBenchmark
{
	ArrayImg< IntType, ? > intImg;

	private int numRuns = 100;

	private long[] dimensions;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 1000, 1000, 11 };

		int numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		int[] intData = new int[ numValues ];
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
		}

		intImg = ( ArrayImg< IntType, ? > ) new ArrayImgFactory< IntType >().create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = intImg.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	// Test KNIME Implementation
	@Test
	public void benchmarkSubsetView()
	{
		long start = System.currentTimeMillis();

		int ctr = 0;
		for ( int i = 0; i < numRuns; i++ )
		{
			for ( int k = 0; k < dimensions[ 2 ]; k++ )
			{
				IterableSubsetView< IntType > iterable = new IterableSubsetView< IntType >( intImg, new FinalInterval( new long[] { 0, 0, k }, new long[] { 999, 999, k } ) );
				Cursor< IntType > intCursor = iterable.cursor();

				while ( intCursor.hasNext() )
				{
					intCursor.next().get();
					ctr++;
				}
			}
		}
		System.out.println( "Time IterableSubsetView Implementation: (" + ctr + ") " + ( System.currentTimeMillis() - start ) );
	}

	// Test new ImgLib2 implementation
	@Test
	public void benchmarkSubsetCursor()
	{
		long start = System.currentTimeMillis();

		int ctr = 0;
		for ( int i = 0; i < numRuns; i++ )
		{
			for ( int k = 0; k < dimensions[ 2 ]; k++ )
			{
				Cursor< IntType > intCursor = intImg.cursor( new FinalInterval( new FinalInterval( new long[] { 0, 0, k }, new long[] { 999, 999, k } ) ) );

				while ( intCursor.hasNext() )
				{
					intCursor.next().get();
					ctr++;
				}
			}
		}

		System.out.println( "Time ImgLib2 Implementation (" + ctr + ") " + ( System.currentTimeMillis() - start ) );
	}
}
