package net.imglib2.ops;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.RandomAccess;
import net.imglib2.ops.condition.PixelOnBorder;
import net.imglib2.ops.condition.ValueGreaterThan;
import net.imglib2.ops.condition.ValueLessThan;
import net.imglib2.ops.function.p1.UnaryOperatorFunction;
import net.imglib2.ops.function.pn.AvgFunction;
import net.imglib2.ops.function.pn.ConstFunction;
import net.imglib2.ops.operation.AssignOperation;
import net.imglib2.ops.operation.TransformOperation;
import net.imglib2.ops.operator.UnaryOperator;
import net.imglib2.ops.operator.unary.MultiplyByConstant;
import net.imglib2.ops.operator.unary.Sqr;

import org.junit.Test;

import net.imglib2.img.Axes;
import net.imglib2.img.Axis;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@SuppressWarnings("unchecked")

public class Rev2FunctionalIdeasTest
{
	// ************  private interface ********************************************************
	
	private static ImgPlus<UnsignedByteType> createImg()
	{
		ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>();
		
		//new UnsignedByteType(), new ArrayContainerFactory());
		
		Img<UnsignedByteType> img = factory.create(new long[]{3,3}, new UnsignedByteType());
		
		return new ImgPlus<UnsignedByteType>(img, "junk1", new Axis[]{Axes.X, Axes.Y});
	}

