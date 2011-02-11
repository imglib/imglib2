package imglib.ops.example.function;

import mpicbg.imglib.type.numeric.RealType;

// TODO - could be derived from a plain ScalarFunction taking real coords. probably want to do this eventually. a discrete one and a continuous one.

public class ConvolutionFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private IntegralScalarFunction<T> otherFunction;
	private int[] kernelDimensions;
	private double[] kernelValues;
	private int[] relPos;
	private T variable;
	
	public ConvolutionFunction(int[] kernelDimensions, double[] kernelValues, IntegralScalarFunction<T> otherFunction)
	{
		// TODO - hack - only work in two dims to get working
		if (kernelDimensions.length != 2)
			throw new IllegalArgumentException("temporarily only allowing 2d convolution");
		
		// TODO - hack - only work in odd dimensions to get things working
		if (((kernelDimensions[0] %2) == 0) || ((kernelDimensions[1] %2) == 0))
			throw new IllegalArgumentException("temporarily only allowing odd numbers for kernel dimensions");
		
		this.relPos = new int[2];  // temporary workspace : TODO - this is a 2d hack

		this.otherFunction = otherFunction;
		this.kernelDimensions = kernelDimensions;
		this.kernelValues = kernelValues;
		this.variable = createVariable();
	}
	
	@Override
	public void evaluate(int[] position, T output)
	{
		double sum = 0;

		int xHalfRange = kernelDimensions[0] / 2;
		
		int yHalfRange = kernelDimensions[1] / 2;
		
		for (int y = -yHalfRange; y <= yHalfRange; y++)
		{
			relPos[1] = position[1] + y;
			
			for (int x = -xHalfRange; x <= xHalfRange; x++)
			{
				relPos[0] = position[0] + x;
				
				variable.setZero();
				
				otherFunction.evaluate(relPos, variable);
				
				int kPos = relPos[1]*kernelDimensions[0] + relPos[0];
				
				double kernelValue = kernelValues[kPos];
				
				sum += kernelValue * variable.getRealDouble();
			}
		}
		
		output.setReal(sum);
	}

	@Override
	public T createVariable()
	{
		return otherFunction.createVariable();
	}
}
