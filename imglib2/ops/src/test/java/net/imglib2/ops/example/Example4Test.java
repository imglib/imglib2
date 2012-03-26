package net.imglib2.ops.example;

import static org.junit.Assert.*;

import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.Condition;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.condition.OnTheXYCrossCondition;
import net.imglib2.ops.function.general.ConditionalFunction;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealProductFunction;
import net.imglib2.type.numeric.real.DoubleType;

// get values that are an average of the 5 values in a 3x3 cross

public class Example4Test {

	private final int XSIZE = 200;
	private final int YSIZE = 300;
	
	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private double expectedValue(int x, int y) {
		double ctr = x+y;
		double ne = (x+1) + (y-1);
		double nw = (x-1) + (y-1);
		double se = (x+1) + (y+1);
		double sw = (x-1) + (y+1);
		return ctr * ne * nw * se * sw;
	}
	
	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private Img<DoubleType> makeInputImage() {
		Img<DoubleType> inputImg = allocateImage();
		RandomAccess<DoubleType> accessor = inputImg.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x+y);
			}			
		}
		return inputImg;
	}
	
	@Test
	public void testCrossNeighborhoodProduct() {

		Img<DoubleType> inputImg = makeInputImage();
		
		DiscreteNeigh neigh = new DiscreteNeigh(new long[2], new long[]{1,1}, new long[]{1,1});
		Condition<long[]> condition = new OnTheXYCrossCondition();
		Function<long[],DoubleType> input = new RealImageFunction<DoubleType,DoubleType>(inputImg, new DoubleType());
		Function<long[],DoubleType> one = new ConstantRealFunction<long[],DoubleType>(inputImg.firstElement(),1);
		Function<long[],DoubleType> conditionalFunc = new ConditionalFunction<long[],DoubleType>(condition, input, one);
		Function<long[],DoubleType> prodFunc = new RealProductFunction<DoubleType>(conditionalFunc); 
		long[] index = new long[2];
		DoubleType output = new DoubleType();
		for (int x = 1; x < XSIZE-1; x++) {
			for (int y = 1; y < YSIZE-1; y++) {
				index[0] = x;
				index[1] = y;
				neigh.moveTo(index);
				prodFunc.evaluate(neigh, neigh.getKeyPoint(), output);
				assertTrue(veryClose(output.getRealDouble(), expectedValue(x,y)));
				/*
				{
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+expectedValue(x,y)+") actual ("+output.getRealDouble()+")");
					success = false;
				}
				*/
			}
		}
	}
}
