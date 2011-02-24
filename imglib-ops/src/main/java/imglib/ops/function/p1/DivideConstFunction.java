package imglib.ops.function.p1;

import imglib.ops.function.RealFunction;
import mpicbg.imglib.type.numeric.RealType;

public class DivideConstFunction<T extends RealType<T>> implements RealFunction<T>
{
	private double constant;
	
	public DivideConstFunction(double constant)
	{
		this.constant = constant;
	}
	
	@Override
	public boolean canAccept(int numParameters) { return numParameters == 1; }

	@Override
	public void compute(T[] inputs, T output)
	{
		double inValue = inputs[0].getRealDouble();
		double outValue;
		if (constant != 0)
			outValue = inValue / constant;
		else
		{
			if (inValue > 0)
				outValue = Double.POSITIVE_INFINITY;
			else if (inValue < 0)
				outValue = Double.NEGATIVE_INFINITY;
			else
				outValue = Double.NaN;
		}
		output.setReal(outValue);
	}
	
}
