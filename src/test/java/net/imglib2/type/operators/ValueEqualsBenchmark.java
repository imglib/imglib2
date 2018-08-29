package net.imglib2.type.operators;

import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Util;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Test if we should prefer ValueEquals for performance reasons.
 */
@State( Scope.Benchmark )
public class ValueEqualsBenchmark
{

	private long[] dims = { 200, 200, 200 };

	private ArrayImg< DoubleType, DoubleArray > imageA = ArrayImgs.doubles( dims );
	private ArrayImg< DoubleType, DoubleArray > imageB = ArrayImgs.doubles( dims );

	@Benchmark
	public void testValueEquals(Blackhole blackhole)
	{
		ArrayCursor< DoubleType > cursorA = imageA.cursor();
		ArrayCursor< DoubleType > cursorB = imageB.cursor();
		boolean result = true;
		while(cursorA.hasNext()) {
			result &= cursorA.next().valueEquals( cursorB.next() );
		}
		blackhole.consume( result );
	}

	@Benchmark
	public void testEquals(Blackhole blackhole)
	{
		ArrayCursor< DoubleType > cursorA = imageA.cursor();
		ArrayCursor< DoubleType > cursorB = imageB.cursor();
		boolean result = true;
		while(cursorA.hasNext()) {
			result &= cursorA.next().equals( cursorB.next() );
		}
		blackhole.consume( result );
	}

	public static void main( String[] args ) throws RunnerException
	{
		Options opt = new OptionsBuilder()
				.include( ValueEqualsBenchmark.class.getSimpleName() )
				.forks( 1 )
				.warmupIterations( 20 )
				.measurementIterations( 20 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
