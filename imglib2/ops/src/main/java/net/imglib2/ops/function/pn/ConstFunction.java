package net.imglib2.ops.function.pn;

import net.imglib2.ops.function.RealFunction;
import net.imglib2.type.numeric.RealType;

public class ConstFunction<T extends RealType<T>> implements RealFunction<T>
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
	public void compute(final T[] inputs, final T output)
	{
		output.setReal(value);
	}

}
