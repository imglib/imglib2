package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class Log implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		if (input <= 0)
			return 0;
		
		return Math.log(input);
	}

}
