package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public final class Not implements Condition
{
	private final Condition cond;
	
	public Not(Condition cond)
	{
		this.cond = cond;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
	{
		return ! cond.isSatisfied(function, position);
	}
}
