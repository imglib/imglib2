package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class XorConstant implements UnaryOperator
{
	private long constant;
	
	public XorConstant(long constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return ((long)input) ^ constant;
	}

}
