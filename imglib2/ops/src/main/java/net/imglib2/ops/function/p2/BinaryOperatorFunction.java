package net.imglib2.ops.function.p2;

import java.util.List;

import net.imglib2.ops.function.RealFunction;
import net.imglib2.ops.operator.BinaryOperator;
import net.imglib2.type.numeric.RealType;

public class BinaryOperatorFunction<T extends RealType<T>> implements RealFunction<T>
{
	private final BinaryOperator op;
	
	public BinaryOperatorFunction(final BinaryOperator op)
	{
		this.op = op;
	}
	
	@Override
	public boolean canAccept(final int numParameters) { return numParameters == 2; }

	@Override
	public void compute(final List<T> inputs, final T output)
	{
		double input1 = inputs.get(0).getRealDouble();
		double input2 = inputs.get(1).getRealDouble();
		double outValue = op.computeValue(input1, input2);
		output.setReal(outValue);
	}
	
}
