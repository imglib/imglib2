package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.operator.BinaryOperator;

public class BinaryFunction implements IntegralScalarFunction
{
	private IntegralScalarFunction leftFunction;
	private IntegralScalarFunction rightFunction;
	private BinaryOperator operator;

	public BinaryFunction(BinaryOperator operator, IntegralScalarFunction leftFunction, IntegralScalarFunction rightFunction)
	{
		this.leftFunction = leftFunction;
		this.rightFunction = rightFunction;
		this.operator = operator;
	}
	
	@Override
	public double evaluate(int[] position)
	{
		double leftValue = leftFunction.evaluate(position);

		double rightValue = rightFunction.evaluate(position);
		
		return operator.computeValue(leftValue, rightValue);
	}
}

