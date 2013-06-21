package net.imglib2.img;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.view.Views;

/*
 * Benchmarked 20.06.2013  Thinkpad i7-2620M CPU @ 2.7 GHz
 * 
 * 	Planar Image: the optimized cursor is faster by ~factor 2
 *  Array Image: the optimized cursor seems to bring no advantage (for localizing cursors performance even decreases)
 *  			 however this is most probably due to bad JIT optimizations. The underlying code should
 *  			 lead to an improvement. 
 */
public class IterableSubIntervalBenchmark {

	protected static void localizingWalkThrough(Img<IntType> img, Interval inter) {
		Cursor<IntType> c = Views.interval(img, inter).localizingCursor();
		while (c.hasNext()) {
			c.fwd();
			c.get();
		}
	}

	protected static void walkThrough(Img<IntType> img, Interval inter) {
		Cursor<IntType> c = Views.interval(img, inter).cursor();
		while ( c.hasNext()) {
			c.fwd();
			c.get();
		}
	}

	public static void main(final String[] args) {
		final int numRuns = 20;
		final boolean printIndividualTimes = false;
		
		
		final long[] dimensions = new long[] { 500, 500, 100, 2 };
		final long[] dimensionsUnoptimized = new long[] { 501, 500, 100, 2 }; //doesn't fit the interval will force unoptimized cursor
		
		final Interval inter = new FinalInterval(new long[] { 0, 0, 1, 0 }, new long[] { 499, 499, 99, 0 });

		// create and fill images
		final ArrayImg<IntType, ?> arrayImg = ArrayImgs.ints(dimensions); //fits the interval
		final ArrayImg<IntType, ?> arrayImgUnOp = ArrayImgs.ints(dimensionsUnoptimized); //doesn't fit the interval
		final PlanarImg<IntType, ?> planarImg = PlanarImgs.ints(dimensions); //fits the interval
		final PlanarImg<IntType, ?> planarImgUnOp = PlanarImgs.ints(dimensionsUnoptimized); //doesn't fit the interval
		
		testArrayImg(numRuns, printIndividualTimes, inter, arrayImg, arrayImgUnOp);
		testPlanarImg(numRuns, printIndividualTimes, inter, planarImg, planarImgUnOp);
	}

	/*
	 * the 2nd img is unoptimized with respect to the provided interval i.e. 
	 * while optimized cursors can be used for the first image given inter this is not possible
	 * for the 2nd one.
	 */
	protected static void testArrayImg(final int numRuns,
			final boolean printIndividualTimes, final Interval inter,
			final ArrayImg<IntType, ?> arrayImg,
			final ArrayImg<IntType, ?> arrayImgUnOp) {
		
		//BLOCK 1
		
		System.out.println("normal cursor | array img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an unoptimized iterator)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						walkThrough(arrayImgUnOp, inter);
					}
				});

		System.out.println("normal cursor | array img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an optimized IterableSubInterval cursor)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						walkThrough(arrayImg, inter);
					}
				});

		//BLOCK 2
		
		System.out.println("localizing cursor | array img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an unoptimized iterator)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						localizingWalkThrough(arrayImgUnOp, inter);
					}
				});

		System.out.println("localizing cursor | array img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an optimized IterableSubInterval cursor)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						localizingWalkThrough(arrayImg, inter);
					}
				});
	}
	

	/*
	 * the 2nd img is unoptimized with respect to the provided interval i.e. 
	 * while optimized cursors can be used for the first image given inter this is not possible
	 * for the 2nd one.
	 */
	protected static void testPlanarImg(final int numRuns,
			final boolean printIndividualTimes, final Interval inter,
			final PlanarImg<IntType, ?> planarImg,
			final PlanarImg<IntType, ?> planarImgUnOp) {
		
		//BLOCK 1
		
		System.out.println("normal cursor | planar img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an unoptimized iterator)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						walkThrough(planarImgUnOp, inter);
					}
				});

		System.out.println("normal cursor | planar img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an optimized IterableSubInterval cursor)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						walkThrough(planarImg, inter);
					}
				});

		//BLOCK 2
		
		System.out.println("localizing cursor | planar img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an unoptimized iterator)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						localizingWalkThrough(planarImgUnOp, inter);
					}
				});

		System.out.println("localizing cursor | planar img");
		System.out.println("walk through a subinterval");
		System.out.println("(using an optimized IterableSubInterval cursor)");
		BenchmarkHelper.benchmarkAndPrint(numRuns, printIndividualTimes,
				new Runnable() {
					@Override
					public void run() {
						localizingWalkThrough(planarImg, inter);
					}
				});
	}
		

}
