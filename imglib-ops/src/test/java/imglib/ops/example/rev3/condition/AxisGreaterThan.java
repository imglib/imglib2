package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

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
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
	{
		return position[axis] > value;
	}
}
