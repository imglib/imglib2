package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Sin extends UnaryOperation {

	public Sin(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Sin(final IFunction fn) {
		super(fn);
	}
	public Sin(final Number val) {
		super(val);
	}

	@Override
	public double eval() {
		return Math.sin(a().eval());
	}
}
