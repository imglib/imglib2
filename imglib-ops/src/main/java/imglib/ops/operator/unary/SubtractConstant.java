package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class SubtractConstant implements UnaryOperator
{
	private final double constant;
	
	public SubtractConstant(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return input - constant;
	}

}
