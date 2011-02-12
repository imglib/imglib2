package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.operator.UnaryOperator;

public class UnaryFunction implements IntegralScalarFunction
{
	private IntegralScalarFunction inputFunction;
	private UnaryOperator operator;

	public UnaryFunction(UnaryOperator operator, IntegralScalarFunction inputFunction)
	{
		this.inputFunction = inputFunction;
		this.operator = operator;
	}
	
	@Override
	public double evaluate(int[] position)
	{
		double input = inputFunction.evaluate(position);

		return operator.computeValue(input);
	}
}

