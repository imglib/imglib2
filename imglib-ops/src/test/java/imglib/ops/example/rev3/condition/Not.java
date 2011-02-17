package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegerIndexedScalarFunction;

public final class Not implements Condition
{
	private final Condition cond;
	
	public Not(Condition cond)
	{
		this.cond = cond;
	}
	
	@Override
	public boolean isSatisfied(IntegerIndexedScalarFunction function, int[] position)
	{
		return ! cond.isSatisfied(function, position);
	}
}
