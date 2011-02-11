package imglib.ops.function;

import mpicbg.imglib.type.numeric.RealType;

public class ConstFunction<T extends RealType<T>> implements RealFunction<T>
{
	final double value;
	
	public ConstFunction(double value)
	{
		this.value = value;
	}
	
	@Override
	public boolean canAccept(int numParameters)
	{
		return numParameters >= 0;
	}

	@Override
	public void compute(T[] inputs, T output)
	{
		output.setReal(value);
	}

}
