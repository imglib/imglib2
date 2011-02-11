package imglib.ops.example.rev3.function;

import mpicbg.imglib.type.numeric.RealType;

// think that maybe you would construct a LessThan Condition with value 2.0 & pass it a DistanceFunction.
// this condition evaluates to true calculated distance from ref pt matches

public class DistanceFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private int[] referencePoint;
	
	public DistanceFunction(int[] fromPoint)
	{
		referencePoint = fromPoint;
	}
	
	@Override
	public T createVariable()
	{
		return null;  // TODO - again this variable factory stuff probably needs to go away
	}

	@Override
	public void evaluate(int[] position, T output)
	{
		double sumOfSquares = 0;
		
		for (int i = 0; i < position.length; i++)
		{
			int delta = position[i] - referencePoint[i];
			sumOfSquares += delta * delta;
		}
		
		double value = Math.sqrt(sumOfSquares);
		
		output.setReal(value);
	}

}
