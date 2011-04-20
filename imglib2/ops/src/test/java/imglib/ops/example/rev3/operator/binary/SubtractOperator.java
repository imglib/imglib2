package net.imglib2.ops.example.rev3.operator.binary;

import net.imglib2.ops.example.rev3.operator.BinaryOperator;

public final class SubtractOperator implements BinaryOperator
{
	@Override
	public double computeValue(double left, double right)
	{
		 return left - right;
	}
}
