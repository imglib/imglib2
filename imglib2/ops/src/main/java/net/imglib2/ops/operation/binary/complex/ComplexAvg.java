package net.imglib2.ops.operation.binary.complex;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Complex;

public class ComplexAvg implements BinaryOperation<Complex> {

	@Override
	public void compute(Complex input1, Complex input2, Complex output) {
		double r = (input1.getReal() + input2.getReal()) / 2;
		double i = (input1.getImag() + input2.getImag()) / 2;
		output.setReal(r);
		output.setImag(i);
	}

}
