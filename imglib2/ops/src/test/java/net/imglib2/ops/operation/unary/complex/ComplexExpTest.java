package net.imglib2.ops.operation.unary.complex;

import static org.junit.Assert.*;

import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexExpTest {

	private ComplexExp<ComplexDoubleType,ComplexDoubleType> op =
			new ComplexExp<ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		
		// try power of 0
		doCase(0,0,1,0);

		// try real integer 1 & -1
		doCase(1,0,Math.E,0);
		doCase(-1,0,0.367879,0);
		
		// try imaginary integer 1 & -1
		doCase(0,1,0.540302,0.841470);
		doCase(0,-1,0.540302,-0.841470);

		// try multiple integral powers
		for (int i = 0; i < 20; i++)
			doCase(i,0,Math.pow(Math.E, i),0);
		
		// try some random values
		doCase(1,2,-1.131204,2.471726);
		doCase(2.4,-2.6,-9.44564,-5.68246);
		doCase(1.5,4.3,-1.79626,-4.10597);
		doCase(-6.2,3.2,-0.002025,-0.000118);
		doCase(-4.4,-2.7,-0.011099,-0.005247);
		doCase(Math.E,Math.E,-13.816654,6.225087);
	}

	private void doCase(double r1, double i1, double expR, double expI) {
		input1.setComplexNumber(r1, i1);
		op.compute(input1, output);
		assertEquals(expR, output.getRealDouble(), 0.00001);
		assertEquals(expI, output.getImaginaryDouble(), 0.00001);
	}
}
