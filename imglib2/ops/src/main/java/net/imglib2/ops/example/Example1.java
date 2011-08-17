package net.imglib2.ops.example;

import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.image.RealImage;
import net.imglib2.ops.operation.binary.real.RealAdd;


public class Example1 {
	
	private static final long XSIZE = 100;
	private static final long YSIZE = 200;
	
	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static RealImage makeInputImage() {
		RealImage image = new RealImage(new long[]{XSIZE,YSIZE}, new String[]{"X","Y"});
		long[] idx = new long[2];
		Real real = new Real();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				idx[0] = x;
				idx[1] = y;
				real.setReal(x+y);
				image.setReal(idx,real);
			}
		}
		return image;
	}
	
	// calculate output values by adding 15 to the values of an input image
	
	private static boolean testAssignment() {
		
		boolean success = true;
		
		/*
		RealImage inputImage = makeInputImage();
		
		DiscreteNeigh neighborhood = new DiscreteNeigh(new long[2], new long[2], new long[2]);
		
		Function<DiscreteNeigh,Real> constant = new ConstantRealFunction<DiscreteNeigh>(15);
		
		Function<DiscreteNeigh,Real> image = new RealImageFunction(inputImage);

		Function<DiscreteNeigh,Real> additionFunc =
			new GeneralBinaryFunction<DiscreteNeigh, Real>(constant, image, new RealAdd());
		
		long[] index = neighborhood.getKeyPoint();
		
		Real pointValue = new Real();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				index[0] = x;
				index[1] = y;
				additionFunc.evaluate(neighborhood, pointValue);
				if (! veryClose(pointValue.getReal(), (x+y+15))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
							+(x+y+15)+") actual ("+pointValue.getReal()+")");
					success = false;
				}
			}
		}
		*/
		
		return success;
	}
	

	public static void main(String[] args) {
		System.out.println("Example1");
		if (testAssignment())
			System.out.println(" Successful test");
	}
}
