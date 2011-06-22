package net.imglib2.ops.function.p2;

import net.imglib2.ops.function.RealFunction;
import net.imglib2.ops.operator.BinaryOperator;

public class BinaryOperatorFunction implements RealFunction
{
	private final BinaryOperator op;
	
	public BinaryOperatorFunction(final BinaryOperator op)
	{
		this.op = op;
	}
	
	@Override
	public boolean canAccept(final int numParameters) { return numParameters == 2; }

	@Override
	public double compute(final double[] inputs)
	{
		double input1 = inputs[0];
		double input2 = inputs[1];
		return op.computeValue(input1, input2);
	}
	
}
