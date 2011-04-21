package net.imglib2.ops.example.rev3.function;


// think that maybe you would construct a LessThan Condition with value 2.0 & pass it a DistanceFunction.
// this condition evaluates to true calculated distance from ref pt matches

public final class DistanceFunction implements IntegerIndexedScalarFunction
{
	private final long[] referencePoint;
	
	public DistanceFunction(long[] fromPoint)
	{
		referencePoint = fromPoint;
	}
	
	@Override
	public double evaluate(long[] position)
	{
		double sumOfSquares = 0;
		
		for (int i = 0; i < position.length; i++)
		{
			long delta = position[i] - referencePoint[i];
			sumOfSquares += delta * delta;
		}
		
		return Math.sqrt(sumOfSquares);
	}

}
