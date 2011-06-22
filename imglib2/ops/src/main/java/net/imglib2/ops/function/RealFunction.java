package net.imglib2.ops.function;

public interface RealFunction
{
	boolean canAccept(final int numParameters);
	double compute(final double[] inputs);
}

