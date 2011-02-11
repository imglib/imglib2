package imglib.ops.example.operator.unary;

import imglib.ops.example.operator.UnaryOperator;

/** simple example of a unary operator that can work with unsigned data (unlike NegateOperator) */
public class HalfOperator implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		return input / 2;
	}
}
