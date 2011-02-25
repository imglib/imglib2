package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Log implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		if (input <= 0)
			return 0;
		
		return Math.log(input);
	}

}
