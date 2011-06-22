package net.imglib2.ops.function.p1;

import net.imglib2.ops.function.RealFunction;
import net.imglib2.ops.operator.UnaryOperator;

public class UnaryOperatorFunction implements RealFunction
{
	private final UnaryOperator op;
	
	public UnaryOperatorFunction(final UnaryOperator op)
	{
		this.op = op;
	}
	
	@Override
	public boolean canAccept(final int numParameters) { return numParameters == 1; }

	@Override
	public double compute(final double[] inputs)
	{
		return op.computeValue(inputs[0]);
	}
	
}
