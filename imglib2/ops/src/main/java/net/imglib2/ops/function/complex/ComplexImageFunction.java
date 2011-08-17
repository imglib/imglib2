package net.imglib2.ops.function.complex;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Complex;
import net.imglib2.type.numeric.ComplexType;

public class ComplexImageFunction implements Function<DiscreteNeigh,Complex> {

	private RandomAccess<? extends ComplexType<?>> accessor;
	
	public ComplexImageFunction(Img<? extends ComplexType<?>> img) {
		this.accessor = img.randomAccess();
	}
	
	@Override
	public Complex createVariable() {
		return new Complex();
	}

	@Override
	public void evaluate(DiscreteNeigh input, Complex output) {
		accessor.setPosition(input.getKeyPoint());
		double r = accessor.get().getRealDouble();
		double i = accessor.get().getImaginaryDouble();
		output.setReal(r);
		output.setImag(i);
	}

}
