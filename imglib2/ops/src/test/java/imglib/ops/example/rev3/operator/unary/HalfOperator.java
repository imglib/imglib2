package net.imglib2.ops.example.rev3.operator.unary;

import net.imglib2.ops.example.rev3.operator.UnaryOperator;

/** simple example of a unary operator that can work with unsigned data (unlike NegateOperator) */
public final class HalfOperator implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return input / 2;
	}
}
