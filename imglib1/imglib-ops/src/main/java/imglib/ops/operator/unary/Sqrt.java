package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class Sqrt implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		if (input <= 0)
			return 0;
		
		return Math.sqrt(input);
	}

}
