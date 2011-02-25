package imglib.ops.function.p1;

import imglib.ops.function.RealFunction;
import imglib.ops.operator.UnaryOperator;
import mpicbg.imglib.type.numeric.RealType;

public class UnaryOperatorFunction<T extends RealType<T>> implements RealFunction<T>
{
	private UnaryOperator op;
	
	public UnaryOperatorFunction(UnaryOperator op)
	{
		this.op = op;
	}
	
	@Override
	public boolean canAccept(int numParameters) { return numParameters == 1; }

	@Override
	public void compute(T[] inputs, T output)
	{
		double inValue = inputs[0].getRealDouble();
		double outValue = op.computeValue(inValue);
		output.setReal(outValue);
	}
	
}
