package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public final class ValueLessThan implements Condition
{
	private final double bound;
	
	public ValueLessThan(double bound)
	{
		this.bound = bound;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
	{
		double value = function.evaluate(position);

		return value < bound;
	}
}
