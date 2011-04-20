package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Sqrt extends UnaryOperation {

	public Sqrt(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Sqrt(final IFunction fn) {
		super(fn);
	}
	public Sqrt(final Number val) {
		super(val);
	}

	@Override
	public double eval() {
		return Math.sqrt(a().eval());
	}
}
