package net.imglib2.ops.function.pn;

import net.imglib2.ops.function.RealFunction;
import net.imglib2.type.numeric.RealType;

public class AvgFunction<T extends RealType<T>> implements RealFunction<T>
{
	@Override
	public boolean canAccept(final int numParameters) { return numParameters >= 0; }
	
	@Override
	public void compute(final T[] inputs, final T output)
	{
		int numElements = inputs.length;
		
		if (numElements == 0)
			return;
		
		double sum = 0;

		for (T element : inputs)
			sum += element.getRealDouble();
		
		output.setReal(sum / numElements);
	}
	
}

