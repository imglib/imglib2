package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.operator.UnaryOperator;

public final class UnaryFunction implements IntegerIndexedScalarFunction
{
	private final IntegerIndexedScalarFunction inputFunction;
	private final UnaryOperator operator;

	public UnaryFunction(UnaryOperator operator, IntegerIndexedScalarFunction inputFunction)
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

