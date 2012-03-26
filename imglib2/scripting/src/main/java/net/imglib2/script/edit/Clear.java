package net.imglib2.script.edit;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/** Return 0 for all values.
 * Equivalent to creating a new empty image with the dimensions of the input.
 * 
 * @author Albert Cardona
 */
public final class Clear extends UnaryOperation
{
	public Clear(final IFunction fn) {
		super(fn);
	}

	public <R extends RealType<R>> Clear(final IterableRealInterval<R> img) {
		super(img);
	}

	/** Present only for completeness. {@param val} is give to {@link UnaryOperation} super constructor,
	 * but {@link #eval()} always returns 0. */
	public Clear(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return 0;
	}
}
