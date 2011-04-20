package net.imglib2.ops.example.rev3.function;


// NOTE - a function will eventually need some information about its domain. either relative info (convolution on +/- 3 x/y)
//   or absolute info (image dims = 0..4 x, 0..12 y, 0..2 z). Ideally we'd also be able to specify an infinite domain using
//   Double.POSITIVE_INFINITY. Think of a constant function who spits back the same value no matter what the position is.

/** the base interface of most functions in net.imglib2.ops */
public interface IntegerIndexedScalarFunction
{
	double evaluate(int[] position);
}
