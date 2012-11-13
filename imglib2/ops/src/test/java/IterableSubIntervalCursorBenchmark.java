import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.junit.Before;
import org.junit.Test;

public class IterableSubIntervalCursorBenchmark {
	ArrayImg<IntType, ?> intImg;

	private final int numRuns = 100;

	private long[] dimensions;

	@Before
	public void createSourceData() {
		dimensions = new long[] { 1000, 1000, 11 };

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

		final long[] pos = new long[dimensions.length];
		final RandomAccess<IntType> a = intImg.randomAccess();

		for (int i = 0; i < numValues; ++i) {
			IntervalIndexer.indexToPosition(i, dimensions, pos);
			a.setPosition(pos);
			a.get().set(intData[i]);
		}
	}

	// // Test KNIME Implementation
	// @Test
	// public void benchmarkSubsetView()
	// {
	// final long start = System.currentTimeMillis();
	//
	// int ctr = 0;
	// for ( int i = 0; i < numRuns; i++ )
	// {
	// for ( int k = 0; k < dimensions[ 2 ]; k++ )
	// {
	// final IterableSubsetView< IntType > iterable = new IterableSubsetView<
	// IntType >( intImg, new FinalInterval( new long[] { 0, 0, k }, new long[]
	// { 999, 999, k } ) );
	// final Cursor< IntType > intCursor = iterable.cursor();
	//
	// while ( intCursor.hasNext() )
	// {
	// intCursor.next().get();
	// ctr++;
	// }
	// }
	// }
	// System.out.println( "Time IterableSubsetView Implementation: (" + ctr +
	// ") " + ( System.currentTimeMillis() - start ) );
	// }

	// // Test new ImgLib2 implementation
	// @Test
	// public void benchmarkSubsetCursor() {
	// final long start = System.currentTimeMillis();
	// int ctr = 0;
	// for (int i = 0; i < numRuns; i++) {
	// for (int k = 0; k < dimensions[2]; k++) {
	// final Cursor<IntType> intCursor = SubsetViews.subsetView(
	// intImg,
	// new FinalInterval(new long[] { 0, 0, k }, new long[] {
	// 999, 999, k })).cursor();
	//
	// while (intCursor.hasNext()) {
	// intCursor.next().get();
	// ctr++;
	// }
	// }
	// }
	//
	// System.out.println("Time ImgLib2 Subset Implementation (" + ctr + ") "
	// + (System.currentTimeMillis() - start));
	// }

	// Test new ImgLib2 implementation
	@Test
	public void benchmarkHyperSliceCursor() {
		final long start = System.currentTimeMillis();
		int ctr = 0;
		for (int i = 0; i < numRuns; i++) {
			for (int k = 0; k < dimensions[2]; k++) {
				final Cursor<IntType> intCursor = Views
						.hyperSlice(intImg, 2, k).cursor();

				while (intCursor.hasNext()) {
					intCursor.next().get();
					ctr++;
				}
			}
		}

		System.out.println("Time ImgLib2 Hyperslice Implementation (" + ctr
				+ ") " + (System.currentTimeMillis() - start));
	}

	// Test new ImgLib2 implementation
	@Test
	public void benchmarkIntervalCursor() {
		final long start = System.currentTimeMillis();
		int ctr = 0;
		for (int i = 0; i < numRuns; i++) {
			for (int k = 0; k < dimensions[2]; k++) {
				// final Cursor< IntType > intCursor = Views.interval( intImg,
				// new FinalInterval( new long[] { 0, 0, k }, new long[] { 999,
				// 999, k } ) ).cursor();
				final Cursor<IntType> intCursor = Views.interval(intImg,
						Intervals.createMinMax(0, 0, k, 999, 999, k)).cursor();

				while (intCursor.hasNext()) {
					intCursor.next().get();
					ctr++;
				}
			}
		}

		System.out.println("Time ImgLib2 Interval Implementation (" + ctr
				+ ") " + (System.currentTimeMillis() - start));
	}
}
