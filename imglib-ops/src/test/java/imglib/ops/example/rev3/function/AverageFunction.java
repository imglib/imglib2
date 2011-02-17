package imglib.ops.example.rev3.function;

public final class AverageFunction implements IntegerIndexedScalarFunction
{
	private final IntegerIndexedScalarFunction otherFunction;
	private final int[] loDeltas;
	private final int[] hiDeltas;
	private final int[] relPos;
	
	public AverageFunction(IntegerIndexedScalarFunction otherFunction, int[] loDeltas, int[] hiDeltas)
	{
		this.otherFunction = otherFunction;
		this.loDeltas = loDeltas;
		this.hiDeltas = hiDeltas;
		
		if (loDeltas.length != 2) // TODO - hack - make work in 2d only to get started
			throw new IllegalArgumentException("only 2d average supported");
		
		relPos = new int[2];
	}
	
	@Override
	public double evaluate(int[] position)
	{
		double sum = 0;
		
		int numElements = 0;
		
		for (int dy = loDeltas[1]; dy <= hiDeltas[1]; dy++)
		{
			relPos[1] = position[1] + dy;
			for (int dx = loDeltas[0]; dx <= hiDeltas[0]; dx++)
			{
				relPos[0] = position[0] + dx;
				sum += otherFunction.evaluate(relPos);
			}
		}

		if (numElements == 0)
			return 0;
		else
			return sum / numElements;
	}

}
