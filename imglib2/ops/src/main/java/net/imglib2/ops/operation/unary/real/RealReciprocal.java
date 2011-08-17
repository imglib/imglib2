package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealReciprocal implements UnaryOperation<Real> {

	private double dbzVal;
	
	public RealReciprocal(double dbzVal) {
		this.dbzVal = dbzVal;
	}
	
	@Override
	public void compute(Real input, Real output) {
		double inputVal = input.getReal();
		if (inputVal == 0)
			output.setReal(dbzVal);
		else
			output.setReal(1.0 / inputVal);
	}

}
