package net.imglib2.ops.function.real;

import net.imglib2.ops.Complex;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.Real;


public class ImaginaryAdapterFunction<N extends Neighborhood<?>> implements Function<N,Real> {
	private Function<N,Complex> complexFunc;
	private Complex variable;

	public ImaginaryAdapterFunction(Function<N,Complex> complexFunc) {
		this.complexFunc = complexFunc;
	}
	
	@Override
	public void evaluate(N neigh, Real r) {
		complexFunc.evaluate(neigh, variable);
		r.setReal(variable.getImag());
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}
}

