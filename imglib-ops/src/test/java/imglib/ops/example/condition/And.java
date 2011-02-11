package imglib.ops.example.condition;

import mpicbg.imglib.type.numeric.RealType;
import imglib.ops.example.function.IntegralScalarFunction;

public class And<T extends RealType<T>> implements Condition<T>
{
	private Condition<T> left, right;
	private double lastEvaluation;
	
	public And(Condition<T> left, Condition<T> right)
	{
		this.left = left;
		this.right = right;
		this.lastEvaluation = Double.NaN;
	}
	
	@Override
	public boolean isSatisfied(IntegralScalarFunction<T> function, int[] position)
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
