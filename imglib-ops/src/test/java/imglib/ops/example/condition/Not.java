package imglib.ops.example.condition;

import mpicbg.imglib.type.numeric.RealType;
import imglib.ops.example.function.IntegralScalarFunction;

public class Not<T extends RealType<T>> implements Condition<T>
{
	private Condition<T> cond;
	
	public Not(Condition<T> cond)
	{
		this.cond = cond;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction<T> function, int[] position)
	{
		return ! cond.isSatisfied(function, position);
	}

	@Override
	public boolean functionWasFullyEvaluated()
	{
		return cond.functionWasFullyEvaluated();
	}

	@Override
	public double getLastFunctionEvaluation()
	{
		return cond.getLastFunctionEvaluation();
	}

	@Override
	public void initEvaluationState()
	{
		cond.initEvaluationState();
	}

}
