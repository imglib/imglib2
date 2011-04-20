package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class Exp implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		return Math.exp(input);
	}

}
