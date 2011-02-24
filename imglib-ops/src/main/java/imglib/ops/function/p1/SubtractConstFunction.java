package imglib.ops.function.p1;

import imglib.ops.function.RealFunction;
import mpicbg.imglib.type.numeric.RealType;

public class SubtractConstFunction<T extends RealType<T>> implements RealFunction<T>
{
	private double constant;
	
	public SubtractConstFunction(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public boolean canAccept(int numParameters) { return numParameters == 1; }

	@Override
	public void compute(T[] inputs, T output)
	{
		double inValue = inputs[0].getRealDouble();
		output.setReal(inValue - constant);
	}
	
}
