package net.imglib2.ops.operation.binary.real;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Real;

public class RealAnd implements BinaryOperation<Real> {

	@Override
	public void compute(Real input1, Real input2, Real output) {
		double value = (long) input1.getReal() & (long) input2.getReal();
		output.setReal(value);
	}

}
