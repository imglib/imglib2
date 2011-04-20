package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Log10 extends UnaryOperation {

	public Log10(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Log10(final IFunction fn) {
		super(fn);
	}
	public Log10(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log10(a().eval());
	}
}
