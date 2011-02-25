package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class SubtractConstant implements UnaryOperator
{
	private double constant;
	
	public SubtractConstant(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return input - constant;
	}

}
