package net.imglib2.ops.operations;

import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.operation.Operations;
import net.imglib2.ops.operation.SubsetOperations;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.real.unary.RealAddConstant;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SlicingCursor;

import org.junit.Before;
import org.junit.Test;

public class ArrayImgSubsetOperationsTest {

	private ArrayImg<IntType, ?> intImg;
	private ArrayImg<IntType, ?> outImg;

	@Before
	public void createSourceData() {
		long[] dimensions = new long[] { 20, 20, 5 };

		int numValues = 1;
		for (int d = 0; d < dimensions.length; ++d)
			numValues *= dimensions[d];

		final int[] intData = new int[numValues];
		final Random random = new Random(0);
		for (int i = 0; i < numValues; ++i) {
			intData[i] = random.nextInt();
		}

		intImg = new ArrayImgFactory<IntType>().create(dimensions,
				new IntType());
		outImg = new ArrayImgFactory<IntType>().create(dimensions,
				new IntType());

		// final long[] pos = new long[dimensions.length];
		// final RandomAccess<IntType> a = intImg.randomAccess();
		//
		// for (int i = 0; i < numValues; ++i) {
		// IntervalIndexer.indexToPosition(i, dimensions, pos);
		// a.setPosition(pos);
		// a.get().set(intData[i]);
		// }
	}

	@Test
	public void testUnaryOperationIteration() {

		try
		{
			SubsetOperations.iterate(new DummyOp<IntType, IntType>(),
					new int[] { 0, 1 }, intImg, outImg, null);
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

	private class DummyOp<T extends RealType<T>, V extends RealType<V>>
			implements
			UnaryOperation<RandomAccessibleInterval<T>, RandomAccessibleInterval<V>> {

		@Override
		public RandomAccessibleInterval<V> compute(
				RandomAccessibleInterval<T> input,
				RandomAccessibleInterval<V> output) {

			assertTrue(input.numDimensions() == 2);
			assertTrue(output.numDimensions() == 2);

			IterableInterval<T> iterable = Views.iterable(input);
			IterableInterval<V> iterable2 = Views.iterable(output);

			assertTrue(iterable.cursor() instanceof SlicingCursor);
			assertTrue(iterable2.cursor() instanceof SlicingCursor);

			Operations.map(new RealAddConstant<T, V>(1)).compute(iterable,
					iterable2);

			return output;
		}

		@Override
		public UnaryOperation<RandomAccessibleInterval<T>, RandomAccessibleInterval<V>> copy() {
			return new DummyOp<T, V>();
		}

	}
}
