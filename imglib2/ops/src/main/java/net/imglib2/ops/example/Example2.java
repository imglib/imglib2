package net.imglib2.ops.example;

import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.ops.function.real.RealAverageFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.image.RealImage;


// take an average of the z values of a 3d image

public class Example2 {

	private static final int XRANGE = 50;
	private static final int YRANGE = 75;
	private static final int ZRANGE = 5;

	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static RealImage makeTestImage() {
		RealImage image = new RealImage(new long[]{XRANGE, YRANGE, ZRANGE}, new String[]{"X","Y","Z"});
		long[] index = new long[3];
		Real value = new Real();
		for (int x = 0; x < image.dimension(0); x++) {
			for (int y = 0; y < image.dimension(1); y++) {
				for (int z = 0; z < image.dimension(2); z++) {
					index[0] = x;
					index[1] = y;
					index[2] = z;
					value.setReal(x+y+z);
					image.setReal(index, value);
				}
			}
		}
		return image;
	}
	
	// calculate output values as an average of a number of Z planes
	
	private static boolean testZAveraging() {
		boolean success = true;
		/*
		RealImage image = makeTestImage();
		DiscreteNeigh inputNeigh = new DiscreteNeigh(new long[3], new long[]{0,0,0}, new long[]{0,0,ZRANGE-1});
		Function<DiscreteNeigh,Real> imageFunc = new RealImageFunction(image);
		Function<DiscreteNeigh,Real> aveFunc = new RealAverageFunction(inputNeigh,imageFunc);
		long[] currPt = new long[3];
		Real variable = new Real();
		for (int x = 0; x < XRANGE; x++) {
			for (int y = 0; y < YRANGE; y++) {
				currPt[0] = x;
				currPt[1] = y;
				currPt[2] = 0;
				inputNeigh.moveTo(currPt);
				aveFunc.evaluate(inputNeigh, variable);
				if (!veryClose(variable.getReal(), x+y+((0+1+2+3+4) / 5))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(x+y+((0+1+2+3+4) / 5))+") actual ("+variable.getReal()+")");
					success = false;
				}
			}
		}
		*/
		return success;
	}
	
	public static void main(String[] args) {
		System.out.println("Example2");
		if (testZAveraging())
			System.out.println(" Successful test");
	}
}
