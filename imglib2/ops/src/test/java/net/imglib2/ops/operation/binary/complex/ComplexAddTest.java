package net.imglib2.ops.operation.binary.complex;

import static org.junit.Assert.*;

import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexAddTest {

	private ComplexAdd<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType> op =
			new ComplexAdd<ComplexDoubleType,ComplexDoubleType,ComplexDoubleType>(new ComplexDoubleType());
	private ComplexDoubleType input1 = new ComplexDoubleType();
	private ComplexDoubleType input2 = new ComplexDoubleType();
	private ComplexDoubleType output = new ComplexDoubleType();

	@Test
	public void test() {
		for (double r1 = -20.0; r1 <= 20.0; r1 += Math.PI / 10)
			for (double i1 = -20.0; i1 <= 20.0; i1 += Math.PI / 11)
				for (double r2 = -20.0; r2 <= 20.0; r2 += Math.PI / 12)
					for (double i2 = -20.0; i2 <= 20.0; i2 += Math.PI / 13)
						doCase(r1,i1,r2,i2);
			
	}
	
	private void doCase(double r1, double i1, double r2, double i2) {
		input1.setComplexNumber(r1, i1);
		input2.setComplexNumber(r2, i2);
		op.compute(input1, input2, output);
		assertEquals(r1+r2, output.getRealDouble(), 0);
		assertEquals(i1+i2, output.getImaginaryDouble(), 0);
	}

}
