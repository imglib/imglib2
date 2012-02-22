package net.imglib2.ops.example;

import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.ops.operation.unary.complex.ComplexCos;
import net.imglib2.ops.operation.unary.complex.ComplexReciprocal;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

public class SpeedCalcs {

	private static final ComplexCos<ComplexDoubleType,ComplexDoubleType>
		cosFunc =	new ComplexCos<ComplexDoubleType, ComplexDoubleType>();
	private static final ComplexReciprocal<ComplexDoubleType,ComplexDoubleType>
		recipFunc = new ComplexReciprocal<ComplexDoubleType, ComplexDoubleType>();
	private static final ComplexDivide<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>
		divFunc = new ComplexDivide<ComplexDoubleType, ComplexDoubleType, ComplexDoubleType>();
	private static final ComplexDoubleType one = new ComplexDoubleType(1,0);
		
	static void method1(ComplexDoubleType in, ComplexDoubleType out) {
		divFunc.compute(one, in, out);
	}
	
	static void method2(ComplexDoubleType in, ComplexDoubleType out) {
		recipFunc.compute(in, out);
	}

	static boolean veryClose(double v1, double v2) {
		return Math.abs(v1-v2) < 0.00001;
	}
	
	public static void main(String[] args) {
		System.out.println("Listing differences to screen");
		ComplexDoubleType cos = new ComplexDoubleType();
		ComplexDoubleType out1 = new ComplexDoubleType();
		ComplexDoubleType out2 = new ComplexDoubleType();
		for (double x = -100; x <= 100; x += 0.5) {
			for (double y = -100; y <= 100; y += 0.3) {
				ComplexDoubleType input = new ComplexDoubleType(x,y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
				method2(cos, out2);
				if (!veryClose(out1.getRealDouble(), out2.getRealDouble()) ||
						!veryClose(out1.getImaginaryDouble(), out2.getImaginaryDouble()))
					System.out.println("Methods differ!");
			}
		}
		System.out.println("Done listing differences to screen");
		System.out.println("Running speed tests");
		
		ComplexDoubleType input = new ComplexDoubleType();
		long pt1 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
			}
		}
		long pt2 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method2(cos, out2);
			}
		}
		long pt3 = System.currentTimeMillis();
		
		System.out.println("Total milleseconds");
		System.out.println("  trial 1 of 2");
		System.out.println("    method1 (divide(1,z))   = "+(pt2-pt1));
		System.out.println("    method2 (reciprocal(z)) = "+(pt3-pt2));
	
		pt1 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
			}
		}
		pt2 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				input.setReal(x);
				input.setImaginary(y);
				cosFunc.compute(input, cos);
				method2(cos, out2);
			}
		}
		pt3 = System.currentTimeMillis();
		
		System.out.println("  trial 2 of 2");
		System.out.println("    method1 (divide(1,z))   = "+(pt2-pt1));
		System.out.println("    method2 (reciprocal(z)) = "+(pt3-pt2));
	}
	
}
