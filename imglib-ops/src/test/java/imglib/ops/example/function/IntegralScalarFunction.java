package imglib.ops.example.function;

import imglib.ops.example.VariableFactory;
import mpicbg.imglib.type.numeric.RealType;

// NOTE - a function will eventually need some information about its domain. either relative info (convolution on +/- 3 x/y)
//   or absolute info (image dims = 0..4 x, 0..12 y, 0..2 z). Ideally we'd also be able to specify an infinite domain using
//   Double.POSITIVE_INFINITY. Think of a constant function who spits back the same value no matter what the position is.

/** the base interface of most functions in imglib-ops */
public interface IntegralScalarFunction<T extends RealType<T>> extends VariableFactory<T>
{
	void evaluate(int[] position, T output);
}
