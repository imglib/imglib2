package net.imglib2.ops.function.real;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.Real;


public class ConstantRealFunction<N extends Neighborhood<?>> implements Function<N,Real> {
	private double real;

	public ConstantRealFunction(double r) {
		real = r;
	}
	
	@Override
	public void evaluate(N neigh, Real r) {
		r.setReal(real);
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}
}

