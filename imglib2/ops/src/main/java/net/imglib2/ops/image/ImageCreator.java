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


package net.imglib2.ops.image;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Function;
import net.imglib2.ops.InputIteratorFactory;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.PointInputIteratorFactory;
import net.imglib2.ops.operation.binary.real.RealAdd;
import net.imglib2.ops.operation.binary.real.RealXor;
import net.imglib2.ops.operation.unary.real.RealMaxConstant;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;


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
			O extends RealType<O>> Img<O> 
		applyOp(BinaryOperation<O,O,O> op, Img<I1> input1,
						Img<I2> input2, ImgFactory<O> imgFactory, O type)
	{
		long[] span = determineSharedExtents(input1, input2);
		long[] origin = new long[span.length];
		Img<O> output = imgFactory.create(span, type);
		final Function<long[], O> f1 = new RealImageFunction<I1, O>(input1, type.copy());
		final Function<long[], O> f2 = new RealImageFunction<I2, O>(input2, type.copy());
		final Function<long[], O> binFunc =
			new GeneralBinaryFunction<long[], O, O, O>(f1, f2, op, type.copy());
		final InputIteratorFactory<long[]> inputFactory = new PointInputIteratorFactory();
		final ImageAssignment<O, O, long[]> assigner =
			new ImageAssignment<O, O, long[]>(output, origin, span, binFunc, null, inputFactory);
		assigner.assign();
		return output;
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
	public <I extends RealType<I>, O extends RealType<O>> Img<O>
		applyOp(UnaryOperation<O,O> op, Img<I> input, ImgFactory<O> imgFactory, O type)
	{
		long[] span = new long[input.numDimensions()];
		long[] origin = new long[span.length];
		input.dimensions(span);
		Img<O> output = imgFactory.create(span, type);
		final Function<long[], O> f1 = new RealImageFunction<I, O>(input, type.copy());
		final Function<long[], O> unaryFunc =
			new GeneralUnaryFunction<long[], O, O>(f1, op, type.copy());
		final InputIteratorFactory<long[]> inputFactory = new PointInputIteratorFactory();
		final ImageAssignment<O, O, long[]> assigner =
			new ImageAssignment<O, O, long[]>(output, origin, span, unaryFunc, null, inputFactory);
		assigner.assign();
		return output;
	}
	
	
	// -- helpers --
	
	private long[] determineSharedExtents(Img<?> img1, Img<?> img2) {
		int numDims = img1.numDimensions();
		if (img2.numDimensions() != numDims)
			throw new IllegalArgumentException("images do not have compatible dimensions");
		long[] commonRegion = new long[numDims];
		for (int i = 0; i < numDims; i++) {
			commonRegion[i] = Math.min(img1.dimension(i), img2.dimension(i));
		}
		return commonRegion;
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
	
	private static void fillImage(Img<? extends RealType<?>> img) {
		Random rng = new Random();
		Cursor<? extends RealType<?>> cursor = img.cursor();
		while (cursor.hasNext()) {
			double value = 256 * rng.nextDouble();
			cursor.next().setReal(value);
		}
	}
}
