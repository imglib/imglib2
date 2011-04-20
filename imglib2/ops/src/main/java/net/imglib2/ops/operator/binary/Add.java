package net.imglib2.ops.operator.binary;

import net.imglib2.ops.operator.BinaryOperator;

public final class Add implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		return input1 + input2;
	}
	
}

