package net.imglib2.ops.function.pn;

import net.imglib2.ops.function.RealFunction;

// NOTE - modified to remove type dependency code
//   Thus compute() returns a value. And thus NullFunction may no longer be useful.

/** NullFunction is a function that does not change the output. It accepts any number of parameters.
 * 
 * This class is useful for keeping an AssignOperation from changing its output image values. If one adds an Observer to
 * the AssignOperation one can do anything with the iteration. For example one could gather statistics. Eliminates the
 * need for a QueryOperation class. 
 */
public class NullFunction implements RealFunction
{
	@Override
	public boolean canAccept(final int numParameters) {
		return true;
	}

	@Override
	public double compute(final double[] inputs)
	{
		return Double.NaN;
	}

}
