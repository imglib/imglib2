package net.imglib2.ops.operator.binary;

import net.imglib2.ops.operator.BinaryOperator;

public final class Divide implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		if (input2 == 0)
		{
			if (input1 > 0)
				return Double.POSITIVE_INFINITY;
			else if (input1 < 0)
				return Double.NEGATIVE_INFINITY;
			else
				return Double.NaN;
		}
		
		return input1 / input2;
	}
	
}

