package net.imglib2.view;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ConstantUtils;

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
	
	@Test
	public void testDimSubsampling() {
		final long[] minValues = new long[] {   0, 30, -10, 303, -302};
		final long[] maxValues = new long[] { 100, 53,   7, 305, 1431};
		final int dimension = minValues.length;
		
		final FinalInterval interval = new FinalInterval(minValues, maxValues);
		
		final long[][] expectedDimensionSizes = new long[][] {
			{},
			{}, // not testing a subsampling size of 0 or 1
			{51, 12, 9, 2, 867},
			{34, 8, 6, 1, 578},
			{26, 6, 5, 1, 434},
			{21, 5, 4, 1, 347},
			{17, 4, 3, 1, 289},
			{15, 4, 3, 1, 248},
			{13, 3, 3, 1, 217},
		};
		
		// tests a bunch of random shifts in each dimension
		final long[][] testShifts = new long[][] {
			{-56,   11,  82, -83,  -33},
			{  4,   18,  10,   8,   13},
			{  4,  -85,   0, -58,  -73},
			{ -7,    3, -75, -23,   31},
			{ 79,   -7,  54,  44,    1},
			{  0, -802, 968, 185, 1072}
		};

		IntervalView< FloatType > shiftedInterval;
		SubsampleIntervalView< FloatType > subInterval;
		
		RandomAccessibleInterval< FloatType > randAccessInterval = ConstantUtils.constantRandomAccessibleInterval( 
			     new FloatType( 1f ), 5, interval );
				
		for(long[] shift : testShifts) {
			shiftedInterval = Views.translate( randAccessInterval, shift );
			for(int subsamplesize = 2; subsamplesize <= 8; subsamplesize ++ ) {
				subInterval = Views.subsample( shiftedInterval, subsamplesize);
				// checks that size of each dimension is what it should be after subsampling
				Assert.assertArrayEquals( expectedDimensionSizes[subsamplesize], subInterval.dimensions );
			}
		}
	}
}
