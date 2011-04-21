package net.imglib2.ops.example.rev3.condition;

import net.imglib2.ops.example.rev3.function.IntegerIndexedScalarFunction;

public final class Not implements Condition
{
	private final Condition cond;
	
	public Not(Condition cond)
	{
		this.cond = cond;
	}
	
	@Override
	public boolean isSatisfied(IntegerIndexedScalarFunction function, long[] position)
	{
		return ! cond.isSatisfied(function, position);
	}
}
