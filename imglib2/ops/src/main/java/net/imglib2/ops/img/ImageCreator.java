/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.img;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.InputIteratorFactory;
import net.imglib2.ops.input.PointInputIteratorFactory;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.binary.RealXor;
import net.imglib2.ops.operation.real.unary.RealMaxConstant;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

// TODO - move example code out to its own test class elsewhere

/**
 * A class used to create images from input images in conjunction with unary or
 * binary operations (see {@link UnaryOperation} and {@link BinaryOperation}).
 * 
 * @author Barry DeZonia
 *
 */
public class ImageCreator
{
	// -- default constructor --
	
	public ImageCreator() { }
	
	// -- public API --
	
	/**
	 * Creates an output image by applying a BinaryOperation to two input images.
	 * The size of the output image is determined as the region of overlap of the
	 * input images.
	 * 
	 * @param op The BinaryOperation used to generate the output image from the input images.
	 * @param input1 The first input Img.
	 * @param input2 The second input Img.
	 * @param imgFactory The factory used to create the output image.
	 * @param type The type of the output image.
	 * @return The combined pixel output image of specified type.
	 */
	public
		<I1 extends RealType<I1>,
			I2 extends RealType<I2>,
			O extends RealType<O>>
		Img<O> applyOp(BinaryOperation<O,O,O> op, Img<I1> input1,
										Img<I2> input2, ImgFactory<O> imgFactory, O type)
	{
		long[] span = determineSharedExtents(input1, input2);
		Img<O> output = imgFactory.create(span, type);
		binaryAssign(op, input1, input2, output, span);
		return output;
	}

	/**
	 * Fills an output image by applying a BinaryOperation to two input images.
	 * The region to fill in the output image is determined as the region of
	 * overlap of all three images.
	 * 
	 * @param op The BinaryOperation used to generate the output image from the input images.
	 * @param input1 The first input Img.
	 * @param input2 The second input Img.
	 * @param output The output Img to fill.
	 */
	public
		<I1 extends RealType<I1>,
			I2 extends RealType<I2>,
			O extends RealType<O>>
		void applyOp(BinaryOperation<O,O,O> op, Img<I1> input1, Img<I2> input2, Img<O> output)
	{
		long[] span = determineSharedExtents(input1, input2, output);
		binaryAssign(op, input1, input2, output, span);
	}
	
	/**
	 * Creates an output image by applying a UnaryOperation to an input image.
	 * The size of the output image matches the size of the input image.
	 * 
	 * @param op The UnaryOperation used to generate the output image from the input image.
	 * @param input The input Img.
	 * @param imgFactory The factory used to create the output image.
	 * @param type The type of the output image.
	 * @return The computed pixel output image of specified type.
	 */
	public <I extends RealType<I>, O extends RealType<O>>
		Img<O> applyOp(UnaryOperation<O,O> op, Img<I> input, ImgFactory<O> imgFactory, O type)
	{
		long[] span = new long[input.numDimensions()];
		input.dimensions(span);
		Img<O> output = imgFactory.create(span, type);
		unaryAssign(op, input, output, span);
		return output;
	}
	
	
	/**
	 * Fills an output image by applying a UnaryOperation to an input image.
	 * The region to fill in the output image is determined as the region of
	 * overlap of both images.
	 * 
	 * @param op The UnaryOperation used to generate the output image from the input image.
	 * @param input The input Img.
	 * @param output The output Img to fill.
	 */
	public <I extends RealType<I>, O extends RealType<O>>
		void applyOp(UnaryOperation<O,O> op, Img<I> input, Img<O> output)
	{
		long[] span = determineSharedExtents(input, output);
		unaryAssign(op, input, output, span);
	}

	// -- helpers --
	
	private long[] determineSharedExtents(Img<?> ... imgs) {
		if (imgs.length == 0)
			throw new IllegalArgumentException("at least one image must be provided");
		int numDims = imgs[0].numDimensions();
		long[] commonRegion = new long[numDims];
		imgs[0].dimensions(commonRegion);
		for (int i = 1; i < imgs.length; i++) {
			if (imgs[i].numDimensions() != numDims)
				throw new IllegalArgumentException(
					"images do not have compatible dimensions");
			for (int d = 0; d < numDims; d++) {
				commonRegion[d] = Math.min(imgs[i].dimension(d), commonRegion[d]);
			}
		}
		return commonRegion;
	}

