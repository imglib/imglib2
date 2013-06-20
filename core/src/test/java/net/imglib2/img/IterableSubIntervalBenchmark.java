package net.imglib2.img;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.view.Views;

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
		final long[] dimensionsUnoptimized = new long[] { 501, 500, 100, 2 }; //don't fit the interval will force unoptimized cursor
		
		final Interval inter = new FinalInterval(new long[] { 0, 0, 1, 0 }, new long[] { 499, 499, 99, 0 });

		// create and fill images
		final ArrayImg<IntType, ?> arrayImg = ArrayImgs.ints(dimensions); //fits the interval
		final ArrayImg<IntType, ?> arrayImgUnOp = ArrayImgs.ints(dimensionsUnoptimized); //doesn't fit the interval

		
		//either run this
		methodA(numRuns, printIndividualTimes, inter, arrayImg, arrayImgUnOp);
		System.out.println("################## again #############");
		methodA(numRuns, printIndividualTimes, inter, arrayImg, arrayImgUnOp);
		
		
		//or this
//		methodB(numRuns, printIndividualTimes, inter, arrayImg, arrayImgUnOp);
		
		
	}

	protected static void methodA(final int numRuns,
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
	
	protected static void methodB(final int numRuns,
			final boolean printIndividualTimes, final Interval inter,
			final ArrayImg<IntType, ?> arrayImg,
			final ArrayImg<IntType, ?> arrayImgUnOp) {
		
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

}
