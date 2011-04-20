package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Round extends UnaryOperation {

	public Round(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Round(final IFunction fn) {
		super(fn);
	}
	public Round(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.round(a().eval());
	}
}
