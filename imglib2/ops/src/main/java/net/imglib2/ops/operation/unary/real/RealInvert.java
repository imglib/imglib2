package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.Real;
import net.imglib2.ops.UnaryOperation;


public class RealInvert implements UnaryOperation<Real> {

	private final double actualMin, actualMax;

	public RealInvert(final double actualMin, final double actualMax)
	{
		this.actualMax = actualMax;
		this.actualMin = actualMin;
	}

	@Override
	public void compute(Real input, Real output) {
		double value = actualMax - (input.getReal() - actualMin);
		output.setReal(value);
	}
}

