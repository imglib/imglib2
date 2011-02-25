package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class OrConstant implements UnaryOperator
{
	private long constant;
	
	public OrConstant(long constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(double input)
	{
		return ((long)input) | constant;
	}

}
