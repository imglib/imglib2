package net.imglib2.ops.example.rev3.condition;

import net.imglib2.ops.example.rev3.function.IntegerIndexedScalarFunction;

public final class Or implements Condition
{
	private final Condition left, right;
	
	public Or(Condition left, Condition right)
	{
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean isSatisfied(IntegerIndexedScalarFunction function, int[] position)
	{
		return left.isSatisfied(function, position) || right.isSatisfied(function, position);
	}
}
