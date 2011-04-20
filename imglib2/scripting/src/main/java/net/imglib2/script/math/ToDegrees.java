package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class ToDegrees extends UnaryOperation {

	public ToDegrees(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public ToDegrees(final IFunction fn) {
		super(fn);
	}
	public ToDegrees(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.toDegrees(a().eval());
	}
}
