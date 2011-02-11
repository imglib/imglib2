package imglib.ops.example.rev3.function;

import java.util.Arrays;

import mpicbg.imglib.type.numeric.RealType;

public class MedianFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private IntegralScalarFunction<T> otherFunction;
	private int[] loDeltas;
	private int[] hiDeltas;
	private int[] relPos;
	private double[] workspace;
	
	public MedianFunction(IntegralScalarFunction<T> otherFunction, int[] loDeltas, int[] hiDeltas)
	{
		this.otherFunction = otherFunction;
		this.loDeltas = loDeltas;
		this.hiDeltas = hiDeltas;
		
		if (loDeltas.length != 2) // TODO - hack - make work in 2d only to get started
			throw new IllegalArgumentException("onbly 2d median supported");
		
		relPos = new int[2];
		
		int numCols = hiDeltas[0] + 1 + Math.abs(loDeltas[0]);
		int numRows = hiDeltas[1] + 1 + Math.abs(loDeltas[1]);
		
		workspace = new double[numCols * numRows];
	}
	
	@Override
	public T createVariable()
	{
		return otherFunction.createVariable();
	}

	@Override
	public void evaluate(int[] position, T output)
	{
		T variable = createVariable();
		
		int numElements = 0;
		
		for (int dy = loDeltas[1]; dy <= hiDeltas[1]; dy++)
		{
			relPos[1] = position[1] + dy;
			for (int dx = loDeltas[0]; dx <= hiDeltas[0]; dx++)
			{
				relPos[0] = position[0] + dx;
				variable.setZero();
				otherFunction.evaluate(relPos, variable);
				workspace[numElements++] = variable.getRealDouble();
			}
		}

		Arrays.sort(workspace);  // TODO - slow but works
		
		double median;
		if ((numElements % 2) == 0)  // even number of elements - return the average of the middle two
		{
			double middle1 = workspace[numElements/2 - 1];

			double middle2 = workspace[numElements/2];
			
			median = (middle1 + middle2) / 2.0;
		}
		else  // odd number of elements -- return the middle one
			median = workspace[numElements/2];
		
		output.setReal(median);
	}

}
