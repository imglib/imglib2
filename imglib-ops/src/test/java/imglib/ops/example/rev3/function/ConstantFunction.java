package imglib.ops.example.rev3.function;


public class ConstantFunction implements IntegralScalarFunction
{
	private double value;
	
	public ConstantFunction(double value)
	{
		this.value = value;
	}
	
	@Override
	public double evaluate(int[] position)
	{
		return value;
	}

}
