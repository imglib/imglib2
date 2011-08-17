package net.imglib2.ops.operation.binary.real;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Real;

public class RealMax implements BinaryOperation<Real> {

	@Override
	public void compute(Real input1, Real input2, Real output) {
		double value;
		if (input1.getReal() > input2.getReal())
			value = input1.getReal();
		else
			value = input2.getReal();
		output.setReal(value);
	}

}
