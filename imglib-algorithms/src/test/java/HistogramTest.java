import ij.IJ;

import java.util.ArrayList;
import java.util.Collections;

import mpicbg.imglib.algorithm.histogram.Histogram;
import mpicbg.imglib.algorithm.histogram.HistogramBin;
import mpicbg.imglib.algorithm.histogram.HistogramBinFactory;
import mpicbg.imglib.algorithm.histogram.HistogramKey;

import mpicbg.imglib.algorithm.histogram.discrete.DiscreteIntHistogramBinFactory;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;


public class HistogramTest {
	
	private static ArrayList<int[]> vectorList;
	private static ArrayList<int[]> expectKeys;
	private static ArrayList<int[]> expectCnts;
	
	private int[] vect;
	private int[] expectKey;
	private int[] expectCnt;
		
	private HistogramTest(int[] invect, int[] inExpectKey, int[] inExpectCnt)
	{
		vect = invect;
		expectKey = inExpectKey;
		expectCnt = inExpectCnt;
		
	}
	
	private boolean runTest()
	{
		ImageFactory<UnsignedByteType> imFactory =
			new ImageFactory<UnsignedByteType>(new UnsignedByteType(),
					new ArrayContainerFactory());
		Image<UnsignedByteType> im = imFactory.createImage(
				new int[]{vect.length});
		Cursor<UnsignedByteType> cursor = im.createCursor();
		Histogram<UnsignedByteType> histogram;
		HistogramBinFactory<UnsignedByteType> binFactory = 
			new DiscreteIntHistogramBinFactory<UnsignedByteType>();
		UnsignedByteType k = new UnsignedByteType();
		boolean check = true;
		

		for (int v: vect)
		{
			cursor.fwd();
			cursor.getType().set(v);
		}
		
		histogram = new Histogram<UnsignedByteType>(
					binFactory,
					im.createCursor());
		
		histogram.process();
		
		for (int i = 0; i < expectKey.length; ++i)
		{
			long cntVal;
			k.set(expectKey[i]);
			cntVal = histogram.getBin(k).getCount();
			IJ.log("For bin " + expectKey[i] + " expected " + expectCnt[i] +
					", got " + cntVal);
			check &= cntVal == expectCnt[i];
		}
		
		return check;
	}
	
	public static void makeTests()
	{
		int n = 2;
		vectorList = new ArrayList<int[]>(n);
		expectKeys = new ArrayList<int[]>(n);
		expectCnts = new ArrayList<int[]>(n);
		
		//Test 1
		vectorList.add(new int[]{1, 2, 3, 4});
		expectKeys.add(new int[]{1, 2, 3, 4, 5});
		expectCnts.add(new int[]{1, 1, 1, 1, 0});
		
		//Test 2
		int test2vect[] = new int[1024];
		int test2keys[] = new int[256];
		int test2cnts[] = new int[256];
		for (int i = 0; i < 1024; ++i)
		{
			test2vect[i] = i % 256;
		}
		for (int i = 0; i < 256; ++i)
		{
			test2keys[i] = i;
			test2cnts[i] = 4;
		}
		
		vectorList.add(test2vect);
		expectKeys.add(test2keys);
		expectCnts.add(test2cnts);
	}
	
	public static void main(String[] args)
	{
		makeTests();
		boolean[] passedTests = new boolean[vectorList.size()];
		for (int i = 0; i < vectorList.size(); ++i)
		{
			passedTests[i] = new HistogramTest(vectorList.get(i), expectKeys.get(i), expectCnts.get(i)).runTest();
		}
		
		for (int i = 0; i < passedTests.length; ++i)
		{
			String indication = passedTests[i] ? "passed" : "FAILED";
			
			IJ.log("Test " + (i + 1) + " " + indication);
		}
	}
	
}
