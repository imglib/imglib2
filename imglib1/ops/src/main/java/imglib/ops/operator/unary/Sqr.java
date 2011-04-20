package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class Sqr implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		return input * input;
	}

}
