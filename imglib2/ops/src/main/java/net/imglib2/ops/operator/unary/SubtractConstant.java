package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

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
