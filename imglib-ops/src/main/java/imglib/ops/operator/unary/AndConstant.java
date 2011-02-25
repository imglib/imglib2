package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class AndConstant implements UnaryOperator
{
	private long constant;
	
	public AndConstant(long constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return ((long)input) & constant;
	}

}
