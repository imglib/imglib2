package net.imglib2.ops.example.rev3.operator.unary;

import net.imglib2.ops.example.rev3.operator.UnaryOperator;

public final class NegateOperator implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return -input;
	}
}
