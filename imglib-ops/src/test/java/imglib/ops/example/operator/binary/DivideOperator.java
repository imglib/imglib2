package imglib.ops.example.operator.binary;

import imglib.ops.example.operator.BinaryOperator;

public class DivideOperator implements BinaryOperator
{
	@Override
	public double computeValue(double left, double right)
	{
		if (right == 0)
		{
			if (left < 0)
				return Double.NEGATIVE_INFINITY;
			else if (left > 0)
				return Double.POSITIVE_INFINITY;
			else
				return Double.NaN;
		}
		
		return left / right;
	}

}
