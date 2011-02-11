package imglib.ops.example.rev3.function;

import mpicbg.imglib.type.numeric.RealType;

public class AverageFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private IntegralScalarFunction<T> otherFunction;
	private int[] loDeltas;
	private int[] hiDeltas;
	private int[] relPos;
	
	public AverageFunction(IntegralScalarFunction<T> otherFunction, int[] loDeltas, int[] hiDeltas)
	{
		this.otherFunction = otherFunction;
		this.loDeltas = loDeltas;
		this.hiDeltas = hiDeltas;
		
		if (loDeltas.length != 2) // TODO - hack - make work in 2d only to get started
			throw new IllegalArgumentException("only 2d average supported");
		
		relPos = new int[2];
	}
	
	@Override
	public T createVariable()
	{
		return otherFunction.createVariable();
	}

	@Override
	public void evaluate(int[] position, T output)
	{
		T variable = createVariable();  // TODO - imagine we're trying to average the values from a ConstantFunction. this will
										// fail. may need to make function.evaluate() return a double rather than assign to a T.
		double sum = 0;					// then we can get rid of create variable everywhere and maybe get rid of much generics too???
		
		int numElements = 0;
		
		for (int dy = loDeltas[1]; dy <= hiDeltas[1]; dy++)
		{
			relPos[1] = position[1] + dy;
			for (int dx = loDeltas[0]; dx <= hiDeltas[0]; dx++)
			{
				relPos[0] = position[0] + dx;
				otherFunction.evaluate(relPos, variable);
				sum += variable.getRealDouble();
			}
		}

		if (numElements == 0)
			output.setZero();
		else
			output.setReal(sum / numElements);
	}

}
