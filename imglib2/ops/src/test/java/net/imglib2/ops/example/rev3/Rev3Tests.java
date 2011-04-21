package net.imglib2.ops.example.rev3;

import static org.junit.Assert.assertEquals;
import net.imglib2.RandomAccess;
import net.imglib2.ops.example.rev3.condition.ValueLessThan;
import net.imglib2.ops.example.rev3.constraints.Constraints;
import net.imglib2.ops.example.rev3.function.AverageFunction;
import net.imglib2.ops.example.rev3.function.BinaryFunction;
import net.imglib2.ops.example.rev3.function.ComposedImageFunction;
import net.imglib2.ops.example.rev3.function.ConstantFunction;
import net.imglib2.ops.example.rev3.function.ConvolutionFunction;
import net.imglib2.ops.example.rev3.function.ImageFunction;
import net.imglib2.ops.example.rev3.function.IntegerIndexedScalarFunction;
import net.imglib2.ops.example.rev3.function.UnaryFunction;
import net.imglib2.ops.example.rev3.operator.BinaryOperator;
import net.imglib2.ops.example.rev3.operator.UnaryOperator;
import net.imglib2.ops.example.rev3.operator.binary.AddOperator;
import net.imglib2.ops.example.rev3.operator.binary.MultiplyOperator;
import net.imglib2.ops.example.rev3.operator.unary.HalfOperator;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class Rev3Tests
{

	// ************  private interface ********************************************************
	
	private static Img<UnsignedByteType> createImage(int width, int height)
	{
		ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>();
		
		return factory.create(new long[]{width,height}, new UnsignedByteType());
	}

	private static Img<UnsignedByteType> createPopulatedImage(int width, int height, int[] values)
	{
		Img<UnsignedByteType> image = createImage(width, height);
		
		RandomAccess<UnsignedByteType> accessor = image.randomAccess();
		
		long[] position = new long[2];
		
		int i = 0;
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				position[0] = x;
				position[1] = y;
				accessor.setPosition(position);
				accessor.get().setInteger(values[i++]);
			}
		}

		return image;
	}
	
	
	private static void assertImageValsEqual(int width, int height, int[] values, Img<UnsignedByteType> image)
	{
		RandomAccess<UnsignedByteType> accessor = image.randomAccess();

		long[] position = new long[2];
		
		int i = 0;
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				position[0] = x;
				position[1] = y;
				accessor.setPosition(position);
				assertEquals(values[i++], accessor.get().getInteger());
			}
		}
	}

	// ************  Tests ********************************************************
	
	@Test
	public void testConstantFill()
	{
		Img<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);
		
		ConstantFunction function = new ConstantFunction(43);
		
		Operation op = new Operation(outputImage, new long[2], new long[]{3,3}, function);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{43,43,43,43,43,43,43,43,43}, outputImage);
	}

	@Test
	public void testCopyOtherImage()
	{
		Img<UnsignedByteType> inputImage = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		
		Img<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);
		
		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction function = new ImageFunction(inputImage);
		
		Operation op = new Operation(outputImage, new long[2], new long[]{3,3}, function);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{1,2,3,4,5,6,7,8,9}, inputImage);
		assertImageValsEqual(3,3,new int[]{1,2,3,4,5,6,7,8,9}, outputImage);
	}
	
	@Test
	public void testConvolve()
	{
		double[] kernel = new double[]{1,1,1,1,1,1,1,1,1};

		Img<UnsignedByteType> inputImage = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		
		Img<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);
		
		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction imageFunction = new ImageFunction(inputImage);

		ConvolutionFunction convolver = new ConvolutionFunction(new int[]{3,3}, kernel, imageFunction);
		
		Operation op = new Operation(outputImage, new long[]{1,1}, new long[]{1,1}, convolver);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{0,0,0,0,45,0,0,0,0}, outputImage);
	}
	
	@Test
	public void testBinaryFunction()
	{
		Img<UnsignedByteType> leftImage = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		
		Img<UnsignedByteType> rightImage = createPopulatedImage(3,3,new int[]{10,20,30,40,50,60,70,80,90});

		Img<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);

		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction leftImageFunction = new ImageFunction(leftImage);
		
		ImageFunction rightImageFunction = new ImageFunction(rightImage);

		BinaryOperator addOp = new AddOperator();
		
		BinaryFunction addFunc = new BinaryFunction(addOp, leftImageFunction, rightImageFunction);
		
		Operation op = new Operation(outputImage, new long[2], new long[]{3,3}, addFunc);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{11,22,33,44,55,66,77,88,99}, outputImage);
	}
	
	@Test
	public void testUnaryFunction()
	{
		Img<UnsignedByteType> inputImage = createPopulatedImage(3,3,new int[]{10,20,30,40,50,60,70,80,90});

		Img<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);

		assertImageValsEqual(3,3,new int[9], outputImage);

		ImageFunction inputImageFunction = new ImageFunction(inputImage);
		
		UnaryOperator halfOp = new HalfOperator();
		
		UnaryFunction halfFunc = new UnaryFunction(halfOp, inputImageFunction);
		
		Operation op = new Operation(outputImage, new long[2], new long[]{3,3}, halfFunc);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{5,10,15,20,25,30,35,40,45}, outputImage);
	}

	@Test
	public void testComposedFunction()
	{
		// lets set an Image's values to half(2*Image1 + 3*Image2 + 4)
		
		Img<UnsignedByteType> inputImage1 = createPopulatedImage(3,3,new int[]{1,2,3,4,5,6,7,8,9});
		Img<UnsignedByteType> inputImage2 = createPopulatedImage(3,3,new int[]{5,10,15,20,25,30,35,40,45});
		Img<UnsignedByteType> outputImage = createPopulatedImage(3,3,new int[9]);

		MultiplyOperator multOp = new MultiplyOperator();
		AddOperator addOp = new AddOperator();
		HalfOperator halfOp = new HalfOperator();
		
		ImageFunction image1Func = new ImageFunction(inputImage1);
		ImageFunction image2Func = new ImageFunction(inputImage2);

		ConstantFunction two = new ConstantFunction(2);
		ConstantFunction three = new ConstantFunction(3);
		ConstantFunction four = new ConstantFunction(4);

		BinaryFunction term1 = new BinaryFunction(multOp, two, image1Func);
		
		BinaryFunction term2 = new BinaryFunction(multOp, three, image2Func);
		
		BinaryFunction twoTerms = new BinaryFunction(addOp, term1, term2);
		
		BinaryFunction threeTerms = new BinaryFunction(addOp, twoTerms, four);

		UnaryFunction totalFunc = new UnaryFunction(halfOp, threeTerms);
		
		Operation op = new Operation(outputImage, new long[2], new long[]{3,3}, totalFunc);
		
		op.execute();
		
		assertImageValsEqual(3,3,new int[]{11,19,28,36,45,53,62,70,79}, outputImage);  // NOTICE IT ROUNDS 0.5 UP ...
	}
	
	private class HalfPlanePositionFunction implements IntegerIndexedScalarFunction
	{
		private double xCoeff;
		private double yCoeff;
		
		public HalfPlanePositionFunction(double xCoeff, double yCoeff)
		{
			this.xCoeff = xCoeff;
			this.yCoeff = yCoeff;
		}

		@Override
		public double evaluate(long[] position)
		{
			return xCoeff*position[0] + yCoeff*position[1];
		}
	}
	
	@Test
	public void testConstraints()
	{
		// make an input image
		Img<UnsignedByteType> inputImage = createImage(9,9);
		RandomAccess<UnsignedByteType> accessor = inputImage.randomAccess();
		long[] pos = new long[2];
		int i = 0;
		for (int y = 0; y < 9; y++)
		{
			pos[1] = y;
			for (int x = 0; x < 9; x++)
			{
				pos[0] = x;
				accessor.setPosition(pos);
				accessor.get().setReal(i++);
			}
		}

		// make an unassigned output image
		Img<UnsignedByteType> outputImage = createPopulatedImage(9,9,new int[81]);

		// make a constraint where we are interested in values to right of 
		
		ImageFunction imageFunction = new ImageFunction(inputImage);
		
		Operation op = new Operation(outputImage, new long[2], new long[]{9,9}, imageFunction);
		
		ValueLessThan lessThan22 = new ValueLessThan(22);

		HalfPlanePositionFunction halfPlaneEquation = new HalfPlanePositionFunction(2,3);
		
		Constraints constraints = new Constraints();
		
		constraints.addConstraint(halfPlaneEquation, lessThan22);
		
		op.setConstraints(constraints);
		
		op.execute();
		
		accessor = outputImage.randomAccess();
		
		i = 0;
		for (int y = 0; y < 9; y++)
		{
			pos[1] = y;
			for (int x = 0; x < 9; x++)
			{
				pos[0] = x;
				accessor.setPosition(pos);
				double actualValue = accessor.get().getRealDouble();
				double expectedValue;
				if (((2*x) + (3*y)) < 22)
					expectedValue = i;
				else
					expectedValue = 0;
				i++;
				assertEquals(expectedValue, actualValue, 0);
			}
		}
	}

	@Test
	public void testComposedImage()
	{
		// set an output image to the average of subregions of 3 planes
		
		// make input images
		
		RandomAccess<UnsignedByteType> accessor;

		// make and populate an input image
		Img<UnsignedByteType> inputImage1 = createImage(9,9);
		accessor = inputImage1.randomAccess();
		for (int x = 0; x < 9; x++)
		{
			for (int y = 0; y < 9; y++)
			{
				int[] pos = new int[]{x,y};
				accessor.setPosition(pos);
				accessor.get().setReal(x*y);
			}
		}
		
		// make and populate an input image
		Img<UnsignedByteType> inputImage2 = createImage(9,9);
		accessor = inputImage2.randomAccess();
		for (int x = 0; x < 9; x++)
		{
			for (int y = 0; y < 9; y++)
			{
				int[] pos = new int[]{x,y};
				accessor.setPosition(pos);
				accessor.get().setReal(x);
			}
		}

		// make and populate an input image
		Img<UnsignedByteType> inputImage3 = createImage(9,9);
		accessor = inputImage3.randomAccess();
		for (int x = 0; x < 9; x++)
		{
			for (int y = 0; y < 9; y++)
			{
				int[] pos = new int[]{x,y};
				accessor.setPosition(pos);
				accessor.get().setReal(y);
			}
		}

		// make an unassigned output image
		
		long[] outputSpan = new long[]{5,5};
		
		Img<UnsignedByteType> outputImage = createImage(5,5);
		
		// compose a 3d image from the separate regions of the 2d images
		
		ComposedImageFunction composedImage = new ComposedImageFunction();
		composedImage.addImageRegion(inputImage1, new long[]{0,0}, outputSpan);
		composedImage.addImageRegion(inputImage2, new long[]{2,2}, outputSpan);
		composedImage.addImageRegion(inputImage3, new long[]{4,4}, outputSpan);
		
		// apply an average of x,y values along the z axis in the ComposedImage
		
		IntegerIndexedScalarFunction function = new AverageFunction(composedImage, new int[]{0,0,0}, new int[]{0,0,2});
		
		Operation op = new Operation(outputImage, new long[]{0,0}, outputSpan, function);
		
		op.execute();
		
		accessor = outputImage.randomAccess();
		for (int x = 0; x < 5; x++)
		{
			for (int y = 0; y < 5; y++)
			{
				int[] pos = new int[]{x,y};
				accessor.setPosition(pos);
				assertEquals(Math.round(((x*y)+(x+2)+(y+4))/3.0), accessor.get().getRealDouble(), 0);
			}
		}
	}
	
	// TODO
	// recreate all rev2 tests from NewFunctionlIdeas.java
}
