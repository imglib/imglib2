package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class MultiplyByConstant implements UnaryOperator
{
	private final double constant;
	
	public MultiplyByConstant(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return input * constant;
	}

}
