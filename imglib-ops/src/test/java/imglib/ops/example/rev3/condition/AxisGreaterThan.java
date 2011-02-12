package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public class AxisGreaterThan implements Condition
{
	private int axis;
	private int value;
	
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

	@Override
	public double getLastFunctionEvaluation()
	{
		return Double.NaN;
	}

	@Override
	public boolean functionWasFullyEvaluated()
	{
		return false;  // ALWAYS FALSE
	}

	@Override
	public void initEvaluationState()
	{
		// NOTHING TO DO
	}
}
