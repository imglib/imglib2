package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class Max implements UnaryOperator
{
	private final double constant;
	
	public Max(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		if (input > constant)
			return constant;
		else
			return input;
	}

}
