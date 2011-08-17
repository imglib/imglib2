package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealGammaConstant implements UnaryOperation<Real> {

	private double constant;
	
	public RealGammaConstant(double constant) {
		this.constant = constant;
	}
	
	@Override
	public void compute(Real input, Real output) {
		double inputVal = input.getReal();
		if (inputVal <= 0)
			output.setReal(0);
		else {
			double value = Math.exp(this.constant * Math.log(inputVal));
			output.setReal(value);
		}
	}

}
