package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class AddConstant implements UnaryOperator
{
	private double constant;
	
	public AddConstant(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return input + constant;
	}

}
