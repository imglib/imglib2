package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class XorConstant implements UnaryOperator
{
	private final long constant;
	
	public XorConstant(final long constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return ((long)input) ^ constant;
	}

}
