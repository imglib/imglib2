package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class Invert implements UnaryOperator
{
	private final double actualMin, actualMax;
	
	public Invert(final double actualMin, final double actualMax)
	{
		this.actualMax = actualMax;
		this.actualMin = actualMin;
	}
	
	@Override
	public double computeValue(final double input)
	{
		return actualMax - (input - actualMin);
	}
	
}
