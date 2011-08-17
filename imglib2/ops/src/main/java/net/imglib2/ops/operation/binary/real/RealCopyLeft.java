package net.imglib2.ops.operation.binary.real;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Real;

public class RealCopyLeft implements BinaryOperation<Real> {

	@Override
	public void compute(Real input1, Real input2, Real output) {
		output.setReal(input1.getReal());
	}

}
