package net.imglib2.blocks;

import static net.imglib2.view.fluent.RandomAccessibleIntervalView.Extension.border;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;

import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;

public class PrimitiveBlocksTest
{
	@Test
	public void testAddDimension()
	{
//		final Img< IntType > img = ArrayImgs.ints( IntStream.range( 0, 6 * 4 ).toArray(), 6, 4, 1 );
		final Img< IntType > img = ArrayImgs.ints( IntStream.range( 0, 6 * 4 ).toArray(), 6, 4 );
		final Interval interval = Intervals.createMinSize( 2, 1, -1, 3, 2, 3 );
		final int[] dest = new int[ ( int ) Intervals.numElements( interval ) ];
		img.view()
				.addDimension( 0, 0 )
				.extend( border() )
				.use( PrimitiveBlocks::of )
				.copy( interval, dest );
		System.out.println( "dest = " + Arrays.toString( dest ) );
	}

}
