package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public class ValueLessThan implements Condition
{
	private double value;
	private boolean wasFullyEvaluated;
	private double lastValue;
	
	public ValueLessThan(double value)
	{
		this.value = value;
		this.wasFullyEvaluated = false;
		this.lastValue = Double.NaN;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
	{
		this.lastValue = function.evaluate(position);

		this.wasFullyEvaluated = true;
		
		return this.lastValue < value;
	}

	@Override
	public double getLastFunctionEvaluation()
	{
		return this.lastValue;
	}

	@Override
	public boolean functionWasFullyEvaluated()
	{
		return this.wasFullyEvaluated;
	}

	@Override
	public void initEvaluationState()
	{
		this.wasFullyEvaluated = false;
	}
}
