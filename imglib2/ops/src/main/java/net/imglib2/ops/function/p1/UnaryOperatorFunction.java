package net.imglib2.ops.function.p1;

import net.imglib2.ops.function.RealFunction;
import net.imglib2.ops.operator.UnaryOperator;
import net.imglib2.type.numeric.RealType;

public class UnaryOperatorFunction<T extends RealType<T>> implements RealFunction<T>
{
	private final UnaryOperator op;
	
	public UnaryOperatorFunction(final UnaryOperator op)
	{
		this.op = op;
	}
	
	@Override
	public boolean canAccept(final int numParameters) { return numParameters == 1; }

	@Override
	public void compute(final T[] inputs, final T output)
	{
		double inValue = inputs[0].getRealDouble();
		double outValue = op.computeValue(inValue);
		output.setReal(outValue);
	}
	
}
