package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class Exp implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		return Math.exp(input);
	}

}
