package net.imglib2.ops.operations;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import net.imglib2.IterableInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.operation.SubsetOperations;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SlicingCursor;

import org.junit.Before;
import org.junit.Test;

public class LabelingSubsetOperationsTest
{

	private ArrayImg< IntType, ? > intImg;

	private ArrayImg< IntType, ? > outImg;

	private Labeling< String > inLabeling;

	private Labeling< String > outLabeling;

	@Before
	public void createSourceData()
	{
		long[] dimensions = new long[] { 20, 20, 5 };

		int numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		final int[] intData = new int[ numValues ];
		final Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
		}

		intImg = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		outImg = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );

		// final long[] pos = new long[dimensions.length];
		// final RandomAccess<IntType> a = intImg.randomAccess();
		//
		// for (int i = 0; i < numValues; ++i) {
		// IntervalIndexer.indexToPosition(i, dimensions, pos);
		// a.setPosition(pos);
		// a.get().set(intData[i]);
		// }

		inLabeling = new NativeImgLabeling< String, IntType >( intImg );
		outLabeling = new NativeImgLabeling< String, IntType >( outImg );
	}

	@Test
	public void testUnaryOperationIteration()
	{

		try
		{
			SubsetOperations.iterate( new DummyOp< String >(), new int[] { 0, 1 }, inLabeling, outLabeling, null );
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
		catch ( ExecutionException e )
		{
			e.printStackTrace();
		}
	}

	private class DummyOp< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
	{

		@Override
		public Labeling< L > compute( Labeling< L > input, Labeling< L > output )
		{

			assertTrue( input.numDimensions() == 2 );
			assertTrue( output.numDimensions() == 2 );

			IterableInterval< LabelingType< L >> iterable = Views.iterable( input );
			IterableInterval< LabelingType< L >> iterable2 = Views.iterable( output );

			assertTrue( iterable.cursor() instanceof SlicingCursor );
			assertTrue( iterable2.cursor() instanceof SlicingCursor );

			return output;
		}

		@Override
		public UnaryOperation< Labeling< L >, Labeling< L >> copy()
		{
			return new DummyOp< L >();
		}

	}
}
