package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Abs implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return Math.abs(input);
	}

}
