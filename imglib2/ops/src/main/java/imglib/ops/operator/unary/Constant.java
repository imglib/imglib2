package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public final class Constant implements UnaryOperator
{
	private final double constant;
	
	public Constant(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return constant;
	}

}
