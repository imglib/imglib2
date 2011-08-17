package net.imglib2.ops.function.complex;

import net.imglib2.ops.Complex;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.Real;


public class ComplexAdapterFunction<N extends Neighborhood<?>> implements Function<N,Complex> {

	private Function<N,Real> realFunc;
	private Real real;
	
	public ComplexAdapterFunction(Function<N,Real> realFunc) {
		this.realFunc = realFunc;
		this.real = new Real();
	}
	
	@Override
	public void evaluate(N neigh, Complex value) {
		realFunc.evaluate(neigh, real);
		value.setReal(real.getReal());
		value.setImag(0);
	}
	
	@Override
	public Complex createVariable() {
		return new Complex();
	}
}
