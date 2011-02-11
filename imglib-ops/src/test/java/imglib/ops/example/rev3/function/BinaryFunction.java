package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.operator.BinaryOperator;
import mpicbg.imglib.type.numeric.RealType;

public class BinaryFunction<R extends RealType<R>> implements IntegralScalarFunction<R>
{
	private IntegralScalarFunction<R> leftFunction;
	private IntegralScalarFunction<R> rightFunction;
	private BinaryOperator operator;

	public BinaryFunction(BinaryOperator operator, IntegralScalarFunction<R> leftFunction, IntegralScalarFunction<R> rightFunction)
	{
		this.leftFunction = leftFunction;
		this.rightFunction = rightFunction;
		this.operator = operator;
	}
	
	@Override
	public R createVariable()
	{
		R variable = leftFunction.createVariable();
		
		if (variable == null)
			variable = rightFunction.createVariable();
		
		return variable;
	}

	@Override
	public void evaluate(int[] position, R output)
	{
		leftFunction.evaluate(position, output);

		double leftValue = output.getRealDouble();
		
		rightFunction.evaluate(position, output);
		
		double rightValue = output.getRealDouble();

		double value = operator.computeValue(leftValue, rightValue);
		
		output.setReal(value);
	}
}

