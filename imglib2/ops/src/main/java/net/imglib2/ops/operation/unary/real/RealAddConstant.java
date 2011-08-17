package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealAddConstant implements UnaryOperation<Real> {

	private double constant;
	
	public RealAddConstant(double constant) {
		this.constant = constant;
	}
	
	@Override
	public void compute(Real input, Real output) {
		double value = input.getReal() + constant;
		output.setReal(value);
	}

}
