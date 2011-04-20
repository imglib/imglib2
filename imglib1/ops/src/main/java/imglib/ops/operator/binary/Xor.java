package imglib.ops.operator.binary;

import imglib.ops.operator.BinaryOperator;

public final class Xor implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		return ((long)input1) ^ ((long)input2);
	}
	
}

