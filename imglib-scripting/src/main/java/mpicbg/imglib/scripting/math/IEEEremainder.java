package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.BinaryOperation;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;

/** Returns sqrt(x2 +y2) without intermediate overflow or underflow. */
public class IEEEremainder extends BinaryOperation {
	public IEEEremainder(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public IEEEremainder(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public IEEEremainder(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public IEEEremainder(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public IEEEremainder(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public IEEEremainder(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public IEEEremainder(final IFunction left, final Number val) {
		super(left, val);
	}

	public IEEEremainder(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public IEEEremainder(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final double eval() {
		return Math.IEEEremainder(a().eval(), b().eval());
	}
}