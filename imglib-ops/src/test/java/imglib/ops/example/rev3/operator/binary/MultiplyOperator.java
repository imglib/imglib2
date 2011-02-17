package imglib.ops.example.rev3.operator.binary;

import imglib.ops.example.rev3.operator.BinaryOperator;

public final class MultiplyOperator implements BinaryOperator
{
	@Override
	public double computeValue(double left, double right)
	{
		 return left * right;
	}
}