	private <I1 extends RealType<I1>, I2 extends RealType<I2>, O extends RealType<O>>
		void binaryAssign(BinaryOperation<O,O,O> op, Img<I1> input1, Img<I2> input2, Img<O> output, long[] span)
	{
		long[] origin = new long[span.length];
		O type = output.firstElement();
		final Function<long[], O> f1 = new RealImageFunction<I1, O>(input1, type.copy());
		final Function<long[], O> f2 = new RealImageFunction<I2, O>(input2, type.copy());
		final Function<long[], O> binFunc =
			new GeneralBinaryFunction<long[], O, O, O>(f1, f2, op, type.copy());
		final InputIteratorFactory<long[]> inputFactory = new PointInputIteratorFactory();
		final ImageAssignment<O, O, long[]> assigner =
			new ImageAssignment<O, O, long[]>(output, origin, span, binFunc, null, inputFactory);
		assigner.assign();
	}
	
	private <I extends RealType<I>, O extends RealType<O>>
		void unaryAssign(UnaryOperation<O,O> op, Img<I> input, Img<O> output, long[] span)
	{
		final O type = output.firstElement();
		final Function<long[], O> f1 = new RealImageFunction<I, O>(input, type.copy());
		final Function<long[], O> unaryFunc =
			new GeneralUnaryFunction<long[], O, O>(f1, op, type.copy());
		final InputIteratorFactory<long[]> inputFactory = new PointInputIteratorFactory();
		final ImageAssignment<O, O, long[]> assigner =
			new ImageAssignment<O, O, long[]>(output, new long[span.length], span, unaryFunc, null, inputFactory);
		assigner.assign();
	}
	
	// -- example code --
	
	public static void main(String[] args) {
		// unary op tests
		maxExample();
		
		// binary op tests
		addExample();
		xorExample();
	}

	private static void addExample() {
		Img<UnsignedByteType> img1 = makeTestImage(new long[]{100,200}, new UnsignedByteType());
		Img<FloatType> img2 = makeTestImage(new long[]{75,225}, new FloatType());
		
		ImgFactory<ShortType> imgFactory = new ArrayImgFactory<ShortType>();

		BinaryOperation<ShortType, ShortType, ShortType> addOp =
				new RealAdd<ShortType, ShortType, ShortType>();
		
		ImageCreator creator = new ImageCreator();
		
		Img<ShortType> output =
				creator.applyOp(addOp, img1, img2, imgFactory, new ShortType());

		System.out.println("--- add test ---");
		System.out.println("Input image one is "+img1.dimension(0)+" x "+img1.dimension(1));
		System.out.println("Input image two is "+img2.dimension(0)+" x "+img2.dimension(1));
		System.out.println("Output image is "+output.dimension(0)+" x "+output.dimension(1));
	}

	private static void maxExample() {
		Img<DoubleType> img = makeTestImage(new long[]{512,512}, new DoubleType());
		
		ImgFactory<IntType> imgFactory = new ArrayImgFactory<IntType>();
		
		UnaryOperation<IntType, IntType> maxOp =	new RealMaxConstant<IntType,IntType>(150.0);
		
		ImageCreator creator =	new ImageCreator();
		
		Img<IntType> output =
				creator.applyOp(maxOp, img, imgFactory, new IntType());

		System.out.println("--- max test ---");
		System.out.println("Input image is "+img.dimension(0)+" x "+img.dimension(1));
		System.out.println("Output image is "+output.dimension(0)+" x "+output.dimension(1));
	}
	
	private static void xorExample() {
		Img<UnsignedByteType> img1 = makeTestImage(new long[]{400,300}, new UnsignedByteType());
		Img<UnsignedByteType> img2 = makeTestImage(new long[]{300,400}, new UnsignedByteType());
		
		ImgFactory<IntType> imgFactory = new ArrayImgFactory<IntType>();
		
		BinaryOperation<IntType, IntType, IntType> xorOp =
				new RealXor<IntType, IntType, IntType>();
		
		ImageCreator creator =	new ImageCreator();
		
		Img<IntType> output =
				creator.applyOp(xorOp, img1, img2, imgFactory, new IntType());

		System.out.println("--- xor test ---");
		System.out.println("Input image one is "+img1.dimension(0)+" x "+img1.dimension(1));
		System.out.println("Input image two is "+img2.dimension(0)+" x "+img2.dimension(1));
		System.out.println("Output image is "+output.dimension(0)+" x "+output.dimension(1));
	}

	// -- example support code --
	
	private static <O extends RealType<O> & NativeType<O>> Img<O> makeTestImage(long[] dims, O type)
	{
		ImgFactory<O> imgFactory = new ArrayImgFactory<O>();
		Img<O> image = imgFactory.create(dims, type);
		fillImage(image);
		return image;
	}
	
	private static <O extends RealType<O>> void fillImage(Img<O> img) {
		Random rng = new Random();
		Cursor<O> cursor = img.cursor();
		while (cursor.hasNext()) {
			double value = 256 * rng.nextDouble();
			cursor.next().setReal(value);
		}
	}
}