	private static ImgPlus<UnsignedByteType> createPopulatedImg(int[] values)
	{
		ImgPlus<UnsignedByteType> image = createImg();
	
		RandomAccess<UnsignedByteType> accessor = image.randomAccess();
		
		long[] position = new long[2];
		
		int i = 0;
		
		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				position[0] = x;
				position[1] = y;
				accessor.setPosition(position);
				accessor.get().setInteger(values[i++]);
			}
		}

		return image;
	}
	
	
	private static void assertImgValsEqual(int[] values, ImgPlus<UnsignedByteType> image)
	{
		RandomAccess<UnsignedByteType> accessor = image.randomAccess();

		long[] position = new long[2];
		
		int i = 0;
		
		for (int y = 0; y < 3; y++)
		{
			for (int x = 0; x < 3; x++)
			{
				position[0] = x;
				position[1] = y;
				accessor.setPosition(position);
				assertEquals(values[i++], accessor.get().getInteger());
			}
		}
	}
	
	// ************  tests ********************************************************

	@Test
	public void testOneImgSquaring()
	{
		//System.out.println("square all input values");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction function = new UnaryOperatorFunction(op);
		
		AssignOperation operation = new AssignOperation(makeList(image0), image0, function);

		operation.execute();
		
		assertImgValsEqual(new int[]{1,4,9,16,25,36,49,64,81}, image0);

		//System.out.println("  success");
	}
	
	@Test
	public void testOneImgInputConditionGreater()
	{
		//System.out.println("square those where input values are greater than 4");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction function = new UnaryOperatorFunction(op);

		AssignOperation operation = new AssignOperation(makeList(image0), image0, function);

		operation.setInputCondition(0, new ValueGreaterThan(4));
		
		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,25,36,49,64,81}, image0);
		
		//System.out.println("  success");
	}
	
	@Test
	public void testOneImgOutputConditionLess()
	{
		//System.out.println("square those where original output values are less than 7");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction function = new UnaryOperatorFunction(op);

		AssignOperation operation = new AssignOperation(makeList(image0), image0, function);

		operation.setOutputCondition(new ValueLessThan(7));
		
		operation.execute();
		
		assertImgValsEqual(new int[]{1,4,9,16,25,36,7,8,9}, image0);

		//System.out.println("  success");
	}
	
	@Test
	public void testSecondImgFromOneImgSquaring()
	{
		//System.out.println("square one image into another");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		ImgPlus<UnsignedByteType> image1 = createPopulatedImg(new int[]{0,0,0,0,0,0,0,0,0});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction function = new UnaryOperatorFunction(op);

		AssignOperation operation = new AssignOperation(makeList(image0), image1, function);

		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,5,6,7,8,9}, image0);
		assertImgValsEqual(new int[]{1,4,9,16,25,36,49,64,81}, image1);

		//System.out.println("  success");
	}
	
	@Test
	public void testThirdImgFromTwoImgsAveraging()
	{
		//System.out.println("average two images into third");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		ImgPlus<UnsignedByteType> image1 = createPopulatedImg(new int[]{11,12,13,14,15,16,17,18,19});

		ImgPlus<UnsignedByteType> image2 = createPopulatedImg(new int[]{0,0,0,0,0,0,0,0,0});
		
		AvgFunction function = new AvgFunction();

		AssignOperation operation = new AssignOperation(makeList(image0, image1), image2, function);

		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,5,6,7,8,9}, image0);
		assertImgValsEqual(new int[]{11,12,13,14,15,16,17,18,19}, image1);
		assertImgValsEqual(new int[]{6,7,8,9,10,11,12,13,14}, image2);

		//System.out.println("  success");
	}
	
	@Test
	public void testEverythingAveraging()
	{
		//System.out.println("average two images into third conditionally");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,
																		4,5,6,
																		7,8,9});
		
		ImgPlus<UnsignedByteType> image1 = createPopulatedImg(new int[]{11,12,13,
																		14,15,16,
																		17,18,19});

		ImgPlus<UnsignedByteType> image2 = createPopulatedImg(new int[]{5,5,6,
																		6,7,7,
																		8,8,9});
		
		AvgFunction function = new AvgFunction();

		AssignOperation operation = new AssignOperation(makeList(image0, image1), image2, function);

		operation.setInputCondition(0, new ValueLessThan(8));
		operation.setInputRegion(0, new long[]{0,1}, new long[]{2,2});

		operation.setInputCondition(1, new ValueGreaterThan(14));
		operation.setInputRegion(1, new long[]{0,1}, new long[]{2,2});

		operation.setOutputRegion(new long[]{0,1}, new long[]{2,2});
		
		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,5,6,7,8,9}, image0);
		assertImgValsEqual(new int[]{11,12,13,14,15,16,17,18,19}, image1);
		assertImgValsEqual(new int[]{5,5,6,6,10,7,12,8,9}, image2);

		//System.out.println("  success");
	}

	@Test
	public void testTwoNonOverlappingRegionsInSameImg()
	{
		//System.out.println("average nonoverlapping regions of a single images into a third");
		
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,
																		4,5,6,
																		7,8,9});
		
		ImgPlus<UnsignedByteType> image1 = createPopulatedImg(new int[]{0,0,0,
																		0,0,0,
																		0,0,0});

		AvgFunction function = new AvgFunction();

		AssignOperation operation = new AssignOperation(makeList(image0, image0), image1, function);

		operation.setInputRegion(0, new long[]{0,0}, new long[]{3,1});

		operation.setInputRegion(1, new long[]{0,2}, new long[]{3,1});
		
		operation.setOutputRegion(new long[]{0,2}, new long[]{3,1});

		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,5,6,7,8,9}, image0);
		assertImgValsEqual(new int[]{0,0,0,0,0,0,4,5,6}, image1);

		//System.out.println("  success");
	}

	@Test
	public void testSpatialCondition()
	{
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(
				new int[]{0,0,0,
						0,255,0,
						0,255,0});

		ImgPlus<UnsignedByteType> image1 = createPopulatedImg(
				new int[]{0,0,0,
						0,0,0,
						0,0,0});
		
		ConstFunction function = new ConstFunction(1);

		AssignOperation operation = new AssignOperation(makeList(image0), image1, function);
		
		PixelOnBorder condition = new PixelOnBorder(image0, 255);
		
		operation.setInputCondition(0, condition);
		
		operation.execute();
		
		assertImgValsEqual(new int[]{0,0,0,0,255,0,0,255,0}, image0);
		assertImgValsEqual(new int[]{0,0,0,0,1,0,0,1,0}, image1);
	}

	@Test
	public void testTransformOperation() {
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(
			new int[]{1,2,3,
					4,5,6,
					7,8,9});
		MultiplyByConstant multiplier = new MultiplyByConstant(3);
		
		TransformOperation operation = new TransformOperation(image0, multiplier);
		
		operation.execute();
		
		assertImgValsEqual(new int[]{3,6,9,12,15,18,21,24,27}, image0);
	}
	
	@Test
	public void testTransformOperationWithRegion() {
		ImgPlus<UnsignedByteType> image0 = createPopulatedImg(
			new int[]{1,2,3,
					4,5,6,
					7,8,9});
		MultiplyByConstant multiplier = new MultiplyByConstant(3);
		
		TransformOperation operation = new TransformOperation(image0, multiplier);
		
		operation.setRegion(new long[]{0,0}, new long[]{2,2});
		
		operation.execute();
		
		assertImgValsEqual(new int[]{3,6,3,12,15,6,7,8,9}, image0);
	}
	
	// -- Helper methods --

	private List<ImgPlus<? extends RealType<?>>>
		makeList(final ImgPlus<? extends RealType<?>>... images)
	{
		
		final List<ImgPlus<? extends RealType<?>>> list =
			new ArrayList<ImgPlus<? extends RealType<?>>>();
		for (ImgPlus<? extends RealType<?>> img : images)
			list.add(img);
		return list;
	}

}
