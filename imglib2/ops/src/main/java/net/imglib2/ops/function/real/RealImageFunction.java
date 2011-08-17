package net.imglib2.ops.function.real;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.type.numeric.RealType;

public class RealImageFunction implements Function<DiscreteNeigh,Real> {

	private RandomAccess<? extends RealType<?>> accessor;
	
	public RealImageFunction(Img<? extends RealType<?>> img) {
		this.accessor = img.randomAccess();
	}
	
	@Override
	public Real createVariable() {
		return new Real();
	}

	@Override
	public void evaluate(DiscreteNeigh input, Real output) {
		accessor.setPosition(input.getKeyPoint());
		double r = accessor.get().getRealDouble();
		output.setReal(r);
	}

}
