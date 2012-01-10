package net.imglib2.ops.operation.unary.complex;

import static org.junit.Assert.*;

import net.imglib2.ops.operation.binary.complex.ComplexPower;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexIntegerPowerTest {

	private ComplexPower opFull = new ComplexPower();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output1 = new ComplexDoubleType();
	private ComplexDoubleType output2 = new ComplexDoubleType();

	@Test
	public void test() {
		
		// NOTE - this test makes sure that ComplexIntegerPower matches
		// ComplexPower when you pass it integer valued complex numbers.
		// ComplexPower needs to be tested thoroughly.
		
		doCase(1,2);
		doCase(4,-3);
		doCase(-Math.PI,Math.E);
		doCase(-2,-3.2);
		doCase(.1,.1);
		doCase(6.1,0.5);
		doCase(-0.3,4.6);
	}

	private void doCase(double r1, double i1) {
		for (int i = 0; i < 13; i++) {
			ComplexDoubleType power = new ComplexDoubleType();
			power.setComplexNumber(i, 0);
			ComplexIntegerPower op = new ComplexIntegerPower(i);
			input1.setComplexNumber(r1, i1);
			input2.setComplexNumber(r1, i1);
			op.compute(input1, output1);
			opFull.compute(input2, power, output2);
			assertEquals(output1.getRealDouble(), output2.getRealDouble(), 0.000001);
			assertEquals(output1.getImaginaryDouble(), output2.getImaginaryDouble(), 0.000001);
		}
	}
}
