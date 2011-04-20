package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class ATan extends UnaryOperation {

	public ATan(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public ATan(final IFunction fn) {
		super(fn);
	}
	public ATan(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.atan(a().eval());
	}
}
