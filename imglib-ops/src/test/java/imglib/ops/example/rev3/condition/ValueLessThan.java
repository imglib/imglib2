package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;
import mpicbg.imglib.type.numeric.RealType;

public class ValueLessThan<T extends RealType<T>> implements Condition<T>
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
	public boolean isSatisfied(IntegralScalarFunction<T> function, int[] position)
	{
		T variable = function.createVariable();
		
		function.evaluate(position, variable);

		this.wasFullyEvaluated = true;
		
		this.lastValue = variable.getRealDouble();
		
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
