package imglib.ops.example.function;

import imglib.ops.example.operator.UnaryOperator;
import mpicbg.imglib.type.numeric.RealType;

public class UnaryFunction<R extends RealType<R>> implements IntegralScalarFunction<R>
{
	private IntegralScalarFunction<R> inputFunction;
	private UnaryOperator operator;

	public UnaryFunction(UnaryOperator operator, IntegralScalarFunction<R> inputFunction)
	{
		this.inputFunction = inputFunction;
		this.operator = operator;
	}
	
	@Override
	public R createVariable()
	{
		return inputFunction.createVariable();
	}

	@Override
	public void evaluate(int[] position, R output)
	{
		inputFunction.evaluate(position, output);

		double input = output.getRealDouble();
		
		double value = operator.computeValue(input);
		
		output.setReal(value);
	}
}

