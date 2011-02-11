package imglib.ops.example.operator.unary;

import imglib.ops.example.operator.UnaryOperator;

public class NegateOperator implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return -input;
	}
}
