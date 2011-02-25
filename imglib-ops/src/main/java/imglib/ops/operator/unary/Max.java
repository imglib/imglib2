package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Max implements UnaryOperator
{
	private double constant;
	
	public Max(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		if (input > constant)
			return constant;
		else
			return input;
	}

}
