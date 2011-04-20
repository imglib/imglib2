package imglib.ops.operator.binary;

import imglib.ops.operator.BinaryOperator;

public final class Multiply implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		return input1 * input2;
	}
	
}

