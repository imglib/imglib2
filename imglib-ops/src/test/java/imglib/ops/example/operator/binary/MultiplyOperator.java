package imglib.ops.example.operator.binary;

import imglib.ops.example.operator.BinaryOperator;

public class MultiplyOperator implements BinaryOperator
{
	@Override
	public double computeValue(double left, double right)
	{
		 return left * right;
	}
}
