package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Sqr implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return input * input;
	}

}
