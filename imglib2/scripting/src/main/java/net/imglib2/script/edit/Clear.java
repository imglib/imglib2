package net.imglib2.script.edit;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/** Return 0 for all values.
 * 
 * @author Albert Cardona
 */
public final class Clear extends UnaryOperation
{
	public Clear(IFunction fn) {
		super(fn);
	}

	public Clear(IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}

	public Clear(Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return 0;
	}
}
