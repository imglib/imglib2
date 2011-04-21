package net.imglib2.ops.example.rev3.function;


public final class ConstantFunction implements IntegerIndexedScalarFunction
{
	private final double value;
	
	public ConstantFunction(double value)
	{
		this.value = value;
	}
	
	@Override
	public double evaluate(long[] position)
	{
		return value;
	}

}
