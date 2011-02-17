package imglib.ops.example.rev3.operator.unary;

import imglib.ops.example.rev3.operator.UnaryOperator;

public final class NegateOperator implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return -input;
	}
}
