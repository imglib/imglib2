package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealDivideConstant implements UnaryOperation<Real> {

	private double constant;
	private double dbzVal;
	
	public RealDivideConstant(double constant, double dbzVal) {
		this.constant = constant;
		this.dbzVal = dbzVal;
	}
	
	@Override
	public void compute(Real input, Real output) {
		if (constant == 0) {
			output.setReal(dbzVal);
		}
		else { // not dividing by zero
			double value = input.getReal() / constant;
			output.setReal(value);
		}
	}

}
