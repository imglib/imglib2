package net.imglib2.ops.function.pn;

import net.imglib2.ops.function.RealFunction;

public class ConstFunction implements RealFunction
{
	private final double value;
	
	public ConstFunction(final double value)
	{
		this.value = value;
	}
	
	@Override
	public boolean canAccept(final int numParameters)
	{
		return numParameters >= 0;
	}

	@Override
	public double compute(final double[] inputs)
	{
		return value;
	}

}
