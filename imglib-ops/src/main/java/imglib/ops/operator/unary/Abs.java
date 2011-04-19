package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class Abs implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		return Math.abs(input);
	}

}
