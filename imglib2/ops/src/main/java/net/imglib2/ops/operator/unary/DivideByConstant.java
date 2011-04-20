package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class DivideByConstant implements UnaryOperator
{
	private final double constant;
	
	public DivideByConstant(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
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
