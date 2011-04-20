package imglib.ops.function.pn;

import imglib.ops.function.RealFunction;
import mpicbg.imglib.type.numeric.RealType;

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
