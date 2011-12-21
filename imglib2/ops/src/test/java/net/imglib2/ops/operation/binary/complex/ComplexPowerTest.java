package net.imglib2.ops.operation.binary.complex;

import static org.junit.Assert.*;

import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexPowerTest {

	private ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
			new ComplexPower<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>(new ComplexDoubleType());
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		// values taken from Wolfram Alpha online calculator
		doCase(4,0,2,0,16,0);
		doCase(0,4,2,0,-16,0);
		doCase(1,1,1,1,0.273957,0.583701);
		doCase(-1,-1,-1,-1,-0.028475,0.060669);
		doCase(1,2,3,4,0.129009,0.033924);
	}

	private void doCase(double r1, double i1, double r2, double i2, double expR, double expI) {
		input1.setComplexNumber(r1, i1);
		input2.setComplexNumber(r2, i2);
		op.compute(input1, input2, output);
		assertEquals(expR, output.getRealDouble(), 0.000001);
		assertEquals(expI, output.getImaginaryDouble(), 0.000001);
	}
}
