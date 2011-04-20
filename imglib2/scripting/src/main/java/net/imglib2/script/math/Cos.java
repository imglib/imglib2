package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Cos extends UnaryOperation {

	public Cos(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Cos(final IFunction fn) {
		super(fn);
	}
	public Cos(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.cos(a().eval());
	}
}
