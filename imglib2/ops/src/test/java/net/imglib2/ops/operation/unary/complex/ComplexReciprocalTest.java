package net.imglib2.ops.operation.unary.complex;

import static org.junit.Assert.*;

import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.type.numeric.complex.ComplexDoubleType;

import org.junit.Test;

public class ComplexReciprocalTest {

	@Test
	public void test() {
		
		doCase(0,0);
		doCase(1,0);
		doCase(0,1);
		doCase(-1,0);
		doCase(0,-1);
		doCase(Math.E,-Math.PI);
		doCase(-Math.PI,Math.E);
		doCase(3,4);
		doCase(-5,7);
		doCase(2,-6);
		doCase(-7,-3);
		doCase(22.5,97.1);
		doCase(1000,1000);
	}

	// this method tests that taking the reciprocal of a number is the same as
	// dividing one by that number.
	
	private void doCase(double r, double i) {
		ComplexDoubleType one = new ComplexDoubleType();
		ComplexDoubleType input = new ComplexDoubleType();
		ComplexDoubleType output1 = new ComplexDoubleType();
		ComplexDoubleType output2 = new ComplexDoubleType();
		one.setOne();
		input.setComplexNumber(r, i);
		ComplexReciprocal<ComplexDoubleType,ComplexDoubleType>
			opRecip = new ComplexReciprocal<ComplexDoubleType, ComplexDoubleType>(input);
		ComplexDivide<ComplexDoubleType, ComplexDoubleType, ComplexDoubleType>
			opDiv = new ComplexDivide<ComplexDoubleType, ComplexDoubleType, ComplexDoubleType>();
		opRecip.compute(input, output1);
		opDiv.compute(one, input, output2);
		assertEquals(output1.getRealDouble(), output2.getRealDouble(), 0.000001);
		assertEquals(output1.getImaginaryDouble(), output2.getImaginaryDouble(), 0.000001);
	}
}
