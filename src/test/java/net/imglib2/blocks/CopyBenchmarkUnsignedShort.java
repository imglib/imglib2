package net.imglib2.blocks;

import java.util.concurrent.TimeUnit;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class CopyBenchmarkUnsignedShort
{
	private final int[] cellDimensions = { 64, 64, 64 };
	private final int[] srcDimensions = { 300, 300, 300 };
	private final int[] destDimensions = { 100, 100, 100 };
	private final int[] pos = { 64, 100, 100 };
	private final int[] oobPos = { -64, -64, -64 };

	private final CellImg< UnsignedShortType, ? > cellImg;

	private final ArrayImg< UnsignedShortType, ? > destArrayImg;

	private final short[] dest;

	public CopyBenchmarkUnsignedShort()
	{
		final CellImgFactory< UnsignedShortType > cellImgFactory = new CellImgFactory<>( new UnsignedShortType(), cellDimensions );
		cellImg = cellImgFactory.create( srcDimensions );
		destArrayImg = new ArrayImgFactory<>( new UnsignedShortType() ).create( destDimensions );
		dest = new short[ ( int ) Intervals.numElements( destDimensions ) ];
	}

	@Benchmark
	public void benchmarkLoopBuilder()
	{
		final long[] min = Util.int2long( pos );
		final long[] max = min.clone();
		for ( int d = 0; d < max.length; d++ )
			max[ d ] += destDimensions[ d ] - 1;
		LoopBuilder
				.setImages( Views.interval( cellImg, min, max), destArrayImg )
				.multiThreaded( false )
				.forEachPixel( (i,o) -> o.set( i.get() ) );
	}

	@Benchmark
	public void benchmarkLoopBuilderOobMirrorSingle()
	{
		final long[] min = Util.int2long( oobPos );
		final long[] max = min.clone();
		for ( int d = 0; d < max.length; d++ )
			max[ d ] += destDimensions[ d ] - 1;
		LoopBuilder
				.setImages( Views.interval( Views.extendMirrorSingle( cellImg ), min, max), destArrayImg )
				.multiThreaded( false )
				.forEachPixel( (i,o) -> o.set( i.get() ) );
	}

	@Benchmark
	public void benchmarkLoopBuilderOobConstant()
	{
		final long[] min = Util.int2long( oobPos );
		final long[] max = min.clone();
		for ( int d = 0; d < max.length; d++ )
			max[ d ] += destDimensions[ d ] - 1;
		LoopBuilder
				.setImages( Views.interval( Views.extendZero( cellImg ), min, max), destArrayImg )
				.multiThreaded( false )
				.forEachPixel( (i,o) -> o.set( i.get() ) );
	}

	@Benchmark
	public void benchmarkCellImgBlocks()
	{
		final PrimitiveBlocks< UnsignedShortType > blocks = PrimitiveBlocks.of( cellImg );
		blocks.copy( pos, dest, destDimensions );
	}

	@Benchmark
	public void benchmarkCellImgBlocksOobMirrorSingle()
	{
		final PrimitiveBlocks< UnsignedShortType > blocks = PrimitiveBlocks.of( Views.extendMirrorSingle( cellImg ) );
		blocks.copy( oobPos, dest, destDimensions );
	}

	@Benchmark
	public void benchmarkCellImgBlocksOobConstant()
	{
		final PrimitiveBlocks< UnsignedShortType > blocks = PrimitiveBlocks.of( Views.extendZero( cellImg ) );
		blocks.copy( oobPos, dest, destDimensions );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( CopyBenchmarkUnsignedShort.class.getSimpleName() + "\\." ).build();
		new Runner( options ).run();
	}
}
