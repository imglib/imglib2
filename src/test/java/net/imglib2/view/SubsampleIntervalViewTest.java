package net.imglib2.view;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.IntType;

public class SubsampleIntervalViewTest
{
	@Test
	public void test1DSubsampling() {
		// Tests single dimensional (array) subsampling
		
		final int[] testData = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		final int[][] expectedSubsamples = new int[][] {
			{},
			{}, // not testing a subsampling size of 0 or 1
			{ 0, 2, 4,  6, 8, 10 }, // sample size 2
			{ 0, 3, 6,  9 },
			{ 0, 4, 8  },
			{ 0, 5, 10 },
			{ 0, 6 },
			{ 0, 7 },
			{ 0, 8 } // sample size 8
		};
		
		final int[] testShifts = new int[] {-17, -8, 2, 0, 5, 8, 9, 10};

		final ArrayImg< IntType, IntArray > interval = ArrayImgs.ints( testData, testData.length );
		IntervalView< IntType > shiftedInterval;
		SubsampleIntervalView< IntType > subInterval;
		
		for(int shift : testShifts)
		{
			shiftedInterval = Views.translate( interval,  shift );
			// subsampling should be shift-invariant
			
			for(int subsamplesize = 2; subsamplesize <= 8; subsamplesize ++)
			{
				subInterval = Views.subsample( shiftedInterval, subsamplesize );
				// try subsample sizes between 2 and 8, compare to the expected subsamples
				Cursor< IntType > subIntCursor = Views.flatIterable( subInterval ).cursor();
				for(int i = 0; i < expectedSubsamples[subsamplesize].length || subIntCursor.hasNext(); i ++) {
					Assert.assertEquals( expectedSubsamples[subsamplesize][i], subIntCursor.next().get() );
				}
			}
		}
	}
}
