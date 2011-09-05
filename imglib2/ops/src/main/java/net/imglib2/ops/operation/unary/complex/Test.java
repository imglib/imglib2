package net.imglib2.ops.operation.unary.complex;

import net.imglib2.ops.Complex;
import net.imglib2.ops.operation.binary.complex.ComplexDivide;

public class Test {

	private static final ComplexCos cosFunc = new ComplexCos();
	private static final ComplexReciprocal recipFunc = new ComplexReciprocal();
	private static final ComplexDivide divFunc = new ComplexDivide();
	private static final Complex one = Complex.createCartesian(1, 0);
		
	static void method1(Complex in, Complex out) {
		divFunc.compute(one, in, out);
	}
	
	static void method2(Complex in, Complex out) {
		recipFunc.compute(in, out);
	}

	static boolean veryClose(double v1, double v2) {
		return Math.abs(v1-v2) < 0.00001;
	}
	
	public static void main(String[] args) {
		System.out.println("Listing differences");
		Complex cos = new Complex();
		Complex out1 = new Complex();
		Complex out2 = new Complex();
		for (double x = -100; x <= 100; x += 0.5) {
			for (double y = -100; y <= 100; y += 0.3) {
				Complex input = Complex.createCartesian(x,y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
				method2(cos, out2);
				if (!veryClose(out1.getX(), out2.getX()) || !veryClose(out1.getY(), out2.getY()))
					System.out.println("Methods differ!");
			}
		}
		System.out.println("Done!");
		
		long pt1 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				Complex input = Complex.createCartesian(x,y);
				cosFunc.compute(input, cos);
				method1(cos, out1);
			}
		}
		long pt2 = System.currentTimeMillis();
		for (double x = -300; x <= 300; x += 0.2) {
			for (double y = -300; y <= 300; y += 0.1) {
				Complex input = Complex.createCartesian(x,y);
				cosFunc.compute(input, cos);
				method2(cos, out2);
			}
		}
		long pt3 = System.currentTimeMillis();
		
		System.out.println("Total milleseconds");
		System.out.println("  method1 = "+(pt2-pt1));
		System.out.println("  method2 = "+(pt3-pt2));
	}
}
