package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealAbs implements UnaryOperation<Real> {

	@Override
	public void compute(Real input, Real output) {
		double value = Math.abs(input.getReal());
		output.setReal(value);
	}

}
