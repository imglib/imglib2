package imglib.ops.example.condition;

import imglib.ops.example.function.IntegralScalarFunction;
import mpicbg.imglib.type.numeric.RealType;

public class AxisGreaterThan<T extends RealType<T>> implements Condition<T>
{
	private int axis;
	private int value;
	
	public AxisGreaterThan(int axis, int value)
	{
		this.axis = axis;
		this.value = value;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction<T> function, int[] position)
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
