package net.imglib2.img.cell;

import java.util.Random;

import net.imglib2.BenchmarkHelper;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;

public class CellRandomAccessBenchmark
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	CellImg< IntType, ?, ? > intImg;
	CellImg< IntType, ?, ? > intImgCopy;

	public void createSourceData()
	{
		dimensions = new long[] { 480, 480, 102 };

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		final Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}

		intImg = new CellImgFactory< IntType >( 40 ).create( dimensions, new IntType() );
	}


	/**
	 * Fill intImg (a CellContainer with 40x40x40 cells) with data using flat array iteration order.
	 */
	public void fillImage()
	{
		final int[] pos = new int[ dimensions.length ];
		final RandomAccess< IntType > a = intImg.randomAccess();

		final int[] idim = new int[ dimensions.length ];
		for ( int d = 0; d < dimensions.length; ++d )
			idim[ d ] = ( int ) dimensions[ d ];

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, idim, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}


	public void copyWithSourceIteration(final Img< IntType > srcImg, final Img< IntType > dstImg)
	{
		final long[] pos = new long[ dimensions.length ];
		final Cursor< IntType > src = srcImg.localizingCursor();
		final RandomAccess< IntType > dst = dstImg.randomAccess();
		while( src.hasNext() ) {
			src.fwd();
			src.localize( pos );
			dst.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	public static void main( final String[] args )
	{
		final CellRandomAccessBenchmark randomAccessBenchmark = new CellRandomAccessBenchmark();
		randomAccessBenchmark.createSourceData();

		System.out.println( "benchmarking fill" );
		BenchmarkHelper.benchmark( new BenchmarkHelper.Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.fillImage();
			}
		} );
		randomAccessBenchmark.intData = null;

		randomAccessBenchmark.intImgCopy = new CellImgFactory< IntType >( 32 ).create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to smaller" );
		BenchmarkHelper.benchmark( new BenchmarkHelper.Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;

		randomAccessBenchmark.intImgCopy = new CellImgFactory< IntType >( 50 ).create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to larger" );
		BenchmarkHelper.benchmark( new BenchmarkHelper.Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;

		randomAccessBenchmark.intImgCopy = new CellImgFactory< IntType >( new int[] {32, 64, 16} ).create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to mixed" );
		BenchmarkHelper.benchmark( new BenchmarkHelper.Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;
	}
}
