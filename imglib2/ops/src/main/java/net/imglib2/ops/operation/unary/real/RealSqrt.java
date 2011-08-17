package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealSqrt implements UnaryOperation<Real> {

	@Override
	public void compute(Real input, Real output) {
		double value = Math.sqrt(input.getReal());
		output.setReal(value);
	}

}
