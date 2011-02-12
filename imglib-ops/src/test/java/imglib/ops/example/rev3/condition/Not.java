package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public class Not implements Condition
{
	private Condition cond;
	
	public Not(Condition cond)
	{
		this.cond = cond;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
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
