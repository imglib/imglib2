package net.imglib2.ops.operation.binary.real;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Real;

public class RealAvg implements BinaryOperation<Real> {

	@Override
	public void compute(Real input1, Real input2, Real output) {
		double value = (input1.getReal() + input2.getReal()) / 2;
		output.setReal(value);
	}

}
