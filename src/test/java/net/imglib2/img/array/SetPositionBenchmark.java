package net.imglib2.img.array;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.real.DoubleType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State( Scope.Benchmark )
public class SetPositionBenchmark
{

	Localizable localizablePoint = new Point( 3, 4, 5 );
	Localizable localizableArray = initCursor( ( ( Img< DoubleType > ) ArrayImgs.doubles( 10, 10, 10 ) ).localizingCursor() );
	Localizable slowCursor = initCursor( ( ( Img< DoubleType > ) ArrayImgs.doubles( 10, 10, 10 ) ).cursor() );
	Localizable localizablePlanar = initCursor( ( ( Img< DoubleType > ) PlanarImgs.doubles( 10, 10, 10 ) ).localizingCursor() );

	private Cursor< DoubleType > initCursor( Cursor< DoubleType > cursor )
	{
		for ( int i = 0; i < 544; i++ ) cursor.fwd();
		return cursor;
	}

	RandomAccess< DoubleType > ra = initRandomAccess(localizablePoint, localizablePlanar, localizableArray);

	private ArrayRandomAccess< DoubleType > initRandomAccess(Localizable... localizables)
	{
		ArrayRandomAccess< DoubleType > randomAccess = ArrayImgs.doubles( 10, 10, 10 ).randomAccess();
		// NB : Polymorphism has a huge effect on the performance of setPosition
		for ( int i = 0; i < 100000; i++ )
			for ( Localizable l : localizables )
				randomAccess.setPosition(l);
		return randomAccess;
	}

	@Benchmark
	public void setPositionPoint() {
		ra.setPosition( localizablePoint );
	}

	@Benchmark
	public void setPositionArray() {
		ra.setPosition( localizableArray );
	}

	@Benchmark
	public void setPositionPlanar() {
		ra.setPosition( localizablePlanar );
	}

	@Benchmark
	public void setPositionSlowCursor() {
		ra.setPosition( slowCursor );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( SetPositionBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 15 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
