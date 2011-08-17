package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealXorConstant implements UnaryOperation<Real> {

	private long constant;
	
	public RealXorConstant(long constant) {
		this.constant = constant;
	}
	
	@Override
	public void compute(Real input, Real output) {
		long value = constant ^ (long) input.getReal();
		output.setReal(value);
	}

}
