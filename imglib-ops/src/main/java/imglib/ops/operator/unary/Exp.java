package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Exp implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return Math.exp(input);
	}

}
