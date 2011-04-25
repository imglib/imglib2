package net.imglib2.ops;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import net.imglib2.RandomAccess;
import net.imglib2.ops.condition.PixelOnBorder;
import net.imglib2.ops.condition.ValueGreaterThan;
import net.imglib2.ops.condition.ValueLessThan;
import net.imglib2.ops.function.p1.UnaryOperatorFunction;
import net.imglib2.ops.function.pn.AvgFunction;
import net.imglib2.ops.function.pn.ConstFunction;
import net.imglib2.ops.operation.AssignOperation;
import net.imglib2.ops.operator.UnaryOperator;
import net.imglib2.ops.operator.unary.Sqr;

import org.junit.Test;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

@SuppressWarnings("unchecked")

public class Rev2FunctionalIdeasTest
{
	// ************  private interface ********************************************************
	
	private static Img<UnsignedByteType> createImg()
	{
		ArrayImgFactory<UnsignedByteType> factory = new ArrayImgFactory<UnsignedByteType>();
		
		//new UnsignedByteType(), new ArrayContainerFactory());
		
		return factory.create(new long[]{3,3}, new UnsignedByteType());
	}

	private static Img<UnsignedByteType> createPopulatedImg(int[] values)
	{
		Img<UnsignedByteType> image = createImg();
	
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
	
	
	private static void assertImgValsEqual(int[] values, Img<UnsignedByteType> image)
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
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction<UnsignedByteType> function = new UnaryOperatorFunction<UnsignedByteType>(op);
		
		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);
		
		AssignOperation<UnsignedByteType> operation =
			new AssignOperation<UnsignedByteType>(inputs, image0, function);
		
		operation.execute();
		
		assertImgValsEqual(new int[]{1,4,9,16,25,36,49,64,81}, image0);

		//System.out.println("  success");
	}
	
	@Test
	public void testOneImgInputConditionGreater()
	{
		//System.out.println("square those where input values are greater than 4");
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction<UnsignedByteType> function = new UnaryOperatorFunction<UnsignedByteType>(op);

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image0, function);

		operation.setInputCondition(0, new ValueGreaterThan<UnsignedByteType>(4));
		
		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,25,36,49,64,81}, image0);
		
		//System.out.println("  success");
	}
	
	@Test
	public void testOneImgOutputConditionLess()
	{
		//System.out.println("square those where original output values are less than 7");
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction<UnsignedByteType> function = new UnaryOperatorFunction<UnsignedByteType>(op);

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image0, function);

		operation.setOutputCondition(new ValueLessThan<UnsignedByteType>(7));
		
		operation.execute();
		
		assertImgValsEqual(new int[]{1,4,9,16,25,36,7,8,9}, image0);

		//System.out.println("  success");
	}
	
	@Test
	public void testSecondImgFromOneImgSquaring()
	{
		//System.out.println("square one image into another");
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		Img<UnsignedByteType> image1 = createPopulatedImg(new int[]{0,0,0,0,0,0,0,0,0});
		
		UnaryOperator op = new Sqr();
		
		UnaryOperatorFunction<UnsignedByteType> function = new UnaryOperatorFunction<UnsignedByteType>(op);

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image1, function);

		operation.execute();
		
		assertImgValsEqual(new int[]{1,2,3,4,5,6,7,8,9}, image0);
		assertImgValsEqual(new int[]{1,4,9,16,25,36,49,64,81}, image1);

		//System.out.println("  success");
	}
	
	@Test
	public void testThirdImgFromTwoImgsAveraging()
	{
		//System.out.println("average two images into third");
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,4,5,6,7,8,9});
		
		Img<UnsignedByteType> image1 = createPopulatedImg(new int[]{11,12,13,14,15,16,17,18,19});

		Img<UnsignedByteType> image2 = createPopulatedImg(new int[]{0,0,0,0,0,0,0,0,0});
		
		AvgFunction<UnsignedByteType> function = new AvgFunction<UnsignedByteType>();

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);
		inputs.add(image1);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image2, function);

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
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,
																		4,5,6,
																		7,8,9});
		
		Img<UnsignedByteType> image1 = createPopulatedImg(new int[]{11,12,13,
																		14,15,16,
																		17,18,19});

		Img<UnsignedByteType> image2 = createPopulatedImg(new int[]{5,5,6,
																		6,7,7,
																		8,8,9});
		
		AvgFunction<UnsignedByteType> function = new AvgFunction<UnsignedByteType>();

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);
		inputs.add(image1);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image2, function);

		operation.setInputCondition(0, new ValueLessThan<UnsignedByteType>(8));
		operation.setInputRegion(0, new long[]{0,1}, new long[]{2,2});

		operation.setInputCondition(1, new ValueGreaterThan<UnsignedByteType>(14));
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
		
		Img<UnsignedByteType> image0 = createPopulatedImg(new int[]{1,2,3,
																		4,5,6,
																		7,8,9});
		
		Img<UnsignedByteType> image1 = createPopulatedImg(new int[]{0,0,0,
																		0,0,0,
																		0,0,0});

		AvgFunction<UnsignedByteType> function = new AvgFunction<UnsignedByteType>();

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);
		inputs.add(image0);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image1, function);

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
		Img<UnsignedByteType> image0 = createPopulatedImg(
				new int[]{0,0,0,
						0,255,0,
						0,255,0});

		Img<UnsignedByteType> image1 = createPopulatedImg(
				new int[]{0,0,0,
						0,0,0,
						0,0,0});
		
		ConstFunction<UnsignedByteType> function = new ConstFunction<UnsignedByteType>(1);

		ArrayList<Img<UnsignedByteType>> inputs = new ArrayList<Img<UnsignedByteType>>();
		inputs.add(image0);

		AssignOperation<UnsignedByteType> operation = new AssignOperation<UnsignedByteType>(inputs, image1, function);
		
		PixelOnBorder<UnsignedByteType> condition = new PixelOnBorder<UnsignedByteType>(image0, 255);
		
		operation.setInputCondition(0, condition);
		
		operation.execute();
		
		assertImgValsEqual(new int[]{0,0,0,0,255,0,0,255,0}, image0);
		assertImgValsEqual(new int[]{0,0,0,0,1,0,0,1,0}, image1);
	}
}
