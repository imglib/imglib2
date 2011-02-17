package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public final class And implements Condition
{
	private final Condition left, right;
	
	public And(Condition left, Condition right)
	{
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
	{
		return left.isSatisfied(function, position) && right.isSatisfied(function, position);
	}
}
