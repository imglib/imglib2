package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class MultiplyByConstant implements UnaryOperator
{
	private double constant;
	
	public MultiplyByConstant(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return input * constant;
	}

}
