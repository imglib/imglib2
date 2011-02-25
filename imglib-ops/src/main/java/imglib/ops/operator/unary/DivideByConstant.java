package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class DivideByConstant implements UnaryOperator
{
	private double constant;
	
	public DivideByConstant(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		if (constant != 0)
			return input / constant;
		else
		{
			if (input > 0)
				return Double.POSITIVE_INFINITY;
			else if (input < 0)
				return Double.NEGATIVE_INFINITY;
			else
				return Double.NaN;
		}
	}

}
