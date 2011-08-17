package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealMaxConstant implements UnaryOperation<Real> {

	private double constant;
	
	public RealMaxConstant(double constant) {
		this.constant = constant;
	}
	
	@Override
	public void compute(Real input, Real output) {
		double value = input.getReal();
		if (value < constant)
			output.setReal(value);
		else
			output.setReal(constant);
	}

}
