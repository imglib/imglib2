package net.imglib2.ops.operation.binary.complex;

import static org.junit.Assert.*;

import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexMultiplyTest {

	private ComplexMultiply<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
			new ComplexMultiply<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>();
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		double[] values = new double[]{.1,.375,12,23.9,100,1250};
		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(r1,i1,r2,i2);

		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(r1,i1,-r2,-i2);

		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(-r1,-i1,r2,i2);

		for (double r1 : values)
			for (double r2 : values)
				for (double i1 : values)
					for (double i2 : values)
						doCase(-r1,-i1,-r2,-i2);

		// multiply 0 by something
		doCase(0,0,1,0);
		
		// multiply something by 0
		doCase(1,0,0,0);
		
		// multiply 0 by 0
		doCase(0,0,0,0);
	}

	private void doCase(double r1, double i1, double r2, double i2) {
		input1.setComplexNumber(r1, i1);
		input2.setComplexNumber(r2, i2);
		op.compute(input1, input2, output);
		double real = r1*r2 - i1*i2;
		double imag = r1*i2 + i1*r2;
		assertEquals(real, output.getRealDouble(), 0);
		assertEquals(imag, output.getImaginaryDouble(), 0);
	}
}
