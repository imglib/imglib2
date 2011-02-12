package imglib.ops.example.rev3.condition;

import imglib.ops.example.rev3.function.IntegralScalarFunction;

public class And implements Condition
{
	private Condition left, right;
	private double lastEvaluation;
	
	public And(Condition left, Condition right)
	{
		this.left = left;
		this.right = right;
		this.lastEvaluation = Double.NaN;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction function, int[] position)
	{
		return left.isSatisfied(function, position) && right.isSatisfied(function, position);
	}

	@Override
	public boolean functionWasFullyEvaluated()
	{
		if (left.functionWasFullyEvaluated())
		{
			lastEvaluation = left.getLastFunctionEvaluation();
			return true;
		}
		if (right.functionWasFullyEvaluated())
		{
			lastEvaluation = right.getLastFunctionEvaluation();
			return true;
		}
		return false;
	}

	@Override
	public double getLastFunctionEvaluation()
	{
		return lastEvaluation;
	}

	@Override
	public void initEvaluationState()
	{
		left.initEvaluationState();
		right.initEvaluationState();
	}

}
