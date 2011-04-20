package net.imglib2.ops.example.rev3.condition;

import net.imglib2.ops.example.rev3.function.IntegerIndexedScalarFunction;

public final class AxisGreaterThan implements Condition
{
	private final int axis;
	private final int value;
	
	public AxisGreaterThan(int axis, int value)
	{
		this.axis = axis;
		this.value = value;
	}
	
	@Override
	public boolean isSatisfied(IntegerIndexedScalarFunction function, int[] position)
	{
		return position[axis] > value;
	}
}
