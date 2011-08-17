package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealSetConstant implements UnaryOperation<Real> {

	private double constant;
	
	public RealSetConstant(double constant) {
		this.constant = constant;
	}
	
	@Override
	public void compute(Real input, Real output) {
		output.setReal(constant);
	}

}
