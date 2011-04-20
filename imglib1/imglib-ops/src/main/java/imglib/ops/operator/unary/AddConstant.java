package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class AddConstant implements UnaryOperator
{
	private final double constant;
	
	public AddConstant(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return input + constant;
	}

}
