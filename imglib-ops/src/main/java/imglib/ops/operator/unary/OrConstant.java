package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class OrConstant implements UnaryOperator
{
	private final long constant;
	
	public OrConstant(final long constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return ((long)input) | constant;
	}

}
