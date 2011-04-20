package net.imglib2.ops.example.rev3.function;


// TODO - could be derived from a plain ScalarFunction taking real coords. probably want to do this eventually. a discrete one and a continuous one.

public final class ConvolutionFunction implements IntegerIndexedScalarFunction
{
	private final IntegerIndexedScalarFunction otherFunction;
	private final int[] kernelDimensions;
	private final double[] kernelValues;
	private final int[] relPos;
	
	public ConvolutionFunction(int[] kernelDimensions, double[] kernelValues, IntegerIndexedScalarFunction otherFunction)
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
	}
	
	@Override
	public double evaluate(int[] position)
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
				
				double value = otherFunction.evaluate(relPos);
				
				int kPos = relPos[1]*kernelDimensions[0] + relPos[0];
				
				double kernelValue = kernelValues[kPos];
				
				sum += kernelValue * value;
			}
		}
		
		return sum;
	}
}
