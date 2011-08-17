package net.imglib2.ops.function.complex;

import net.imglib2.ops.Complex;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;


public class ConstantComplexFunction<N extends Neighborhood<?>> implements Function<N,Complex> {
	private double real;
	private double imag;

	public ConstantComplexFunction(double r, double i) {
		real = r;
		imag = i;
	}
	
	@Override
	public void evaluate(N neigh, Complex c) {
		c.setReal(real);
		c.setImag(imag);
	}
	
	@Override
	public Complex createVariable() {
		return new Complex();
	}
}

