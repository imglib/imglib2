package imglib.ops.example.rev3.function;

import mpicbg.imglib.type.numeric.RealType;

public class ConstantFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private double value;
	
	public ConstantFunction(double value)
	{
		this.value = value;
	}
	
	@Override
	public T createVariable()
	{
		return null;  // can't create a variable on a constant
	}

	@Override
	public void evaluate(int[] position, T output)
	{
		output.setReal(value);
	}

}
