package net.imglib2.ops.function.pn;

import net.imglib2.ops.function.RealFunction;

public class AvgFunction implements RealFunction
{
	@Override
	public boolean canAccept(final int numParameters) { return numParameters >= 0; }
	
	@Override
	public double compute(final double[] inputs)
	{
		int numElements = inputs.length;
		
		if (numElements == 0)
			return 0;
		
		double sum = 0;

		for (double element : inputs)
			sum += element;
		
		return sum / numElements;
	}
	
}

