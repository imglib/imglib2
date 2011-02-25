package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Constant implements UnaryOperator
{
	private double constant;
	
	public Constant(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return constant;
	}

}
