package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class Gamma implements UnaryOperator
{
	private final double constant;
	
	public Gamma(final double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public double computeValue(final double input)
	{
		if (input <= 0)
			return 0;
		
		return Math.exp(this.constant * Math.log(input));
	}

}
