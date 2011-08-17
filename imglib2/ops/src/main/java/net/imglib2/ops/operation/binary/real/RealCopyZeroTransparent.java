package net.imglib2.ops.operation.binary.real;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Real;

// NB - this method required by IJ2 for IJ1 compatibility

public class RealCopyZeroTransparent implements BinaryOperation<Real> {

	@Override
	public void compute(Real input1, Real input2, Real output) {
		if (input2.getReal() == 0)
			output.setReal(input1.getReal());
		else
			output.setReal(input2.getReal());
	}

}
