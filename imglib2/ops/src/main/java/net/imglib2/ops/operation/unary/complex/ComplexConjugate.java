package net.imglib2.ops.operation.unary.complex;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.Complex;

public class ComplexConjugate implements UnaryOperation<Complex> {

	@Override
	public void compute(Complex input, Complex output) {
		double r = input.getReal();
		double i = -input.getImag();
		output.setReal(r);
		output.setImag(i);
	}

}
